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
@REM Stop script for the JOBX agent
@REM ---------------------------------------------------------------------------

setlocal

@REM Guess JOBX_HOME if not defined
set WORK_DIR=%~dp0
cd "%WORK_DIR%.."
set JOBX_HOME=%cd%
set EXECUTABLE=%JOBX_HOME%\bin\jobx.bat

if exist "%EXECUTABLE%" goto okExec
echo Cannot find "%EXECUTABLE%"
echo This file is needed to run this program
goto exit

:okExec
call "%EXECUTABLE%" stop
goto end

:exit
exit /b 1

:end
exit /b 0
