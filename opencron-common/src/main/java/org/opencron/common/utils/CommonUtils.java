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

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;


/**
 *
 * @author <a href="mailto:benjobs@qq.com">benjobs@qq.com</a>
 * @name:CommonUtil
 * @version: 1.0.0
 * @company: org.opencron
 * @description: 常用工具类
 * @date: 2012-10-9 pa 18:03 于大麦<br/><br/>
 *
 * <b style="color:RED">呵呵</b><br/><br/>
 * 我说我喜欢你,你说呵呵,<br/>
 * 我问你喜欢我不,你说呵呵,<br/>
 * 然后你便悄然离线,<br/>
 * 灰色头像不再跳闪，<br/>
 * 空留我傻傻的感叹。<br/>
 * 聊天就这样止步于呵呵,<br/>
 * 我们能不能不说呵呵,<br/>
 * 我已经怕了这个词了, <br/>
 * 请你不要敷衍我,<br/>
 * 我爱你那么多, <br/>
 * 你也喜欢下我好么？<br/><br/>
 *
 * 记得上次我们一起唱的歌,<br/>
 * 我小声唱,你也跟着合,<br/>
 * 看着你脸上醉人的酒窝,<br/>
 * 我心里有说不出的快乐.<br/>
 * 我问你喜欢和我在一起么？<br/>
 * 你低头不语,笑着不说。<br/>
 * 原来只是我一厢情愿，<br/>
 * 回想起以往的缠绵,<br/>
 * 痛苦在我身上蔓延,<br/>
 * 眼泪慢慢模糊视线, <br/>
 * 你的影子在我脑海盘旋,<br/>
 * 是我对你太依恋，<br/>
 * 我只想陪你走完这一段, <br/>
 * 只要你在我身边, <br/>
 * 沿途风景,即便再美,我也不会留恋，<br/>
 * 为什么我们不能把这场恋爱谈完？<br/><br/>
 *
 * 往日温柔的你现在何处,<br/>
 * 我的心只想向你倾诉，<br/>
 * 你怎忍心一人远走, <br/>
 * 让我承受思念之苦, <br/>
 * 你可知我在为你守候, <br/>
 * 我多想为你分担痛苦, <br/>
 * 时刻陪伴在你的左右, <br/>
 * 感受你的感受,去除你的烦忧,<br/>
 * 分享你的快乐,擦干你的泪珠。<br/>
 * 梦想着能和你在未来的旅途, <br/>
 * 一起嬉戏,没了忧愁, <br/>
 * 执子之手,相约白头! <br/><br/>
 *
 * 后来你打电话说已经有了男朋友,<br/>
 * 一起过的很幸福。  <br/>
 * 我说了声:呵呵,祝你幸福! <br/>
 * 你问:你怎么也说呵呵呢? <br/>
 * 我还想说啥已说不出, <br/>
 * 眼泪已经止不住的流。<br/>
 * 你决定要走,    <br/>
 * 寻找属于你的归宿,  <br/>
 * 我也不能挽留，    <br/>
 * 只能真心的祝你幸福! <br/><br/>
 *
 * 我用尽一生的思念，<br/>
 * 只为等待你的出现,<br/>
 * 如今你已越走越远,  <br/>
 * 而我只能独自感叹,    <br/>
 * 情话给谁听,眉毛为谁画? <br/>
 * 翘首企盼谁,携谁走天涯?  <br/>
 * 愿得谁之心,白首不相离？<br/>
 * 该执谁之手,相守到白发? <br/>
 * <hr style="color:RED"/>
 */

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class CommonUtils implements Serializable {

    private static final long serialVersionUID = 6458428317155311192L;

    /**
     * 非空判断
     *
     * @param objs 要判断,处理的对象
     * @return Boolean
     * @author <a href="mailto:benjobs@qq.com">Ben</a>
     * @see <b>对象为Null返回true,集合的大小为0也返回true,迭代器没有下一个也返回true..</b>
     * @since 1.0
     */
    public static Boolean isEmpty(Object... objs) {

        if (objs == null) {
            return Boolean.TRUE;
        }

        if (objs.length == 0) return Boolean.TRUE;

        for (Object obj : objs) {
            if (obj == null) {
                return true;
            }

            // 字符序列集
            if ((obj instanceof CharSequence) && "".equals(obj.toString().trim())) {
                return true;
            }
            // 单列集合
            if (obj instanceof Collection) {
                if (((Collection<?>) obj).isEmpty()) {
                    return true;
                }
            }
            // 双列集合
            if (obj instanceof Map) {
                if (((Map<?, ?>) obj).isEmpty()) {
                    return true;
                }
            }

            if (obj instanceof Iterable) {
                if (((Iterable<?>) obj).iterator() == null || !((Iterable<?>) obj).iterator().hasNext()) {
                    return true;
                }
            }

            // 迭代器
            if (obj instanceof Iterator) {
                if (!((Iterator<?>) obj).hasNext()) {
                    return true;
                }
            }

            // 文件类型
            if (obj instanceof File) {
                if (!((File) obj).exists()) {
                    return true;
                }
            }

            if ((obj instanceof Object[]) && ((Object[]) obj).length == 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * 空判断
     *
     * @param obj 要判断,处理的对象
     * @return Boolean
     * @author <a href="mailto:benjobs@qq.com">Ben</a>
     * @see <b>与非空相反</b>
     * @since 1.0
     */
    public static Boolean notEmpty(Object... obj) {
        return !isEmpty(obj);
    }

    public static Map<String, Object> object2Map(Object obj) {

        if (isEmpty(obj))
            return Collections.EMPTY_MAP;
        Map<String, Object> resultMap = new HashMap<String, Object>(0);
        // 拿到属性器数组
        try {
            PropertyDescriptor[] pds = Introspector.getBeanInfo(obj.getClass()).getPropertyDescriptors();
            for (int index = 0; pds.length > 1 && index < pds.length; index++) {
                if (Class.class == pds[index].getPropertyType() || pds[index].getReadMethod() == null) {

                    continue;
                }
                Object value = pds[index].getReadMethod().invoke(obj);
                // 只处理简单类型,对于对象类型,集合不处理
                if (notEmpty(value)) {

                    if (isPrototype(pds[index].getPropertyType())//java里的原始类型(去除自己定义类型)
                            || pds[index].getPropertyType().isPrimitive()//基本类型
                            || ReflectUitls.isPrimitivePackageType(pds[index].getPropertyType())
                            || pds[index].getPropertyType() == String.class) {

                        resultMap.put(pds[index].getName(), value.toString());
                    }else {
                        resultMap.put(pds[index].getName(), object2Map(value) );
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    public static Long toLong(Object val, Long defVal) {
        if (isEmpty(val)) {
            return defVal;
        }
        try {
            return Long.parseLong(val.toString());
        } catch (NumberFormatException e) {
            return defVal;
        }
    }

    public static Long toLong(Object val) {
        return toLong(val, null);
    }

    public static Integer toInt(Object val, Integer defVal) {
        if (isEmpty(val)) {
            return defVal;
        }
        try {
            return Integer.parseInt(val.toString());
        } catch (NumberFormatException e) {
            return defVal;
        }
    }



    public static float toFloat(Object val, float defVal) {
        if (isEmpty(val)) {
            return defVal;
        }
        try {
            return Float.parseFloat(val.toString());
        } catch (NumberFormatException e) {
            return defVal;
        }
    }

    public static Boolean toBoolean(String text,Boolean defVal) {
        if (isEmpty(text)) {
            return false;
        }
        try {
            return Boolean.parseBoolean(text);
        } catch (NumberFormatException e) {
            return defVal;
        }
    }

    public static Boolean toBoolean(String text) {
      return toBoolean(text,false);
    }

    public static Integer toInt(Object val) {
        return toInt(val, null);
    }

    public static Float toFloat(Object val) {
        return toFloat(val, 0f);
    }

    /**
     * 对Null作预处理
     *
     * @param obj   待处理的对象
     * @param clazz 该对象的类型
     * @return T 返回处理后的不为Null的该对象
     * @author <a href="mailto:benjobs@qq.com">Ben</a>
     * @see <b>对Null作预处理,有效避免NullPointerException</b>
     * @since 1.0
     */
    public static <T> T preparedNull(T obj, Class<?> clazz) {

        if (notEmpty(obj)) {
            return obj;
        }

        AssertUtils.notNull(clazz, "this class must be not null!");

        Object val = null;

        // 单列集合
        if (List.class.isAssignableFrom(clazz)) {
            val = new ArrayList<Object>(0);
        } else if (Set.class.isAssignableFrom(clazz)) {
            val = new HashSet<Object>(0);
        } else if (Map.class.isAssignableFrom(clazz)) {
            val = new HashMap<Object, Object>(0);
        } else {
            try {
                val = clazz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (T) val;
    }


    public static List arrayToList(Object source) {
        return Arrays.asList(ObjectUtils.toObjectArray(source));
    }

    public static boolean contains(Iterator iterator, Object element) {
        if (iterator != null) {
            while (iterator.hasNext()) {
                Object candidate = iterator.next();
                if (ObjectUtils.safeEquals(candidate, element)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check whether the given Enumeration contains the given element.
     * @param enumeration the Enumeration to check
     * @param element the element to look for
     * @return <code>true</code> if found, <code>false</code> else
     */
    public static boolean contains(Enumeration enumeration, Object element) {
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                Object candidate = enumeration.nextElement();
                if (ObjectUtils.safeEquals(candidate, element)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check whether the given Collection contains the given element instance.
     * <p>Enforces the given instance to be present, rather than returning
     * <code>true</code> for an equal element as well.
     * @param collection the Collection to check
     * @param element the element to look for
     * @return <code>true</code> if found, <code>false</code> else
     */
    public static boolean containsInstance(Collection collection, Object element) {
        if (collection != null) {
            for (Object candidate : collection) {
                if (candidate == element) {
                    return true;
                }
            }
        }
        return false;
    }


    public static <A, E extends A> A[] toArray(Enumeration<E> enumeration, A[] array) {
        ArrayList<A> elements = new ArrayList<A>();
        while (enumeration.hasMoreElements()) {
            elements.add(enumeration.nextElement());
        }
        return elements.toArray(array);
    }

    /**
     * Adapt an enumeration to an iterator.
     * @param enumeration the enumeration
     * @return the iterator
     */
    public static <E> Iterator<E> toIterator(Enumeration<E> enumeration) {
        @SuppressWarnings("hiding")
        class EnumerationIterator<E> implements Iterator<E> {
            private Enumeration<E> enumeration;

            public EnumerationIterator(Enumeration<E> enumeration) {
                this.enumeration = enumeration;
            }

            public boolean hasNext() {
                return this.enumeration.hasMoreElements();
            }

            public E next() {
                return this.enumeration.nextElement();
            }

            public void remove() throws UnsupportedOperationException {
                throw new UnsupportedOperationException("Not supported");
            }
        }
        return new EnumerationIterator<E>(enumeration);
    }

    //获取系统名字
    public static String getOsName() {
        return System.getProperties().getProperty("os.name");
    }

    public static boolean isLinuxOs() {
        return getOsName().toUpperCase().startsWith("LIN");
    }

    //是否为Window系统
    public static boolean isWindowOs() {
        return getOsName().toUpperCase().startsWith("WIN");
    }

    //判断类型是否为jdk里自带的原始类型
    public static boolean isPrototype(Class clazz) {
        return clazz.getClassLoader() == null;
    }


    //获取泛型上的具体类型（第一个）
    public static Class<?> getGenericType(Class<?> clazz) {
        return getGenericType(clazz, 0);
    }

    //获取泛型上的具体类型（指定哪个类型）
    public static Class<?> getGenericType(Class<?> clazz, int i) {
        Type type = clazz.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            return (Class<?>) types[i];
        }
        return null;
    }

    /**
     * 将String数组转化为Long数组
     * @Title: strArr2LongArr
     * @param strArr String数组
     * @return Long数组
     * @author: wanghajie 2012-12-13上午10:15:42
     */
    public static Long[] string2LongArray(String[] strArr) {
        if (CommonUtils.isEmpty(strArr)) {
            return null;
        }
        Long longArray[] = new Long[strArr.length];
        for (int i = 0; i < longArray.length; i++) {
            longArray[i] = StringUtils.parseLong(strArr[i]);
        }
        return longArray;
    }

    /**
     * 将将String数组转化为LongList
     * @Title: strArr2LongList
     * @param strArr String数组
     * @return LongList
     * @author: wanghajie 2012-12-13上午11:09:10
     */
    public static List<Long> string2LongList(String[] strArr) {
        // 将String数组转化为Long数组
        Long[] longArr = string2LongArray(strArr);
        return longArr == null ? ((List<Long>) Collections.EMPTY_LIST) : Arrays.asList(longArr);
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static <T>T[] arrayRemoveElements(T[] array,T... elem) {
        AssertUtils.notNull(array);
        List<T> arrayList = new ArrayList<T>(0);
        Collections.addAll(arrayList,array);
        if (isEmpty(elem)) return array;
        for(T el:elem)  arrayList.remove(el);
        return Arrays.copyOf(arrayList.toArray(array), arrayList.size());
    }

    public static <T>T[] arrayRemoveIndex(T[] array,int... index) {
        AssertUtils.notNull(array);
        for(int j:index) {
            if (j < 0 || j > array.length - 1) throw new IndexOutOfBoundsException("index error.@"+j);
        }
        List<T> arrayList = new ArrayList<T>(0);
        Collections.addAll(arrayList, array);
        int i=0;
        for(int j:index) {
            arrayList.remove(j-i);
            ++i;
        }
        return Arrays.copyOf(arrayList.toArray(array), arrayList.size());
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    /**
     * 生成指定长度的uuid
     * @param len
     * @return
     */
    public static String uuid(int len) {
        StringBuffer sb = new StringBuffer();
        while (sb.length()<len) {
            sb.append(uuid());
        }
        return sb.toString().substring(0,len);
    }


}


