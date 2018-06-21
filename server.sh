#!/bin/bash
#
# Copyright (c) 2015 The JobX Project
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements. See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership. The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License. You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied. See the License for the
# specific language governing permissions and limitations
# under the License.
#

#echo color
WHITE_COLOR="\E[1;37m";
RED_COLOR="\E[1;31m";
BLUE_COLOR='\E[1;34m';
GREEN_COLOR="\E[1;32m";
YELLOW_COLOR="\E[1;33m";
RES="\E[0m";

echo_r () {
    # Color red: Error, Failed
    [ $# -ne 1 ] && return 1
    printf "[${BLUE_COLOR}jobx${RES}] ${RED_COLOR}$1${RES}\n"
}

echo_g () {
    # Color green: Success
    [ $# -ne 1 ] && return 1
    printf "[${BLUE_COLOR}jobx${RES}] ${GREEN_COLOR}$1${RES}\n"
}

echo_y () {
    # Color yellow: Warning
    [ $# -ne 1 ] && return 1
    printf "[${BLUE_COLOR}jobx${RES}] ${YELLOW_COLOR}$1${RES}\n"
}

echo_w () {
    # Color yellow: White
    [ $# -ne 1 ] && return 1
    printf "[${BLUE_COLOR}jobx${RES}] ${WHITE_COLOR}$1${RES}\n"
}

if [ -z "$JAVA_HOME" -a -z "$JRE_HOME" ]; then
    echo_r "Neither the JAVA_HOME nor the JRE_HOME environment variable is defined"
    echo_r "At least one of these environment variable is needed to run this program"
    exit 1
fi

# Set standard commands for invoking Java, if not already set.
if [ -z "$RUNJAVA" ]; then
  RUNJAVA="$JAVA_HOME"/bin/java
fi

#check java exists.
$RUNJAVA >/dev/null 2>&1

if [ $? -ne 1 ];then
  echo_r "ERROR: java is not install,please install java first!"
  exit 1;
fi

#check openjdk
if [ "`${RUNJAVA} -version 2>&1 | head -1|grep "openjdk"|wc -l`"x == "1"x ]; then
  echo_r "ERROR: please uninstall OpenJDK and install jdk first"
  exit 1;
fi

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false
darwin=false
os400=false
case "`uname`" in
CYGWIN*) cygwin=true;;
Darwin*) darwin=true;;
OS400*) os400=true;;
esac

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

WORKDIR=`cd "$PRGDIR" >/dev/null; pwd`;

# Get standard environment variables
###############################################################################################
APP_ARTIFACT=jobx-server
APP_VERSION="1.2.0-RELEASE";
APP_WAR_NAME=${APP_ARTIFACT}-${APP_VERSION}.war
MAVEN_TARGET_WAR="${WORKDIR}"/${APP_ARTIFACT}/target/${APP_WAR_NAME}
DIST_PATH=${WORKDIR}/dist/
###############################################################################################

[ ! -d "${DIST_PATH}" ] && mkdir -p "${DIST_PATH}"

DEPLOY_PATH=${WORKDIR}/dist/jobx-server

STARTUP_SHELL=${WORKDIR}/${APP_ARTIFACT}/startup.sh

#先检查dist下是否有war包
if [ ! -f "${DIST_PATH}/${APP_WAR_NAME}" ] ; then
    #dist下没有war包则检查server的target下是否有war包.
   if [ ! -f "${MAVEN_TARGET_WAR}" ] ; then
      echo_w "[JobX] please build project first!"
      exit 0;
   else
      cp ${MAVEN_TARGET_WAR} ${DIST_PATH};
   fi
fi

[ -d "${DEPLOY_PATH}" ] && rm -rf ${DEPLOY_PATH}/* || mkdir -p ${DEPLOY_PATH}

# unpackage war to dist
cp ${DIST_PATH}/${APP_WAR_NAME} ${DEPLOY_PATH} && cd ${DEPLOY_PATH} && jar xvf ${APP_WAR_NAME} >/dev/null 2>&1 && rm -rf ${DEPLOY_PATH}/${APP_WAR_NAME}

#copy jars...
cp -r ${WORKDIR}/${APP_ARTIFACT}/container ${DEPLOY_PATH}

#copy startup.sh
cp  ${STARTUP_SHELL} ${DEPLOY_PATH} && chmod +x ${DEPLOY_PATH}/startup.sh >/dev/null 2>&1

#startup
EXECUTABLE=${DEPLOY_PATH}/startup.sh

# Check that target executable exists
if $os400; then
  # -x will Only work on the os400 if the files are:
  # 1. owned by the user
  # 2. owned by the PRIMARY group of the user
  # this will not work if the user belongs in secondary groups
  eval
else
  if [ ! -x "$EXECUTABLE" ]; then
    echo "Cannot find $EXECUTABLE"
    echo "The file is absent or does not have execute permission"
    echo "This file is needed to run this program"
    exit 1
  fi
fi

exec "$EXECUTABLE" "$@"
