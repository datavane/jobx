#!/bin/bash

# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# -----------------------------------------------------------------------------
# Control Script for the OPENCRON Server
#
# Environment Variable Prerequisites
#
#   OPENCRON_HOME   May point at your opencron "build" directory.
#
#   OPENCRON_BASE   (Optional) Base directory for resolving dynamic portions
#                   of a opencron installation.  If not present, resolves to
#                   the same directory that OPENCRON_HOME points to.
#
#   OPENCRON_OUT    (Optional) Full path to a file where stdout and stderr
#                   will be redirected.
#                   Default is $OPENCRON_BASE/logs/opencron.out
#
#   OPENCRON_TMPDIR (Optional) Directory path location of temporary directory
#                   the JVM should use (java.io.tmpdir).  Defaults to
#                   $OPENCRON_BASE/temp.
#
#   OPENCRON_PID    (Optional) Path of the file which should contains the pid
#                   of the opencron startup java process, when start (fork) is
#                   used
# -----------------------------------------------------------------------------

#echo color
WHITE_COLOR="\E[1;37m";
RED_COLOR="\E[1;31m";
BLUE_COLOR='\E[1;34m';
GREEN_COLOR="\E[1;32m";
YELLOW_COLOR="\E[1;33m";
RES="\E[0m";

printf "${GREEN_COLOR}\n"
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
printf "${RES}\n";

echo_r () {
    # Color red: Error, Failed
    [ $# -ne 1 ] && return 1
    printf "[${BLUE_COLOR}opencron${RES}] ${RED_COLOR}$1${RES}\n"
}

echo_g () {
    # Color green: Success
    [ $# -ne 1 ] && return 1
    printf "[${BLUE_COLOR}opencron${RES}] ${GREEN_COLOR}$1${RES}\n"
}

echo_y () {
    # Color yellow: Warning
    [ $# -ne 1 ] && return 1
    printf "[${BLUE_COLOR}opencron${RES}] ${YELLOW_COLOR}$1${RES}\n"
}

echo_w () {
    # Color yellow: White
    [ $# -ne 1 ] && return 1
    printf "[${BLUE_COLOR}opencron${RES}] ${WHITE_COLOR}$1${RES}\n"
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

# Only set OPENCRON_HOME if not already set
[ -z "$OPENCRON_HOME" ] && OPENCRON_HOME=`cd "$PRGDIR/.." >/dev/null; pwd`

# Copy OPENCRON_BASE from OPENCRON_HOME if not already set
[ -z "$OPENCRON_BASE" ] && OPENCRON_BASE="$OPENCRON_HOME"

# Ensure that any user defined CLASSPATH variables are not used on startup,
# but allow them to be specified in setenv.sh, in rare case when it is needed.
CLASSPATH=

if [ -r "$OPENCRON_BASE/bin/setenv.sh" ]; then
  . "$OPENCRON_BASE/bin/setenv.sh"
elif [ -r "$OPENCRON_HOME/bin/setenv.sh" ]; then
  . "$OPENCRON_HOME/bin/setenv.sh"
fi

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [ -n "$JRE_HOME" ] && JRE_HOME=`cygpath --unix "$JRE_HOME"`
  [ -n "$OPENCRON_HOME" ] && OPENCRON_HOME=`cygpath --unix "$OPENCRON_HOME"`
  [ -n "$OPENCRON_BASE" ] && OPENCRON_BASE=`cygpath --unix "$OPENCRON_BASE"`
  [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi

# Ensure that neither OPENCRON_HOME nor OPENCRON_BASE contains a colon
# as this is used as the separator in the classpath and Java provides no
# mechanism for escaping if the same character appears in the path.
case $OPENCRON_HOME in
  *:*) echo_w "Using OPENCRON_HOME:   $OPENCRON_HOME";
       echo_w "Unable to start as OPENCRON_HOME contains a colon (:) character";
       exit 1;
esac
case $OPENCRON_BASE in
  *:*) echo_w "Using OPENCRON_BASE:   $OPENCRON_BASE";
       echo_w "Unable to start as OPENCRON_BASE contains a colon (:) character";
       exit 1;
esac


# For OS400
if $os400; then
  # Set job priority to standard for interactive (interactive - 6) by using
  # the interactive priority - 6, the helper threads that respond to requests
  # will be running at the same priority as interactive jobs.
  COMMAND='chgjob job('$JOBNAME') runpty(6)'
  system $COMMAND

  # Enable multi threading
  export QIBM_MULTI_THREADED=Y
fi

# Get standard Java environment variables
if $os400; then
  # -r will Only work on the os400 if the files are:
  # 1. owned by the user
  # 2. owned by the PRIMARY group of the user
  # this will not work if the user belongs in secondary groups
  . "$OPENCRON_HOME"/bin/setclasspath.sh
else
  if [ -r "$OPENCRON_HOME"/bin/setclasspath.sh ]; then
    . "$OPENCRON_HOME"/bin/setclasspath.sh
  else
    echo "Cannot find $OPENCRON_HOME/bin/setclasspath.sh"
    echo "This file is needed to run this program"
    exit 1
  fi
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


if [ -z "$OPENCRON_OUT" ] ; then
  OPENCRON_OUT="$OPENCRON_BASE"/logs/opencron.out
fi

if [ -z "$OPENCRON_TMPDIR" ] ; then
  # Define the java.io.tmpdir to use for opencron
  OPENCRON_TMPDIR="$OPENCRON_BASE"/temp
fi

OPENCRON_PIDDIR="/var/run";
 if [ ! -x "$OPENCRON_PIDDIR" ] ; then
   mkdir $OPENCRON_PIDDIR;
 fi
OPENCRON_PID="$OPENCRON_PIDDIR/opencron.pid";

#opencron version
OPENCRON_VERSION="1.1.0-RELEASE"

# Add bootstrap.jar to classpath
if [ ! -z "$CLASSPATH" ] ; then
  CLASSPATH="$CLASSPATH":
fi
CLASSPATH="$CLASSPATH""$OPENCRON_BASE"/lib/opencron-agent-${OPENCRON_VERSION}.jar

# Bugzilla 37848: When no TTY is available, don't output to console
have_tty=0
if [ "`tty`" != "not a tty" ]; then
    have_tty=1
fi

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
  JRE_HOME=`cygpath --absolute --windows "$JRE_HOME"`
  OPENCRON_HOME=`cygpath --absolute --windows "$OPENCRON_HOME"`
  OPENCRON_BASE=`cygpath --absolute --windows "$OPENCRON_BASE"`
  OPENCRON_TMPDIR=`cygpath --absolute --windows "$OPENCRON_TMPDIR"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
fi

# ----- Execute The Requested Command -----------------------------------------

# Bugzilla 37848: only output this if we have a TTY
if [ $have_tty -eq 1 ]; then
  echo_w "Using OPENCRON_BASE:   $OPENCRON_BASE"
  echo_w "Using OPENCRON_HOME:   $OPENCRON_HOME"
  echo_w "Using OPENCRON_TMPDIR: $OPENCRON_TMPDIR"
  if [ "$1" = "debug" ] ; then
    echo_w "Using JAVA_HOME:       $JAVA_HOME"
  else
    echo_w "Using JRE_HOME:        $JRE_HOME"
  fi
  echo_w "Using CLASSPATH:       $CLASSPATH"
  if [ ! -z "$OPENCRON_PID" ]; then
    echo_w "Using OPENCRON_PID:    $OPENCRON_PID"
  fi
fi

case "$1" in
    start)
        GETOPT_ARGS=`getopt -o P:p:s:k: -al port:,password: -- "$@"`
        eval set -- "$GETOPT_ARGS"
        while [ -n "$1" ]
        do
            case "$1" in
                -P|--port)
                    OPENCRON_PORT=$2;
                    shift 2;;
                -p|--pass)
                    OPENCRON_PASSWORD=$2;
                    shift 2;;
                -s|--server)
                     OPENCRON_SERVER=$2;
                     shift 2;;
                -k|--key)
                     OPENCRON_REGKEY=$2;
                     shift 2;;
                --) break ;;
                *)
                    echo "usage {-P\${port}|-p\${pasword}}"
                 break ;;
            esac
        done

        if [ -z "$OPENCRON_PORT" ];then
            OPENCRON_PORT=1577;
            echo_w "opencron port not input,will be used port:1577"
        elif [ $OPENCRON_PORT -lt 0 ] || [ $OPENCRON_PORT -gt 65535 ];then
            echo_r "port error,muse be between 0 and 65535!"
        fi

        if [ -z "$OPENCRON_PASSWORD" ];then
            #.password file not exists
            if [ ! -f "$OPENCRON_BASE/.password" ];then
                  OPENCRON_PASSWORD="opencron";
                  echo_w "opencron password not input,will be used password:opencron"
            else
                #.password file already exists but empty
               if [ x`cat "$OPENCRON_BASE/.password"` = x"" ];then
                  OPENCRON_PASSWORD="opencron";
                  echo_r "opencron password not input,will be used password:opencron"
               fi
            fi
        fi

        if [ ! -z "$OPENCRON_PID" ]; then
           if [ -f "$OPENCRON_PID" ]; then
              if [ -s "$OPENCRON_PID" ]; then
                echo_w "Existing PID file found during start."
                if [ -r "$OPENCRON_PID" ]; then
                  PID=`cat "$OPENCRON_PID"`
                  ps -p $PID >/dev/null 2>&1
                  if [ $? -eq 0 ] ; then
                    echo_r "opencron appears to still be running with PID $PID. Start aborted."
                    echo_r "If the following process is not a opencron process, remove the PID file and try again:"
                    ps -f -p $PID
                    exit 1
                  else
                    echo_w "Removing/clearing stale PID file."
                    rm -f "$OPENCRON_PID" >/dev/null 2>&1
                    if [ $? != 0 ]; then
                      if [ -w "$OPENCRON_PID" ]; then
                        cat /dev/null > "$OPENCRON_PID"
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
                rm -f "$OPENCRON_PID" >/dev/null 2>&1
                if [ $? != 0 ]; then
                  if [ ! -w "$OPENCRON_PID" ]; then
                    echo_r "Unable to remove or write to empty PID file. Start aborted."
                    exit 1
                  fi
                fi
              fi
           fi
        fi

        touch "$OPENCRON_OUT"
        eval "\"$RUNJAVA\"" \
        -classpath "\"$CLASSPATH\"" \
        -Dopencron.home="$OPENCRON_HOME" \
        -Dopencron.pid="$OPENCRON_PID" \
        -Djava.io.tmpdir="$OPENCRON_TMPDIR" \
        -Dopencron.port="$OPENCRON_PORT" \
        -Dopencron.server="$OPENCRON_SERVER" \
        -Dopencron.regkey="$OPENCRON_REGKEY" \
        -Dopencron.password="$OPENCRON_PASSWORD" \
        org.opencron.agent.Bootstrap start \
        >> "$OPENCRON_OUT" 2>&1 "&";

      if [ ! -z "$OPENCRON_PID" ]; then
        echo $! > "$OPENCRON_PID"
      fi
      echo_g "opencron started."
      exit $?
      ;;

    stop)
       shift;
          SLEEP=2
          if [ ! -z "$1" ]; then
            echo $1 | grep "[^0-9]" >/dev/null 2>&1
            if [ $? -gt 0 ]; then
              SLEEP=$1
              shift
            fi
          fi

          FORCE=0
          if [ "$1" = "-force" ]; then
            shift
            FORCE=1
          fi

          # $OPENCRON_PID is not empty
          if [ ! -z "$OPENCRON_PID" ]; then
            #pid file exist
            if [ -f "$OPENCRON_PID" ]; then
              #pid file exist and not empty
              if [ -s "$OPENCRON_PID" ]; then
                #kill..
                kill -0 `cat "$OPENCRON_PID"` >/dev/null 2>&1
                if [ $? -gt 0 ]; then
                  echo_r "PID file found but no matching process was found. Stop aborted."
                  exit 1
                fi
              else
                echo_r "PID file is empty and has been ignored."
              fi
            else
              echo_r "\$OPENCRON_PID was set but the specified file does not exist. Is opencron running? Stop aborted."
              exit 1
            fi
          fi

          eval "\"$RUNJAVA\"" \
            -classpath "\"$CLASSPATH\"" \
            -Dopencron.home="$OPENCRON_HOME" \
            -Dopencron.pid="$OPENCRON_PID" \
            org.opencron.agent.AgentBootstrap stop

          # stop failed. Shutdown port disabled? Try a normal kill.
          if [ $? != 0 ]; then
            if [ ! -z "$OPENCRON_PID" ]; then
              echo_r "The stop command failed. Attempting to signal the process to stop through OS signal."
              kill -15 `cat "$OPENCRON_PID"` >/dev/null 2>&1
            fi
          fi

          if [ ! -z "$OPENCRON_PID" ]; then
            if [ -f "$OPENCRON_PID" ]; then
              while [ $SLEEP -ge 0 ]; do
                kill -0 `cat "$OPENCRON_PID"` >/dev/null 2>&1
                if [ $? -gt 0 ]; then
                  rm -f "$OPENCRON_PID" >/dev/null 2>&1
                  if [ $? != 0 ]; then
                    if [ -w "$OPENCRON_PID" ]; then
                      cat /dev/null > "$OPENCRON_PID"
                      # If opencron has stopped don't try and force a stop with an empty PID file
                      FORCE=0
                    else
                      echo_r "The PID file could not be removed or cleared."
                    fi
                  fi
                  echo_r "opencron stopped."
                  break
                fi
                if [ $SLEEP -gt 0 ]; then
                  sleep 1
                fi
                if [ $SLEEP -eq 0 ]; then
                  echo_w "opencron did not stop in time."
                  if [ $FORCE -eq 0 ]; then
                    echo_w "PID file was not removed."
                  fi
                  echo_w "To aid diagnostics a thread dump has been written to standard out."
                  kill -3 `cat "$OPENCRON_PID"`
                fi
                SLEEP=`expr $SLEEP - 1 `;
              done
            fi
          fi

          KILL_SLEEP_INTERVAL=5
          if [ $FORCE -eq 1 ]; then
            if [ -z "$OPENCRON_PID" ]; then
              echo_w "Kill failed: \$OPENCRON_PID not set"
            else
              if [ -f "$OPENCRON_PID" ]; then
                PID=`cat "$OPENCRON_PID"`
                echo_w "Killing opencron with the PID: $PID"
                kill -9 $PID
                while [ $KILL_SLEEP_INTERVAL -ge 0 ]; do
                    kill -0 `cat "$OPENCRON_PID"` >/dev/null 2>&1
                    if [ $? -gt 0 ]; then
                        rm -f "$OPENCRON_PID" >/dev/null 2>&1
                        if [ $? != 0 ]; then
                            if [ -w "$OPENCRON_PID" ]; then
                                cat /dev/null > "$OPENCRON_PID"
                            else
                                echo_r "The PID file could not be removed."
                            fi
                        fi
                        echo_w "The opencron process has been killed."
                        break
                    fi
                    if [ $KILL_SLEEP_INTERVAL -gt 0 ]; then
                        sleep 1
                    fi
                    KILL_SLEEP_INTERVAL=`expr $KILL_SLEEP_INTERVAL - 1 `
                done
                if [ $KILL_SLEEP_INTERVAL -lt 0 ]; then
                    echo_r "opencron has not been killed completely yet. The process might be waiting on some system call or might be UNINTERRUPTIBLE."
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
      echo_g "  start             Start opencron"
      echo_g "  stop              Stop opencron"
      echo_g "                    are you running?"
      exit 1
    ;;
    esac

exit 0;
