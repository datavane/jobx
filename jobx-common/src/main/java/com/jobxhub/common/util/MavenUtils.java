/**
 * Copyright (c) 2015 The JobX Project
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.jobxhub.common.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;


/**
 * @author benjobs
 */
public class MavenUtils {

    private String projectPath;

    public static MavenUtils get(ClassLoader classLoader) {
        MavenUtils utils = new MavenUtils();
        String path = classLoader.getResource("").getPath();
        utils.projectPath = path.replaceFirst("/target/(.*)|/classes/", "");
        return utils;
    }

    private Model getModel(String pom) {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        try {
            return reader.read(new FileReader(pom));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("[JobX]:mavenUtils getModel error,please check " + pom);
    }

    private Model getParentModel() {
        return getModel("./pom.xml");
    }

    private Model getCurrentModel() {
        Model model = getParentModel();
        List<String> modules = model.getModules();
        for (String module : modules) {
            if (projectPath.endsWith(module)) {
                String currentPom = "./".concat(module).concat("/pom.xml");
                model = getModel(currentPom);
                return model;
            }
        }
        return null;
    }

    public String getArtifactId() {
        Model model = getCurrentModel();
        if (model == null) {
            return null;
        }
        return model.getArtifactId();
    }

    public String getArtifactVersion() {

        Model model = getCurrentModel();
        if (model != null && model.getVersion() != null) {
            return model.getVersion();
        }

        model = getParentModel();
        if (model != null) {
            return model.getVersion();
        }
        return null;
    }

    public String getArtifact() {
        return getArtifactId().concat("-").concat(getArtifactVersion());
    }


    public static void main(String[] args) {
        MavenUtils mavenUtils = MavenUtils.get(Thread.currentThread().getContextClassLoader());
        System.out.println(mavenUtils.getArtifactId() + "---> " + mavenUtils.getArtifact());
    }


}
