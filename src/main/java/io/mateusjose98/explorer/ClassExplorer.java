package io.mateusjose98.explorer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ClassExplorer {

  public static List<String> retrieveAllClasses(Class<?> classes) {
    return packageExplorer(classes.getPackageName());
  }

  public static List<String> packageExplorer(String packageName) {
    List<String> names = new ArrayList<>();
    try {
      InputStream stream = ClassLoader
          .getSystemClassLoader()
          .getResourceAsStream(packageName.replaceAll("\\.", "/"));

      BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
      String linha;
      while ((linha = reader.readLine()) != null) {
        if (linha.endsWith(".class")) {
          names.add(packageName + "." + linha.substring(0, linha.lastIndexOf('.')));
        } else {
          names.addAll(packageExplorer(packageName + "." + linha));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return names;
  }
}
