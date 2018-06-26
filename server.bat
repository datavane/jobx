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
@REM ---------------------------------------------------------------------------
@REM Set JAVA_HOME or JRE_HOME if not already set, ensure any provided settings
@REM are valid and consistent with the selected start-up options and set up the
@REM endorsed directory.
@REM ---------------------------------------------------------------------------
@REM Make sure prerequisite environment variables are set

@REM In debug mode we need a real JDK (JAVA_HOME)

echo\
echo                                       _______
echo     /\   _________       ______  _____   /  /
echo    (())  ______  / ________   /   ___  \/  /
echo     \/   ___ _  / _  __ \_   __ \  ___    /
echo          / /_/ /  / /_/ /   /_/ /  __   . \
echo          \____/   \____/ /_.___/  __   / \_\__
echo                                 _____ /
echo\

if ""%1"" == ""debug"" goto needJavaHome
@REM Otherwise either JRE or JDK are fine
if not "%JRE_HOME%" == "" goto gotJreHome
if not "%JAVA_HOME%" == "" goto gotJavaHome
echo Neither the JAVA_HOME nor the JRE_HOME environment variable is defined
echo At least one of these environment variable is needed to run this program
goto exit

:needJavaHome
@REM Check if we have a usable JDK
if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
if not exist "%JAVA_HOME%\bin\javaw.exe" goto noJavaHome
if not exist "%JAVA_HOME%\bin\jdb.exe" goto noJavaHome
if not exist "%JAVA_HOME%\bin\javac.exe" goto noJavaHome
set "JRE_HOME=%JAVA_HOME%"
goto okJava

:noJavaHome
echo The JAVA_HOME environment variable is not defined correctly.
echo It is needed to run this program in debug mode.
echo NB: JAVA_HOME should point to a JDK not a JRE.
goto exit

:gotJavaHome
@REM No JRE given, use JAVA_HOME as JRE_HOME
set "JRE_HOME=%JAVA_HOME%"

:gotJreHome
@REM Check if we have a usable JRE
if not exist "%JRE_HOME%\bin\java.exe" goto noJreHome
if not exist "%JRE_HOME%\bin\javaw.exe" goto noJreHome
goto okJava

:noJreHome
@REM Needed at least a JRE
echo The JRE_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto exit

:okJava
@REM Don't override the endorsed dir if the user has set it previously
if not "%JAVA_ENDORSED_DIRS%" == "" goto gotEndorseddir
@REM Java 9 no longer supports the java.endorsed.dirs
@REM system property. Only try to use it if
@REM JOBX_HOME/endorsed exists.
if not exist "%JOBX_HOME%\endorsed" goto gotEndorseddir
set "JAVA_ENDORSED_DIRS=%JOBX_HOME%\endorsed"
:gotEndorseddir

@REM Don't override _RUNJAVA if the user has set it previously
if not "%_RUNJAVA%" == "" goto gotRunJava
@REM Set standard command for invoking Java.
@REM Also note the quoting as JRE_HOME may contain spaces.
set _RUNJAVA="%JRE_HOME%\bin\java.exe"
:gotRunJava

@REM Don't override _RUNJDB if the user has set it previously
@REM Also note the quoting as JAVA_HOME may contain spaces.
if not "%_RUNJDB%" == "" goto gotRunJdb
set _RUNJDB="%JAVA_HOME%\bin\jdb.exe"
:gotRunJdb

@REM Don't override _RUNJAR if the user has set it previously
if not "%_RUNJAR%" == "" goto gotRunJar
@REM Set standard command for invoking Java.
@REM Also note the quoting as JRE_HOME may contain spaces.
set _RUNJAR="%JRE_HOME%\bin\jar.exe"
:gotRunJar

setlocal

@REM Guess JOBX_HOME if not defined

set "WORK_DIR=%~dp0"
set "JOBX_HOME=%WORK_DIR%"
set "JOBX_BASE=%JOBX_HOME%"

@REM #################################################################################################
set APP_ARTIFACT=jobx-server
set APP_VERSION="1.2.0-RELEASE";
set APP_WAR_NAME=%APP_ARTIFACT%-%APP_VERSION%.war
set MAVEN_TARGET_WAR="%WORK_DIR%"/%APP_ARTIFACT%/target/%APP_WAR_NAME%
set DIST_PATH=%WORK_DIR%/dist/
set DEPLOY_PATH=${WORK_DIR}/dist/jobx-server

@REM #################################################################################################
@REM 先检查dist下是否有war包
if exist %DIST_PATH%/%APP_WAR_NAME% (
    goto initEvn
) else (
    @REM dist下没有war包则检查server的target下是否有war包.
   if exist %MAVEN_TARGET_WAR% (
        cp %MAVEN_TARGET_WAR% %DIST_PATH%
        goto initEvn
   ) else (
        echo "[JobX] please build project first!"
        goto exit
   )
)

if exist %DEPLOY_PATH% (
    rd %DEPLOY_PATH%
)else (
    md %DEPLOY_PATH%
)

:initEvn
cp %DIST_PATH%\%APP_WAR_NAME% %DEPLOY_PATH%
cd %DEPLOY_PATH%
%_RUNJAR% xvf %APP_WAR_NAME%
del %DEPLOY_PATH%\%APP_WAR_NAME%
cp -r %WORK_DIR%\%APP_ARTIFACT%\container %DEPLOY_PATH%
set LOG_PATH=%WORK_DIR%\container\logs
if [ ! -d "%LOG_PATH%" ] ; then
  md -p %LOG_PATH%
fi
set LOG_PATH=%LOG_PATH%\jobx.out
goto doStart

:doStart
if "%TITLE%" == "" set TITLE=JobX-Server
set EXECJAVA=start "%TITLE%" %_RUNJAVA%
set MAIN=com.jobxhub.server.bootstrap.Startup
set JOBX_LAUNCHER="tomcat";
set JOBX_PORT=8090
%EXECJAVA% ^
    -classpath "%CLASSPATH%" ^
    -Dserver.launcher=%JOBX_LAUNCHER% ^
    -Dserver.port=%JOBX_PORT% ^
    %MAIN% start >> %LOG_PATH%
goto end

:exit
exit /b 1

:end
exit /b 0

