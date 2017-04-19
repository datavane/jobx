#!/bin/bash

# Name: doDeploy.sh
#Execute this shell script to deploy Java projects built by Maven automatically on remote hosts.

# debug option
DEBUG=false
#DEBUG=true

if $DEBUG ; then
    old_PS4=$PS4
#    export PS4='+${BASH_SOURCE}:${LINENO}:${FUNCNAME[0]}: '
    export PS4='+${LINENO}: ${FUNCNAME[0]}: ' # if there is only one bash script, do not display ${BASH_SOURCE}
    _XTRACE_FUNCTIONS=$(set +o | grep xtrace)
    set -o xtrace
fi

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

# echo color function, smarter, learn from lnmp.org lnmp install.sh
function echo_r (){
    # Color red: Error, Failed
    [ $# -ne 1 ] && return 1
    echo -e "\033[31m$1\033[0m"
}
function echo_g (){
    # Color green: Success
    [ $# -ne 1 ] && return 1
    echo -e "\033[32m$1\033[0m"
}
function echo_y (){
    # Color yellow: Warning
    [ $# -ne 1 ] && return 1
    echo -e "\033[33m$1\033[0m"
}
function echo_b (){
    # Color blue: Debug Level 1
    [ $# -ne 1 ] && return 1
    echo -e "\033[34m$1\033[0m"
}

function echo_p (){
    # Color purple,magenta: Debug Level 2
    [ $# -ne 1 ] && return 1
    echo -e "\033[35m$1\033[0m"
}

function echo_c (){
    # Color cyan: friendly prompt, Level 1
    [ $# -ne 1 ] && return 1
    echo -e "\033[36m$1\033[0m"
}
# end echo color function, smarter

#WORKDIR="`realpath ${WORKDIR}`"
WORKDIR="`readlink -f ${PRGDIR}`"

# end public header
# =============================================================================================================================

USER="`id -un`"
LOGNAME="$USER"
if [ $UID -ne 0 ]; then
    echo "WARNING: Running as a non-root user, \"$LOGNAME\". Functionality may be unavailable. Only root can use some commands or options"
fi


function build(){
    echo_b "Do mvn build java project for `echo $1 | awk -F '[/.]+' '{ print $(NF-1)}'`... "

    mvn install >>${WORKDIR}/mvn_build_$(date +%Y%m%d)_$$.log 2>&1
    retval=$?
    if [ ${retval} -ne 0 ] ; then
        echo_r "mvn install failed! More details refer to ${WORKDIR}/mvn_build_$(date +%Y%m%d)_$$.log"
        exit 1
    else
        echo_g "mvn install for ${project_clone_repository_name} successfully! "
    fi

    mvn clean package >>${WORKDIR}/mvn_build_$(date +%Y%m%d)_$$.log 2>&1
    retval=$?
    if [ ${retval} -ne 0 ] ; then
        echo_r "mvn clean package for ${project_clone_repository_name} failed! More details refer to ${WORKDIR}/mvn_build_$(date +%Y%m%d)_$$.log"
        exit 1
    else
        echo_g "mvn clean package for ${project_clone_repository_name} successfully! "
    fi
    cd ${WORKDIR}
    echo_g "Do mvn build java project finished for ${project_clone_repository_name} with exit code 0! "
    echo
}

# debug option
if $DEBUG ; then
    export PS4=$old_PS4
    ${_XTRACE_FUNCTIONS}
fi

