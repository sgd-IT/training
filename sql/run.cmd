@echo off
chcp 65001 >nul
setlocal

set "MYSQL_EXE=D:\Tool\mysql-8.0.46-winx64\bin\mysql.exe"
set "MYSQL_USER=root"
set "MYSQL_PWD=123456"
set "DB=training"
set "SQLDIR=%~dp0"

if "%~1"=="" goto usage
if /I "%~1"=="setup"  goto setup
if /I "%~1"=="-setup" goto setup
if /I "%~1"=="-i"     goto interactive
if /I "%~1"=="-shell" goto interactive

set "ARG=%~1"
if exist "%ARG%" goto run
set "ARG=%SQLDIR%%~1"

:run
set "ARG=%ARG:\=/%"
echo.
echo ==^> %ARG%
"%MYSQL_EXE%" -u %MYSQL_USER% --table --default-character-set=utf8mb4 %DB% --execute="source %ARG%"
goto end

:setup
echo.
echo ==^> 00_setup.sql   (drop + create, clean slate: no secondary indexes)
"%MYSQL_EXE%" -u %MYSQL_USER% --table --default-character-set=utf8mb4 --execute="source %SQLDIR:\=/%00_setup.sql"
if errorlevel 1 goto end
echo.
echo ==^> 01_seed.sql    (seeding, ~20s)
"%MYSQL_EXE%" -u %MYSQL_USER% --table --default-character-set=utf8mb4 --execute="source %SQLDIR:\=/%01_seed.sql"
echo.
echo [OK] bench ready. Type DDL here:  run.cmd -i
goto end

:interactive
echo.
echo === training db - interactive. Ctrl+C or "exit" to quit. ===
echo.
"%MYSQL_EXE%" -u %MYSQL_USER% --table --default-character-set=utf8mb4 %DB%
goto end

:usage
echo.
echo   SQL bench  (training db @ 127.0.0.1:3306)
echo   ----------------------------------------------------------
echo     run.cmd -Setup            rebuild + reseed (clean slate)
echo     run.cmd -i                interactive shell (hand-type DDL)
echo     run.cmd s1_bplus_tree.sql run a worksheet file
echo.

:end
endlocal