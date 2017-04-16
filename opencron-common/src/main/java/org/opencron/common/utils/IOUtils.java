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

import java.io.*;

/**
 * Created by benjobs on 15/6/24.
 */
public abstract class IOUtils implements Serializable {

    private static final int EOF = -1;
    /**
     * The Unix directory separator character.
     */
    public static final char DIR_SEPARATOR_UNIX = '/';
    /**
     * The Windows directory separator character.
     */
    public static final char DIR_SEPARATOR_WINDOWS = '\\';
    /**
     * The system directory separator character.
     */
    public static final char DIR_SEPARATOR = File.separatorChar;
    /**
     * The Unix line separator string.
     */
    public static final String LINE_SEPARATOR_UNIX = "\n";
    /**
     * The Windows line separator string.
     */
    public static final String LINE_SEPARATOR_WINDOWS = "\r\n";


    public static String readText(File file, String charset) {
        InputStream inputStream = null;
        InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;

        if (CommonUtils.notEmpty(file)) {
            try {
                inputStream = new FileInputStream(file);
                inputReader = new InputStreamReader(new FileInputStream(file), charset);
                bufferReader = new BufferedReader(inputReader);

                StringBuffer strBuffer = new StringBuffer();
                // 读取一行
                String line;
                while ((line = bufferReader.readLine()) != null) {
                    strBuffer.append(line).append("\n\r");
                }
                return strBuffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bufferReader != null) {
                        bufferReader.close();
                    }

                    if (inputReader != null) {
                        inputReader.close();
                    }

                    if (inputStream != null) {
                        inputStream.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static boolean writeText(File file, Serializable text, String charset) {
        try {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), charset));
            out.write(text.toString());
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void writeFile(File file, InputStream inputStream) throws IOException {
        AssertUtils.notNull(file,inputStream);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        BufferedOutputStream  output= new BufferedOutputStream(new FileOutputStream(file));
        int r;
        while((r=inputStream.read())!=-1){
            output.write((byte)r);
        }
        output.close();
    }

    public static byte[] readFileToArray(File file) throws IOException {
        InputStream input = null;
        try {
            input = openInputStream(file);
            long size = file.length();
            if(file.length() > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Size cannot be greater than Integer max value: " + size);
            }

            if (size < 0) {
                throw new IllegalArgumentException("Size must be equal or greater than zero: " + size);
            }

            if (size == 0) {
                return new byte[0];
            }

            byte[] data = new byte[(int) size];
            int offset = 0;
            int readed;

            while (offset < size && (readed = input.read(data, offset, (int)size - offset)) != EOF) {
                offset += readed;
            }

            if (offset != size) {
                throw new IOException("Unexpected readed size. current: " + offset + ", excepted: " + size);
            }
            return data;
        } finally {
            if (input!=null) {
                input.close();
            }
        }
    }

    public static FileInputStream openInputStream(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (file.canRead() == false) {
                throw new IOException("File '" + file + "' cannot be read");
            }
        } else {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }
        return new FileInputStream(file);
    }


    public static final synchronized String getTempFolderPath() {
        return System.getProperty("java.io.tmpdir");
    }

    public static final synchronized String getProjectFolderPath() {
        String path = null;
        try {
            File directory = new File("");
            path = directory.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    public static void main(String[] args) {
        System.out.println(getProjectFolderPath());
    }

    public static boolean fileExists(Object file) {
        AssertUtils.notNull(file);
        if (file instanceof String) {
            file = new File((String) file);
        }

        return  ((File)file).exists();
    }
}
