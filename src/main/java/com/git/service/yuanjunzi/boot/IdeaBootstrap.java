package com.git.service.yuanjunzi.boot;

/**
 * Created by yuanjunzi on 2019/5/10.
 */
public class IdeaBootstrap extends AbstractIdeaBootstrap {
    public IdeaBootstrap() {
        super("jetty9_idea.xml");
    }

    public static void main(String[] args) throws Exception {
        IdeaBootstrap bootstrap = new IdeaBootstrap();
        bootstrap.initEnv();
        bootstrap.boot();
    }
}
