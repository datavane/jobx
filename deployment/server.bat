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
set "WORK_BASE=%WORK_DIR%\..\"

@REM #################################################################################################
set APP_ARTIFACT=jobx-server
set APP_VERSION=1.2.0-RELEASE
set APP_WAR_NAME=%APP_ARTIFACT%-%APP_VERSION%.war
set MAVEN_TARGET_WAR=%WORK_BASE%%APP_ARTIFACT%\target\%APP_WAR_NAME%
set DEPLOY_PATH=%WORK_BASE%\%APP_ARTIFACT%
set CONTAINER_PATH=%DEPLOY_PATH%\container
@REM #################################################################################################

if exist "%WORK_BASE%\%APP_WAR_NAME%" goto initEnv
if not exist %WORK_BASE% mkdir %WORK_BASE%
if exist "%MAVEN_TARGET_WAR%" (
    copy %MAVEN_TARGET_WAR% %WORK_BASE%
    goto initEnv
) else (
    echo [JobX] please build project first!
    goto exit
)

:initEnv
if not exist "%DEPLOY_PATH%" (
    mkdir %DEPLOY_PATH%
    copy %WORK_BASE%\%APP_WAR_NAME% %DEPLOY_PATH%
    cd %DEPLOY_PATH%
    %_RUNJAR% xvf %APP_WAR_NAME% 1>nul
    del %DEPLOY_PATH%\%APP_WAR_NAME%
)

@REM cd to DEPLOY_PATH
cd %DEPLOY_PATH%

@REM copy container to deploy_path
if not exist "%CONTAINER_PATH%" (
    mkdir %CONTAINER_PATH%
    xcopy %WORK_BASE%%APP_ARTIFACT%\container %CONTAINER_PATH% /E 1>nul
)
@REM create log
set LOG_PATH=%CONTAINER_PATH%\logs
if exist %LOG_PATH% (
    set LOG_PATH=%LOG_PATH%\jobx.out
)else (
    md %LOG_PATH%
    set LOG_PATH=%LOG_PATH%\jobx.out
)
@REM set classpath
set jar_dir=%DEPLOY_PATH%\WEB-INF\lib
setLocal EnableDelayedExpansion
set CLASSPATH="%CLASSPATH%;
for /R %jar_dir% %%a in (*.jar) do set CLASSPATH=!CLASSPATH!;%%a
set CLASSPATH=!CLASSPATH!;%DEPLOY_PATH%\WEB-INF\classes"
goto doStart

:doStart
if "%TITLE%" == "" set TITLE=JobX-Server
set EXECJAVA=start "%TITLE%" %_RUNJAVA%
set MAIN="com.jobxhub.server.bootstrap.Startup"
set JOBX_LAUNCHER=tomcat
set JOBX_PORT=8090
%EXECJAVA% ^
    -classpath "%CLASSPATH%" ^
    -Dserver.launcher="%JOBX_LAUNCHER%" ^
    -Dserver.port="%JOBX_PORT%" ^
    %MAIN% start >> %LOG_PATH%
goto end

:exit
exit 1

:end
exit 0
