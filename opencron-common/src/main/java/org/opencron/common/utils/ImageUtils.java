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

package org.opencron.common.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by evilgod528 on 15/2/11.
 */
public class ImageUtils {

    private File originImage;
    //正在处理的内存中的图片
    private static BufferedImage bufferedImage;
    private static BufferedImage dealedImage;

    private static ImageUtils imageUtils;

    //获取实例
    public static ImageUtils instance(File image) throws IOException {
        if(image.exists()) {
            imageUtils = new ImageUtils();
            imageUtils.originImage = image;
            imageUtils.bufferedImage = ImageIO.read(image);
            imageUtils.dealedImage = imageUtils.bufferedImage;
            return imageUtils;
        }else{
            throw new FileNotFoundException("图片文件文件不存在");
        }
    }

    private String getFileType(String imageName){
        String imageType = "jpg";
        int index = imageName.lastIndexOf(".");
        if(index!=-1 && index!=imageName.length()){
            imageType = imageName.substring(index+1);
        }
        return imageType;
    }

    public boolean build(){
        String imageType = getFileType(originImage.getName());
        try {
            ImageIO.write(dealedImage,imageType,originImage);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //生成处理后的图片，若参数为null，则修改原始图片
    public boolean build(File disposedImage){
        boolean flag = false;
        if(disposedImage==null){
            disposedImage = originImage;
        }
        String imageType = getFileType(disposedImage.getName());
        try {
            flag = ImageIO.write(dealedImage,imageType,disposedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }
    //图片缩放处理
    public ImageUtils scale(int width, int height){
        BufferedImage newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = newImg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(this.dealedImage, 0, 0, width, height, null);
        this.dealedImage = newImg;
        g.dispose();

        return this;
    }
    //剪切处理
    public ImageUtils clip(int srcX, int srcY, int width, int height){
        BufferedImage newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = newImg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(this.dealedImage, 0, 0, width, height, srcX, srcY, srcX + width, srcY + height, null);
        this.dealedImage = newImg;
        g.dispose();

        return this;
    }
    //angle：角度，图片旋转角度
    public ImageUtils rotate(int angle){
        if (angle == 0) {
            return this;
        }

        if (angle>360) {
            angle = Math.abs(angle)%360;
        }

        if (angle<0){
            if (Math.abs(angle)<360) {
                angle = 360+angle;
            }else {
                angle =(Math.abs(angle)/360+1)*360+angle;
            }
        }

        int width = this.dealedImage.getWidth();
        int height = this.dealedImage.getHeight();
        int new_w, new_h;
        int new_radian = angle;
        if (angle <= 90) {
            new_w = (int) (width * Math.cos(Math.toRadians(new_radian)) + height * Math.sin(Math.toRadians(new_radian)));
            new_h = (int) (height * Math.cos(Math.toRadians(new_radian)) + width * Math.sin(Math.toRadians(new_radian)));
        } else if (angle <= 180) {
            new_radian = angle - 90;
            new_w = (int) (height * Math.cos(Math.toRadians(new_radian)) + width * Math.sin(Math.toRadians(new_radian)));
            new_h = (int) (width * Math.cos(Math.toRadians(new_radian)) + height * Math.sin(Math.toRadians(new_radian)));
        } else if (angle <= 270) {
            new_radian = angle - 180;
            new_w = (int) (width * Math.cos(Math.toRadians(new_radian)) + height * Math.sin(Math.toRadians(new_radian)));
            new_h = (int) (height * Math.cos(Math.toRadians(new_radian)) + width * Math.sin(Math.toRadians(new_radian)));
        } else {
            new_radian = angle - 270;
            new_w = (int) (height * Math.cos(Math.toRadians(new_radian)) + width * Math.sin(Math.toRadians(new_radian)));
            new_h = (int) (width * Math.cos(Math.toRadians(new_radian)) + height * Math.sin(Math.toRadians(new_radian)));
        }
        BufferedImage toStore = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = toStore.createGraphics();
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(Math.toRadians(angle), width / 2, height / 2);
        if (angle != 180) {
            AffineTransform translationTransform = this.findTranslation(affineTransform, this.dealedImage, angle);
            affineTransform.preConcatenate(translationTransform);
        }
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, new_w, new_h);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawRenderedImage(this.dealedImage, affineTransform);
        g.dispose();
        this.dealedImage = toStore;

        return this;
    }
    //isVertical:true,垂直翻转 ; false,水平翻转
    public ImageUtils reverse(boolean isVertical){
        int width = this.dealedImage.getWidth();
        int height = this.dealedImage.getHeight();
        double[] matrix;
        if(isVertical){
            matrix = new double[]{1, 0, 0, -1, 0,height};
        }else {
            matrix = new double[]{-1, 0, 0, 1,width,0};
        }
        AffineTransform affineTransform = new AffineTransform(matrix);
        BufferedImage newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = newImg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawRenderedImage(this.dealedImage, affineTransform);
        g.dispose();
        this.dealedImage = newImg;

        return this;
    }
    private AffineTransform findTranslation(AffineTransform at, BufferedImage bi, int angle) {//45
        Point2D p2din, p2dout;
        double ytrans = 0.0, xtrans = 0.0;
        if (angle <= 90) {
            p2din = new Point2D.Double(0.0, 0.0);
            p2dout = at.transform(p2din, null);
            ytrans = p2dout.getY();

            p2din = new Point2D.Double(0, bi.getHeight());
            p2dout = at.transform(p2din, null);
            xtrans = p2dout.getX();
        }
        /*else if(angle<=135){
            p2din = new Point2D.Double(0.0, bi.getHeight());
            p2dout = at.transform(p2din, null);
            ytrans = p2dout.getY();

            p2din = new Point2D.Double(bi.getWidth(),bi.getHeight());
            p2dout = at.transform(p2din, null);
            xtrans = p2dout.getX();

        }*/
        else if (angle <= 180) {
            p2din = new Point2D.Double(0.0, bi.getHeight());
            p2dout = at.transform(p2din, null);
            ytrans = p2dout.getY();

            p2din = new Point2D.Double(bi.getWidth(), bi.getHeight());
            p2dout = at.transform(p2din, null);
            xtrans = p2dout.getX();

        }
        /*else if(angle<=225){
            p2din = new Point2D.Double(bi.getWidth(), bi.getHeight());
            p2dout = at.transform(p2din, null);
            ytrans = p2dout.getY();

            p2din = new Point2D.Double(bi.getWidth(),0.0);
            p2dout = at.transform(p2din, null);
            xtrans = p2dout.getX();

        }*/
        else if (angle <= 270) {
            p2din = new Point2D.Double(bi.getWidth(), bi.getHeight());
            p2dout = at.transform(p2din, null);
            ytrans = p2dout.getY();

            p2din = new Point2D.Double(bi.getWidth(), 0.0);
            p2dout = at.transform(p2din, null);
            xtrans = p2dout.getX();

        } else {
            p2din = new Point2D.Double(bi.getWidth(), 0.0);
            p2dout = at.transform(p2din, null);
            ytrans = p2dout.getY();


            p2din = new Point2D.Double(0.0, 0.0);
            p2dout = at.transform(p2din, null);
            xtrans = p2dout.getX();

        }
        AffineTransform tat = new AffineTransform();
        tat.translate(-xtrans, -ytrans);
        return tat;
    }

}