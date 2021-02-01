package br.gov.es.openpmo;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import br.gov.es.openpmo.configuration.ResourceExceptionHandler;
import br.gov.es.openpmo.dto.ErroDto;
import br.gov.es.openpmo.exception.AutenticacaoException;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.utils.ApplicationMessage;

@SpringBootTest
public class ResourceExceptionHandlerTest {



    @Autowired
    private ResourceExceptionHandler resourceExceptionHandler;

    @Test
    public void shouldHandleException() {
        ErroDto erroDto = resourceExceptionHandler.handle(new Exception("Exception"));
        Assertions.assertNotNull(erroDto);
        Assertions.assertEquals(erroDto.getErro(), ApplicationMessage.ERRO_NEGOCIO + "$Exception");

        erroDto = resourceExceptionHandler.handle(new IllegalArgumentException("IllegalArgumentException"));
        Assertions.assertNotNull(erroDto);
        Assertions.assertEquals(erroDto.getErro(), "IllegalArgumentException");

        erroDto = resourceExceptionHandler.handle(new NegocioException("NegocioException"));
        Assertions.assertNotNull(erroDto);
        Assertions.assertEquals(erroDto.getErro(), "NegocioException");

        erroDto = resourceExceptionHandler.handle(new RegistroNaoEncontradoException("RegistroNaoEncontradoException"));
        Assertions.assertNotNull(erroDto);
        Assertions.assertEquals(erroDto.getErro(), "RegistroNaoEncontradoException");

        erroDto = resourceExceptionHandler.handle(new AutenticacaoException("AutenticacaoException"));
        Assertions.assertNotNull(erroDto);
        Assertions.assertEquals(erroDto.getErro(), "AutenticacaoException");

        erroDto = resourceExceptionHandler.handle(new NullPointerException("NullPointerException"));
        Assertions.assertNotNull(erroDto);
        Assertions.assertEquals(erroDto.getErro(), ApplicationMessage.ERRO_NEGOCIO);

        erroDto = resourceExceptionHandler.handle(new RuntimeException("RuntimeException"));
        Assertions.assertNotNull(erroDto);
        Assertions.assertEquals(erroDto.getErro(), ApplicationMessage.ERRO_NEGOCIO + "$RuntimeException");

        erroDto = resourceExceptionHandler.handle(new IOException("IOException"));
        Assertions.assertNotNull(erroDto);
        Assertions.assertEquals(erroDto.getErro(), ApplicationMessage.ERRO_NEGOCIO + "$IOException");

    }
}
