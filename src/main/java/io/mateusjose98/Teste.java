package io.mateusjose98;

import io.mateusjose98.annotations.CBODY;
import io.mateusjose98.annotations.CController;
import io.mateusjose98.annotations.CGET;
import io.mateusjose98.annotations.CPOST;

@CController
public class Teste {

  @CGET("/teste")
  public String teste() {
    return "Teste";
  }

  @CPOST("/teste2")
  public String teste2(@CBODY String body) {
    return "Teste2";
  }

}
