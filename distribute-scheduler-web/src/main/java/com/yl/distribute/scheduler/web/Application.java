package com.yl.distribute.scheduler.web;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.servlet.ServletContainer;
//import org.jboss.weld.environment.servlet.Listener;
import javax.servlet.ServletException;
import static io.undertow.servlet.Servlets.listener;
import org.springframework.web.context.ContextLoaderListener ;
/**
 * Application entry point.
 *
 */
public class Application {

    private static Undertow server;

    private static DeploymentManager deploymentManager;

    private static final int DEFAULT_PORT = 8085;

    private static final Log LOG = LogFactory.getLog(Application.class);

    /**
     * Start server on the port 8085.
     *
     * @param args
     */
    public static void main(final String[] args) {
        startServer(DEFAULT_PORT);
    }

    /**
     * Start server on the given port.
     *
     * @param port
     */
    public static void startServer(int port) {

        LOG.info(String.format("Starting server on port %d", port));

        PathHandler path = Handlers.path();

        server = Undertow.builder()
                .addHttpListener(port, "localhost")
                .setHandler(path)
                .build();

        server.start();

        LOG.info(String.format("Server started on port %d", port));

        DeploymentInfo servletBuilder = Servlets.deployment()
                .setClassLoader(Application.class.getClassLoader())
                .setContextPath("/")
                //添加spring listener
                .addListeners(listener(ContextLoaderListener.class))
                .setResourceManager(new ClassPathResourceManager(Application.class.getClassLoader()))
                .addServlets(
                        Servlets.servlet("jerseyServlet", ServletContainer.class)
                                .setLoadOnStartup(1)
                                .addInitParam("javax.ws.rs.Application", JerseyConfig.class.getName())
                                .addMapping("/api/*"))
                .setDeploymentName("application.war");

        LOG.info("Starting application deployment");

        deploymentManager = Servlets.defaultContainer().addDeployment(servletBuilder);
        deploymentManager.deploy();

        try {
            path.addPrefixPath("/", deploymentManager.start());
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        LOG.info("Application deployed");
    }

    /**
     * Stop server.
     */
    public static void stopServer() {

        if (server == null) {
            throw new IllegalStateException("Server has not been started yet");
        }

        LOG.info("Stopping server");

        deploymentManager.undeploy();
        server.stop();

        LOG.info("Server stopped");
    }
}