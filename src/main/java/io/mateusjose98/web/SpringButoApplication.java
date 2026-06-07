package io.mateusjose98.web;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import io.mateusjose98.annotations.CController;
import io.mateusjose98.explorer.ClassExplorer;
import io.mateusjose98.logger.CustomLogger;

public class SpringButoApplication {
  public static void run(Class<?> sourceClass) {

    try {
      extractMetaData(sourceClass);
    } catch (ClassNotFoundException e) {
      CustomLogger.error("Erro ao extrair metadados: " + e.getMessage());
      e.printStackTrace();
    }

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

  private static void extractMetaData(Class<?> sourceClass) throws ClassNotFoundException {
    for (String klass : ClassExplorer.retrieveAllClasses(sourceClass)) {
      CustomLogger.info("Classe encontrada: " + klass);
      Annotation[] anotacoes = Class.forName(klass).getAnnotations();
      for (Annotation anotacao : anotacoes) {
        if (anotacao.annotationType().getName().equals(CController.class.getName())) {
          CustomLogger.info("Controlador encontrado: " + klass);
          extractMethod(klass);
        }
      }
    }
  }

  private static void extractMethod(String className) throws ClassNotFoundException {
    for (Method method : Class.forName(className).getDeclaredMethods()) {
      for (Annotation anotacao : method.getAnnotations()) {
        if (anotacao.annotationType().getName().equals("io.mateusjose98.annotations.CGET")) {
          CustomLogger.info("Método GET encontrado: " + method.getName());
        } else if (anotacao.annotationType().getName().equals("io.mateusjose98.annotations.CPOST")) {
          CustomLogger.info("Método POST encontrado: " + method.getName());
        } else if (anotacao.annotationType().getName().equals("io.mateusjose98.annotations.CPUT")) {
          CustomLogger.info("Método PUT encontrado: " + method.getName());
        } else if (anotacao.annotationType().getName().equals("io.mateusjose98.annotations.CDELETE")) {
          CustomLogger.info("Método DELETE encontrado: " + method.getName());
        }
      }
    }
  }
}