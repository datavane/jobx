@echo off
@REM
@REM Copyright (c) 2015 The JobX Project
@REM
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements. See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership. The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License. You may obtain a copy of the License at
@REM
@REM http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied. See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM
@REM -----------------------------------------------------------------------------
@REM Control Script for the JOBX Server
@REM
@REM Environment Variable Prerequisites
@REM
@REM   JOBX_HOME   May point at your jobx "build" directory.
@REM
@REM   JOBX_BASE   (Optional) Base directory for resolving dynamic portions
@REM                   of a jobx installation.  If not present, resolves to
@REM                   the same directory that JOBX_HOME points to.
@REM
@REM   JOBX_OUT    (Optional) Full path to a file where stdout and stderr
@REM                   will be redirected.
@REM                   Default is $JOBX_BASE/logs/jobx.out
@REM
@REM   JOBX_TMPDIR (Optional) Directory path location of temporary directory
@REM                   the JVM should use (java.io.tmpdir).  Defaults to
@REM                   $JOBX_BASE/temp.
@REM -----------------------------------------------------------------------------

echo\
echo                                       _______ 
echo     /\   _________       ______  _____   /  / 
echo    (())  ______  / ________   /   ___  \/  /  
echo     \/   ___ _  / _  __ \_   __ \  ___    /      
echo          / /_/ /  / /_/ /   /_/ /  __   . \      
echo          \____/   \____/ /_.___/  __   / \_\__
echo                                 _____ /          
echo\                                              

setlocal

@REM Suppress Terminate batch job on CTRL+C
if not ""%1"" == ""run"" goto mainEntry
if "%TEMP%" == "" goto mainEntry
if exist "%TEMP%\%~nx0.run" goto mainEntry
echo Y>"%TEMP%\%~nx0.run"
if not exist "%TEMP%\%~nx0.run" goto mainEntry
echo Y>"%TEMP%\%~nx0.Y"
call "%~f0" %* <"%TEMP%\%~nx0.Y"
@REM Use provided errorlevel
set RETVAL=%ERRORLEVEL%
del /Q "%TEMP%\%~nx0.Y" >NUL 2>&1
exit /B %RETVAL%
:mainEntry
del /Q "%TEMP%\%~nx0.run" >NUL 2>&1

@REM -----------------------------------------------------------------------------
@REM Guess JOBX_HOME if not defined
set JOBX_VERSION=1.2.0-RELEASE
set "WORK_DIR=%~dp0"
cd "%WORK_DIR%.."
set JOBX_HOME=%cd%
if not "%JOBX_BASE%" == "" goto gotBase
set "JOBX_BASE=%JOBX_HOME%"
set "JOBX_TMPDIR=%JOBX_BASE%\temp"
@REM -----------------------------------------------------------------------------

:gotBase
@REM Ensure that neither JOBX_HOME nor JOBX_BASE contains a semi-colon
@REM as this is used as the separator in the classpath and Java provides no
@REM mechanism for escaping if the same character appears in the path. Check this
@REM by replacing all occurrences of ';' with '' and checking that neither
@REM JOBX_HOME nor JOBX_BASE have changed
if "%JOBX_HOME%" == "%JOBX_HOME:;=%" goto homeNoSemicolon
echo Using JOBX_HOME:   "%JOBX_HOME%"
echo Unable to start as JOBX_HOME contains a semicolon (;) character
goto end

:homeNoSemicolon
if "%JOBX_BASE%" == "%JOBX_BASE:;=%" goto baseNoSemicolon
echo Using JOBX_BASE:   "%JOBX_BASE%"
echo Unable to start as JOBX_BASE contains a semicolon (;) character
goto end

:baseNoSemicolon
@REM Ensure that any user defined CLASSPATH variables are not used on startup,
@REM but allow them to be specified in setenv.bat, in rare case when it is needed.
set CLASSPATH=

@REM Get standard environment variables
if not exist "%JOBX_BASE%\bin\setenv.bat" goto checkSetenvHome
call "%JOBX_BASE%\bin\setenv.bat"
goto setenvDone
:checkSetenvHome
if exist "%JOBX_HOME%\bin\setenv.bat" call "%JOBX_HOME%\bin\setenv.bat"

:setenvDone
@REM Get standard Java environment variables
if exist "%JOBX_HOME%\bin\setclasspath.bat" goto okSetclasspath
echo Cannot find "%JOBX_HOME%\bin\setclasspath.bat"
echo This file is needed to run this program
goto end

:okSetclasspath
call "%JOBX_HOME%\bin\setclasspath.bat" %1
if errorlevel 1 goto end

@REM Add on extra jar file to CLASSPATH
@REM Note that there are no quotes as we do not want to introduce random
@REM quotes into the CLASSPATH

if "%CLASSPATH%" == "" goto emptyClasspath
set "CLASSPATH=%CLASSPATH%;"

:emptyClasspath
set "CLASSPATH=%CLASSPATH%%JOBX_HOME%\lib\jobx-agent-%JOBX_VERSION%.jar"


@REM ----- Execute The Requested Command ---------------------------------------
echo Using JOBX_BASE:   "%JOBX_BASE%"
echo Using JOBX_HOME:   "%JOBX_HOME%"
echo Using JOBX_TMPDIR: "%JOBX_TMPDIR%"

if "%TITLE%" == "" set TITLE=JobX-Agent
set _EXECJAVA=start "%TITLE%" %_RUNJAVA%
set MAINCLASS=com.jobxhub.agent.bootstrap.JobXAgent

set Action=%1
if "%Action%" == "start" goto doAction
if "%Action%" == "stop" goto doAction
if "%Action%" == "version" goto doVersion

echo Usage:  jobx ( commands ... )
echo commands:
echo  start             Start jobx-Agent
echo  stop              Stop jobx-Agent
echo  version           print jobx Version
goto  end

:doAction
%_EXECJAVA% ^
    -classpath "%CLASSPATH%" ^
    -Djobx.home="%JOBX_HOME%" ^
    -Djava.io.tmpdir="%JOBX_TMPDIR%" ^
    %MAINCLASS% %Action%
goto end

:doVersion
echo %JOBX_VERSION%
goto end

:exit
exit /b 1

:end
exit /b 0