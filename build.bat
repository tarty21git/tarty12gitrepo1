@echo off
echo ========================================================
echo Building International Payment Platform
echo ========================================================

call mvn -f payment-platform/pom.xml clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Maven build failed!
    exit /b %ERRORLEVEL%
)

echo [SUCCESS] Package build completed successfully.
