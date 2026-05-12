# Set Java 17 in environment
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Verify Java is available
try {
    $null = Invoke-Expression "java -version 2>&1"
} catch {
    Write-Error "Java 17 not found. Please install from https://www.oracle.com/java/technologies/downloads/#java17"
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "Starting Plate IQ..." -ForegroundColor Green
Set-Location (Split-Path -Parent $MyInvocation.MyCommand.Path)
mvn clean compile exec:java@run-app

Write-Host "Application closed. Press Enter to exit..." -ForegroundColor Yellow
Read-Host
