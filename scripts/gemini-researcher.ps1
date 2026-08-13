param(
    [Parameter(Mandatory = $true)]
    [string]$Prompt,

    [string[]]$Models = @(
        'gemini-3.1-flash-lite',
        'gemini-3.5-flash-lite',
        'gemma-4-26b-a4b-it',
        'gemma-4-31b-it',
        'gemini-3.5-flash',
        'gemini-3.6-flash',
        'gemini-3-flash-preview'
    ),

    [ValidateRange(15, 300)]
    [int]$TimeoutSeconds = 45,

    [switch]$UseCliFallback
)

$ErrorActionPreference = 'Stop'

if ([string]::IsNullOrWhiteSpace($env:GEMINI_API_KEY)) {
    throw 'GEMINI_API_KEY is not set. Set it in the environment before invoking Gemini.'
}

$geminiCommand = Get-Command gemini -ErrorAction SilentlyContinue
if ($geminiCommand) {
    $geminiExecutable = $geminiCommand.Source
} else {
    $npmPrefix = npm prefix --global
    $geminiExecutable = Join-Path $npmPrefix 'gemini.cmd'
}

if (-not (Test-Path -LiteralPath $geminiExecutable)) {
    throw 'Gemini CLI was not found. Install it with: npm install --global @google/gemini-cli'
}

function Invoke-GeminiCli {
    param([string]$Model, [string]$PromptText)

    $stdoutPath = [System.IO.Path]::GetTempFileName()
    $stderrPath = [System.IO.Path]::GetTempFileName()
    try {
        Set-Content -LiteralPath $stdoutPath -Value $PromptText -Encoding UTF8
        $startInfo = [System.Diagnostics.ProcessStartInfo]::new()
        $startInfo.FileName = 'powershell.exe'
        $startInfo.UseShellExecute = $false
        $startInfo.CreateNoWindow = $true
        $startInfo.RedirectStandardOutput = $true
        $startInfo.RedirectStandardError = $true
        $escapedExecutable = $geminiExecutable.Replace("'", "''")
        $escapedPromptPath = $stdoutPath.Replace("'", "''")
        $escapedModel = $Model.Replace("'", "''")
        $childCommand = "`$promptText = Get-Content -Raw -LiteralPath '$escapedPromptPath'; & '$escapedExecutable' --skip-trust --approval-mode plan --model '$escapedModel' --prompt `$promptText --output-format text; exit `$LASTEXITCODE"
        $encodedCommand = [Convert]::ToBase64String([Text.Encoding]::Unicode.GetBytes($childCommand))
        $startInfo.Arguments = "-NoProfile -NonInteractive -ExecutionPolicy Bypass -EncodedCommand $encodedCommand"

        $process = [System.Diagnostics.Process]::new()
        $process.StartInfo = $startInfo
        [void]$process.Start()
        $stdoutTask = $process.StandardOutput.ReadToEndAsync()
        $stderrTask = $process.StandardError.ReadToEndAsync()

        if (-not $process.WaitForExit($TimeoutSeconds * 1000)) {
            try { $process.Kill() } catch {}
            [void]$process.WaitForExit(5000)
            return [pscustomobject]@{
                ExitCode = 124
                Output = "[gemini-researcher] Timed out after $TimeoutSeconds seconds."
            }
        }

        $stdout = $stdoutTask.GetAwaiter().GetResult()
        $stderr = $stderrTask.GetAwaiter().GetResult()
        return [pscustomobject]@{
            ExitCode = $process.ExitCode
            Output = (($stdout, $stderr | Where-Object { -not [string]::IsNullOrWhiteSpace($_) }) -join "`n").Trim()
        }
    } finally {
        Remove-Item -LiteralPath $stdoutPath, $stderrPath -Force -ErrorAction SilentlyContinue
    }
}

function Invoke-GeminiRest {
    param([string]$Model, [string]$PromptText)

    $headers = @{
        'x-goog-api-key' = $env:GEMINI_API_KEY
        'Content-Type' = 'application/json'
    }
    $body = @{
        contents = @(@{
            role = 'user'
            parts = @(@{ text = $PromptText })
        })
        generationConfig = @{
            temperature = 0.2
        }
    } | ConvertTo-Json -Depth 8

    $uri = "https://generativelanguage.googleapis.com/v1beta/models/$Model`:generateContent"
    try {
        $response = Invoke-RestMethod -Uri $uri -Headers $headers -Method Post -Body $body -TimeoutSec $TimeoutSeconds
        return [pscustomobject]@{
            Success = $true
            Output = ($response.candidates[0].content.parts | ForEach-Object { $_.text }) -join "`n"
        }
    } catch {
        $status = $null
        try { $status = [int]$_.Exception.Response.StatusCode } catch {}
        return [pscustomobject]@{
            Success = $false
            Output = if ($status) { "HTTP $status" } else { $_.Exception.Message }
        }
    }
}

foreach ($model in $Models) {
    Write-Host "[gemini-researcher] REST model: $model"
    $restResult = Invoke-GeminiRest -Model $model -PromptText $Prompt
    if ($restResult.Success) {
        Write-Output $restResult.Output
        exit 0
    }
    Write-Warning "[gemini-researcher] REST model $model failed: $($restResult.Output)"
    Start-Sleep -Seconds 2
}

if ($UseCliFallback) {
    Write-Warning '[gemini-researcher] REST models failed; trying optional CLI fallback.'
    foreach ($model in $Models) {
        Write-Host "[gemini-researcher] CLI fallback model: $model"
        $attempt = Invoke-GeminiCli -Model $model -PromptText $Prompt
        if (-not [string]::IsNullOrWhiteSpace($attempt.Output)) {
            Write-Output $attempt.Output
        }
        if ($attempt.ExitCode -eq 0) {
            exit 0
        }
        Write-Warning "[gemini-researcher] CLI model $model failed."
        Start-Sleep -Seconds 2
    }
}

Write-Error '[gemini-researcher] All configured Gemini REST models failed (likely quota or temporary service exhaustion).'
exit 1
