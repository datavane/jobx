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

#
#kill_model
#  0) soft kill
#  1) force kill
#
kill_model=$1

#this process id
pid=$2

if [ !"${kill_model}" == "0"x ] ||[ !"${kill_model}" == "1"x ];  then
    echo "[JobX] the first args must be [0|1]"
    exit 1;
fi

if [ "${pid}"x == ""x ];then
  echo "[JobX] pid is null"
  exit 1;
fi


# if pstree exists
if [ -n "`which pstree`" ];then
     array=$(pstree -p ${pid}| sed 's/[^0-9]/ /g');
     for id in ${array}
     do
       if [ ${pid} -gt 300 ];then
         if [ "${kill_model}" == "0"x ];then
           kill ${id} >/dev/null 2>&1;
          else
           kill -9 ${id} >/dev/null 2>&1;
         fi
       fi
     done
else
    while true
    do
      #find pid by ppid
      cmd="ps -ef|awk '{if(\$3~/${pid}/) print \$2}'"
      pid=$(eval ${cmd})
      if [ "${pid}"x == ""x ] || [ ${pid} -lt 300 ] ; then
        break;
      fi
      if [ "${kill_model}"x == "0"x ];then
       kill ${pid} >/dev/null 2>&1;
      else
       kill -9 ${pid} >/dev/null 2>&1;
      fi
    done
fi

echo "[JobX] kill done";

exit 0;