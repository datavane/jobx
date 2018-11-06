#!/bin/sh

#kill server
server_pid=`ps auxf | grep "sentosa-hive-service" | grep -v "grep" | awk '{print $2}'`

echo "spaces server pid is ${server_pid}"

if [ -n $server_pid ] ; then
  kill $server_pid
  echo "$server_pid is killed!"
fi