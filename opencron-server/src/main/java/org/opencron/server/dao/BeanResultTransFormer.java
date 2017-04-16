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


package org.opencron.server.dao;

import org.apache.commons.beanutils.ConvertUtils;
import org.opencron.common.utils.ExceptionHandler;
import org.hibernate.transform.BasicTransformerAdapter;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@SuppressWarnings("unchecked")
public class BeanResultTransFormer<T> extends BasicTransformerAdapter implements Serializable {


    private static final long serialVersionUID = 1483752531899989840L;


    /**
     * 已注册实例
     */
    private final static Map<Class, BeanResultTransFormer> instanceRegistered = new ConcurrentHashMap<Class, BeanResultTransFormer>();

    /**
     * 属性名到setter方法映射
     */
    private Map<String, List<Setter>> setters = new HashMap<String, List<Setter>>();
    /**
     * 实体类型
     */
    private Class<T> beanClass;

    /**
     * 构造方法
     *
     * @param beanClass 实体类型
     */
    protected BeanResultTransFormer(Class beanClass) {
        this.register(this.beanClass = beanClass);
    }

    /**
     * 注册实体类型
     *
     * @param beanClass
     */
    protected void register(Class beanClass) {
        Method[] methods = beanClass.getMethods();

        //根据setter方法机获取属性名称
        for (Method method : methods) {
            if (filter(method)) {
                String name = method.getName().toLowerCase().substring(3);
                if (!setters.containsKey(name)) {
                    setters.put(name, new ArrayList<Setter>());
                }
                //同名的方法列表,不分大小写
                setters.get(name).add(new Setter(method));
            }
        }
    }

    /**
     * 选择Setter方法
     *
     * @param method
     * @return
     */
    boolean filter(Method method) {
        if (method.getReturnType() == Void.TYPE && method.getParameterTypes().length == 1) {
            String methodName = method.getName();
            return methodName.startsWith("set") && methodName.length() > 3;
        }
        return false;
    }

    /**
     * 或者指定类型的转换对象
     *
     * @param beanClass
     * @return
     */
    public static BeanResultTransFormer get(Class beanClass) {
        synchronized (beanClass) {
            if (!instanceRegistered.containsKey(beanClass)) {
                instanceRegistered.put(beanClass, new BeanResultTransFormer(beanClass));
            }
        }
        return instanceRegistered.get(beanClass);
    }


    /**
     * 字段转换
     * {@inheritDoc}
     */
    public Object transformTuple(Object[] tuple, String[] aliases) {
        try {
            T data = beanClass.newInstance();
            boolean isAllNull = true;
            for (int i = 0; i < tuple.length; i++) {
                String name = aliases[i];
                Object value = tuple[i];
                handle(data, name, value);
                if (value != null && isAllNull) {
                    isAllNull = false;
                }
            }
            if (isAllNull) {
                return null;
            }
            return data;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List transformList(List list) {
        if (list.size() == 1) {
            if (list.get(0) == null) {
                list.remove(0);
            }
        }
        return list;
    }

    /**
     * 尽可能地调用最匹配的Setter方法进行赋值
     *
     * @param data
     * @param name
     * @param value
     * @throws Exception
     */
    protected void handle(T data, String name, Object value) throws Exception {
        //忽略空值
        if (value == null)
            return;
        List<Setter> setterList = setters.get(name.toLowerCase());
        if (setterList == null || setterList.isEmpty())
            return;
        //唯一setter方法
        if (setterList.size() == 1) {
            Setter setter = setterList.get(0);
            if (value != null && !setter.type.isInstance(value)) {
                value = ConvertUtils.convert(value, setter.type);
            }
            setter.invoke(data, value);
            return;
        }
        //参数兼容的方法列表
        List<Setter> compatibleList = new ArrayList<Setter>();
        //数字参数的方法列表
        List<Setter> numericList = new ArrayList<Setter>();
        for (Setter setter : setterList) {
            Class<?> type = setter.type;
            //参数类型一致
            if (value != null && type == value.getClass()) {
                setter.invoke(data, value);
            }
            //参数类型兼容
            if (type.isInstance(value)) {
                compatibleList.add(setter);
            } else if (Number.class.isAssignableFrom(type)
                    && value instanceof Number) {//数值型参数
                numericList.add(setter);
            }
        }
        if (compatibleList.size() == 1) {
            compatibleList.get(0).invoke(data, value);
            return;
        }
        if (compatibleList.isEmpty() && numericList.size() == 1) {
            value = ConvertUtils.convert(value, numericList.get(0).type);
            numericList.get(0).invoke(data, value);
            return;
        }
        if (compatibleList.size() > 0 || numericList.size() > 0) {
            throw new Exception("ambiguous setter methods to call");
        }
    }


    class Setter {
        Method method;
        Class<?> type;

        Setter(Method method) {
            this.method = method;
            this.type = wrapClass(method.getParameterTypes()[0]);
        }

        void invoke(Object object, Object arg) {
            try {
                if (type.isInstance(arg)) {
                    method.invoke(object, arg);
                }
            } catch (Exception e) {
                ExceptionHandler.throwException(e);
            }
        }

        Class<?> wrapClass(Class<?> clazz) {
            if (clazz.equals(Boolean.TYPE)) {
                return Boolean.class;
            }
            if (clazz.equals(Byte.TYPE)) {
                return Byte.class;
            }
            if (clazz.equals(Short.TYPE)) {
                return Short.class;
            }
            if (clazz.equals(Integer.TYPE)) {
                return Integer.class;
            }
            if (clazz.equals(Long.TYPE)) {
                return Long.class;
            }
            if (clazz.equals(Float.TYPE)) {
                return Float.class;
            }
            if (clazz.equals(Double.TYPE)) {
                return Double.class;
            }
            if (clazz.equals(Character.TYPE)) {
                return Character.class;
            }
            return clazz;
        }

    }

}
