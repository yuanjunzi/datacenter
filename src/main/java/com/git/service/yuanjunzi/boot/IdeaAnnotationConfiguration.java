package com.git.service.yuanjunzi.boot;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.annotations.AnnotationParser;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.statistic.CounterStatistic;
import org.eclipse.jetty.webapp.FragmentDescriptor;
import org.eclipse.jetty.webapp.WebAppContext;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by yuanjunzi on 2019/5/10.
 */
public class IdeaAnnotationConfiguration extends AnnotationConfiguration {
    @Override
    public void parseWebInfLib(final WebAppContext context, AnnotationParser parser) throws Exception {
        List<FragmentDescriptor> frags = context.getMetaData().getFragments();

        //email from Rajiv Mordani jsrs 315 7 April 2010
        //jars that do not have a web-fragment.xml are still considered fragments
        //they have to participate in the ordering
        ArrayList<URI> webInfUris = new ArrayList<URI>();

        List<Resource> jars = null;

        if (context.getMetaData().getOrdering() != null)
            jars = context.getMetaData().getOrderedWebInfJars();
        else
            //No ordering just use the jars in any order
            jars = context.getMetaData().getWebInfJars();

        if (jars != null && jars.size() > 0) {
            super.parseWebInfLib(context, parser);
        } else {
            _webInfLibStats = new CounterStatistic();
            for (URL jar : IdeaClasspath.allJarsWithoutIdeas()) {
                Resource jarResource = Resource.newResource(jar);
                //for each jar, we decide which set of annotations we need to parse for
                final Set<AnnotationParser.Handler> handlers = new HashSet<AnnotationParser.Handler>();

                FragmentDescriptor f = getFragmentFromJar(jarResource, frags);

                //if its from a fragment jar that is metadata complete, we should skip scanning for @webservlet etc
                // but yet we still need to do the scanning for the classes on behalf of  the servletcontainerinitializers
                //if a jar has no web-fragment.xml we scan it (because it is not excluded by the ordering)
                //or if it has a fragment we scan it if it is not metadata complete
                if (f == null || !isMetaDataComplete(f) || _classInheritanceHandler != null || !_containerInitializerAnnotationHandlers.isEmpty()) {
                    //register the classinheritance handler if there is one
                    if (_classInheritanceHandler != null)
                        handlers.add(_classInheritanceHandler);

                    //register the handlers for the @HandlesTypes values that are themselves annotations if there are any
                    handlers.addAll(_containerInitializerAnnotationHandlers);

                    //only register the discoverable annotation handlers if this fragment is not metadata complete, or has no fragment descriptor
                    if (f == null || !isMetaDataComplete(f))
                        handlers.addAll(_discoverableAnnotationHandlers);

                    addParserTasks(parser, handlers, jarResource, _webInfLibStats);
                }
            }
        }
    }

    protected void addParserTasks(AnnotationParser parser, Set<? extends AnnotationParser.Handler> handlers, Resource resource, CounterStatistic counterStatistic) {
        if (_parserTasks != null) {
            ParserTask task = new ParserTask(parser, handlers, resource);
            _parserTasks.add(task);
            counterStatistic.increment();
        }
    }

    @Override
    public void parseWebInfClasses(final WebAppContext context, AnnotationParser parser) throws Exception {
        List<Resource> resources = context.getMetaData().getWebInfClassesDirs();
        if (resources != null && !resources.isEmpty()) {
            super.parseWebInfClasses(context, parser);
        } else {
            Set<AnnotationParser.Handler> handlers = new HashSet<AnnotationParser.Handler>();
            handlers.addAll(_discoverableAnnotationHandlers);
            if (_classInheritanceHandler != null)
                handlers.add(_classInheritanceHandler);
            handlers.addAll(_containerInitializerAnnotationHandlers);
            _webInfClassesStats = new CounterStatistic();
            Resource dir = Resource.newResource(ClassLoader.getSystemClassLoader().getResource(""));
            addParserTasks(parser, handlers, dir, _webInfClassesStats);
        }
    }
}
