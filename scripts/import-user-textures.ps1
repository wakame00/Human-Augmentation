param([string]$SourceDirectory = 'C:\Users\wakame\AppData\Local\Temp')

Add-Type -AssemblyName System.Drawing
$projectRoot = Split-Path -Parent $PSScriptRoot
$itemOutput = Join-Path $projectRoot 'src\main\resources\assets\humanaugmentation\textures\item'
$blockOutput = Join-Path $projectRoot 'src\main\resources\assets\humanaugmentation\textures\block'
New-Item -ItemType Directory -Force -Path $itemOutput, $blockOutput | Out-Null

function Export-PixelTexture {
    param([string]$InputName, [int]$X, [int]$Y, [int]$Size, [string]$OutputPath, [bool]$TransparentBackground)
    $source = [System.Drawing.Bitmap]::FromFile((Join-Path $SourceDirectory $InputName))
    try {
        $cropped = New-Object System.Drawing.Bitmap $Size, $Size
        try {
            $graphics = [System.Drawing.Graphics]::FromImage($cropped)
            try {
                $graphics.CompositingMode = [System.Drawing.Drawing2D.CompositingMode]::SourceCopy
                $graphics.DrawImage($source, 0, 0, [System.Drawing.Rectangle]::new($X, $Y, $Size, $Size), [System.Drawing.GraphicsUnit]::Pixel)
            } finally { $graphics.Dispose() }

            $output = New-Object System.Drawing.Bitmap 16, 16, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
            try {
                $graphics = [System.Drawing.Graphics]::FromImage($output)
                try {
                    $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
                    $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half
                    $graphics.CompositingMode = [System.Drawing.Drawing2D.CompositingMode]::SourceCopy
                    $graphics.DrawImage($cropped, [System.Drawing.Rectangle]::new(0, 0, 16, 16), 0, 0, $Size, $Size, [System.Drawing.GraphicsUnit]::Pixel)
                } finally { $graphics.Dispose() }

                if ($TransparentBackground) {
                    for ($py = 0; $py -lt 16; $py++) {
                        for ($px = 0; $px -lt 16; $px++) {
                            $color = $output.GetPixel($px, $py)
                            $maximum = [Math]::Max($color.R, [Math]::Max($color.G, $color.B))
                            $minimum = [Math]::Min($color.R, [Math]::Min($color.G, $color.B))
                            if (($maximum - $minimum) -le 8 -and $color.R -ge 115 -and $color.R -le 205) {
                                $output.SetPixel($px, $py, [System.Drawing.Color]::Transparent)
                            }
                        }
                    }
                }
                $output.Save($OutputPath, [System.Drawing.Imaging.ImageFormat]::Png)
            } finally { $output.Dispose() }
        } finally { $cropped.Dispose() }
    } finally { $source.Dispose() }
}

Export-PixelTexture 'codex-clipboard-eb46f0d4-5158-44d7-bfef-d806aecd2e14.png' 233 135 666 (Join-Path $itemOutput 'dna_sampler.png') $true
Export-PixelTexture 'codex-clipboard-069c404a-d0d4-4df9-bedd-26b70029b0e8.png' 1 9 160 (Join-Path $itemOutput 'dna_sample.png') $true
Export-PixelTexture 'codex-clipboard-0d2795fe-6496-4e2e-9add-2f00ed271f26.png' 1 5 256 (Join-Path $itemOutput 'gene_injection.png') $true
Export-PixelTexture 'codex-clipboard-7013337d-7b2a-4264-bb48-1cd0f0f59cb8.png' 4 4 160 (Join-Path $blockOutput 'rugged_machine_side.png') $false
Export-PixelTexture 'codex-clipboard-bc4e0b05-4a6d-46b6-bddb-5d8315e9ee6c.png' 7 5 192 (Join-Path $blockOutput 'dna_analyzer_top.png') $false
Export-PixelTexture 'codex-clipboard-cdb04419-09e4-4e73-8468-ede88fe697fd.png' 8 5 176 (Join-Path $blockOutput 'creative_fe_front.png') $false
Export-PixelTexture 'codex-clipboard-f20c637d-61cc-4068-90a5-20471f71c28f.png' 27 27 128 (Join-Path $blockOutput 'dna_analyzer_front.png') $false
Write-Host 'Imported user-provided textures as 16x16 PNG assets.'
