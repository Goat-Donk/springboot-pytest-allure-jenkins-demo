param(
    [string]$HealthUrl = "http://127.0.0.1:18080/api/health",
    [int]$TimeoutSeconds = 60
)

$deadline = (Get-Date).AddSeconds($TimeoutSeconds)

while ((Get-Date) -lt $deadline) {
    try {
        $response = Invoke-WebRequest -Uri $HealthUrl -UseBasicParsing -TimeoutSec 5
        if ($response.StatusCode -eq 200) {
            Write-Host "Application is ready."
            exit 0
        }
    } catch {
        Start-Sleep -Seconds 2
    }
}

throw "Application did not become ready within $TimeoutSeconds seconds."
