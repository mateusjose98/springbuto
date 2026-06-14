package io.mateusjose98;

import io.mateusjose98.annotations.CBODY;
import io.mateusjose98.annotations.CController;
import io.mateusjose98.annotations.CGET;
import io.mateusjose98.annotations.CINJECTED;
import io.mateusjose98.annotations.CPOST;

@CController
public class Teste {

  @CINJECTED
  private IService service;

  @CGET("/all")
  public Produto listar() {
    Produto p = new Produto();
    p.setId(1);
    p.setNome("Produto 1");
    return p;
  }

  @CPOST("/create")
  public Produto criar(@CBODY Produto produto) {
    return produto;
  }

  @CGET("/service")
  public String testeService() {
    return service.hello("World");
  }

}
