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

kill_model=$1

process_id=$2

if [ ${process_id}x == "x" ];then
  echo "pid is null"
  exit 1;
fi

if [ -n "`which pstree`" ];then
     array=$(pstree -p ${process_id}| sed 's/[^0-9]/ /g');
     for pid in ${array}
     do
       if [ ${pid} > 300 ];then
         if [ ${kill_model} == 0 ];then
            kill {id} >/dev/null 2>&1;
          else
            kill -9 {id} >/dev/null 2>&1;
         fi
       fi
     done
else
    cmd="ps -ef|awk '{if($2~/${process_id}/) print $3}'|grep -v "grep""
    ppid=$(eval ${cmd})
    if [ ${ppid} > 300 ];then
     if [ ${kill_model} == 0 ];then
        kill {ppid} >/dev/null 2>&1;
      else
        kill -9 {ppid} >/dev/null 2>&1;
     fi
    fi
fi

echo "kill done";

exit 0;