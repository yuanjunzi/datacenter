package com.git.service.yuanjunzi.boot;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.regex.Pattern;

/**
 * Created by yuanjunzi on 2019/5/10.
 */
@Slf4j
public class AbstractBootstrap {

    protected ClassLoader classLoader;
    protected File classpath;
    protected String defaultJettyXmlName;
    protected String customJettyXmlRegex = "^jetty(\\d|(-\\d)?).xml$";

    public AbstractBootstrap(String defaultJettyXmlName) {
        this.defaultJettyXmlName = defaultJettyXmlName;
    }

    protected static boolean isIdeaStart() {
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        if (systemClassLoader instanceof URLClassLoader) {
            URLClassLoader loader = (URLClassLoader) systemClassLoader;
            URL[] var2 = loader.getURLs();
            int var3 = var2.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                URL jarUrl = var2[var4];
                if ((new File(jarUrl.getFile())).getName().contains("idea_rt.jar")) {
                    return true;
                }
            }
        }

        return false;
    }

    protected void initEnv() {
        String mmsBootPath = AbstractBootstrap.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File libDir = (new File(mmsBootPath)).getParentFile();
        if (!this.isLibDir(libDir)) {
            throw new RuntimeException(String.format("[%s]的所在目录不是[WEB-INF/lib]中,[%s]只能运行在编译打包后的代码中", new Object[]{mmsBootPath, AbstractBootstrap.class.getName()}));
        } else {
            this.classpath = new File(libDir.getParentFile(), "classes");
            this.classLoader = new Classpath.MmsBootClassLoader(Classpath.filterMmsBootJars(libDir.listFiles()), ClassLoader.getSystemClassLoader());
            Thread.currentThread().setContextClassLoader(this.classLoader);
        }
    }

    protected String getWebRootPath() {
        return this.classpath.getParentFile().getParent();
    }

    private boolean isLibDir(File libDir) {
        return libDir.getName().equals("lib") && libDir.getParentFile().getName().equals("WEB-INF");
    }

    protected void boot() throws Exception {
        log.info("\n");
        this.loadConfig();
        this.prepareLogDir();
        log.info("user.dir=" + System.getProperty("user.dir"));
        this.start();
    }

    protected void loadConfig() throws IOException {
        this.loadBootProperties();
        this.loadConfigProperties();
        this.setJettyWebRoot();
        this.setJettyContext();
    }

    protected void setJettyWebRoot() throws IOException {
        String webroot = System.getProperty("jetty.webroot");
        if (webroot != null && !webroot.trim().isEmpty()) {
            webroot = webroot.trim();
        } else {
            webroot = this.getWebRootPath();
        }

        log.info(String.format("jetty.webroot is [%s]", new Object[]{webroot}));
        System.setProperty("jetty.webroot", webroot);
    }

    private void setJettyContext() {
        String context = System.getProperty("jetty.context");
        if (context != null) {
            context = context.trim();
        }

        if (context != null && !context.isEmpty()) {
            if (context.charAt(0) != 47) {
                context = "/" + context;
            }
        } else {
            context = "/";
        }

        log.info(String.format("jetty.context is [%s]", new Object[]{context}));
        System.setProperty("jetty.context", context);
    }

    private void loadBootProperties() throws IOException {
        File bootProperties = new File(this.classpath, "jetty/boot.properties");
        if (bootProperties.exists() && bootProperties.isFile()) {
            System.getProperties().load(new FileInputStream(bootProperties));
            log.info("Boot Loaded...");
        } else {
            log.info("No /jetty/boot.properties found to load...");
        }

    }

    private void loadConfigProperties() throws IOException {
        File configProperties = new File(this.classpath, "config.properties");
        if (configProperties.exists() && configProperties.isFile()) {
            System.getProperties().load(new FileInputStream(configProperties));
            log.info("Config Loaded...");
        } else {
            log.info("No /config.properties found to load...");
        }

    }

    private void prepareLogDir() {
        String logs = System.getProperty("jetty.logs");
        if (logs == null) {
            if ((new File("/opt/logs/mobile")).exists()) {
                logs = "/opt/logs/mobile";
            } else {
                logs = "./logs";
            }
        }

        if (!(new File(logs)).exists()) {
            (new File(logs)).mkdirs();
        }

    }

    protected void start() throws Exception {
        Object server = this.loadFromJettyXml();
        log.info("\n\n");
        server.getClass().getMethod("start", new Class[0]).invoke(server, new Object[0]);
    }

    protected Object loadFromJettyXml() throws Exception {
        InputStream input = this.findJettyXml();
        Object xmlConfiguration = this.createXmlConfiguration(input);
        return this.invokeConfigure(xmlConfiguration);
    }

    protected InputStream findJettyXml() {
        final Pattern jettyXmlPattern = Pattern.compile(this.customJettyXmlRegex);
        File[] customjettyXml = (new File(this.classpath, "jetty")).listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isFile() && jettyXmlPattern.matcher(file.getName()).matches();
            }
        });

        try {
            if (customjettyXml != null && customjettyXml.length != 0) {
                if (customjettyXml.length == 1) {
                    log.info(String.format("使用自定义的[%s]配置jetty", new Object[]{customjettyXml[0].getCanonicalPath()}));
                    return new FileInputStream(customjettyXml[0]);
                } else {
                    StringBuilder jettyFiles = new StringBuilder();
                    File[] var4 = customjettyXml;
                    int var5 = customjettyXml.length;

                    for (int var6 = 0; var6 < var5; ++var6) {
                        File file = var4[var6];
                        jettyFiles.append("\n").append(file.getCanonicalPath()).append(", ");
                    }

                    throw new RuntimeException(String.format("resources/jetty目录下只能有一个jetty.xml文件,名字的匹配规则是:[%s],现在发现有多个类似的文件:[%s]", new Object[]{this.customJettyXmlRegex, jettyFiles.toString()}));
                }
            } else {
                log.info("使用内置的" + this.defaultJettyXmlName + "配置jetty");
                return AbstractBootstrap.class.getResourceAsStream("/" + this.defaultJettyXmlName);
            }
        } catch (IOException var8) {
            throw new RuntimeException(var8);
        }
    }

    private Object createXmlConfiguration(InputStream input) {
        Constructor xmlConfigurationConstructor = ReflectUtil.getConstructor(this.classLoader, "org.eclipse.jetty.xml.XmlConfiguration", new Class[]{InputStream.class});
        return ReflectUtil.instance(xmlConfigurationConstructor, new Object[]{input});
    }

    private Object invokeConfigure(Object xmlConfiguration) {
        Method configure = (Method) ReflectUtil.getMethod(xmlConfiguration, "configure", new Class[0]);
        return ReflectUtil.invoke(xmlConfiguration, configure, new Object[0]);
    }
}
