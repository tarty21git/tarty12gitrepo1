@echo off
echo ========================================================
echo Stopping International Payment Platform (Windows Environment)
echo ========================================================

for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080 ^| findstr LISTENING') do (
    echo Terminating process with PID: %%a
    taskkill /F /PID %%a
)
echo Server stopped.
