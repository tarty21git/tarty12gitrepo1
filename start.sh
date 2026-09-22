#!/bin/bash
echo "========================================================"
echo "Starting International Payment Platform (Linux Environment)"
echo "========================================================"

nohup java -jar payment-platform/payment-api/target/payment-api-1.0.0-SNAPSHOT.jar > app.log 2>&1 &
PID=$!
echo "Application started with PID: $PID"
