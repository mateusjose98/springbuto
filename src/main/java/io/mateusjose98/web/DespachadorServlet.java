package io.mateusjose98.web;

import java.util.List;

import io.mateusjose98.logger.CustomLogger;
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
    String path = req.getRequestURI();
    CustomLogger.info("Requisição recebida: " + path);
  }
}
