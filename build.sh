#!/bin/bash

echo -ne "\033[0;32m"
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
echo -ne "\033[m";

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

WORKDIR="`readlink -f ${PRGDIR}`"

MAVEN_URL="http://mirror.bit.edu.cn/apache/maven/maven-3/3.5.0/binaries/apache-maven-3.5.0-bin.tar.gz";

MAVEN_NAME="apache-maven-3.5.0-bin"

UNPKG_MAVEN_NAME="apache-maven-3.5.0";

OPENCRON_VERSION="1.1.0-RELEASE";

BUILD_HOME=${WORKDIR}/build

[ ! -d ${BUILD_HOME} ] && mkdir ${BUILD_HOME}

build_log=${BUILD_HOME}/build.$(date +%Y%m%d).log

[ ! -d ${BUILD_HOME}/dist ] && mkdir ${BUILD_HOME}/dist/
rm -rf ${BUILD_HOME}/dist/*

function echo_r () {
    # Color red: Error, Failed
    [ $# -ne 1 ] && return 1
    echo -e "\033[31m$1\033[0m"
}

function echo_g () {
    # Color green: Success
    [ $# -ne 1 ] && return 1
    echo -e "\033[32m$1\033[0m"
}
function echo_y () {
    # Color yellow: Warning
    [ $# -ne 1 ] && return 1
    echo -e "\033[33m$1\033[0m"
}

function echo_p () {
    # Color purple,magenta: Debug Level 2
    [ $# -ne 1 ] && return 1
    echo -e "\033[35m$1\033[0m"
}

function echo_c () {
    # Color cyan: friendly prompt, Level 1
    [ $# -ne 1 ] && return 1
    echo -e "\033[36m$1\033[0m"
}

USER="`id -un`"
LOGNAME="$USER"
if [ $UID -ne 0 ]; then
    echo "WARNING: Running as a non-root user, \"$LOGNAME\". Functionality may be unavailable. Only root can use some commands or options"
fi

#check maven exists
if [ `mvn -h 2>&1|grep 'command not found'|wc -l` -ne 0 ]; then
    echo_r "maven is not exists."
    echo_g "install maven Starting..."
    echo_g "checking network connectivity ... "
    net_check_ip=114.114.114.114
    ping_count=2
    ping -c ${ping_count} ${net_check_ip} >/dev/null
    retval=$?
    if [ ${retval} -ne 0 ] ; then
        echo_r "Network is blocked! please check your network!"
        echo_r "Build error! bye!"
        exit 1
    elif [ ${retval} -eq 0 ]; then
        echo_g "Check network connectivity passed! "
        if [ ! -x "${BUILD_HOME}/${UNPKG_MAVEN_NAME}" ] ; then
             rm -rf ${BUILD_HOME} && mkdir ${BUILD_HOME}/maven;
             echo_y "download maven Starting..."
             wget -P ${BUILD_HOME} $MAVEN_URL && {
                echo_g "download maven successful!";
                tar -xzvf ${BUILD_HOME}/${MAVEN_NAME}.tar.gz -C ${BUILD_HOME}
                OPENCRON_MAVEN=${BUILD_HOME}/${UNPKG_MAVEN_NAME}/bin/mvn
             }
        else
             OPENCRON_MAVEN=${BUILD_HOME}/${UNPKG_MAVEN_NAME}/bin/mvn
        fi
    fi
fi

if [ "$OPENCRON_MAVEN"x = ""x ]; then
    OPENCRON_MAVEN="mvn";
fi

echo_g "build opencron Starting...";

$OPENCRON_MAVEN clean package > $build_log 2>&1

retval=$?
if [ ${retval} -ne 0 ] ; then
    echo_r "mvn clean package for opencron failed! More details refer to $build_log"
    exit 1
else
    echo_g "mvn clean package for opencron successfully! "
fi

mvn install >>$build_log 2>&1

retval=$?

if [ ${retval} -ne 0 ] ; then
    echo_r "build opencron failed! More details refer to ${build_log}.log"
    exit 1
else
    echo_g "build opencron successfully! "
    cp ${WORKDIR}/opencron-agent/target/opencron-agent-{OPENCRON_VERSION}.tar.gz ${BUILD_HOME}/dist
    cp ${WORKDIR}/opencron-server/target/opencron-server.war ${BUILD_HOME}/dist
    echo_g "please go ${BUILD_HOME}/dist ";
fi