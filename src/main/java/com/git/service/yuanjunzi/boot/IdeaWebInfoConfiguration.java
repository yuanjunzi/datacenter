package com.git.service.yuanjunzi.boot;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by yuanjunzi on 2019/5/10.
 * 因为正常的web应用的classes实在WEB-INF下面的,而用idea启动时WEB资源是直接用的源码,里面没有classes目录,classes是用单独的classpath放进去的
 * 所以要想在idea本地启动实现相同的加载方式,就需要对这种情况特殊处理一下
 */
public class IdeaWebInfoConfiguration extends WebInfConfiguration {
    private static final Logger LOG = Log.getLogger(IdeaWebInfoConfiguration.class);

    @Override
    public void configure(WebAppContext context) throws Exception {
        //cannot configure if the context is already started
        if (context.isStarted()) {
            if (LOG.isDebugEnabled())
                LOG.debug("Cannot configure webapp " + context + " after it is started");
            return;
        }

        Resource web_inf = context.getWebInf();
        // Add WEB-INF classes and lib classpaths
        if (web_inf != null && web_inf.isDirectory() && context.getClassLoader() instanceof WebAppClassLoader)
        {
            // Look for classes directory
            Resource classes= web_inf.addPath("classes/");
            // Look for jars
            Resource lib= web_inf.addPath("lib/");
            if (classes.exists() || lib.exists()) {
                if (classes.exists()) {
                    ((WebAppClassLoader) context.getClassLoader()).addClassPath(classes);
                }
                if (lib.exists() || lib.isDirectory())
                    ((WebAppClassLoader) context.getClassLoader()).addJars(lib);
            } else {
                //请看ProjectClassLoader.getClasspaths()的功能和注释
                for (URL url : IdeaClasspath.allJarsWithoutIdeas()) {
                    ((WebAppClassLoader) context.getClassLoader()).addClassPath(url.getFile());
                }
            }
        }

        // Look for extra resource
        @SuppressWarnings("unchecked")
        Set<Resource> resources = (Set<Resource>) context.getAttribute(RESOURCE_DIRS);
        if (resources != null && !resources.isEmpty()) {
            Resource[] collection = new Resource[resources.size() + 1];
            int i = 0;
            collection[i++] = context.getBaseResource();
            for (Resource resource : resources)
                collection[i++] = resource;
            context.setBaseResource(new ResourceCollection(collection));
        }
    }

    @Override
    protected List<Resource> findWebInfLibJars(WebAppContext context) throws Exception {
        Resource web_inf = context.getWebInf();
        if (web_inf != null && web_inf.exists()) {
            Resource web_inf_lib = web_inf.addPath("/lib");
            if (web_inf_lib.exists() && web_inf_lib.isDirectory()) {
                return super.findWebInfLibJars(context);
            }
        }

        List<Resource> jarResources = new ArrayList<Resource>();
        for (URL url : IdeaClasspath.allJarsWithoutIdeas()) {
            try {
                Resource file = Resource.newResource(url);
                String fnlc = file.getName().toLowerCase(Locale.ENGLISH);
                int dot = fnlc.lastIndexOf('.');
                String extension = (dot < 0 ? null : fnlc.substring(dot));
                if (extension != null && (extension.equals(".jar") || extension.equals(".zip"))) {
                    jarResources.add(file);
                }
            } catch (Exception ex) {
                LOG.warn(Log.EXCEPTION, ex);
            }
        }
        return jarResources;
    }
}
