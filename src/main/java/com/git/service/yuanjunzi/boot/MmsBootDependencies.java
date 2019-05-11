package com.git.service.yuanjunzi.boot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by yuanjunzi on 2019/5/10.
 */
public class MmsBootDependencies {
    public MmsBootDependencies() {
    }

    public static Set<Dependency> getMmsBootDependencies() {
        InputStream dependencies;
        BufferedReader dependenciesReader = null;

        try {
            dependencies = MmsBootDependencies.class.getResourceAsStream("/dependencies.txt");
            if (dependencies == null) {
                throw new NullPointerException("无法找到dependencies.txt!");
            } else {
                Set<Dependency> jettyDependencies = new HashSet<>();
                dependenciesReader = new BufferedReader(new InputStreamReader(dependencies));

                for (String line = dependenciesReader.readLine(); line != null; line = dependenciesReader.readLine()) {
                    String[] jettyDependencyDetail = line.split(":");
                    jettyDependencies.add(new Dependency(jettyDependencyDetail[0], jettyDependencyDetail[1], jettyDependencyDetail[2], jettyDependencyDetail[3], jettyDependencyDetail[4]));
                }

                Set<Dependency> var14 = jettyDependencies;
                return var14;
            }
        } catch (IOException var12) {
            throw new RuntimeException(var12);
        } finally {
            if (dependenciesReader != null) {
                try {
                    dependenciesReader.close();
                } catch (IOException var11) {
                    throw new RuntimeException(var11);
                }
            }

        }
    }

    public static Set<String> getMmsBootJarNames() {
        Set<String> jettyJarNames = new HashSet<>();

        for (Dependency dependency : getMmsBootDependencies()) {
            jettyJarNames.add(dependency.getJarName());
        }

        return jettyJarNames;
    }

    private static class Dependency {
        private String groupId;
        private String artifactId;
        private String packaging;
        private String version;
        private String scope;

        public Dependency(String groupId, String artifactId, String packaging, String version, String scope) {
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.version = version;
            this.packaging = packaging;
            this.scope = scope;
        }

        public String getJarName() {
            return String.format("%s-%s.%s", this.artifactId, this.version, this.packaging);
        }

        public String toString() {
            return String.format("%s:%s:%s:%s:%s", this.groupId, this.artifactId, this.packaging, this.version, this.packaging);
        }
    }
}
