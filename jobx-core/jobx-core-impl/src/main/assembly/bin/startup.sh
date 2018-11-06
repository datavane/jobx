#!/bin/sh
cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
CONF_DIR=$DEPLOY_DIR/conf
LOGS_DIR=$DEPLOY_DIR/logs

cd ${BIN_DIR}
if [ ! -d $LOGS_DIR ]; then
    mkdir $LOGS_DIR
fi

echo "Using JAVA_HOME: $JAVA_HOME"

JAVARUN="$JAVA_HOME/bin/java"

JAVA_ARGS=" -server -Xms1024m -Xmx1024m -Xmn256m -XX:NewSize=100m -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=70 -XX:PermSize=128m -XX:MaxPermSize=128m -XX:ThreadStackSize=512 -Xloggc:${DEPLOY_DIR}/logs/gc.log"

MAINCLASS=" com.sqkb.venus.hive.server.HiveServer"

LIB_DIR=$DEPLOY_DIR/lib
CLASSPATH=.:$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:${DEPLOY_DIR}/classes/:${CONF_DIR}/applicationContext-manage.xml:${CONF_DIR}/
files=`ls -1 ${LIB_DIR}`
for file in ${files} ;do
        CLASSPATH=$CLASSPATH:${LIB_DIR}/${file}
done

RUNNING_ENV=" -classpath "$CLASSPATH

COMMAND=$JAVARUN$RUNNING_ENV$JAVA_ARGS$MAINCLASS

$COMMAND >>${LOGS_DIR}/stdout.log 2>&1 &

PIDS=`ps  --no-heading -C java -f --width 1000 | grep "$DEPLOY_DIR" | awk '{print $2}'`
echo "PID: $PIDS"
echo "STDOUT: ${LOGS_DIR}/stdout.log"
