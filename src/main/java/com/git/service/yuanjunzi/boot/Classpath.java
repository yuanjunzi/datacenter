package com.git.service.yuanjunzi.boot;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by yuanjunzi on 2019/5/10.
 */
public class Classpath {
    public Classpath() {
    }

    public static URLClassLoader getClassLoader() {
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        if (parent == null) {
            parent = Classpath.class.getClassLoader();
        }

        if (parent == null) {
            parent = ClassLoader.getSystemClassLoader();
        }

        return getClassLoader(parent, parent);
    }

    public static URLClassLoader getClassLoader(ClassLoader loader, ClassLoader parent) {
        return new Classpath.MmsBootClassLoader(filterMmsBootJars(loader), parent);
    }

    private static URL[] filterMmsBootJars(ClassLoader classLoader) {
        if (classLoader instanceof URLClassLoader) {
            URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
            URL[] allJars = urlClassLoader.getURLs();
            return filterMmsBootJars(allJars);
        } else {
            return null;
        }
    }

    public static URL[] filterMmsBootJars(File[] allJarPaths) {
        URL[] allJars = new URL[allJarPaths.length];

        try {
            for (int i = 0; i < allJarPaths.length; ++i) {
                allJars[i] = allJarPaths[i].toURI().toURL();
            }
        } catch (MalformedURLException var3) {
            throw new RuntimeException(var3);
        }

        return filterMmsBootJars(allJars);
    }

    public static URL[] filterMmsBootJars(URL[] allJars) {
        Set<String> mmsBootDependencies = MmsBootDependencies.getMmsBootJarNames();
        Set<URL> mmsBootJars = new HashSet(mmsBootDependencies.size());
        URL[] var3 = allJars;
        int var4 = allJars.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            URL url = var3[var5];
            if ("file".equals(url.getProtocol())) {
                File jar = new File(url.getFile());
                if (mmsBootDependencies.contains(jar.getName())) {
                    mmsBootJars.add(url);
                    mmsBootDependencies.remove(jar.getName());
                }
            }
        }

        mmsBootJars.add(Classpath.class.getProtectionDomain().getCodeSource().getLocation());
        if (!mmsBootDependencies.isEmpty()) {
            throw new IllegalStateException(String.format("系统中缺少以下mms-boot依赖的jetty相关jar包,需要有mms-boot完整的依赖jetty才能正常启动,请检查是否把mms-boot中依赖的包排除了,缺少的jar包:%s", new Object[]{mmsBootDependencies}));
        } else {
            return (URL[]) (new ArrayList(mmsBootJars)).toArray(new URL[mmsBootJars.size()]);
        }
    }

    public static class MmsBootClassLoader extends URLClassLoader {
        public MmsBootClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
            URL[] var3 = urls;
            int var4 = urls.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                URL url = var3[var5];
            }

        }
    }
}
