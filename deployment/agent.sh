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
RED_COLOR="\E[1;31m";
BLUE_COLOR='\E[1;34m';
RES="\E[0m";

echo_r () {
    # Color red: Error, Failed
    [ $# -ne 1 ] && return 1
    printf "[${BLUE_COLOR}jobx${RES}] ${RED_COLOR}$1${RES}\n"
}

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
APP_ARTIFACT=jobx-agent
APP_VERSION="1.2.0-RELEASE";
APP_TAR_NAME=${APP_ARTIFACT}-${APP_VERSION}.tar.gz
MAVEN_TARGET_TAR="${WORKBASE}"/${APP_ARTIFACT}/target/${APP_TAR_NAME}
DEPLOY_PATH=${WORKDIR}/jobx-agent
CONFIG_TEMPLATE=${WORKDIR}/conf.properties
CONFIG_PATH=${DEPLOY_PATH}/conf/conf.properties
###############################################################################################

#先检查dist下是否有war包
if [ ! -f "${WORKDIR}/${APP_TAR_NAME}" ] ; then
    #dist下没有tar包则检查agent的target下是否有tar包.
   if [ ! -f "${MAVEN_TARGET_TAR}" ] ; then
      echo_r "[JobX] please build project first!"
      exit 0;
   else
      cp ${MAVEN_TARGET_TAR} ${WORKDIR};
   fi
fi

[ -d "${DEPLOY_PATH}" ] && rm -rf ${DEPLOY_PATH}/* || mkdir -p ${DEPLOY_PATH}
#untar..
tar -xzvf ${WORKDIR}/${APP_TAR_NAME} && chmod +x ${DEPLOY_PATH}/bin/* >/dev/null 2>&1
EXECUTABLE=${DEPLOY_PATH}/bin/startup.sh
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
