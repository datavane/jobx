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
# -----------------------------------------------------------------------------
# Control Script for the JOBX Server
#
# Environment Variable Prerequisites
#
#   JOBX_HOME   May point at your jobx "build" directory.
#
#   JOBX_BASE   (Optional) Base directory for resolving dynamic portions
#                   of a jobx installation.  If not present, resolves to
#                   the same directory that JOBX_HOME points to.
#
#   JOBX_OUT    (Optional) Full path to a file where stdout and stderr
#                   will be redirected.
#                   Default is $JOBX_BASE/logs/jobx.out
#
#   JOBX_TMPDIR (Optional) Directory path location of temporary directory
#                   the JVM should use (java.io.tmpdir).  Defaults to
#                   $JOBX_BASE/temp.
#
#   JOBX_PID    (Optional) Path of the file which should contains the pid
#                   of the jobx startup java process, when start (fork) is
#                   used
# -----------------------------------------------------------------------------

#echo color
WHITE_COLOR="\E[1;37m";
RED_COLOR="\E[1;31m";
BLUE_COLOR='\E[1;34m';
GREEN_COLOR="\E[1;32m";
YELLOW_COLOR="\E[1;33m";
RES="\E[0m";


printf "${GREEN_COLOR}                                        _____       ${RES}\n"
printf "${GREEN_COLOR}         _________       ______  ______  /  /       ${RES}\n"
printf "${GREEN_COLOR}         ______  / ________   /   ___  \/  /        ${RES}\n"
printf "${GREEN_COLOR}         ___ _  / _  __ \_   __ \  ___    /         ${RES}\n"
printf "${GREEN_COLOR}         / /_/ /  / /_/ /   /_/ /  __   . \         ${RES}\n"
printf "${GREEN_COLOR}         \____/   \____/ /_.___/  __   / \_\__      ${RES}\n"
printf "${GREEN_COLOR}                                      /             ${RES}\n"
printf "${GREEN_COLOR}        _____ V1.2.0-RELEASE ___.____/              ${RES}\n"
printf "${GREEN_COLOR}                                                    ${RES}\n"
printf "${GREEN_COLOR}                  ----- Let's schedule easy ^_^     ${RES}\n"
printf "${GREEN_COLOR}                                                    ${RES}\n\n"


echo_r () {
    # Color red: Error, Failed
    [[ $# -ne 1 ]] && return 1
    printf "[${BLUE_COLOR}jobx${RES}] ${RED_COLOR}$1${RES}\n"
}

echo_g () {
    # Color green: Success
    [[ $# -ne 1 ]] && return 1
    printf "[${BLUE_COLOR}jobx${RES}] ${GREEN_COLOR}$1${RES}\n"
}

echo_y () {
    # Color yellow: Warning
    [[ $# -ne 1 ]] && return 1
    printf "[${BLUE_COLOR}jobx${RES}] ${YELLOW_COLOR}$1${RES}\n"
}

echo_w () {
    # Color yellow: White
    [[ $# -ne 1 ]] && return 1
    printf "[${BLUE_COLOR}jobx${RES}] ${WHITE_COLOR}$1${RES}\n"
}


# OS specific support.  $var _must_ be set to either true or false.
cygwin=false
darwin=false
os400=false
hpux=false
case "`uname`" in
CYGWIN*) cygwin=true;;
Darwin*) darwin=true;;
OS400*) os400=true;;
HP-UX*) hpux=true;;
esac

# resolve links - $0 may be a softlink…
PRG="$0"

while [[ -h "$PRG" ]]; do
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

# Only set JOBX_HOME if not already set
[[ -z "$JOBX_HOME" ]] && JOBX_HOME=`cd "$PRGDIR/.." >/dev/null; pwd`

# Copy JOBX_BASE from JOBX_HOME if not already set
[[ -z "$JOBX_BASE" ]] && JOBX_BASE="$JOBX_HOME"

# Ensure that any user defined CLASSPATH variables are not used on startup,
# but allow them to be specified in setenv.sh, in rare case when it is needed.
CLASSPATH=

if [[ -r "$JOBX_BASE/bin/setenv.sh" ]]; then
  . "$JOBX_BASE/bin/setenv.sh"
elif [[ -r "$JOBX_HOME/bin/setenv.sh" ]]; then
  . "$JOBX_HOME/bin/setenv.sh"
fi

# For Cygwin, ensure paths are in UNIX format before anything is touched
if ${cygwin}; then
  [[ -n "$JAVA_HOME" ]] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [[ -n "$JRE_HOME" ]] && JRE_HOME=`cygpath --unix "$JRE_HOME"`
  [[ -n "$JOBX_HOME" ]] && JOBX_HOME=`cygpath --unix "$JOBX_HOME"`
  [[ -n "$JOBX_BASE" ]] && JOBX_BASE=`cygpath --unix "$JOBX_BASE"`
  [[ -n "$CLASSPATH" ]] && CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi

# Ensure that neither JOBX_HOME nor JOBX_BASE contains a colon
# as this is used as the separator in the classpath and Java provides no
# mechanism for escaping if the same character appears in the path.
case ${JOBX_HOME} in
  *:*) echo "Using JOBX_HOME:   $JOBX_HOME";
       echo "Unable to start as JOBX_HOME contains a colon (:) character";
       exit 1;
esac
case ${JOBX_BASE} in
  *:*) echo "Using JOBX_BASE:   $JOBX_BASE";
       echo "Unable to start as JOBX_BASE contains a colon (:) character";
       exit 1;
esac

# For OS400
if ${os400}; then
  # Set job priority to standard for interactive (interactive - 6) by using
  # the interactive priority - 6, the helper threads that respond to requests
  # will be running at the same priority as interactive jobs.
  COMMAND='chgjob job('$JOBNAME') runpty(6)'
  system ${COMMAND}

  # Enable multi threading
  export QIBM_MULTI_THREADED=Y
fi

# Get standard Java environment variables
if ${os400}; then
  # -r will Only work on the os400 if the files are:
  # 1. owned by the user
  # 2. owned by the PRIMARY group of the user
  # this will not work if the user belongs in secondary groups
  . "$JOBX_HOME"/bin/setclasspath.sh
else
  if [[ -r "$JOBX_HOME"/bin/setclasspath.sh ]]; then
    . "$JOBX_HOME"/bin/setclasspath.sh
  else
    echo "Cannot find $JOBX_HOME/bin/setclasspath.sh"
    echo "This file is needed to run this program"
    exit 1
  fi
fi

#check java exists.
$RUNJAVA >/dev/null 2>&1

if [[ $? -ne 1 ]];then
  echo_r "ERROR: java is not install,please install java first!"
  exit 1;
fi

#check openjdk
if [[ "`${RUNJAVA} -version 2>&1 | head -1|grep "openjdk"|wc -l`"x == "1"x ]]; then
  echo_r "ERROR: please uninstall OpenJDK and install jdk first"
  exit 1;
fi

if [[ -z "$JOBX_OUT" ]] ; then
  JOBX_OUT="$JOBX_BASE"/logs/jobx.out
fi

if [[ -z "$JOBX_TMPDIR" ]] ; then
  # Define the java.io.tmpdir to use for jobx
  JOBX_TMPDIR="$JOBX_BASE"/temp
fi

JOBX_PIDDIR="/var/run";
if [[ ! -d "$JOBX_PIDDIR" ]] ; then
    mkdir $JOBX_PIDDIR;
fi
JOBX_PID="$JOBX_BASE/jobx.pid";

#jobx version
JOBX_VERSION="1.2.0-RELEASE"
# Add on extra jar files to CLASSPATH
if [[ ! -z "$CLASSPATH" ]] ; then
  CLASSPATH="$CLASSPATH":
fi
CLASSPATH="$CLASSPATH""$JOBX_BASE"/lib/jobx-agent-${JOBX_VERSION}.jar
MAIN="com.jobxhub.agent.bootstrap.JobXAgent"

# Bugzilla 37848: When no TTY is available, don't output to console
have_tty=0
if [[ "`tty`" != "not a tty" ]]; then
    have_tty=1
fi

# Bugzilla 37848: When no TTY is available, don't output to console
have_tty=0
if [[ "`tty`" != "not a tty" ]]; then
    have_tty=1
fi

# For Cygwin, switch paths to Windows format before running java
if ${cygwin}; then
  JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
  JRE_HOME=`cygpath --absolute --windows "$JRE_HOME"`
  JOBX_HOME=`cygpath --absolute --windows "$JOBX_HOME"`
  JOBX_BASE=`cygpath --absolute --windows "$JOBX_BASE"`
  JOBX_TMPDIR=`cygpath --absolute --windows "$JOBX_TMPDIR"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
fi

# Set UMASK unless it has been overridden
if [[ -z "$UMASK" ]]; then
    UMASK="0027"
fi
umask $UMASK

# Uncomment the following line to make the umask available when using the
if [[ -z "$USE_NOHUP" ]]; then
    if ${hpux}; then
        USE_NOHUP="true"
    else
        USE_NOHUP="false"
    fi
fi
unset _NOHUP
if [[ "$USE_NOHUP" = "true" ]]; then
    _NOHUP=nohup
fi

# ----- Execute The Requested Command -----------------------------------------

# Bugzilla 37848: only output this if we have a TTY
if [[ ${have_tty} -eq 1 ]]; then
  echo_w "Using JOBX_BASE:   $JOBX_BASE"
  echo_w "Using JOBX_HOME:   $JOBX_HOME"
  echo_w "Using JOBX_TMPDIR: $JOBX_TMPDIR"
  if [[ "$1" = "debug" ]] ; then
    echo_w "Using JAVA_HOME:       $JAVA_HOME"
  else
    echo_w "Using JRE_HOME:        $JRE_HOME"
  fi
  echo_w "Using CLASSPATH:       $CLASSPATH"
  if [[ ! -z "$JOBX_PID" ]]; then
    echo_w "Using JOBX_PID:    $JOBX_PID"
  fi
fi

case "$1" in
    start)
        ARGS=`getopt -o P:p:h:d -al port:,password:,host:,daemon -- "$@"`
        eval set -- "${ARGS}"
        while [[ -n "$1" ]]
        do
            case "$1" in
                -P|--port)
                    JOBX_PORT=$2;
                    shift 2;;
                -p|--pass)
                    JOBX_PASSWORD=$2;
                    shift 2;;
                -h|--host)
                    JOBX_HOST=$2;
                    shift 2;;
                -d|--daemon)
                    JOBX_DAEMON="daemon";
		            shift 2;;
                --) break ;;
                *)
                    echo "usage {-P\${port}|-p\${pasword}}"
                 break ;;
            esac
        done

        if [[ ! -z "$JOBX_PORT" ]];then
          if [[ ${JOBX_PORT} -lt 0 ]] || [[ ${JOBX_PORT} -gt 65535 ]];then
             echo_r "port error,muse be between 0 and 65535!"
          fi
        fi

        if [[ ! -z "$JOBX_PID" ]]; then
           if [[ -f "$JOBX_PID" ]]; then
              if [[ -s "$JOBX_PID" ]]; then
                echo_w "Existing PID file found during start."
                if [[ -r "$JOBX_PID" ]]; then
                  PID=`cat "$JOBX_PID"`
                  ps -p ${PID} >/dev/null 2>&1
                  if [[ $? -eq 0 ]] ; then
                    echo_r "jobx appears to still be running with PID $PID. Start aborted."
                    echo_r "If the following process is not a jobx process, remove the PID file and try again:"
                    ps -f -p ${PID}
                    exit 1
                  else
                    echo_w "Removing/clearing stale PID file."
                    rm -f "$JOBX_PID" >/dev/null 2>&1
                    if [[ $? != 0 ]]; then
                      if [[ -w "$JOBX_PID" ]]; then
                        cat /dev/null > "$JOBX_PID"
                      else
                        echo_r "Unable to remove or clear stale PID file. Start aborted."
                        exit 1
                      fi
                    fi
                  fi
                else
                  echo_r "Unable to read PID file. Start aborted."
                  exit 1
                fi
              else
                rm -f "$JOBX_PID" >/dev/null 2>&1
                if [[ $? != 0 ]]; then
                  if [[ ! -w "$JOBX_PID" ]]; then
                    echo_r "Unable to remove or write to empty PID file. Start aborted."
                    exit 1
                  fi
                fi
              fi
           fi
        fi


        if [[ "$JOBX_DAEMON"x == "daemon" ]]; then
            REDIRECT_LOG=">> ${JOBX_OUT} 2>&1 \"&\""
        else
            _NOHUP=""
            REDIRECT_LOG=""
        fi

        touch "$JOBX_OUT"

        eval ${_NOHUP} "\"${RUNJAVA}\"" \
            -classpath "\"${CLASSPATH}\"" \
            -Djobx.home="${JOBX_HOME}" \
            -Djobx.pid="${JOBX_PID}" \
            -Djava.io.tmpdir="${JOBX_TMPDIR}" \
            -Djobx.port="${JOBX_PORT}" \
            -Djobx.host="${JOBX_HOST}" \
            -Djobx.password="${JOBX_PASSWORD}" \
            ${MAIN} start "${REDIRECT_LOG}";

      if [[ ! -z "$JOBX_PID" ]]; then
         echo +x $! > "$JOBX_PID"
      fi

      echo_g "jobx started."
      exit $?
      ;;

    stop)
       shift;
          SLEEP=2
          if [[ ! -z "$1" ]]; then
            echo $1 | grep "[^0-9]" >/dev/null 2>&1
            if [[ $? -gt 0 ]]; then
              SLEEP=$1
              shift
            fi
          fi

          FORCE=0
          if [[ "$1" = "-force" ]]; then
            shift
            FORCE=1
          fi

          # $JOBX_PID is not empty
          if [[ ! -z "$JOBX_PID" ]]; then
            #pid file exist
            if [[ -f "$JOBX_PID" ]]; then
              #pid file exist and not empty
              if [[ -s "$JOBX_PID" ]]; then
                #kill..
                kill -0 `cat "$JOBX_PID"` >/dev/null 2>&1
                if [[ $? -gt 0 ]]; then
                  echo_r "PID file found but no matching process was found. Stop aborted."
                  exit 1
                fi
              else
                echo_r "PID file is empty and has been ignored."
              fi
            else
              echo_r "$JOBX_PID was set but the specified file does not exist. Is jobx running? Stop aborted."
              exit 1
            fi
          fi

          eval "\"${RUNJAVA}\"" \
            -classpath "\"${CLASSPATH}\"" \
            -Djobx.home="${JOBX_HOME}" \
            -Djobx.pid="${JOBX_PID}" \
            ${MAIN} stop >> ${JOBX_OUT} 2>&1 "&";

          # stop failed. Shutdown port disabled? Try a normal kill.
          if [[ $? != 0 ]]; then
            if [[ ! -z "$JOBX_PID" ]]; then
              echo_r "The stop command failed. Attempting to signal the process to stop through OS signal."
              kill -15 `cat "$JOBX_PID"` >/dev/null 2>&1
            fi
          fi

          if [[ ! -z "$JOBX_PID" ]]; then
            if [[ -f "$JOBX_PID" ]]; then
              while [[ ${SLEEP} -ge 0 ]]; do
                kill -0 `cat "$JOBX_PID"` >/dev/null 2>&1
                if [[ $? -gt 0 ]]; then
                  rm -f "${JOBX_PID}" >/dev/null 2>&1
                  if [[ $? != 0 ]]; then
                    if [[ -w "$JOBX_PID" ]]; then
                      cat /dev/null > "$JOBX_PID"
                      # If jobx has stopped don't try and force a stop with an empty PID file
                      FORCE=0
                    else
                      echo_r "The PID file could not be removed or cleared."
                    fi
                  fi
                  echo_r "jobx stopped."
                  break
                fi
                if [[ ${SLEEP} -gt 0 ]]; then
                  sleep 1
                fi
                if [[ ${SLEEP} -eq 0 ]]; then
                  echo_w "jobx did not stop in time."
                  if [[ ${FORCE} -eq 0 ]]; then
                    echo_w "PID file was not removed."
                  fi
                  echo_w "To aid diagnostics a thread dump has been written to standard out."
                  kill -3 `cat "$JOBX_PID"`
                fi
                SLEEP=`expr ${SLEEP} - 1 `;
              done
            fi
          fi

          KILL_SLEEP_INTERVAL=5
          if [[ ${FORCE} -eq 1 ]]; then
            if [[ -z "$JOBX_PID" ]]; then
              echo_w "Kill failed: \$JOBX_PID not set"
            else
              if [[ -f "$JOBX_PID" ]]; then
                PID=`cat "$JOBX_PID"`
                echo_w "Killing jobx with the PID: $PID"
                kill -9 ${PID}
                while [[ ${KILL_SLEEP_INTERVAL} -ge 0 ]]; do
                    kill -0 `cat "${JOBX_PID}"` >/dev/null 2>&1
                    if [[ $? -gt 0 ]]; then
                        rm -f "$JOBX_PID" >/dev/null 2>&1
                        if [[ $? != 0 ]]; then
                            if [[ -w "$JOBX_PID" ]]; then
                                cat /dev/null > "$JOBX_PID"
                            else
                                echo_r "The PID file could not be removed."
                            fi
                        fi
                        echo_w "The jobx process has been killed."
                        break
                    fi
                    if [[ ${KILL_SLEEP_INTERVAL} -gt 0 ]]; then
                        sleep 1
                    fi
                    KILL_SLEEP_INTERVAL=`expr ${KILL_SLEEP_INTERVAL} - 1 `
                done
                if [[ ${KILL_SLEEP_INTERVAL} -lt 0 ]]; then
                    echo_r "jobx has not been killed completely yet. The process might be waiting on some system call or might be UNINTERRUPTIBLE."
                fi
              fi
            fi
          fi
      exit $?
      ;;

    *)
      echo_g "Unknown command: $1"
      echo_g "Usage: $PROGRAM ( commands ... )"
      echo_g "commands:"
      echo_g "  start             Start jobx"
      echo_g "  stop              Stop jobx"
      echo_g "                    are you running?"
      exit 1
    ;;
    esac

exit 0;
