param(
    [string]$JarPattern = "target\springboot-jenkins-demo-*.jar",
    [int]$Port = 18080
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$runDir = Join-Path $root "run"
$logsDir = Join-Path $root "logs"
$pidFile = Join-Path $runDir "app.pid"
$stdoutLog = Join-Path $logsDir "app-stdout.log"
$stderrLog = Join-Path $logsDir "app-stderr.log"

New-Item -ItemType Directory -Force -Path $runDir | Out-Null
New-Item -ItemType Directory -Force -Path $logsDir | Out-Null

if (Test-Path $pidFile) {
    $oldPid = Get-Content $pidFile | Select-Object -First 1
    if ($oldPid) {
        $existingProcess = Get-Process -Id $oldPid -ErrorAction SilentlyContinue
        if ($existingProcess) {
            Stop-Process -Id $oldPid -Force
            Start-Sleep -Seconds 2
        }
    }
    Remove-Item -LiteralPath $pidFile -Force
}

$jar = Get-ChildItem -Path (Join-Path $root $JarPattern) | Sort-Object LastWriteTime -Descending | Select-Object -First 1
if (-not $jar) {
    throw "Jar package not found, please run mvn clean package first."
}

$javaCommand = (Get-Command java).Source
$processPath = [Environment]::GetEnvironmentVariable("Path", "Process")
if ($processPath) {
    [Environment]::SetEnvironmentVariable("PATH", $null, "Process")
    [Environment]::SetEnvironmentVariable("Path", $processPath, "Process")
}

$process = Start-Process -FilePath $javaCommand `
    -ArgumentList "-jar `"$($jar.FullName)`" --server.port=$Port" `
    -RedirectStandardOutput $stdoutLog `
    -RedirectStandardError $stderrLog `
    -PassThru `
    -WindowStyle Hidden

Set-Content -Path $pidFile -Value $process.Id
Write-Host "Application started. PID=$($process.Id)"
