#!/bin/bash

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
    echo -e "[${GREEN_COLOR}opencron${RES}] ${RED_COLOR}$1${RES}"
}

echo_g () {
    # Color green: Success
    [ $# -ne 1 ] && return 1
    echo -e "[${GREEN_COLOR}opencron${RES}] ${GREEN_COLOR}$1${RES}"
}

echo_y () {
    # Color yellow: Warning
    [ $# -ne 1 ] && return 1
    echo -e "[${GREEN_COLOR}opencron${RES}] ${YELLOW_COLOR}$1${RES}"
}

echo_w () {
    # Color yellow: White
    [ $# -ne 1 ] && return 1
    echo -e "[${GREEN_COLOR}opencron${RES}] ${WHITE_COLOR}$1${RES}"
}

echo -ne "${GREEN_COLOR}"
cat<<EOT

      --------------------------------------------
    /                                              \\
   /   ___  _ __   ___ _ __   ___ _ __ ___  _ __    \\
  /   / _ \| '_ \ / _ \ '_ \ / __| '__/ _ \| '_ \\    \\
 /   | (_) | |_) |  __/ | | | (__| | | (_) | | | |    \\
 \\    \___/| .__/ \___|_| |_|\___|_|  \___/|_| |_|    /
  \\        |_|                                       /
   \\                                                /
    \\       --opencron,Let's crontab easy!         /
      --------------------------------------------

EOT
echo -ne "${RES}";

# Make sure prerequisite environment variables are set
if [ -z "$JAVA_HOME" -a -z "$JRE_HOME" ]; then
  if $darwin; then
    # Bugzilla 54390
    if [ -x '/usr/libexec/java_home' ] ; then
      export JAVA_HOME=`/usr/libexec/java_home`
    # Bugzilla 37284 (reviewed).
    elif [ -d "/System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Home" ]; then
      export JAVA_HOME="/System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Home"
    fi
  else
    JAVA_PATH=`which java 2>/dev/null`
    if [ "x$JAVA_PATH" != "x" ]; then
      JAVA_PATH=`dirname $JAVA_PATH 2>/dev/null`
      JRE_HOME=`dirname $JAVA_PATH 2>/dev/null`
    fi
    if [ "x$JRE_HOME" = "x" ]; then
      # XXX: Should we try other locations?
      if [ -x /usr/bin/java ]; then
        JRE_HOME=/usr
      fi
    fi
  fi
  if [ -z "$JAVA_HOME" -a -z "$JRE_HOME" ]; then
    echo "Neither the JAVA_HOME nor the JRE_HOME environment variable is defined"
    echo "At least one of these environment variable is needed to run this program"
    exit 1
  fi
fi
if [ -z "$JAVA_HOME" -a "$1" = "debug" ]; then
  echo "JAVA_HOME should point to a JDK in order to run in debug mode."
  exit 1
fi
if [ -z "$JRE_HOME" ]; then
  JRE_HOME="$JAVA_HOME"
fi

# If we're running under jdb, we need a full jdk.
if [ "$1" = "debug" ] ; then
  if [ "$os400" = "true" ]; then
    if [ ! -x "$JAVA_HOME"/bin/java -o ! -x "$JAVA_HOME"/bin/javac ]; then
      echo "The JAVA_HOME environment variable is not defined correctly"
      echo "This environment variable is needed to run this program"
      echo "NB: JAVA_HOME should point to a JDK not a JRE"
      exit 1
    fi
  else
    if [ ! -x "$JAVA_HOME"/bin/java -o ! -x "$JAVA_HOME"/bin/jdb -o ! -x "$JAVA_HOME"/bin/javac ]; then
      echo "The JAVA_HOME environment variable is not defined correctly"
      echo "This environment variable is needed to run this program"
      echo "NB: JAVA_HOME should point to a JDK not a JRE"
      exit 1
    fi
  fi
fi

# Don't override the endorsed dir if the user has set it previously
if [ -z "$JAVA_ENDORSED_DIRS" ]; then
  # Set the default -Djava.endorsed.dirs argument
  JAVA_ENDORSED_DIRS="$OPENCRON_HOME"/endorsed
fi

# Set standard commands for invoking Java, if not already set.
if [ -z "$RUNJAVA" ]; then
  RUNJAVA="$JRE_HOME"/bin/java
fi
if [ "$os400" != "true" ]; then
  if [ -z "$_RUNJDB" ]; then
    _RUNJDB="$JAVA_HOME"/bin/jdb
  fi
fi

#check java exists.
$RUNJAVA >/dev/null 2>&1

if [ $? -ne 1 ];then
  echo_r "ERROR: java is not install,please install java first!"
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

APP_ARTIFACT=opencron-server

APP_VERSION="1.1.0-RELEASE";

APP_WAR_NAME=${APP_ARTIFACT}.war

MAVEN_TARGET_WAR="${WORKDIR}"/${APP_ARTIFACT}/target/${APP_WAR_NAME}

DIST_PATH=${WORKDIR}/dist/

[ ! -d "${DIST_PATH}" ] && mkdir -p "${DIST_PATH}"

DEPLOY_PATH=${WORKDIR}/dist/opencron-server

STARTUP_SHELL=${WORKDIR}/${APP_ARTIFACT}/startup.sh

#先检查dist下是否有war包
if [ ! -f "${DIST_PATH}/${APP_WAR_NAME}" ] ; then
    #dist下没有war包则检查server的target下是否有war包.
   if [ ! -f "${MAVEN_TARGET_WAR}" ] ; then
      echo_w "[opencron] please build project first!"
      exit 0;
   else
      cp ${MAVEN_TARGET_WAR} ${DIST_PATH};
   fi
fi

[ -d "${DEPLOY_PATH}" ] && rm -rf ${DEPLOY_PATH}/* || mkdir -p ${DEPLOY_PATH}

#将target下的war包解到dist下
cp ${DIST_PATH}/${APP_WAR_NAME} ${DEPLOY_PATH} && cd ${DEPLOY_PATH} && jar xvf ${APP_WAR_NAME} >/dev/null 2>&1 && rm -rf ${DEPLOY_PATH}/${APP_WAR_NAME}

#copy jettyJar
mkdir ${DEPLOY_PATH}/jetty && cp ${WORKDIR}/${APP_ARTIFACT}/jetty/*.jar ${DEPLOY_PATH}/jetty

#copy startup.sh
cp  ${STARTUP_SHELL} ${DEPLOY_PATH}

#startup
/bin/bash +x "${DEPLOY_PATH}/startup.sh" "$@"

