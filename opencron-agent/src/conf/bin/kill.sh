#!/bin/sh

# Copyright 2016 benjobs
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


sleep 2;

pid=$(ps -ef|grep $1.sh|grep -v grep|awk '{print $2}');
if [ ${pid}x == "x" ];then
  echo "pid is null"
  exit 1;
fi

if [ -n "`which pstree`" ];then
 arr=$(pstree -p $pid| sed 's/[^0-9]/ /g');
 for p in $arr
 do
   if [ $p > 300 ];then
     kill -9 $p >/dev/null 2>&1;
   fi
 done

 echo "kill done!";

 exit 1;
fi

function akill() {
   aa="ps -ef|grep ${pid}|grep -v ${ppid}|grep -v grep|wc -l"
   num=`eval ${aa}`
     if [ $pid -gt 0 ];then
         tmp_ppid=$pid
         bb="ps -ef|grep $pid|grep -v $ppid|grep -v grep|awk '{print $2}'"
         cc=`eval ${bb}`
         tmp_pid=`echo $cc|awk '{print $2}'`
         ppid=${tmp_ppid};
         pid=${tmp_pid};
     fi
     return $pid
}
pid=`ps -ef|grep $1.sh|egrep -v grep|awk '{print $2}'`
ppid=`ps -ef|grep $1.sh|egrep -v grep|awk '{print $3}'`
aa="ps -ef|grep ${pid}|grep -v ${ppid}|grep -v grep|wc -l"
num=`eval ${aa}`

i=1;
sum_pid[0]=$pid;
while [ $pid ]
do
    akill;
    sum_pid[$i]=$pid;
    let "i++";
    if [ $num -le 0 ];then
        break;
    fi
done

kill -9  ${sum_pid[*]} >/dev/null 2>&1;

echo "kill done";

exit 0;
