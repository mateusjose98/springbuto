package io.mateusjose98.web;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import io.mateusjose98.annotations.CController;
import io.mateusjose98.explorer.ClassExplorer;
import io.mateusjose98.logger.CustomLogger;
import io.mateusjose98.structures.ControllersMap;
import io.mateusjose98.structures.RequestControllerData;
import io.mateusjose98.structures.ServiceImplementationMap;

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
        } else if (anotacao.annotationType().getName().equals("io.mateusjose98.annotations.CService")) {
          CustomLogger.info("Serviço encontrado: " + klass);

          for (Class<?> interfaceClass : Class.forName(klass).getInterfaces()) {
            CustomLogger.info("Registrando serviço: " + interfaceClass.getName() + " -> " + klass);
            ServiceImplementationMap.implementations.put(interfaceClass.getName(), klass);

          }

        }
      }
    }

  }

  private static void extractMethod(String className) throws ClassNotFoundException {
    for (Method method : Class.forName(className).getDeclaredMethods()) {
      for (Annotation anotacao : method.getAnnotations()) {
        if (anotacao.annotationType().getName().equals("io.mateusjose98.annotations.CGET")) {
          RequestControllerData requestControllerData = new RequestControllerData();
          String path = ((io.mateusjose98.annotations.CGET) anotacao).value();
          requestControllerData.setHttpMethod("GET");
          requestControllerData.setUrl(path);
          requestControllerData.setControllerClass(className);
          requestControllerData.setControllerMethod(method.getName());
          ControllersMap.controllersMap.put("GET " + path.toUpperCase(), requestControllerData);

        } else if (anotacao.annotationType().getName().equals("io.mateusjose98.annotations.CPOST")) {
          String path = ((io.mateusjose98.annotations.CPOST) anotacao).value();
          RequestControllerData requestControllerData = new RequestControllerData();
          requestControllerData.setHttpMethod("POST");
          requestControllerData.setUrl(path);
          requestControllerData.setControllerClass(className);
          requestControllerData.setControllerMethod(method.getName());
          ControllersMap.controllersMap.put("POST " + path.toUpperCase(), requestControllerData);
        }
      }
    }

    for (RequestControllerData data : ControllersMap.controllersMap.values()) {
      CustomLogger.info("Rota registrada: " + data.getHttpMethod() + " " + data.getUrl() + " -> "
          + data.getControllerClass() + "." + data.getControllerMethod());
    }
  }
}