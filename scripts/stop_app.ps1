$root = Split-Path -Parent $PSScriptRoot
$pidFile = Join-Path $root "run\app.pid"

if (-not (Test-Path $pidFile)) {
    Write-Host "PID file not found, skip stopping application."
    exit 0
}

$appPid = Get-Content $pidFile | Select-Object -First 1
if ($appPid) {
    $process = Get-Process -Id $appPid -ErrorAction SilentlyContinue
    if ($process) {
        Stop-Process -Id $appPid -Force
        Write-Host "Application stopped. PID=$appPid"
    }
}

Remove-Item -LiteralPath $pidFile -Force -ErrorAction SilentlyContinue
