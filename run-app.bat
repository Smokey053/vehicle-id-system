@echo off
REM Set Java 17 in PATH
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%

REM Verify Java is available
java -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java 17 not found. Please install Java 17 from https://www.oracle.com/java/technologies/downloads/#java17
    pause
    exit /b 1
)

REM Run the application
echo Starting Plate IQ...
mvn clean compile exec:java@run-app

pause
