package com.raviraj.simplemoneytransfer.service;

import java.util.logging.Level;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Startup {

	private static final Logger LOGGER = LoggerFactory.getLogger(Startup.class);
	
    public static void main(String[] args) {

    	
        Server server = new Server(5555);

        ServletContextHandler ctx = 
                new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
                
        ctx.setContextPath("/");
        server.setHandler(ctx);

        ServletHolder serHol = ctx.addServlet(ServletContainer.class, "/moneytransfer/*");
        serHol.setInitOrder(1);
        serHol.setInitParameter("jersey.config.server.provider.packages", 
                "com.raviraj.simplemoneytransfer.service");

        try {
            server.start();
            LOGGER.info("server started");
            server.join();
        } catch (Exception ex) {
        	ex.printStackTrace();
            LOGGER.error("Error starting server " , ex);
        } finally {
        	server.destroy();
        }
    }
}