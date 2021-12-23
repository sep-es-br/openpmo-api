package br.gov.es.openpmo.integration;

import br.gov.es.openpmo.configuration.ResourceExceptionHandler;
import br.gov.es.openpmo.dto.ErroDto;
import br.gov.es.openpmo.exception.AutenticacaoException;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

@SpringBootTest class ResourceExceptionHandlerTest {


  @Autowired
  private ResourceExceptionHandler resourceExceptionHandler;

  @Test void shouldHandleException() {
    ErroDto erroDto = this.resourceExceptionHandler.handle(new Exception("Exception"));
    assertNotNull(erroDto);
    assertEquals(ApplicationMessage.ERRO_NEGOCIO + "$Exception", erroDto.getErro());

    erroDto = this.resourceExceptionHandler.handle(new IllegalArgumentException("IllegalArgumentException"));
    assertNotNull(erroDto);
    assertEquals("IllegalArgumentException", erroDto.getErro());

    erroDto = this.resourceExceptionHandler.handle(new NegocioException("NegocioException"));
    assertNotNull(erroDto);
    assertEquals("NegocioException", erroDto.getErro());

    erroDto = this.resourceExceptionHandler.handle(new RegistroNaoEncontradoException("RegistroNaoEncontradoException"));
    assertNotNull(erroDto);
    assertEquals("RegistroNaoEncontradoException", erroDto.getErro());

    erroDto = this.resourceExceptionHandler.handle(new AutenticacaoException("AutenticacaoException"));
    assertNotNull(erroDto);
    assertEquals("AutenticacaoException", erroDto.getErro());

    erroDto = this.resourceExceptionHandler.handle(new NullPointerException("NullPointerException"));
    assertNotNull(erroDto);
    assertEquals(ApplicationMessage.ERRO_NEGOCIO, erroDto.getErro());

    erroDto = this.resourceExceptionHandler.handle(new RuntimeException("RuntimeException"));
    assertNotNull(erroDto);
    assertEquals(erroDto.getErro(), ApplicationMessage.ERRO_NEGOCIO + "$RuntimeException");

    erroDto = this.resourceExceptionHandler.handle(new IOException("IOException"));
    assertNotNull(erroDto);
    assertEquals(erroDto.getErro(), ApplicationMessage.ERRO_NEGOCIO + "$IOException");

  }
}
