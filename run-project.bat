@echo off
echo Starting Medication Lot Tracking System...
where mvn >nul 2>nul
if %errorlevel% neq 0 (
  echo Maven was not found in PATH.
  echo Install Maven 3.9+ and configure MAVEN_HOME/PATH, then reopen VS Code.
  pause
  exit /b 1
)
mvn spring-boot:run
pause
