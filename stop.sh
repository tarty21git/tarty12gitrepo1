#!/bin/bash
echo "========================================================"
echo "Stopping International Payment Platform (Linux Environment)"
echo "========================================================"

PID=$(lsof -t -i:8080)
if [ -n "$PID" ]; then
    echo "Stopping process $PID running on port 8080..."
    kill -9 $PID
    echo "Server stopped successfully."
else
    echo "No process running on port 8080."
fi
