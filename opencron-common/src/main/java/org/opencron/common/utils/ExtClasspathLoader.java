package org.opencron.common.utils;


import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public final class ExtClasspathLoader {

    /** URLClassLoader的addURL方法 */
    private static Method addURL = initAddMethod();

    private static URLClassLoader classloader = (URLClassLoader) ClassLoader.getSystemClassLoader();

    private static Method initAddMethod() {
        try {
            Method add = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
            add.setAccessible(true);
            return add;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadPath(String filepath) {
        File file = new File(filepath);
        loopFiles(file);
    }

    private static void loadResourceDir(String filepath) {
        File file = new File(filepath);
        loopDirs(file);
    }


    private static void loopDirs(File file) {
        // 资源文件只加载路径
        if (file.isDirectory()) {
            addURL(file);
            File[] tmps = file.listFiles();
            for (File tmp : tmps) {
                loopDirs(tmp);
            }
        }
    }


    private static void loopFiles(File file) {
        if (file.isDirectory()) {
            File[] tmps = file.listFiles();
            for (File tmp : tmps) {
                loopFiles(tmp);
            }
        }
        else {
            if (file.getAbsolutePath().endsWith(".jar") || file.getAbsolutePath().endsWith(".zip")) {
                addURL(file);
            }
        }
    }


    private static void addURL(File file) {
        try {
            addURL.invoke(classloader, new Object[] { file.toURI().toURL() });
        }
        catch (Exception e) {
        }
    }

    public static void loadJar(String jarFilePath) {
        File jarFile = new File(jarFilePath);
        if (!jarFile.exists()) {
            throw new IllegalArgumentException("[opencron] jarFilePath:"+jarFilePath+" is not exists");
        }
        if (jarFile.isFile()) {
            throw new IllegalArgumentException("[opencron] jarFile "+jarFilePath+" is not file");
        }
        loadPath(jarFile.getAbsolutePath());
    }

    public static void scanJar(String path) {
        File jarDir = new File(path);
        if (!jarDir.exists()) {
            throw new IllegalArgumentException("[opencron] jarPath:"+path+" is not exists");
        }
        if (!jarDir.isDirectory()) {
            throw new IllegalArgumentException("[opencron] jarPath:"+path+" is not directory");
        }

        if ( jarDir.listFiles().length == 0 ) {
            throw new IllegalArgumentException("[opencron] have not jar in path:"+path);
        }

        for (File jarFile:jarDir.listFiles()) {
            loadPath(jarFile.getAbsolutePath());
        }
    }

}