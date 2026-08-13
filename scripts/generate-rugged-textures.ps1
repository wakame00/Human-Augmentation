Add-Type -AssemblyName System.Drawing
$out = Join-Path $PSScriptRoot '..\src\main\resources\assets\humanaugmentation\textures\block'
New-Item -ItemType Directory -Force -Path $out | Out-Null
function C([string]$h) { [System.Drawing.ColorTranslator]::FromHtml($h) }
function Fill($b,$x1,$y1,$x2,$y2,[string]$c) {
    for($y=$y1;$y -le $y2;$y++){for($x=$x1;$x -le $x2;$x++){$b.SetPixel($x,$y,(C $c))}}
}
function Base($b,[string]$c='#151B24') {
    Fill $b 0 0 15 15 '#080B10'; Fill $b 1 1 14 14 $c; Fill $b 2 2 13 13 '#27313A'; Fill $b 3 3 12 12 $c
    foreach($p in @(@(2,2),@(13,2),@(2,13),@(13,13))){$b.SetPixel($p[0],$p[1],(C '#8B9296'))}
}
function Tex([string]$n,[scriptblock]$p){
    $b=[System.Drawing.Bitmap]::new(16,16); & $p $b
    $b.Save((Join-Path $out "$n.png"),[System.Drawing.Imaging.ImageFormat]::Png);$b.Dispose()
}
Tex 'rugged_machine_side' {param($b);Base $b;Fill $b 5 4 10 11 '#090D12';for($y=5;$y-le10;$y+=2){Fill $b 6 $y 9 $y '#46515A'};Fill $b 3 6 3 10 '#9A542D';$b.SetPixel(12,11,(C '#00D6FF'))}
Tex 'dna_analyzer_front' {param($b);Base $b;Fill $b 3 3 11 12 '#06151B';Fill $b 4 4 10 11 '#08798D';for($y=4;$y-le11;$y++){$x=if(($y%4)-lt2){6}else{8};$b.SetPixel($x,$y,(C '#B347FF'));$b.SetPixel(14-$x,$y,(C '#8131B8'))};Fill $b 12 5 13 5 '#00D6FF';Fill $b 12 8 13 8 '#00FF66';$b.SetPixel(13,11,(C '#FF3344'))}
Tex 'dna_analyzer_top' {param($b);Base $b;Fill $b 4 4 11 11 '#07141B';Fill $b 5 5 10 10 '#0C5E70';Fill $b 6 6 9 9 '#00A9C7';Fill $b 7 7 8 8 '#B347FF'}
Tex 'creative_fe_front' {param($b);Base $b '#191326';Fill $b 4 3 11 12 '#090D12';Fill $b 5 4 10 11 '#142F3A';Fill $b 6 5 9 10 '#0089A5';Fill $b 7 6 8 9 '#B9FAFF';Fill $b 2 5 3 10 '#5A2B79';Fill $b 12 5 13 10 '#5A2B79'}
Tex 'creative_fe_top' {param($b);Base $b '#191326';Fill $b 5 5 10 10 '#24113A';Fill $b 6 6 9 9 '#007F99';Fill $b 7 7 8 8 '#B9FAFF'}
Tex 'combustion_front' {param($b);Base $b '#1D1A18';Fill $b 3 4 12 12 '#080808';Fill $b 4 5 11 11 '#21100A';for($x=5;$x-le10;$x+=2){Fill $b $x 6 $x 10 '#C04C19'};Fill $b 3 2 12 2 '#734024';Fill $b 2 3 2 12 '#9A542D';Fill $b 13 5 13 10 '#00FF66'}
Tex 'combustion_top' {param($b);Base $b '#1D1A18';Fill $b 4 4 11 11 '#090909';for($y=5;$y-le10;$y+=2){Fill $b 5 $y 10 $y '#5A3B27'}}
Tex 'medical_frame' {param($b);Fill $b 0 0 15 15 '#080B10';Fill $b 1 1 14 3 '#29333C';Fill $b 1 12 14 14 '#202830';Fill $b 1 1 3 14 '#29333C';Fill $b 12 1 14 14 '#202830';Fill $b 4 4 11 11 '#10161D';foreach($p in @(@(2,2),@(13,2),@(2,13),@(13,13))){$b.SetPixel($p[0],$p[1],(C '#8B9296'))}}
Tex 'medical_frame_t2' {param($b);Fill $b 0 0 15 15 '#080B10';Fill $b 1 1 14 3 '#263A43';Fill $b 1 12 14 14 '#1D3038';Fill $b 1 1 3 14 '#263A43';Fill $b 12 1 14 14 '#1D3038';Fill $b 4 4 11 11 '#0B1820';foreach($p in @(@(2,2),@(13,2),@(2,13),@(13,13))){$b.SetPixel($p[0],$p[1],(C '#00D6FF'))}}
Tex 'surgical_arm' {param($b);Base $b;Fill $b 3 10 12 12 '#754124';Fill $b 6 5 9 10 '#9A542D';Fill $b 8 3 12 5 '#4A535A';Fill $b 11 5 13 8 '#343B42';$b.SetPixel(12,9,(C '#00FF66'))}
Tex 'surgery_table' {param($b);Fill $b 0 0 15 15 '#080B10';Fill $b 1 1 14 14 '#303941';Fill $b 2 2 13 13 '#111820';Fill $b 3 3 12 12 '#202B34';Fill $b 4 3 4 12 '#754124';Fill $b 11 3 11 12 '#754124';foreach($p in @(@(2,2),@(13,2),@(2,13),@(13,13))){$b.SetPixel($p[0],$p[1],(C '#8B9296'))}}
