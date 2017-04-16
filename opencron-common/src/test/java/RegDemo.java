/*
 * Copyright 2016 benjobs
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by benjobs on 15/12/5.
 */
public class RegDemo {

    public static void main(String[] args) {


        String str = "id=1213&csrf=0c9a2fbd97c64534aa2c368ddd66becf?";

        String xx1 = str.replaceAll("\\?$","");
        System.out.println(xx1);

    }
}
