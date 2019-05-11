package com.git.service.yuanjunzi.boot;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

/**
 * Created by yuanjunzi on 2019/5/10.
 */
public class IdeaClasspath extends Classpath {

    public IdeaClasspath() {
    }

    public static URL[] allJarsWithoutIdeas() {
        return allJarsWithoutIdeas(((URLClassLoader)ClassLoader.getSystemClassLoader()).getURLs());
    }

    public static URL[] allJarsWithoutIdeas(URL[] allJars) {
        String javaHome = System.getProperty("java.home");
        if(javaHome.endsWith("/jre")) {
            javaHome = javaHome.substring(0, javaHome.length() - 4);
        }

        ArrayList urls = new ArrayList();

        try {
            URL[] var3 = allJars;
            int var4 = allJars.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                URL jar = var3[var5];
                String path = (new File(jar.getFile())).getCanonicalPath();
                if(!path.startsWith("-n-") && !path.startsWith(javaHome) && !path.contains("/Contents/lib/idea_rt.jar")) {
                    if(path.startsWith("-y-") || path.startsWith("-n-")) {
                        path = path.substring(3);
                    }

                    urls.add(jar);
                }
            }

            return (URL[])urls.toArray(new URL[urls.size()]);
        } catch (IOException var8) {
            throw new RuntimeException(var8);
        }
    }
}
