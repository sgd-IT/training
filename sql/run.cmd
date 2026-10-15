@echo off
chcp 65001 >nul
setlocal

rem ============================================================
rem  Portable config. Every knob below can be overridden by an
rem  environment variable, so the SAME file works on both
rem  machines (office / home). Nothing is machine-locked except
rem  the default path, and even that falls back to PATH.
rem
rem    MYSQL_EXE    full path to mysql.exe    (default D:\Tool\...)
rem    MYSQL_USER   db user                   (default root)
rem    MYSQL_PWD    db password               (default 123456)
rem    TRAINING_DB  schema name               (default training)
rem
rem  Self-check on a new machine:   run.cmd -env
rem ============================================================

if not defined MYSQL_EXE   set "MYSQL_EXE=D:\Tool\mysql-8.0.46-winx64\bin\mysql.exe"
if not defined MYSQL_USER  set "MYSQL_USER=root"
if not defined MYSQL_PWD   set "MYSQL_PWD=123456"
if not defined TRAINING_DB set "TRAINING_DB=training"

rem fallback: if the default mysql.exe is not there, use PATH
if not exist "%MYSQL_EXE%" set "MYSQL_EXE=mysql"

set "DB=%TRAINING_DB%"
set "SQLDIR=%~dp0"

if "%~1"=="" goto usage
if /I "%~1"=="setup"   goto setup
if /I "%~1"=="-setup"  goto setup
if /I "%~1"=="-i"      goto interactive
if /I "%~1"=="-shell"  goto interactive
if /I "%~1"=="-env"    goto env
if /I "%~1"=="-doctor" goto env

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
echo === training db - interactive. Type "exit" to quit. ===
echo.
"%MYSQL_EXE%" -u %MYSQL_USER% --table --default-character-set=utf8mb4 %DB%
goto end

:env
echo.
echo   effective config   - override with env vars if wrong
echo   ----------------------------------------------------------
echo     MYSQL_EXE    = %MYSQL_EXE%
echo     MYSQL_USER   = %MYSQL_USER%
echo     TRAINING_DB  = %DB%
echo     MYSQL_PWD    = ******
echo   ----------------------------------------------------------
echo.
echo   connection test:
echo.
"%MYSQL_EXE%" -u %MYSQL_USER% --table --default-character-set=utf8mb4 -e "SELECT VERSION() AS mysql_version;"
if errorlevel 1 echo   [FAIL] cannot connect. Set MYSQL_EXE / MYSQL_USER / MYSQL_PWD env vars.
if not errorlevel 1 echo   [OK] connected. If the database is empty, run:  run.cmd -Setup
goto end

:usage
echo.
echo   SQL bench  (training db @ 127.0.0.1:3306)
echo   ----------------------------------------------------------
echo     run.cmd -env              show config + test connection
echo     run.cmd -Setup            rebuild + reseed (clean slate)
echo     run.cmd -i                interactive shell (hand-type DDL)
echo     run.cmd s1.sql            run a worksheet file
echo.

:end
endlocal
