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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class StringUtils {

    public static final String SEARCH_SEPERATOR = "[ ,;|　]";

    public static boolean hasText(String str) {
        return org.springframework.util.StringUtils.hasText(str);
    }

    public static String joinString(Object[] arrays, String separator) {
        return joinString(Arrays.asList(arrays), separator);
    }

    public static String joinString(Collection<?> collection, String separator) {
        AssertUtils.notEmpty(collection, "joinString arguments collection and separator can not be null");
        StringBuffer collStr = new StringBuffer();
        for (Object o : collection) {
            collStr.append(o).append(separator);
        }
        return collStr.substring(0, collStr.length() - separator.length());
    }

    public static String joinString(Object[] arrays) {
        return joinString(arrays, ",");
    }

    public static String joinString(Collection<?> collection) {
        return joinString(collection, ",");
    }

    /**
     *将字符串转换成String[]
     * @param str 要转换的目标字符串
     * @return
     */
    public static String[] stringToArray(String str) {
        return stringToArray(str, null, 0);
    }

    /**
     *将字符串转换成String[]
     * @param str 要转换的目标字符串
     * @param limit 要转换的目标字符串
     * @return
     */
    public static String[] stringToArray(String str, int limit) {
        return stringToArray(str, null, limit);
    }

    /**
     *将字符串转换成String[]
     * @param str 要转换的目标字符串
     * @param split 对字符串截取的分隔符
     * @return
     */
    public static String[] stringToArray(String str, String split) {
        return stringToArray(str, split, 0);
    }

    /**
     *将字符串转换成String[]
     * @param str 要转换的目标字符串
     * @param split 对字符串截取的分隔符
     * @param limit 长度
     * @return
     */
    public static String[] stringToArray(String str, String split, int limit) {
        List<String> list = stringToList(str, split, limit);
        if (list == null) {
            return null;
        }
        return list.toArray(new String[0]);
    }

    /**
     *将字符串转换成List<String>
     * @param str 要转换的目标字符串
     * @return
     */
    public static List<String> stringToList(String str) {
        return stringToList(str, null, 0);
    }

    /**
     *将字符串转换成List<String>
     * @param str 要转换的目标字符串
     * @param limit 对字符串截取的分隔符
     * @return
     */
    public static List<String> stringToList(String str, int limit) {
        return stringToList(str, null, limit);
    }

    /**
     *将字符串转换成List<String>
     * @param str 要转换的目标字符串
     * @param split 长度
     * @return
     */
    public static List<String> stringToList(String str, String split) {
        return stringToList(str, split, 0);
    }

    /**
     * 转换字符串成list
     *
     * @param str  字符串
     * @param split 分隔符
     * @param limit 长度
     * @return
     */
    public static List<String> stringToList(String str, String split, int limit) {
        if (str == null) {
            return null;
        }
        String[] ret = null;
        if (split == null) {
            split = SEARCH_SEPERATOR;
        }
        ret = str.split(split, limit);
        if (ret == null) {
            return null;
        }

        List<String> list = new ArrayList<String>();
        for (String aRet : ret) {
            String s = aRet;
            if (s == null) {
                continue;
            }
            s = s.trim();
            if (!"".equals(s)) {
                list.add(s);
            }
        }
        return list;
    }

    /**
     * 转换字符串为int 失败时返回默认值 -1
     *
     * @param str
     * @return
     */
    public static int parseInt(String str) {
        return parseInt(str, -1);
    }

    /**
     * 转换字符串为int 失败时返回默认值 defaultValue
     *
     * @param str
     * @param defaultValue
     * @return
     */
    public static int parseInt(String str, int defaultValue) {
        if (str == null || "".equals(str.trim())) {
            return defaultValue;
        }
        int num = 0;
        try {
            num = Integer.parseInt(str);
        } catch (Exception e) {
            num = defaultValue;
        }
        return num;
    }

    /**
     * 转换字符串为long类型，如果转换失败，则返回默认值 -1
     * @param str
     *            字符串
     * @return
     */
    public static long parseLong(String str) {
        return parseLong(str, -1);
    }

    /**
     * 转换字符串为long类型，如果转换失败，则返回默认值 defaultValue
     *
     * @param str
     *            字符串
     * @param defaultValue
     *            默认值
     * @return
     */
    public static long parseLong(String str, long defaultValue) {
        if (str == null || "".equals(str.trim())) {
            return defaultValue;
        }
        long num = 0;
        try {
            num = Long.parseLong(str);
        } catch (Exception e) {
            num = defaultValue;
        }
        return num;
    }

    /**
     * 判断字符串是否为null或者为""
     *
     * @param str
     * @return
     */
    public static boolean isNullString(String str) {
        if (str == null || "".equals(str.trim())) {
            return true;
        }
        return false;
    }

    public static boolean isNotNullString(String str) {
        return !isNullString(str);
    }

    public static String checkString(String str, String defaultValue) {
        if (str == null || "".equals(str.trim())) {
            return defaultValue;
        }
        return str;
    }

    public static String objectToString(Object o) {
        if (o == null) {
            return "";
        }
        if ("".equals(o)) {
            return "";
        }
        return o.toString();
    }

    /**
     * 过滤字符串内的所有script标签
     *
     * @param htmlStr
     * @return writer:<a href="mailto:benjobs@qq.com">benjobs</a> 2012.2.1
     */
    public static String escapeJavaScript(String htmlStr) {
        if (htmlStr == null || "".equals(htmlStr)) {
            return "";
        }
        String regEx_script = "<script[^>]*?>[\\s\\S]*?</script>"; // 定义script的正则表达式
        Pattern p_script = Pattern.compile(regEx_script,
                Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); // 过滤script标签
        return htmlStr.trim(); // 返回文本字符串
    }

    /**
     * 过滤字符串内的所有html标签
     *
     * @param htmlStr
     * @return writer:<a href="mailto:benjobs@qq.com">benjobs</a> 2012.2.1
     */
    public static String escapeHtml(String htmlStr) {
        if (htmlStr == null || "".equals(htmlStr)) {
            return "";
        }
        String regEx_script = "<script[^>]*?>[\\s\\S]*?</script>"; // 定义script的正则表达式
        String regEx_style = "<style[^>]*?>[\\s\\S]*?</style>"; // 定义style的正则表达式
        String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

        Pattern p_script = Pattern.compile(regEx_script,
                Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); // 过滤script标签

        Pattern p_style = Pattern
                .compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); // 过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); // 过滤html标签

        return htmlStr.trim(); // 返回文本字符串
    }

    public static String htmlEncode(String source) {
        if (source == null) {
            return "";
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            switch (c) {
                case '<':
                    buffer.append("&lt;");
                    break;
                case '>':
                    buffer.append("&gt;");
                    break;
                case '&':
                    buffer.append("&amp;");
                    break;
                case '"':
                    buffer.append("&quot;");
                    break;
                case 10:
                case 13:
                    break;
                default:
                    buffer.append(c);
            }
        }
        return buffer.toString();

    }

    public static String HtmlToTextGb2312(String inputString) {
        if (inputString == null || "".equals(inputString)) {
            return "";
        }
        String htmlStr = inputString; // 含html标签的字符串
        String textStr = "";
        Pattern p_script;
        Matcher m_script;
        Pattern p_style;
        Matcher m_style;
        Pattern p_html;
        Matcher m_html;
        Pattern p_houhtml;
        Matcher m_houhtml;
        Pattern p_spe;
        Matcher m_spe;
        Pattern p_blank;
        Matcher m_blank;
        Pattern p_table;
        Matcher m_table;
        Pattern p_enter;
        Matcher m_enter;

        try {
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?/[\\s]*?script[\\s]*?>";
            // 定义script的正则表达式.
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?/[\\s]*?style[\\s]*?>";
            // 定义style的正则表达式.
            String regEx_html = "<[^>]+>";
            // 定义HTML标签的正则表达式
            String regEx_houhtml = "/[^>]+>";
            // 定义HTML标签的正则表达式
            String regEx_spe = "\\&[^;]+;";
            // 定义特殊符号的正则表达式
            String regEx_blank = " +";
            // 定义多个空格的正则表达式
            String regEx_table = "\t+";
            // 定义多个制表符的正则表达式
            String regEx_enter = "\n+";
            // 定义多个回车的正则表达式

            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); // 过滤script标签

            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); // 过滤style标签

            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); // 过滤html标签

            p_houhtml = Pattern
                    .compile(regEx_houhtml, Pattern.CASE_INSENSITIVE);
            m_houhtml = p_houhtml.matcher(htmlStr);
            htmlStr = m_houhtml.replaceAll(""); // 过滤html标签

            p_spe = Pattern.compile(regEx_spe, Pattern.CASE_INSENSITIVE);
            m_spe = p_spe.matcher(htmlStr);
            htmlStr = m_spe.replaceAll(""); // 过滤特殊符号

            p_blank = Pattern.compile(regEx_blank, Pattern.CASE_INSENSITIVE);
            m_blank = p_blank.matcher(htmlStr);
            htmlStr = m_blank.replaceAll(" "); // 过滤过多的空格

            p_table = Pattern.compile(regEx_table, Pattern.CASE_INSENSITIVE);
            m_table = p_table.matcher(htmlStr);
            htmlStr = m_table.replaceAll(" "); // 过滤过多的制表符

            p_enter = Pattern.compile(regEx_enter, Pattern.CASE_INSENSITIVE);
            m_enter = p_enter.matcher(htmlStr);
            htmlStr = m_enter.replaceAll(" "); // 过滤过多的制表符

            textStr = htmlStr;

        } catch (Exception e) {
            System.err.println("Html2Text: " + e.getMessage());
        }

        return textStr;// 返回文本字符串
    }

    /**
     * 取字符串的前toCount个字符
     *
     * @param str
     *            被处理字符串
     * @param toCount
     *            截取长度
     * @param more
     *            后缀字符串
     * @version 2004.11.24
     * @author zhulx
     * @return String
     */
    public static String subString(String str, int toCount, String more) {
        int reInt = 0;
        String reStr = "";
        if (str == null)
            return "";
        char[] tempChar = str.toCharArray();
        for (int kk = 0; (kk < tempChar.length && toCount > reInt); kk++) {
            String s1 = String.valueOf(tempChar[kk]);
            byte[] b = s1.getBytes();
            reInt += b.length;
            reStr += tempChar[kk];
        }
        if (toCount == reInt || (toCount == reInt - 1)) {
            reStr += more;
        }
        return reStr;
    }

    /**
     * 按字节截取字符串，并保证不会截取半个汉字
     *
     * @param str
     * @param byteLength
     * @return
     */
    public static String truncate(String str, int byteLength) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return str;
        }
        if (byteLength < 0) {
            throw new IllegalArgumentException(
                    "Parameter byteLength must be great than 0");
        }
        char[] chs = str.toCharArray();
        int i = 0;
        int len = 0;
        while ((len < byteLength) && (i < chs.length)) {
            len = (chs[i++] > 0xff) ? (len + 2) : (len + 1);
        }
        if (len > byteLength) {
            i--;
        }
        return new String(chs, 0, i);
    }

    /**
     * 取得字符串s的char长度
     *
     * @param s
     * @return
     */
    public static int getWordCount(String s) {
        int length = 0;
        for (int i = 0; i < s.length(); i++) {
            int ascii = Character.codePointAt(s, i);
            if (ascii >= 0 && ascii <= 255)
                length++;
            else
                length += 2;
        }
        return length;
    }

    public static String changeCharset(String str, String newCharset) {
        if (str != null) {
            //用默认字符编码解码字符串。
            byte[] bs = str.getBytes();
            //用新的字符编码生成字符串
            try {
                return new String(bs, newCharset);
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }
        return null;
    }

    public static <T> String join(T[] array, String limit) {
        if (array == null || array.length == 0)
            return "";
        StringBuilder sb = new StringBuilder();
        for (T t : array) {
            sb.append(t.toString()).append(limit);
        }
        String str = sb.toString();
        return str.substring(0, str.length() - limit.length());
    }

    public static String toUpperCase(String str) {
        return toUpperCase(str, 1);
    }

    public static String toUpperCase(String str, int position) {
        if (CommonUtils.isEmpty(str))
            throw new NullPointerException("str can not be empty！！");
        if (position <= 0 || position > str.length()) {
            throw new IndexOutOfBoundsException("Position must be greater than 0 and not less than the length of the string to be processed");
        }

        if (position == 1) {//将数个字母小写
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }

        return str.substring(0, position - 1) + str.substring(position - 1, position).toUpperCase() + str.substring(position);
    }

    public static String toUpperCase(String str, int index, int len) {
        if (CommonUtils.isEmpty(str))
            throw new NullPointerException("str can not be empty！！");
        if (index <= 0 || (index + len - 1) > str.length()) {
            throw new IndexOutOfBoundsException("Position must be greater than 0 and not less than the length of the string to be processed");
        }

        if (index == 1) {//将数个字母小写
            return str.substring(0, len).toUpperCase() + str.substring(len);
        }
        return str.substring(0, index - 1) + str.substring(index - 1, len + 1).toUpperCase() + str.substring(index + len - 1);
    }

    public static String toLowerCase(String str) {
        return toLowerCase(str, 1);
    }

    public static String toLowerCase(String str, int position) {
        AssertUtils.notNull(str, "str can not be empty！！");
        if (position <= 0 || position > str.length()) {
            throw new IndexOutOfBoundsException("Position must be greater than 0 and not less than the length of the string to be processed");
        }

        if (position == 1) {//将数个字母小写
            return str.substring(0, 1).toLowerCase() + str.substring(1);
        }

        return str.substring(0, position - 1) + str.substring(position - 1, position).toLowerCase() + str.substring(position);
    }

    public static String toLowerCase(String str, int index, int len) {
        if (CommonUtils.isEmpty(str))
            throw new NullPointerException("str can not be empty！！");
        if (index <= 0 || (index + len - 1) > str.length()) {
            throw new IndexOutOfBoundsException("Position must be greater than 0 and not less than the length of the string to be processed");
        }

        if (index == 1) {//将数个字母小写
            return str.substring(0, len).toLowerCase() + str.substring(len);
        }
        return str.substring(0, index - 1) + str.substring(index - 1, len + 1).toLowerCase() + str.substring(index + len - 1);
    }

    public static String clearLine(String val) {
        AssertUtils.notNull(val, "clearLine arguments must be not null");
        return val.replaceAll("\\n|\\r", "");
    }

    public static String replaceBlank(String val) {
        AssertUtils.notNull(val, "replaceBlank arguments must be not null");
        return Pattern.compile("\\s*|\\t|\\r|\\n").matcher(val).replaceAll("");
    }

    public static String replace(Object obj, int start, int end, String s1) {
        if (CommonUtils.isEmpty(obj)) return "";
        if (start < 0 || end < 0) throw new IndexOutOfBoundsException("replace:startIndex and endIndex error");
        String str = obj.toString();
        String str1 = str.substring(0, start - 1);
        String str2 = str.substring(start + end - 1);
        String replStr = "";
        for (int j = 0; j < end; j++) {
            replStr += s1;
        }
        return str1 + replStr + str2;
    }

    /**
     * 生成计费ID bs+四位随机生成的
     * */
    public static String generateString(int length) {
        String corpid = "";
        int value;
        for (int i = 0; i < length; i++) {
            value = (int) (Math.random() * 26); // 26 表示只用小写字母 52表示所有字母 61表示数字字母组合
            corpid += generateChar(value);
        }
        return corpid;
    }

    private static char generateChar(int value) {
        char temp = 't';
        if (value >= 0 && value < 26) {
            temp = (char) ('a' + value);
        } else if (value >= 26 && value < 52) {
            temp = (char) ('A' + value - 26);
        } else if (value >= 52 && value < 62) {
            temp = (char) ('0' + value - 52);
        }
        if (temp == 's') {
            temp = 'z';
        }
        return temp;
    }

    public static boolean isNumeric(String text) {
        if (CommonUtils.isEmpty(text)) return false;
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(text);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
}

