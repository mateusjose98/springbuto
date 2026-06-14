package io.mateusjose98.web;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

import com.google.gson.Gson;

import io.mateusjose98.logger.CustomLogger;
import io.mateusjose98.structures.ControllersIntances;
import io.mateusjose98.structures.ControllersMap;
import io.mateusjose98.structures.RequestControllerData;
import io.mateusjose98.structures.ServiceImplementationMap;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DespachadorServlet extends HttpServlet {

  List<String> ignored = List.of("/favicon.ico");

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse res) {
    if (ignored.stream().anyMatch(req.getRequestURI()::startsWith)) {
      return;
    }
    String uri = req.getRequestURI().toUpperCase();
    String method = req.getMethod();
    String key = method + " " + uri;
    RequestControllerData requestData = ControllersMap.controllersMap.get(key);
    CustomLogger.info(
        "Received request: " + key + " handler " + (requestData != null ? requestData.getControllerClass() : "null"));

    Object controller;
    try {

      controller = ControllersIntances.controllersInstances.get(requestData.getControllerClass());
      if (controller == null) {
        controller = Class.forName(requestData.getControllerClass()).getDeclaredConstructor().newInstance();
        ControllersIntances.controllersInstances.put(requestData.getControllerClass(), controller);

      }

      injectDependencies(controller);

      Method m = null;

      for (Method methodController : controller.getClass().getDeclaredMethods()) {
        if (methodController.getName().equals(requestData.getControllerMethod())) {
          m = methodController;
          break;
        }
      }

      if (m == null) {
        CustomLogger.error("Método " + requestData.getControllerMethod() + " não encontrado na classe "
            + requestData.getControllerClass());
        res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return;
      }
      Gson gson = new Gson();
      PrintWriter writer = new PrintWriter(res.getWriter());

      if (m.getParameterCount() > 0) {
        Object arg;
        Parameter parameter = m.getParameters()[0];
        if (parameter.getAnnotations().length > 0 && parameter.getAnnotations()[0].annotationType().getName()
            .equals("io.mateusjose98.annotations.CBODY")) {
          CustomLogger.info("Parâmetro do método " + m.getName() + " na classe " + controller.getClass().getName()
              + " possui a anotação @CRequestBody. O corpo da requisição será mapeado para o tipo "
              + parameter.getType().getName());
          arg = gson.fromJson(readBytesFromRequest(req), parameter.getType());
          writer.println(gson.toJson(m.invoke(controller, arg)));
        } else {
          CustomLogger.warn("O parâmetro do método " + m.getName() + " na classe " + controller.getClass().getName()
              + " não possui a anotação @CBODY. O corpo da requisição será ignorado.");

        }
      } else {
        CustomLogger.warn("O parâmetro do método " + m.getName() + " na classe " + controller.getClass().getName()
            + " não possui a anotação @CBODY. O corpo da requisição será ignorado.");
        writer.println(gson.toJson(m.invoke(controller)));
      }

      writer.flush();
      writer.close();

    } catch (Exception e) {
      CustomLogger.error("Erro ao instanciar controlador: " + e.getMessage());
      e.printStackTrace();
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return;
    }
  }

  private void injectDependencies(Object controller) {
    for (var field : controller.getClass().getDeclaredFields()) {
      if (field.getAnnotations().length > 0 && field.getAnnotations()[0].annotationType().getName()
          .equals("io.mateusjose98.annotations.CINJECTED")) {
        CustomLogger.info("Campo " + field.getName() + " da classe " + controller.getClass().getName()
            + " possui a anotação @CINJECTED. Será injetada uma instância do serviço "
            + field.getType().getName());
        String implementationClass = ServiceImplementationMap.implementations.get(field.getType().getName());
        if (implementationClass == null) {
          CustomLogger.error("Nenhuma implementação encontrada para o serviço " + field.getType().getName()
              + ". Certifique-se de que existe uma classe anotada com @CService que implemente essa interface.");
          continue;
        }
        try {
          Object serviceInstance = ControllersIntances.controllersInstances.get(implementationClass);
          if (serviceInstance == null) {
            serviceInstance = Class.forName(implementationClass).getDeclaredConstructor().newInstance();
            ControllersIntances.controllersInstances.put(implementationClass, serviceInstance);
            injectDependencies(serviceInstance);
          }
          field.setAccessible(true);
          field.set(controller, serviceInstance);
        } catch (Exception e) {
          CustomLogger.error("Erro ao instanciar serviço: " + e.getMessage());
          e.printStackTrace();
        }
      }
    }
  }

  private String readBytesFromRequest(HttpServletRequest req) {
    StringBuilder stringBuilder = new StringBuilder();
    try (var reader = req.getReader()) {
      String line;
      while ((line = reader.readLine()) != null) {
        stringBuilder.append(line);
      }
    } catch (Exception e) {
      CustomLogger.error("Erro ao ler corpo da requisição: " + e.getMessage());
      e.printStackTrace();
    }
    return stringBuilder.toString();
  }
}
