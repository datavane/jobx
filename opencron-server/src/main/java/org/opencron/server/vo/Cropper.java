/**
 * Copyright 2016 benjobs
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

package org.opencron.server.vo;

import java.io.Serializable;

/**
 * Created by benjobs on 2016/12/13.
 */
public class Cropper implements Serializable {

    private Double x;
    private Double y;
    private Double width;
    private Double height;
    private int rotate;

    public Integer getX() {
        return x==null?null:x.intValue();
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Integer getY() {
        return y==null?null:y.intValue();
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Integer getWidth() {
        return width==null?null:width.intValue();
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height==null?null:height.intValue();
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public int getRotate() {
        return rotate;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }
}
