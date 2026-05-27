param(
    [int]$Port = 18080
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$logsDir = Join-Path $root "logs"
$stdoutLog = Join-Path $logsDir "app-stdout.log"
$stderrLog = Join-Path $logsDir "app-stderr.log"
$jar = Join-Path $root "target\springboot-jenkins-demo-1.0.0.jar"
$healthUrl = "http://127.0.0.1:$Port/api/health"

New-Item -ItemType Directory -Force -Path $logsDir | Out-Null

if (-not (Test-Path $jar)) {
    throw "Jar package not found, please run mvn clean package -DskipTests first."
}

$javaCommand = (Get-Command java).Source
$processPath = [Environment]::GetEnvironmentVariable("Path", "Process")
if ($processPath) {
    [Environment]::SetEnvironmentVariable("PATH", $null, "Process")
    [Environment]::SetEnvironmentVariable("Path", $processPath, "Process")
}

$process = Start-Process -FilePath $javaCommand `
    -ArgumentList "-jar `"$jar`" --server.port=$Port" `
    -RedirectStandardOutput $stdoutLog `
    -RedirectStandardError $stderrLog `
    -PassThru

try {
    $deadline = (Get-Date).AddSeconds(60)
    $ready = $false
    while ((Get-Date) -lt $deadline) {
        try {
            $response = Invoke-WebRequest -Uri $healthUrl -UseBasicParsing -TimeoutSec 5
            if ($response.StatusCode -eq 200) {
                $ready = $true
                break
            }
        } catch {
            Start-Sleep -Seconds 2
        }
    }

    if (-not $ready) {
        throw "Application did not become ready within 60 seconds."
    }

    & python -m pytest tests --alluredir=allure-results
    if ($LASTEXITCODE -ne 0) {
        exit $LASTEXITCODE
    }
} finally {
    if ($process -and (Get-Process -Id $process.Id -ErrorAction SilentlyContinue)) {
        Stop-Process -Id $process.Id -Force
    }
}
