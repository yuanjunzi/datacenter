package com.git.service.yuanjunzi.boot;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;

/**
 * Created by yuanjunzi on 2019/5/10.
 */
public class AbstractIdeaBootstrap extends AbstractBootstrap {
    public AbstractIdeaBootstrap(String defaultJettyXmlName) {
        super(defaultJettyXmlName);
    }

    protected void initEnv() {
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        if (systemClassLoader instanceof URLClassLoader) {
            URLClassLoader loader = (URLClassLoader) systemClassLoader;
            super.classLoader = new Classpath.MmsBootClassLoader(Classpath.filterMmsBootJars(loader.getURLs()), loader.getParent());
            super.classpath = new File(systemClassLoader.getResource("").getFile());
            super.customJettyXmlRegex = "^jetty(\\d|(-\\d)?)_idea.xml$";
            Thread.currentThread().setContextClassLoader(this.classLoader);
        } else {
            throw new RuntimeException("System ClassLoader不是一个URLClassLoader,无法计算加载的jar包");
        }
    }

    protected String getWebRootPath() {
        return ".";
    }

    protected void setJettyWebRoot() throws IOException {
        String webroot = System.getProperty("jetty.webroot");
        if (webroot == null) {
            if (!(new File("src/main/webapp")).exists()) {
                throw new RuntimeException("[src/main/webapp]不存在,无法计算对应的webapp目录");
            }

            webroot = "./src/main/webapp";
        }

        System.setProperty("jetty.webroot", webroot);
    }
}
