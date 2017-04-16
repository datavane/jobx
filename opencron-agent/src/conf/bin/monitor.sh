#!/bin/bash

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

#disk
disk=$(df -h|sed -r 's/\s+/ /g'|sed -r 's/Mounted\s+on/Mounted/g'|sed -r 's/%//g');

#load
load=$(cat /proc/loadavg |awk '{print $1","$2","$3}');

#swap
total=$(cat /proc/meminfo |grep SwapTotal |awk '{print $2}');
free=$(cat /proc/meminfo |grep SwapFree |awk '{print $2}');
swap=$(echo  "{total:$total,free:$free}");

#cpu
cpulog_1=$(cat /proc/stat | grep 'cpu ' | awk '{print $2" "$3" "$4" "$5" "$6" "$7" "$8}');
sysidle1=$(echo $cpulog_1 | awk '{print $4}');
total1=$(echo $cpulog_1 | awk '{print $1+$2+$3+$4+$5+$6+$7}');
cpulog_2=$(cat /proc/stat | grep 'cpu ' | awk '{print $2" "$3" "$4" "$5" "$6" "$7" "$8}');
sysidle2=$(echo $cpulog_2 | awk '{print $4}');
total2=$(echo $cpulog_2 | awk '{print $1+$2+$3+$4+$5+$6+$7}');
cpudetail=$(top -b -n 1 | grep Cpu |sed -r 's/\s+//g'|awk -F ":" '{print $2}');
cpu=$(echo  "{id2:\"$sysidle2\",id1:\"$sysidle1\",total2:\"$total2\",total1:\"$total1\",detail:\"$cpudetail\"}");

#mem
loadmemory=$(cat /proc/meminfo | awk '{print $2}');
total=$(echo $loadmemory | awk '{print $1}');
free1=$(echo $loadmemory | awk '{print $2}');
free2=$(echo $loadmemory | awk '{print $3}');
free3=$(echo $loadmemory | awk '{print $4}');
used=$(($total - $free1 - $free2 - $free3));
mem=$(echo  "{total:$total,used:$used}");

#conf
#修复ubuntu系统下os名存在\n \l导致解析失败的bug
hostname=$(echo `hostname`|sed 's/\\.//g');
os=$(echo `head -n 1 /etc/issue`|sed 's/\\.//g');
#修复系统版本7.0之后获取os失败问题
if [ -z "$os" ];then
 os=$(echo `cat /etc/redhat-release`|sed 's/\\.//g');
fi
kernel=$(uname -r);
machine=$(uname -m);

#top
top=$(echo "P"|top -b -n 1| head -18|sed -r 's/\s+/ /g'| sed  '1,6d');

#get cpudata and trim...
cpucount=$(cat /proc/cpuinfo | grep name | wc -l);
cpuname=$(cat /proc/cpuinfo | grep name|uniq -c |awk -F ":" '{print $2}'|awk -F "@" '{print $1}'|sed -r 's/^\\s|\\s$//g');
cpuinfo=$(cat /proc/cpuinfo | grep name|uniq -c |awk -F ":" '{print $2}'|awk -F "@" '{print $2}'|sed -r 's/^\\s|\\s$//g');
cpuconf="cpuinfo:{\"count\":\"$cpucount\",\"name\":\"$cpuname\",\"info\":\"$cpuinfo\"}";

#to json data...
conf=$(echo  "{"hostname":\"$hostname\","os":\"$os\","kernel":\"$kernel\","machine":\"$machine\",$cpuconf}");

echo  "{top:\"$top\",cpu:$cpu,disk:\"$disk\",mem:$mem,swap:$swap,load:\"$load\",conf:$conf}";

exit 0;
