package io.mateusjose98;

import io.mateusjose98.annotations.CService;

@CService
public class ServiceImpl implements IService {
  @Override
  public String hello(String name) {
    return "Hello, " + name + "!";
  }
}