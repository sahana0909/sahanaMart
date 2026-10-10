package com.sahana.sahanamart;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

/**
 * Embedded Tomcat 9 Server Runner.
 * Enables running and previewing the SahanaMart website instantly without installing standalone Tomcat.
 */
public class ServerRunner {

    public static void main(String[] args) throws Exception {
        String webappDirLocation = "src/main/webapp";
        Tomcat tomcat = new Tomcat();

        int port = 8080;
        String portEnv = System.getenv("PORT");
        if (portEnv != null && !portEnv.trim().isEmpty()) {
            try {
                port = Integer.parseInt(portEnv.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        tomcat.setPort(port);
        tomcat.getConnector();

        String baseDir = new File("target/tomcat-embedded").getAbsolutePath();
        tomcat.setBaseDir(baseDir);

        StandardContext ctx = (StandardContext) tomcat.addWebapp("/sahanamart", new File(webappDirLocation).getAbsolutePath());
        ctx.setParentClassLoader(ServerRunner.class.getClassLoader());
        File additionWebInfClasses = new File("target/classes");
        WebResourceRoot resources = new StandardRoot(ctx);
        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                additionWebInfClasses.getAbsolutePath(), "/"));
        ctx.setResources(resources);

        // Also register /SahanaMart for case-insensitive convenience
        StandardContext ctxUpper = (StandardContext) tomcat.addWebapp("/SahanaMart", new File(webappDirLocation).getAbsolutePath());
        ctxUpper.setParentClassLoader(ServerRunner.class.getClassLoader());
        WebResourceRoot resourcesUpper = new StandardRoot(ctxUpper);
        resourcesUpper.addPreResources(new DirResourceSet(resourcesUpper, "/WEB-INF/classes",
                additionWebInfClasses.getAbsolutePath(), "/"));
        ctxUpper.setResources(resourcesUpper);

        System.out.println("==================================================================");
        System.out.println("  SahanaMart E-Commerce Platform Server Starting...");
        System.out.println("  Home / Storefront:  http://localhost:" + port + "/sahanamart/");
        System.out.println("  Health Endpoint:    http://localhost:" + port + "/sahanamart/api/v1/health");
        System.out.println("==================================================================");

        tomcat.start();
        System.out.println(">> Server is UP! Open http://localhost:" + port + "/sahanamart/ in your browser.");
        tomcat.getServer().await();
    }
}
