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

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.*;
import com.jobxhub.common.util.collection.HashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import static com.jobxhub.common.util.AssertUtils.checkNotNull;


public final class ReflectUtils {


    /**
     * void(V).
     */
    public static final char JVM_VOID = 'V';

    /**
     * boolean(Z).
     */
    public static final char JVM_BOOLEAN = 'Z';

    /**
     * byte(B).
     */
    public static final char JVM_BYTE = 'B';

    /**
     * char(C).
     */
    public static final char JVM_CHAR = 'C';

    /**
     * double(D).
     */
    public static final char JVM_DOUBLE = 'D';

    /**
     * float(F).
     */
    public static final char JVM_FLOAT = 'F';

    /**
     * int(I).
     */
    public static final char JVM_INT = 'I';

    /**
     * long(J).
     */
    public static final char JVM_LONG = 'J';

    /**
     * short(S).
     */
    public static final char JVM_SHORT = 'S';

    public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];

    public static final String JAVA_IDENT_REGEX = "(?:[_$a-zA-Z][_$a-zA-Z0-9]*)";

    public static final String JAVA_NAME_REGEX = "(?:" + JAVA_IDENT_REGEX + "(?:\\." + JAVA_IDENT_REGEX + ")*)";

    public static final String CLASS_DESC = "(?:L" + JAVA_IDENT_REGEX + "(?:\\/" + JAVA_IDENT_REGEX + ")*;)";

    public static final String ARRAY_DESC = "(?:\\[+(?:(?:[VZBCDFIJS])|" + CLASS_DESC + "))";

    public static final String DESC_REGEX = "(?:(?:[VZBCDFIJS])|" + CLASS_DESC + "|" + ARRAY_DESC + ")";

    public static final Pattern DESC_PATTERN = Pattern.compile(DESC_REGEX);

    public static final String METHOD_DESC_REGEX = "(?:(" + JAVA_IDENT_REGEX + ")?\\((" + DESC_REGEX + "*)\\)(" + DESC_REGEX + ")?)";

    public static final Pattern METHOD_DESC_PATTERN = Pattern.compile(METHOD_DESC_REGEX);

    public static final Pattern GETTER_METHOD_DESC_PATTERN = Pattern.compile("get([A-Z][_a-zA-Z0-9]*)\\(\\)(" + DESC_REGEX + ")");

    public static final Pattern SETTER_METHOD_DESC_PATTERN = Pattern.compile("set([A-Z][_a-zA-Z0-9]*)\\((" + DESC_REGEX + ")\\)V");

    public static final Pattern IS_HAS_CAN_METHOD_DESC_PATTERN = Pattern.compile("(?:is|has|can)([A-Z][_a-zA-Z0-9]*)\\(\\)Z");

    private static final ConcurrentMap<String, Class<?>> DESC_CLASS_CACHE = new HashMap<String, Class<?>>();

    private static final ConcurrentMap<String, Class<?>> NAME_CLASS_CACHE = new HashMap<String, Class<?>>();

    private static final ConcurrentMap<String, Method> Signature_METHODS_CACHE = new HashMap<String, Method>();

    private ReflectUtils() {
    }

    public static boolean isPrimitives(Class<?> cls) {
        if (cls.isArray()) {
            return isPrimitive(cls.getComponentType());
        }
        return isPrimitive(cls);
    }

    public static boolean isPrimitive(Class<?> cls) {
        return cls.isPrimitive() || cls == String.class || cls == Boolean.class || cls == Character.class
                || Number.class.isAssignableFrom(cls) || Date.class.isAssignableFrom(cls);
    }

    public static Class<?> getBoxedClass(Class<?> c) {
        if (c == int.class)
            c = Integer.class;
        else if (c == boolean.class)
            c = Boolean.class;
        else if (c == long.class)
            c = Long.class;
        else if (c == float.class)
            c = Float.class;
        else if (c == double.class)
            c = Double.class;
        else if (c == char.class)
            c = Character.class;
        else if (c == byte.class)
            c = Byte.class;
        else if (c == short.class)
            c = Short.class;
        return c;
    }

    /**
     * is compatible.
     *
     * @param c class.
     * @param o instance.
     * @return compatible or not.
     */
    public static boolean isCompatible(Class<?> c, Object o) {
        boolean pt = c.isPrimitive();
        if (o == null)
            return !pt;

        if (pt) {
            if (c == int.class)
                c = Integer.class;
            else if (c == boolean.class)
                c = Boolean.class;
            else if (c == long.class)
                c = Long.class;
            else if (c == float.class)
                c = Float.class;
            else if (c == double.class)
                c = Double.class;
            else if (c == char.class)
                c = Character.class;
            else if (c == byte.class)
                c = Byte.class;
            else if (c == short.class)
                c = Short.class;
        }
        if (c == o.getClass())
            return true;
        return c.isInstance(o);
    }

    /**
     * is compatible.
     *
     * @param cs class array.
     * @param os object array.
     * @return compatible or not.
     */
    public static boolean isCompatible(Class<?>[] cs, Object[] os) {
        int len = cs.length;
        if (len != os.length) return false;
        if (len == 0) return true;
        for (int i = 0; i < len; i++)
            if (!isCompatible(cs[i], os[i])) return false;
        return true;
    }

    public static String getCodeBase(Class<?> cls) {
        if (cls == null)
            return null;
        ProtectionDomain domain = cls.getProtectionDomain();
        if (domain == null)
            return null;
        CodeSource source = domain.getCodeSource();
        if (source == null)
            return null;
        URL location = source.getLocation();
        if (location == null)
            return null;
        return location.getFile();
    }

    /**
     * get name.
     * java.lang.Object[][].class => "java.lang.Object[][]"
     *
     * @param c class.
     * @return name.
     */
    public static String getName(Class<?> c) {
        if (c.isArray()) {
            StringBuilder sb = new StringBuilder();
            do {
                sb.append("[]");
                c = c.getComponentType();
            }
            while (c.isArray());

            return c.getName() + sb.toString();
        }
        return c.getName();
    }

    public static Class<?> getGenericClass(Class<?> cls) {
        return getGenericClass(cls, 0);
    }

    public static Class<?> getGenericClass(Class<?> cls, int i) {
        try {
            ParameterizedType parameterizedType = ((ParameterizedType) cls.getGenericInterfaces()[0]);
            Object genericClass = parameterizedType.getActualTypeArguments()[i];
            if (genericClass instanceof ParameterizedType) { // handle nested generic type
                return (Class<?>) ((ParameterizedType) genericClass).getRawType();
            } else if (genericClass instanceof GenericArrayType) { // handle array generic type
                return (Class<?>) ((GenericArrayType) genericClass).getGenericComponentType();
            } else if (((Class) genericClass).isArray()) {
                // Requires JDK 7 or higher, Foo<int[]> is no longer GenericArrayType
                return ((Class) genericClass).getComponentType();
            } else {
                return (Class<?>) genericClass;
            }
        } catch (Throwable e) {
            throw new IllegalArgumentException(cls.getName()
                    + " generic type undefined!", e);
        }
    }

    /**
     * get method name.
     * "void do(int)", "void do()", "int do(java.lang.String,boolean)"
     *
     * @param m method.
     * @return name.
     */
    public static String getName(final Method m) {
        StringBuilder ret = new StringBuilder();
        ret.append(getName(m.getReturnType())).append(' ');
        ret.append(m.getName()).append('(');
        Class<?>[] parameterTypes = m.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0)
                ret.append(',');
            ret.append(getName(parameterTypes[i]));
        }
        ret.append(')');
        return ret.toString();
    }

    public static String getSignature(String methodName, Class<?>[] parameterTypes) {
        StringBuilder sb = new StringBuilder(methodName);
        sb.append("(");
        if (parameterTypes != null && parameterTypes.length > 0) {
            boolean first = true;
            for (Class<?> type : parameterTypes) {
                if (first) {
                    first = false;
                } else {
                    sb.append(",");
                }
                sb.append(type.getName());
            }
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * get constructor name.
     * "()", "(java.lang.String,int)"
     *
     * @param c constructor.
     * @return name.
     */
    public static String getName(final Constructor<?> c) {
        StringBuilder ret = new StringBuilder("(");
        Class<?>[] parameterTypes = c.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0)
                ret.append(',');
            ret.append(getName(parameterTypes[i]));
        }
        ret.append(')');
        return ret.toString();
    }

    /**
     * get class desc.
     * boolean[].class => "[Z"
     * Object.class => "Ljava/lang/Object;"
     *
     * @param c class.
     * @return desc.
     */
    public static String getDesc(Class<?> c) {
        StringBuilder ret = new StringBuilder();

        while (c.isArray()) {
            ret.append('[');
            c = c.getComponentType();
        }

        if (c.isPrimitive()) {
            String t = c.getName();
            if ("void".equals(t)) ret.append(JVM_VOID);
            else if ("boolean".equals(t)) ret.append(JVM_BOOLEAN);
            else if ("byte".equals(t)) ret.append(JVM_BYTE);
            else if ("char".equals(t)) ret.append(JVM_CHAR);
            else if ("double".equals(t)) ret.append(JVM_DOUBLE);
            else if ("float".equals(t)) ret.append(JVM_FLOAT);
            else if ("int".equals(t)) ret.append(JVM_INT);
            else if ("long".equals(t)) ret.append(JVM_LONG);
            else if ("short".equals(t)) ret.append(JVM_SHORT);
        } else {
            ret.append('L');
            ret.append(c.getName().replace('.', '/'));
            ret.append(';');
        }
        return ret.toString();
    }

    /**
     * get class array desc.
     * [int.class, boolean[].class, Object.class] => "I[ZLjava/lang/Object;"
     *
     * @param cs class array.
     * @return desc.
     */
    public static String getDesc(final Class<?>[] cs) {
        if (cs.length == 0)
            return "";

        StringBuilder sb = new StringBuilder(64);
        for (Class<?> c : cs)
            sb.append(getDesc(c));
        return sb.toString();
    }

    /**
     * get method desc.
     * int do(int arg1) => "do(I)I"
     * void do(String arg1,boolean arg2) => "do(Ljava/lang/String;Z)V"
     *
     * @param m method.
     * @return desc.
     */
    public static String getDesc(final Method m) {
        StringBuilder ret = new StringBuilder(m.getName()).append('(');
        Class<?>[] parameterTypes = m.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++)
            ret.append(getDesc(parameterTypes[i]));
        ret.append(')').append(getDesc(m.getReturnType()));
        return ret.toString();
    }

    /**
     * get constructor desc.
     * "()V", "(Ljava/lang/String;I)V"
     *
     * @param c constructor.
     * @return desc
     */
    public static String getDesc(final Constructor<?> c) {
        StringBuilder ret = new StringBuilder("(");
        Class<?>[] parameterTypes = c.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++)
            ret.append(getDesc(parameterTypes[i]));
        ret.append(')').append('V');
        return ret.toString();
    }

    /**
     * get method desc.
     * "(I)I", "()V", "(Ljava/lang/String;Z)V"
     *
     * @param m method.
     * @return desc.
     */
    public static String getDescWithoutMethodName(Method m) {
        StringBuilder ret = new StringBuilder();
        ret.append('(');
        Class<?>[] parameterTypes = m.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++)
            ret.append(getDesc(parameterTypes[i]));
        ret.append(')').append(getDesc(m.getReturnType()));
        return ret.toString();
    }


    /**
     * name to desc.
     * java.util.Map[][] => "[[Ljava/util/Map;"
     *
     * @param name name.
     * @return desc.
     */
    public static String name2desc(String name) {
        StringBuilder sb = new StringBuilder();
        int c = 0, index = name.indexOf('[');
        if (index > 0) {
            c = (name.length() - index) / 2;
            name = name.substring(0, index);
        }
        while (c-- > 0) sb.append("[");
        if ("void".equals(name)) sb.append(JVM_VOID);
        else if ("boolean".equals(name)) sb.append(JVM_BOOLEAN);
        else if ("byte".equals(name)) sb.append(JVM_BYTE);
        else if ("char".equals(name)) sb.append(JVM_CHAR);
        else if ("double".equals(name)) sb.append(JVM_DOUBLE);
        else if ("float".equals(name)) sb.append(JVM_FLOAT);
        else if ("int".equals(name)) sb.append(JVM_INT);
        else if ("long".equals(name)) sb.append(JVM_LONG);
        else if ("short".equals(name)) sb.append(JVM_SHORT);
        else sb.append('L').append(name.replace('.', '/')).append(';');
        return sb.toString();
    }

    /**
     * desc to name.
     * "[[I" => "int[][]"
     *
     * @param desc desc.
     * @return name.
     */
    public static String desc2name(String desc) {
        StringBuilder sb = new StringBuilder();
        int c = desc.lastIndexOf('[') + 1;
        if (desc.length() == c + 1) {
            switch (desc.charAt(c)) {
                case JVM_VOID: {
                    sb.append("void");
                    break;
                }
                case JVM_BOOLEAN: {
                    sb.append("boolean");
                    break;
                }
                case JVM_BYTE: {
                    sb.append("byte");
                    break;
                }
                case JVM_CHAR: {
                    sb.append("char");
                    break;
                }
                case JVM_DOUBLE: {
                    sb.append("double");
                    break;
                }
                case JVM_FLOAT: {
                    sb.append("float");
                    break;
                }
                case JVM_INT: {
                    sb.append("int");
                    break;
                }
                case JVM_LONG: {
                    sb.append("long");
                    break;
                }
                case JVM_SHORT: {
                    sb.append("short");
                    break;
                }
                default:
                    throw new RuntimeException();
            }
        } else {
            sb.append(desc.substring(c + 1, desc.length() - 1).replace('/', '.'));
        }
        while (c-- > 0) sb.append("[]");
        return sb.toString();
    }

    public static Class<?> forName(String name) {
        try {
            return name2class(name);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Not found class " + name + ", cause: " + e.getMessage(), e);
        }
    }

    /**
     * name to class.
     * "boolean" => boolean.class
     * "java.util.Map[][]" => java.util.Map[][].class
     *
     * @param name name.
     * @return Class instance.
     */
    public static Class<?> name2class(String name) throws ClassNotFoundException {
        return name2class(ClassHelper.getClassLoader(), name);
    }

    /**
     * name to class.
     * "boolean" => boolean.class
     * "java.util.Map[][]" => java.util.Map[][].class
     *
     * @param cl   ClassLoader instance.
     * @param name name.
     * @return Class instance.
     */
    private static Class<?> name2class(ClassLoader cl, String name) throws ClassNotFoundException {
        int c = 0, index = name.indexOf('[');
        if (index > 0) {
            c = (name.length() - index) / 2;
            name = name.substring(0, index);
        }
        if (c > 0) {
            StringBuilder sb = new StringBuilder();
            while (c-- > 0)
                sb.append("[");

            if ("void".equals(name)) sb.append(JVM_VOID);
            else if ("boolean".equals(name)) sb.append(JVM_BOOLEAN);
            else if ("byte".equals(name)) sb.append(JVM_BYTE);
            else if ("char".equals(name)) sb.append(JVM_CHAR);
            else if ("double".equals(name)) sb.append(JVM_DOUBLE);
            else if ("float".equals(name)) sb.append(JVM_FLOAT);
            else if ("int".equals(name)) sb.append(JVM_INT);
            else if ("long".equals(name)) sb.append(JVM_LONG);
            else if ("short".equals(name)) sb.append(JVM_SHORT);
            else sb.append('L').append(name).append(';'); // "java.lang.Object" ==> "Ljava.lang.Object;"
            name = sb.toString();
        } else {
            if ("void".equals(name)) return void.class;
            else if ("boolean".equals(name)) return boolean.class;
            else if ("byte".equals(name)) return byte.class;
            else if ("char".equals(name)) return char.class;
            else if ("double".equals(name)) return double.class;
            else if ("float".equals(name)) return float.class;
            else if ("int".equals(name)) return int.class;
            else if ("long".equals(name)) return long.class;
            else if ("short".equals(name)) return short.class;
        }

        if (cl == null)
            cl = ClassHelper.getClassLoader();
        Class<?> clazz = NAME_CLASS_CACHE.get(name);
        if (clazz == null) {
            clazz = Class.forName(name, true, cl);
            NAME_CLASS_CACHE.put(name, clazz);
        }
        return clazz;
    }

    /**
     * Find method from method signature
     *
     * @param clazz      Target class to find method
     * @param methodName Method signature, e.g.: method1(int, String). It is allowed to provide method name only, e.g.: method2
     * @return target method
     * @throws NoSuchMethodException
     * @throws ClassNotFoundException
     * @throws IllegalStateException  when multiple methods are found (overridden method when parameter info is not provided)
     */
    public static Method findMethodByMethodSignature(Class<?> clazz, String methodName, String[] parameterTypes)
            throws NoSuchMethodException, ClassNotFoundException {
        String signature = clazz.getName() + "." + methodName;
        if (parameterTypes != null && parameterTypes.length > 0) {
            signature += StringUtils.join(parameterTypes);
        }
        Method method = Signature_METHODS_CACHE.get(signature);
        if (method != null) {
            return method;
        }
        if (parameterTypes == null) {
            List<Method> finded = new ArrayList<Method>();
            for (Method m : clazz.getMethods()) {
                if (m.getName().equals(methodName)) {
                    finded.add(m);
                }
            }
            if (finded.isEmpty()) {
                throw new NoSuchMethodException("No such method " + methodName + " in class " + clazz);
            }
            if (finded.size() > 1) {
                String msg = String.format("Not unique method for method name(%s) in class(%s), find %d methods.",
                        methodName, clazz.getName(), finded.size());
                throw new IllegalStateException(msg);
            }
            method = finded.get(0);
        } else {
            Class<?>[] types = new Class<?>[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                types[i] = ReflectUtils.name2class(parameterTypes[i]);
            }
            method = clazz.getMethod(methodName, types);

        }
        Signature_METHODS_CACHE.put(signature, method);
        return method;
    }

    public static Method findMethodByMethodName(Class<?> clazz, String methodName)
            throws NoSuchMethodException, ClassNotFoundException {
        return findMethodByMethodSignature(clazz, methodName, null);
    }

    public static Constructor<?> findConstructor(Class<?> clazz, Class<?> paramType) throws NoSuchMethodException {
        Constructor<?> targetConstructor;
        try {
            targetConstructor = clazz.getConstructor(new Class<?>[]{paramType});
        } catch (NoSuchMethodException e) {
            targetConstructor = null;
            Constructor<?>[] constructors = clazz.getConstructors();
            for (Constructor<?> constructor : constructors) {
                if (Modifier.isPublic(constructor.getModifiers())
                        && constructor.getParameterTypes().length == 1
                        && constructor.getParameterTypes()[0].isAssignableFrom(paramType)) {
                    targetConstructor = constructor;
                    break;
                }
            }
            if (targetConstructor == null) {
                throw e;
            }
        }
        return targetConstructor;
    }

    /**
     * Check if one object is the implementation for a given interface.
     * <p>
     * This method will not trigger classloading for the given interface, therefore it will not lead to error when
     * the given interface is not visible by the classloader
     *
     * @param obj                Object to examine
     * @param interfaceClazzName The given interface
     * @return true if the object implements the given interface, otherwise return false
     */
    public static boolean isInstance(Object obj, String interfaceClazzName) {
        for (Class<?> clazz = obj.getClass();
             clazz != null && !clazz.equals(Object.class);
             clazz = clazz.getSuperclass()) {
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> itf : interfaces) {
                if (itf.getName().equals(interfaceClazzName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Object getEmptyObject(Class<?> returnType) {
        return getEmptyObject(returnType, new HashMap<Class<?>, Object>(), 0);
    }

    private static Object getEmptyObject(Class<?> returnType, Map<Class<?>, Object> emptyInstances, int level) {
        if (level > 2)
            return null;
        if (returnType == null) {
            return null;
        } else if (returnType == boolean.class || returnType == Boolean.class) {
            return false;
        } else if (returnType == char.class || returnType == Character.class) {
            return '\0';
        } else if (returnType == byte.class || returnType == Byte.class) {
            return (byte) 0;
        } else if (returnType == short.class || returnType == Short.class) {
            return (short) 0;
        } else if (returnType == int.class || returnType == Integer.class) {
            return 0;
        } else if (returnType == long.class || returnType == Long.class) {
            return 0L;
        } else if (returnType == float.class || returnType == Float.class) {
            return 0F;
        } else if (returnType == double.class || returnType == Double.class) {
            return 0D;
        } else if (returnType.isArray()) {
            return Array.newInstance(returnType.getComponentType(), 0);
        } else if (returnType.isAssignableFrom(ArrayList.class)) {
            return new ArrayList<Object>(0);
        } else if (returnType.isAssignableFrom(HashSet.class)) {
            return new HashSet<Object>(0);
        } else if (returnType.isAssignableFrom(HashMap.class)) {
            return new HashMap<Object, Object>(0);
        } else if (String.class.equals(returnType)) {
            return "";
        } else if (!returnType.isInterface()) {
            try {
                Object value = emptyInstances.get(returnType);
                if (value == null) {
                    value = returnType.newInstance();
                    emptyInstances.put(returnType, value);
                }
                Class<?> cls = value.getClass();
                while (cls != null && cls != Object.class) {
                    Field[] fields = cls.getDeclaredFields();
                    for (Field field : fields) {
                        Object property = getEmptyObject(field.getType(), emptyInstances, level + 1);
                        if (property != null) {
                            try {
                                if (!field.isAccessible()) {
                                    field.setAccessible(true);
                                }
                                field.set(value, property);
                            } catch (Throwable e) {
                            }
                        }
                    }
                    cls = cls.getSuperclass();
                }
                return value;
            } catch (Throwable e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static boolean checkZeroArgConstructor(Class clazz) {
        try {
            clazz.getDeclaredConstructor();
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public static boolean isBeanPropertyReadMethod(Method method) {
        return method != null
                && Modifier.isPublic(method.getModifiers())
                && !Modifier.isStatic(method.getModifiers())
                && method.getReturnType() != void.class
                && method.getDeclaringClass() != Object.class
                && method.getParameterTypes().length == 0
                && ((method.getName().startsWith("get") && method.getName().length() > 3)
                || (method.getName().startsWith("is") && method.getName().length() > 2));
    }

    public static String getPropertyNameFromBeanReadMethod(Method method) {
        if (isBeanPropertyReadMethod(method)) {
            if (method.getName().startsWith("get")) {
                return method.getName().substring(3, 4).toLowerCase()
                        + method.getName().substring(4);
            }
            if (method.getName().startsWith("is")) {
                return method.getName().substring(2, 3).toLowerCase()
                        + method.getName().substring(3);
            }
        }
        return null;
    }

    public static boolean isBeanPropertyWriteMethod(Method method) {
        return method != null
                && Modifier.isPublic(method.getModifiers())
                && !Modifier.isStatic(method.getModifiers())
                && method.getDeclaringClass() != Object.class
                && method.getParameterTypes().length == 1
                && method.getName().startsWith("set")
                && method.getName().length() > 3;
    }

    public static String getPropertyNameFromBeanWriteMethod(Method method) {
        if (isBeanPropertyWriteMethod(method)) {
            return method.getName().substring(3, 4).toLowerCase()
                    + method.getName().substring(4);
        }
        return null;
    }

    public static boolean isPublicInstanceField(Field field) {
        return Modifier.isPublic(field.getModifiers())
                && !Modifier.isStatic(field.getModifiers())
                && !Modifier.isFinal(field.getModifiers())
                && !field.isSynthetic();
    }

    public static Map<String, Field> getBeanPropertyFields(Class cl) {
        Map<String, Field> properties = new HashMap<String, Field>();
        for (; cl != null; cl = cl.getSuperclass()) {
            Field[] fields = cl.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isTransient(field.getModifiers())
                        || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                field.setAccessible(true);

                properties.put(field.getName(), field);
            }
        }

        return properties;
    }

    public static Map<String, Method> getBeanPropertyReadMethods(Class cl) {
        Map<String, Method> properties = new HashMap<String, Method>();
        for (; cl != null; cl = cl.getSuperclass()) {
            Method[] methods = cl.getDeclaredMethods();
            for (Method method : methods) {
                if (isBeanPropertyReadMethod(method)) {
                    method.setAccessible(true);
                    String property = getPropertyNameFromBeanReadMethod(method);
                    properties.put(property, method);
                }
            }
        }

        return properties;
    }

    private static final Objenesis objenesis = new ObjenesisStd(true);

    /**
     * Maps primitive {@link Class}es to their corresponding wrapper {@link Class}.
     */
    private static final Map<Class<?>, Class<?>> primitiveWrapperMap = ContainerUtils.newIdentityHashMap();

    static {
        primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
        primitiveWrapperMap.put(Byte.TYPE, Byte.class);
        primitiveWrapperMap.put(Character.TYPE, Character.class);
        primitiveWrapperMap.put(Short.TYPE, Short.class);
        primitiveWrapperMap.put(Integer.TYPE, Integer.class);
        primitiveWrapperMap.put(Long.TYPE, Long.class);
        primitiveWrapperMap.put(Double.TYPE, Double.class);
        primitiveWrapperMap.put(Float.TYPE, Float.class);
        primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
    }

    /**
     * Maps wrapper {@link Class}es to their corresponding primitive types.
     */
    private static final Map<Class<?>, Class<?>> wrapperPrimitiveMap = ContainerUtils.newIdentityHashMap();

    static {
        for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperMap.entrySet()) {
            final Class<?> wrapperClass = entry.getValue();
            final Class<?> primitiveClass = entry.getKey();
            if (!primitiveClass.equals(wrapperClass)) {
                wrapperPrimitiveMap.put(wrapperClass, primitiveClass);
            }
        }
    }

    /**
     * Array of primitive number types ordered by "promotability".
     */
    private static final Class<?>[] ORDERED_PRIMITIVE_TYPES = {
            Byte.TYPE,
            Short.TYPE,
            Character.TYPE,
            Integer.TYPE,
            Long.TYPE,
            Float.TYPE,
            Double.TYPE
    };

    /**
     * Creates a new object.
     *
     * @param clazz the class to instantiate
     * @return new instance of clazz
     */
    public static <T> T newInstance(Class<T> clazz) {
        return newInstance(clazz, true);
    }

    /**
     * Creates a new object.
     *
     * @param clazz             the class to instantiate
     * @param constructorCalled whether or not any constructor being called
     * @return new instance of clazz
     */
    public static <T> T newInstance(Class<T> clazz, boolean constructorCalled) {
        if (constructorCalled) {
            try {
                return clazz.newInstance();
            } catch (Exception e) {
                ExceptionUtils.throwException(e);
            }
        } else {
            // without any constructor being called
            return objenesis.newInstance(clazz);
        }
        return null; // should never get here
    }

    /**
     * Invokes the underlying method.
     *
     * @param obj            the object the underlying method is invoked from
     * @param methodName     the method name this object
     * @param parameterTypes the parameter types for the method this object
     * @param args           the arguments used for the method call
     * @return the result of dispatching the method represented by this object on {@code obj} with parameters
     */
    public static Object invoke(Object obj, String methodName, Class<?>[] parameterTypes, Object[] args) {
        Object value = null;
        try {
            Method method = obj.getClass().getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            value = method.invoke(obj, args);
        } catch (Exception e) {
            ExceptionUtils.throwException(e);
        }
        return value;
    }

    /**
     * Invokes the underlying method, fast invoke using ASM.
     *
     * @param obj            the object the underlying method is invoked from
     * @param methodName     the method name this object
     * @param parameterTypes the parameter types for the method this object
     * @param args           the arguments used for the method call
     * @return the result of dispatching the method represented by this object on {@code obj} with parameters
     */
    public static Object fastInvoke(Object obj, String methodName, Class<?>[] parameterTypes, Object[] args) {
        ClassAccessor accessor = ClassAccessor.get(obj.getClass());
        return accessor.invoke(obj, methodName, parameterTypes, args);
    }

    /**
     * Returns a {@code Field} object that reflects the specified declared field
     * of the {@code Class} or interface represented by this {@code Class} object.
     * The {@code name} parameter is a {@code String} that specifies the
     * simple name of the desired field.
     *
     * @param clazz class
     * @param name  field name
     * @return the {@code Field} object for the specified field in this class
     * @throws NoSuchFieldException
     */
    public static Field getField(Class<?> clazz, String name) throws NoSuchFieldException {
        for (Class<?> cls = checkNotNull(clazz, "class"); cls != null; cls = cls.getSuperclass()) {
            try {
                Field field = cls.getDeclaredField(name);
                if (field!=null) {
                    field.setAccessible(true);
                }
                return field;
            } catch (Throwable ignored) {
            }
        }
        throw new NoSuchFieldException(clazz.getName() + "#" + name);
    }

    /**
     * Returns the static value by name, on the specified {@code Class}. The value is
     * automatically wrapped in an object if it has a primitive type.
     *
     * @param clazz the specified class
     * @param name  the name of the represented field in class
     * @return the value of the represented field in class
     */
    public static Object getStaticValue(Class<?> clazz, String name) {
        Object value = null;
        try {
            Field fd = setAccessible(getField(clazz, name));
            value = fd.get(null);
        } catch (Exception e) {
            ExceptionUtils.throwException(e);
        }
        return value;
    }

    /**
     * Sets new value by name, on the specified {@code Class}. The new value is
     * automatically unwrapped if the underlying field has
     * a primitive type.
     *
     * @param clazz the specified class
     * @param name  the name of the the field in class
     * @param value the new value for the field in class
     */
    public static void setStaticValue(Class<?> clazz, String name, Object value) {
        try {
            Field fd = setAccessible(getField(clazz, name));
            fd.set(null, value);
        } catch (Exception e) {
            ExceptionUtils.throwException(e);
        }
    }

    /**
     * Returns the value by name, on the specified object. The value is
     * automatically wrapped in an object if it has a primitive type.
     *
     * @param o    the specified object
     * @param name the name of the represented field in object
     * @return the value of the represented field in object
     */
    public static Object getValue(Object o, String name) {
        Object value = null;
        try {
            Field fd = setAccessible(getField(o.getClass(), name));
            value = fd.get(o);
        } catch (Exception e) {
            ExceptionUtils.throwException(e);
        }
        return value;
    }

    /**
     * Sets new value by name, on the specified object. The new value
     * is automatically unwrapped if the underlying field has a primitive type.
     *
     * @param o     the specified object
     * @param name  the name of the the field in object
     * @param value the new value for the field in object
     */
    public static void setValue(Object o, String name, Object value) {
        try {
            Field fd = setAccessible(getField(o.getClass(), name));
            fd.set(o, value);
        } catch (Exception e) {
            ExceptionUtils.throwException(e);
        }
    }

    /**
     * Returns the default value for the specified class.
     */
    public static Object getTypeDefaultValue(Class<?> clazz) {
        checkNotNull(clazz, "clazz");

        if (clazz.isPrimitive()) {
            if (clazz == byte.class) {
                return (byte) 0;
            }
            if (clazz == short.class) {
                return (short) 0;
            }
            if (clazz == int.class) {
                return 0;
            }
            if (clazz == long.class) {
                return 0L;
            }
            if (clazz == float.class) {
                return 0F;
            }
            if (clazz == double.class) {
                return 0D;
            }
            if (clazz == char.class) {
                return (char) 0;
            }
            if (clazz == boolean.class) {
                return false;
            }
        }
        return null;
    }

    /**
     * The shortcut to {@link #simpleClassName(Class) simpleClassName(o.getClass())}.
     */
    public static String simpleClassName(Object o) {
        return o == null ? "null_object" : simpleClassName(o.getClass());
    }

    /**
     * Generates a simplified name from a {@link Class}. Similar to {@link Class#getSimpleName()},
     * but it works fine with anonymous classes.
     */
    public static String simpleClassName(Class<?> clazz) {
        if (clazz == null) {
            return "null_class";
        }

        Package pkg = clazz.getPackage();
        return pkg == null ? clazz.getName() : clazz.getName().substring(pkg.getName().length() + 1);
    }

    /**
     * Find an array of parameter {@link Type}s that matches the given compatible parameters.
     */
    public static Class<?>[] findMatchingParameterTypes(List<Class<?>[]> parameterTypesList, Object[] args) {
        if (parameterTypesList.size() == 1) {
            return parameterTypesList.get(0);
        }

        // 获取参数类型
        Class<?>[] parameterTypes;
        if (args == null || args.length == 0) {
            parameterTypes = new Class[0];
        } else {
            parameterTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = args[i].getClass();
            }
        }

        Class<?>[] bestMatch = null;
        for (Class<?>[] pTypes : parameterTypesList) {
            if (isAssignable(parameterTypes, pTypes, true)) {
                if (bestMatch == null
                        || compareParameterTypes(pTypes, bestMatch, parameterTypes) < 0) {
                    bestMatch = pTypes;
                }
            }
        }

        return bestMatch;
    }

    /**
     * Checks if an array of {@link Class}es can be assigned to another array of {@link Class}es.
     */
    public static boolean isAssignable(Class<?>[] classArray, Class<?>[] toClassArray, final boolean autoboxing) {
        if (classArray.length != toClassArray.length) {
            return false;
        }

        for (int i = 0; i < classArray.length; i++) {
            if (!isAssignable(classArray[i], toClassArray[i], autoboxing)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if one {@link Class} can be assigned to a variable of another {@link Class}.
     */
    public static boolean isAssignable(Class<?> cls, final Class<?> toClass, final boolean autoboxing) {
        if (toClass == null) {
            return false;
        }

        // have to check for null, as isAssignableFrom doesn't
        if (cls == null) {
            return !(toClass.isPrimitive());
        }

        // autoboxing
        if (autoboxing) {
            if (cls.isPrimitive() && !toClass.isPrimitive()) {
                cls = primitiveToWrapper(cls);
                if (cls == null) {
                    return false;
                }
            }
            if (toClass.isPrimitive() && !cls.isPrimitive()) {
                cls = wrapperToPrimitive(cls);
                if (cls == null) {
                    return false;
                }
            }
        }

        if (cls.equals(toClass)) {
            return true;
        }

        // 对于原子类型, 根据JLS的规则进行扩展
        if (cls.isPrimitive()) {
            if (!toClass.isPrimitive()) {
                return false;
            }
            if (Boolean.TYPE.equals(cls)) {
                return false;
            }
            if (Integer.TYPE.equals(cls)) {
                return Long.TYPE.equals(toClass)
                        || Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            if (Long.TYPE.equals(cls)) {
                return Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            if (Float.TYPE.equals(cls)) {
                return Double.TYPE.equals(toClass);
            }
            if (Double.TYPE.equals(cls)) {
                return false;
            }
            if (Character.TYPE.equals(cls)) {
                return Integer.TYPE.equals(toClass)
                        || Long.TYPE.equals(toClass)
                        || Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            if (Short.TYPE.equals(cls)) {
                return Integer.TYPE.equals(toClass)
                        || Long.TYPE.equals(toClass)
                        || Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            if (Byte.TYPE.equals(cls)) {
                return Short.TYPE.equals(toClass)
                        || Integer.TYPE.equals(toClass)
                        || Long.TYPE.equals(toClass)
                        || Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            // should never get here
            return false;
        }

        return toClass.isAssignableFrom(cls);
    }

    /**
     * Converts the specified primitive {@link Class} object to its corresponding
     * wrapper Class object.
     */
    public static Class<?> primitiveToWrapper(final Class<?> cls) {
        Class<?> convertedClass = cls;
        if (cls != null && cls.isPrimitive()) {
            convertedClass = primitiveWrapperMap.get(cls);
        }
        return convertedClass;
    }

    /**
     * Converts the specified wrapper {@link Class} to its corresponding primitive
     * class.
     */
    public static Class<?> wrapperToPrimitive(final Class<?> cls) {
        return wrapperPrimitiveMap.get(cls);
    }

    /**
     * Compares the relative fitness of two sets of parameter types in terms of
     * matching a third set of runtime parameter types, such that a list ordered
     * by the results of the comparison would return the best match first
     * (least).
     *
     * @param left   the "left" parameter set
     * @param right  the "right" parameter set
     * @param actual the runtime parameter types to match against
     *               {@code left}/{@code right}
     * @return int consistent with {@code compare} semantics
     */
    private static int compareParameterTypes(Class<?>[] left, Class<?>[] right, Class<?>[] actual) {
        final float leftCost = getTotalTransformationCost(actual, left);
        final float rightCost = getTotalTransformationCost(actual, right);
        return leftCost < rightCost ? -1 : rightCost < leftCost ? 1 : 0;
    }

    /**
     * Returns the sum of the object transformation cost for each class in the
     * source argument list.
     *
     * @param srcArgs the source arguments
     * @param dstArgs the destination arguments
     * @return the total transformation cost
     */
    private static float getTotalTransformationCost(final Class<?>[] srcArgs, final Class<?>[] dstArgs) {
        float totalCost = 0.0f;
        for (int i = 0; i < srcArgs.length; i++) {
            Class<?> srcClass, dstClass;
            srcClass = srcArgs[i];
            dstClass = dstArgs[i];
            totalCost += getObjectTransformationCost(srcClass, dstClass);
        }
        return totalCost;
    }

    /**
     * Gets the number of steps required needed to turn the source class into
     * the destination class. This represents the number of steps in the object
     * hierarchy graph.
     *
     * @param srcClass the source class
     * @param dstClass the destination class
     * @return the cost of transforming an object
     */
    private static float getObjectTransformationCost(Class<?> srcClass, final Class<?> dstClass) {
        if (dstClass.isPrimitive()) {
            return getPrimitivePromotionCost(srcClass, dstClass);
        }
        float cost = 0.0f;
        while (srcClass != null && !dstClass.equals(srcClass)) {
            if (dstClass.isInterface() && isAssignable(srcClass, dstClass, true)) {
                // slight penalty for interface match.
                // we still want an exact match to override an interface match,
                // but
                // an interface match should override anything where we have to
                // get a superclass.
                cost += 0.25f;
                break;
            }
            cost++;
            srcClass = srcClass.getSuperclass();
        }
        /*
         * If the destination class is null, we've travelled all the way up to
         * an Object match. We'll penalize this by adding 1.5 to the cost.
         */
        if (srcClass == null) {
            cost += 1.5f;
        }
        return cost;
    }

    /**
     * Gets the number of steps required to promote a primitive number to another
     * type.
     *
     * @param srcClass the (primitive) source class
     * @param dstClass the (primitive) destination class
     * @return the cost of promoting the primitive
     */
    private static float getPrimitivePromotionCost(final Class<?> srcClass, final Class<?> dstClass) {
        float cost = 0.0f;
        Class<?> cls = srcClass;
        if (!cls.isPrimitive()) {
            // slight unwrapping penalty
            cost += 0.1f;
            cls = wrapperToPrimitive(cls);
        }
        for (int i = 0; cls != dstClass && i < ORDERED_PRIMITIVE_TYPES.length; i++) {
            if (cls == ORDERED_PRIMITIVE_TYPES[i]) {
                cost += 0.1f;
                if (i < ORDERED_PRIMITIVE_TYPES.length - 1) {
                    cls = ORDERED_PRIMITIVE_TYPES[i + 1];
                }
            }
        }
        return cost;
    }

    /**
     * Set the {@code accessible} flag for this object to the indicated boolean value.
     * A value of {@code true} indicates that the reflected object should suppress
     * Java language access checking when it is used.  A value of {@code false} indicates
     * that the reflected object should enforce Java language access checks.
     */
    private static Field setAccessible(Field fd) {
        if (!Modifier.isPublic(fd.getModifiers()) || !Modifier.isPublic(fd.getDeclaringClass().getModifiers())) {
            fd.setAccessible(true);
        }
        return fd;
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
     * java反射bean的get方法
     *
     * @param clazz     javaBean对象类型
     * @param fieldName 字段名称
     * @return get方法
     */
    public static Method getter(Class<?> clazz, String fieldName) throws NoSuchMethodException {
        // get+字段名第一个字母小写，得到get方法名

        // 拿到拷贝源上的属性器数组
        try {
            PropertyDescriptor[] objPds = Introspector.getBeanInfo(clazz).getPropertyDescriptors();

            for (int i = 0; objPds.length > 1 && i < objPds.length; i++) {
                //跳出从object继承的class属性,源上必须有get方法
                if (Class.class == objPds[i].getPropertyType()
                        || objPds[i].getReadMethod() == null) {
                    continue;
                }

                if (objPds[i].getName().equals(fieldName)) {
                    return objPds[i].getReadMethod();
                }
            }

        } catch (IntrospectionException e) {
            throw new NoSuchMethodException(e.getMessage());
        }

        return null;

    }

    /**
     * java反射bean的set方法
     *
     * @param clazz     javaBean对象
     * @param fieldName 字段名称
     * @return set方法
     */
    public static Method setter(Class<?> clazz, String fieldName) {
        AssertUtils.notNull(clazz, fieldName);
        try {
            PropertyDescriptor[] objPds = Introspector.getBeanInfo(clazz).getPropertyDescriptors();

            for (int i = 0; objPds.length > 1 && i < objPds.length; i++) {
                //跳出从object继承的class属性,源上必须有get方法
                if (Class.class == objPds[i].getPropertyType()
                        || objPds[i].getReadMethod() == null) {
                    continue;
                }

                if (objPds[i].getName().equals(fieldName)) {
                    return objPds[i].getWriteMethod();
                }
            }

        } catch (IntrospectionException e) {
            throw new RuntimeException(e.getMessage());
        }

        return null;
    }

    /**
     * 获得目标类中含有某注解的某些个Field
     *
     * @Title: getFieldsByAnnotation
     * @param objClass
     *            目标类Class
     * @param annotationClass
     *            查询的注解
     * @param index
     *            查询多少个
     * @return Field
     * @author <a href="mailto:benjobs@qq.com">Wanghuajie</a> 2013-1-5下午3:35:07
     */
    /**
     * 根据注解获取类上有该注解的全部字段
     *
     * @param objClass
     * @param annotationClass
     * @return
     */

    private static List<Field> getFieldsByAnnotation(Class<?> objClass, Class<? extends Annotation> annotationClass) {
        AssertUtils.notNull(objClass, annotationClass);
        List<Field> fields = new ArrayList<Field>(0);
        while (!objClass.getSuperclass().equals(Object.class)) {
            Collections.copy(fields, getDeclaredFieldByAnnotation(objClass, annotationClass));
            objClass = objClass.getSuperclass();
        }
        return fields;
    }

    private static List<Field> getDeclaredFieldByAnnotation(Class<?> objClass, Class<? extends Annotation> annotationClass) {
        AssertUtils.notNull(objClass, annotationClass);
        List<Field> fields = new ArrayList<Field>(0);
        for (Field field : objClass.getDeclaredFields()) {
            if (field.getAnnotation(annotationClass) != null) {
                field.setAccessible(true);
                fields.add(field);
            }
        }
        return fields;
    }

    public static List<Field> getAllFields(Class<?> objClass) {
        AssertUtils.notNull(objClass);
        List<Field> fields = new ArrayList<Field>(0);
        while (!objClass.getSuperclass().equals(Object.class)) {
            Collections.copy(fields, Arrays.asList(objClass.getDeclaredFields()));
            objClass = objClass.getSuperclass();
        }
        return fields;
    }

    public static List<Method> getMethodsByAnnotation(Class<?> objClass, Class<? extends Annotation> annoClass) throws ClassNotFoundException {
        List<Method> methods = new ArrayList<Method>(0);
        for (Method method : objClass.getDeclaredMethods()) {
            if (method.getAnnotation(annoClass) != null) {
                methods.add(method);
            }
        }
        return methods;
    }

    public static List<Class<?>> scanPackageClass(String packageName) throws Exception {
        List<Class<?>> classNames = new ArrayList<Class<?>>(0);

        String resourceName = packageName.replaceAll("\\.", "/");
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        if (url == null) {
            throw new FileNotFoundException();
        }
        String kindClass = ".class";
        if ("jar".equals(url.getProtocol())) {
            String jarPath = url.getPath().substring(0, url.getPath().indexOf("!")).replaceFirst("file:/", "").replaceAll("%20", " ");
            JarFile jarFile = new JarFile(new File(jarPath));
            Enumeration<JarEntry> es = jarFile.entries();
            while (es.hasMoreElements()) {
                String name = null;
                for (int i = 0; i <= packageName.split("\\.").length; i++) {
                    if (es.hasMoreElements()) name = es.nextElement().getName();
                    else break;
                    if (name.contains(kindClass)) break;
                }
                if (name != null && name.endsWith(kindClass)) {
                    String className = name.replaceAll("/", ".").replace(kindClass, "");
                    classNames.add(Class.forName(className));
                }
            }
        } else {
            File urlFile = new File(url.toURI());
            for (File pkgFile : urlFile.listFiles()) {
                if (pkgFile.isFile()) {
                    String className = packageName + "." + pkgFile.getName().replace(kindClass, "");
                    classNames.add(Class.forName(className));
                }
            }
        }

        return classNames;
    }

    /**
     * 执行get方法
     *
     * @param o         执行对象
     * @param fieldName 属性
     * @return 该get方法的返回值
     */
    public static Object invokeGet(Object o, String fieldName) {
        try {
            Method method = getter(o.getClass(), fieldName);
            return method.invoke(o);
        } catch (Exception e) {
            throw new RuntimeException("invoke " + o.getClass().getName() + "get Method Error，Detail：" + e.getMessage());
        }
    }

    public static boolean methodHasAnnotation(Method method, Class<? extends Annotation> annoClazz) {
        AssertUtils.notNull(method, annoClazz);
        return method.getAnnotation(annoClazz) != null;
    }

    /**
     * 检查第一个(第二个)参数类型是否为第二个参数个(第一个)类型的包装类
     *
     * @param objType1
     * @param objType2
     * @return
     */
    public static boolean checkPackageType(Class<?> objType1, Class<?> objType2) {
        if (CommonUtils.isEmpty(objType1) || CommonUtils.isEmpty(objType2)
                || (objType1.isPrimitive() && objType2.isPrimitive())
                || (!objType1.isPrimitive() && !objType2.isPrimitive())
                ) {
            return Boolean.FALSE;
        }

        try {
            if (objType1.isPrimitive())
                return objType2.getField("TYPE").get(null) == objType1;
            return objType1.getField("TYPE").get(null) == objType2;
        } catch (Exception e) {
        }
        return Boolean.FALSE;
    }

    //是否为数字或集合
    public static boolean isArrayOrSet(Object object) {
        if (object instanceof Class) {
            return ((Class) object).isArray() || Collection.class.isAssignableFrom((Class) object);
        }
        return object.getClass().isArray() || Collection.class.isAssignableFrom(object.getClass());
    }

    //判断类型是否为jdk里自带的原始类型
    public static boolean isPrototype(Class clazz) {
        return clazz.getClassLoader() == null;
    }

}
