# Farmer Platform One-Click Startup Script

Write-Host "--- Starting All Services ---" -ForegroundColor Cyan

# 1. Backend: Spring Boot (8080)
Write-Host "[1/3] Launching Backend API (Port: 8080)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd backend/farmer_platform; ./gradlew.bat bootRun"

# 2. AI Service: DeepSeek Agent (3000)
Write-Host "[2/3] Launching AI Agent Service (Port: 3000)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd ai-agent; npm start"

# 3. Frontend: Vue.js (5173)
Write-Host "[3/3] Launching Frontend UI (Port: 5173)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "npm run dev"

Write-Host ""
Write-Host "SUCCESS: All processes have been spawned in separate windows." -ForegroundColor Green
Write-Host "------------------------------------------------------------"
Write-Host "Backend  : http://localhost:8080"
Write-Host "AI Agent : http://localhost:3000"
Write-Host "Frontend : http://localhost:5173" -ForegroundColor Cyan
Write-Host "------------------------------------------------------------"
