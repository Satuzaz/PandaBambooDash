@echo off
setlocal
cd /d "%~dp0"
javac --release 17 -encoding UTF-8 -d out src\*.java
if errorlevel 1 (
    echo Build failed. Please install JDK 17 or newer and check the errors above.
    pause
    exit /b 1
)
java -cp "%~dp0out" Main
if errorlevel 1 pause
