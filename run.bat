@echo off
setlocal
cd /d "%~dp0"

rem 中文 Windows 的終端機預設是 950 字碼頁，可直接顯示中文；其他語系改用 UTF-8
chcp | find "950" >nul || chcp 65001 >nul

rem 如果 setup.bat 安裝了攜帶版 JDK，就用它
set "JAVAC=javac"
set "JAVA=java"
if exist ".jdk_home" set /p JDKHOME=<".jdk_home"
if defined JDKHOME set "JAVAC=%JDKHOME%\bin\javac.exe"
if defined JDKHOME set "JAVA=%JDKHOME%\bin\java.exe"

rem 用法:  .\run.bat   ^|   .\run.bat check   ^|   .\run.bat setup
set "TARGET=Main"
if /i "%~1"=="check" set "TARGET=Checkpoint"
if /i "%~1"=="setup" set "TARGET=CheckSetup"

if exist out rmdir /s /q out
"%JAVAC%" -encoding UTF-8 -d out src\*.java
if errorlevel 1 (
  echo.
  echo  編譯失敗！請看上面「第一個」錯誤訊息，裡面有檔案名稱和行號。
  exit /b 1
)
"%JAVA%" -Djava.net.useSystemProxies=true -cp out %TARGET%
exit /b %errorlevel%
