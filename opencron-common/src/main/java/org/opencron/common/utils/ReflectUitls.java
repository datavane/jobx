package org.opencron.common.utils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *
 * @ClassName: ReflectUitls
 * @author <a href="mailto:benjobs@qq.com">Wanghuajie</a>
 * @date:2012-12-5 下午2:59:49
 * @version V1.0
 * 
 */

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class ReflectUitls {

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
	 * 
	 * java反射bean的get方法
	 * 
	 * @param clazz
	 *            javaBean对象类型
	 * @param fieldName
	 *            字段名称
	 * 
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

                if (objPds[i].getName().equals(fieldName)){
                    return objPds[i].getReadMethod();
                }
            }

        } catch (IntrospectionException e) {
            throw new NoSuchMethodException(e.getMessage());
        }

        return null;

	}

	/**
	 * 
	 * java反射bean的set方法
	 * 
	 * @param clazz
	 *            javaBean对象
	 * @param fieldName
	 *            字段名称
	 * 
	 * @return set方法
	 */
	public static Method setter(Class<?> clazz, String fieldName) {
        AssertUtils.notNull(clazz,fieldName);
        try {
            PropertyDescriptor[] objPds = Introspector.getBeanInfo(clazz).getPropertyDescriptors();

            for (int i = 0; objPds.length > 1 && i < objPds.length; i++) {
                //跳出从object继承的class属性,源上必须有get方法
                if (Class.class == objPds[i].getPropertyType()
                        || objPds[i].getReadMethod() == null) {
                    continue;
                }

                if (objPds[i].getName().equals(fieldName)){
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
     * @param objClass
     * @param annotationClass
     * @return
     */

    private static List<Field> getFieldsByAnnotation(Class<?> objClass,Class<? extends Annotation> annotationClass) {
        AssertUtils.notNull(objClass,annotationClass);
        List<Field> fields = new ArrayList<Field>(0);
        while (!objClass.getSuperclass().equals(Object.class)){
            Collections.copy(fields, getDeclaredFieldByAnnotation(objClass, annotationClass));
            objClass = objClass.getSuperclass();
        }
        return fields;
    }

    private static List<Field> getDeclaredFieldByAnnotation(Class<?> objClass,Class<? extends Annotation> annotationClass) {
        AssertUtils.notNull(objClass,annotationClass);
        List<Field> fields = new ArrayList<Field>(0);
        for (Field field:objClass.getDeclaredFields()) {
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
        while (!objClass.getSuperclass().equals(Object.class)){
            Collections.copy(fields, Arrays.asList(objClass.getDeclaredFields()));
            objClass = objClass.getSuperclass();
        }
        return fields;
    }

    public static List<Method> getMethodsByAnnotation(Class<?> objClass,Class<? extends Annotation> annoClass) throws ClassNotFoundException {
        List<Method> methods = new ArrayList<Method>(0);
        for(Method method:objClass.getDeclaredMethods()){
            if (method.getAnnotation(annoClass)!=null){
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
            JarFile jarFile =  new JarFile(new File(jarPath));
            Enumeration<JarEntry> es = jarFile.entries();
            while (es.hasMoreElements()) {
                String name = null;
                for (int i = 0; i <= packageName.split("\\.").length; i++) {
                    if (es.hasMoreElements()) name = es.nextElement().getName();
                    else break;
                    if (name.contains(kindClass)) break;
                }
                if (name != null && name.endsWith(kindClass)) {
                    String className = name.replaceAll("/", ".").replace(kindClass,"");
                    classNames.add(Class.forName(className));
                }
            }
        } else {
            File urlFile = new File(url.toURI());
            for (File pkgFile : urlFile.listFiles()) {
                if (pkgFile.isFile()) {
                    String className = packageName + "." + pkgFile.getName().replace(kindClass,"");
                    classNames.add(Class.forName(className));
                }
            }
        }

        return classNames;
    }

    /**
     *
     * 执行get方法
     * @param o
     *            执行对象
     * @param fieldName
     *            属性
     * @return 该get方法的返回值
     */
    public static Object invokeGet(Object o, String fieldName) {
        try {
            Method method = getter(o.getClass(), fieldName);
            return method.invoke(o);
        } catch (Exception e) {
            throw new RuntimeException("invoke " + o.getClass().getName()+ "get Method Error，Detail：" + e.getMessage());
        }
    }

    public static boolean methodHasAnnotation(Method method, Class<? extends Annotation> annoClazz) {
        AssertUtils.notNull(method,annoClazz);
        return method.getAnnotation(annoClazz)!=null;
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
        if (object instanceof Class){
            return ((Class)object).isArray() || Collection.class.isAssignableFrom((Class)object);
        }
        return object.getClass().isArray() || Collection.class.isAssignableFrom(object.getClass());
    }

    //判断类型是否为java 8大基本类型的包装类
    public static boolean isPrimitivePackageType(Class clazz) {
        if (CommonUtils.isEmpty(clazz) || clazz.isPrimitive()) {
            return Boolean.FALSE;
        }
        return Java8Type.getIndexByObjectTypeName(clazz.getName()) > 0;
    }

    enum Java8Type {
        BYTE(1, "byte", "java.lang.Byte"),
        INT(2, "int", "java.lang.Integer"),
        SHORT(3, "short", "java.lang.Short"),
        LONG(4, "long", "java.lang.Long"),
        FLOAT(5, "float", "java.lang.Float"),
        DOUBLE(6, "double", "java.lang.Double"),
        BOOLEAN(7, "boolean", "java.lang.Boolean"),
        CHAR(8, "char", "java.lang.Character");

        private int index;
        private String primitiveTypeName;
        private String objectTypeName;

        Java8Type(int index, String primitiveTypeName, String objectTypeName) {
            this.index = index;
            this.primitiveTypeName = primitiveTypeName;
            this.objectTypeName = objectTypeName;
        }

        public static int getIndexByPrimitiveTypeName(String primitiveTypeName) {
            for (Java8Type javaLang8Type : Java8Type.values()) {
                if (javaLang8Type.getPrimitiveTypeName().equals(primitiveTypeName)) {
                    return javaLang8Type.getIndex();
                }
            }
            return -1;
        }

        public static int getIndexByObjectTypeName(String objectTypeName) {
            for (Java8Type jva8Type : Java8Type.values()) {
                if (jva8Type.getObjectTypeName().equals(objectTypeName)) {
                    return jva8Type.getIndex();
                }
            }
            return 0;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getPrimitiveTypeName() {
            return primitiveTypeName;
        }

        public void setPrimitiveTypeName(String primitiveTypeName) {
            this.primitiveTypeName = primitiveTypeName;
        }

        public String getObjectTypeName() {
            return objectTypeName;
        }

        public void setObjectTypeName(String objectTypeName) {
            this.objectTypeName = objectTypeName;
        }

    }

}
