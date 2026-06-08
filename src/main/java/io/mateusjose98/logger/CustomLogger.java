package io.mateusjose98.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomLogger {

  public static final String GREEN = "\u001B[32m";
  public static final String RED = "\u001B[31m";
  public static final String YELLOW = "\u001B[33m";
  public static final String RESET = "\u001B[0m";
  public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

  public static void info(String message) {
    LocalDateTime now = LocalDateTime.now();
    String threadName = Thread.currentThread().getName();
    System.out.println(GREEN + "[INFO] " + formatter.format(now) + " [" + threadName + "] - " + message + RESET);
  }

  public static void warn(String message) {
    LocalDateTime now = LocalDateTime.now();
    String threadName = Thread.currentThread().getName();
    System.out.println(YELLOW + "[WARN] " + formatter.format(now) + " [" + threadName + "] - " + message + RESET);
  }

  public static void error(String message) {
    LocalDateTime now = LocalDateTime.now();
    String threadName = Thread.currentThread().getName();
    System.out.println(RED + "[ERROR] " + formatter.format(now) + " [" + threadName + "] - " + message + RESET);
  }

  public static void debug(String message) {
    LocalDateTime now = LocalDateTime.now();
    String threadName = Thread.currentThread().getName();
    System.out.println("[DEBUG] " + formatter.format(now) + " [" + threadName + "] - " + message);
  }

}
