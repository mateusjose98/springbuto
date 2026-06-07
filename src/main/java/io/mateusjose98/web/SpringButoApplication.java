package io.mateusjose98.web;

import java.io.File;
import java.util.List;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import io.mateusjose98.explorer.ClassExplorer;
import io.mateusjose98.logger.CustomLogger;

public class SpringButoApplication {
  public static void run(Class<?> sourceClass) {
    ClassExplorer.retrieveAllClasses(sourceClass).forEach(CustomLogger::info);

    try {
      java.util.logging.Logger.getLogger("org.apache").setLevel(java.util.logging.Level.SEVERE);
      long inicio, fim;
      inicio = System.currentTimeMillis();
      Tomcat tomcat = new Tomcat();
      Connector connector = new Connector();
      connector.setPort(8080);
      tomcat.getService().addConnector(connector);
      Context context = tomcat.addContext("", new File(".").getAbsolutePath());
      Tomcat.addServlet(context, "dispatcher", new DespachadorServlet());
      context.addServletMappingDecoded("/*", "dispatcher");
      tomcat.start();
      fim = System.currentTimeMillis();
      CustomLogger.info("Servidor iniciado em " + (fim - inicio) + " ms");
      tomcat.getServer().await();
    } catch (Exception e) {
      CustomLogger.error("Erro ao iniciar o servidor: " + e.getMessage());
      e.printStackTrace();
    }
  }
}