$ErrorActionPreference='Stop'
$projectRoot=Split-Path $PSScriptRoot -Parent
$demoBase=Join-Path $projectRoot '.local\tomcat'
$processId=[int](Get-Content -LiteralPath (Join-Path $demoBase 'process.id'))
$processInfo=Get-CimInstance Win32_Process -Filter "ProcessId=$processId"
if ($processInfo -and $processInfo.Name -eq 'java.exe' -and $processInfo.CommandLine.Contains($demoBase) -and $processInfo.CommandLine.Contains('org.apache.catalina.startup.Bootstrap')) {
    Stop-Process -Id $processId
    Write-Output "Đã dừng Tomcat demo PID $processId"
} else { throw 'Không khớp process Tomcat riêng của dự án; không dừng process khác.' }
