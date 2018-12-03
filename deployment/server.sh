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

if [ -z "$RUNJAR" ]; then
  RUNJAR="$JAVA_HOME"/bin/jar
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
WORKBASE=`cd "$PRGDIR"/../ >/dev/null; pwd`;

# Get standard environment variables
###############################################################################################
APP_ARTIFACT=jobx-server
APP_VERSION="1.2.0-RELEASE";
APP_WAR_NAME=${APP_ARTIFACT}-${APP_VERSION}.war
MAVEN_TARGET_WAR=${WORKBASE}/${APP_ARTIFACT}/target/${APP_WAR_NAME}
DEPLOY_PATH=${WORKDIR}/${APP_ARTIFACT}
LIB_PATH=${DEPLOY_PATH}/WEB-INF/lib
CONFIG_TEMPLATE=${WORKDIR}/conf.properties
CONFIG_PATH=${DEPLOY_PATH}/WEB-INF/classes/config.properties
###############################################################################################

#先检查dist下是否有war包
if [ ! -f "${WORKDIR}/${APP_WAR_NAME}" ] ; then
    #dist下没有war包则检查server的target下是否有war包.
   if [ ! -f "${MAVEN_TARGET_WAR}" ] ; then
      echo_w "[JobX] please build project first!"
      exit 0;
   else
      cp ${MAVEN_TARGET_WAR} ${WORKDIR};
   fi
fi
if [ ! -f "${DEPLOY_PATH}" ] ; then
    mkdir -p ${DEPLOY_PATH}
    # unpackage war to dist
    cp ${WORKDIR}/${APP_WAR_NAME} ${DEPLOY_PATH} &&
    cd ${DEPLOY_PATH} &&
    ${RUNJAR} xvf ${APP_WAR_NAME} >/dev/null 2>&1 &&
    rm -rf ${DEPLOY_PATH}/${APP_WAR_NAME}
fi

# Add jars to classpath
if [ ! -z "$CLASSPATH" ] ; then
  CLASSPATH="$CLASSPATH":
fi

for jar in ${LIB_PATH}/*
do
  CLASSPATH="$CLASSPATH":"$jar"
done
CLASSPATH="$CLASSPATH":${DEPLOY_PATH}/WEB-INF/classes
#default launcher
[ -z "${JOBX_LAUNCHER}" ] && JOBX_LAUNCHER="tomcat";
#server'port
if [ $# -gt 0 ] ;then
  JOBX_PORT=$1
  if [ "$JOBX_PORT" -gt 0 ] 2>/dev/null ;then
      if [ $JOBX_PORT -lt 0 ] || [ $JOBX_PORT -gt 65535 ];then
         echo_r "server'port error,muse be between 0 and 65535!"
      fi
  else
      echo_r "server'port bust be number."
      exit 1;
  fi
fi
[ -z "${JOBX_PORT}" ] && JOBX_PORT="20501";
#start server....
printf "[${BLUE_COLOR}jobx${RES}] ${WHITE_COLOR} server Starting @ [${GREEN_COLOR}${JOBX_PORT}${RES}].... ${RES}\n"
MAIN="com.jobxhub.server.JobXServer"
cd ${DEPLOY_PATH}
eval "$RUNJAVA" \
        -classpath "$CLASSPATH" \
        -Dserver.launcher=${JOBX_LAUNCHER} \
        -Dserver.port=${JOBX_PORT} \
        ${MAIN} $1 >> /dev/null 2>&1 &
printf "[${BLUE_COLOR}jobx${RES}] ${WHITE_COLOR} please see log for more detail:${RES}${GREEN_COLOR} ${DEPLOY_PATH}/jobx.out ${RES}\n"
exit $?
