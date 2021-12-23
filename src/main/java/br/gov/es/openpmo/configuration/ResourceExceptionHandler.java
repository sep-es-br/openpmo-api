package br.gov.es.openpmo.configuration;

import br.gov.es.openpmo.dto.ErroDto;
import br.gov.es.openpmo.dto.FormValidationErrorDto;
import br.gov.es.openpmo.exception.AutenticacaoException;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ResourceExceptionHandler {

  @Autowired
  private Logger log;

  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public List<FormValidationErrorDto> handle(final MethodArgumentNotValidException exception) {
    final List<FormValidationErrorDto> errors = new ArrayList<>();
    final List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();

    fieldErrors.forEach(error -> {
      final String message = error.getDefaultMessage();
      final FormValidationErrorDto formValidationErrorDto = new FormValidationErrorDto(error.getField(), message);

      errors.add(formValidationErrorDto);
    });

    return errors;
  }

  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(IllegalArgumentException.class)
  public ErroDto handle(final IllegalArgumentException exception) {
    return new ErroDto(exception.getMessage());
  }

  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(NegocioException.class)
  public ErroDto handle(final NegocioException exception) {
    this.log.error("Error NegocioException", exception);
    return new ErroDto(exception.getMessage());
  }

  @ResponseStatus(code = HttpStatus.NOT_FOUND)
  @ExceptionHandler(RegistroNaoEncontradoException.class)
  public ErroDto handle(final RegistroNaoEncontradoException exception) {
    return new ErroDto(exception.getMessage());
  }

  @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(AutenticacaoException.class)
  public ErroDto handle(final AutenticacaoException exception) {
    return new ErroDto(exception.getMessage());
  }

  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(NullPointerException.class)
  public ErroDto handle(final NullPointerException exception) {
    this.log.error("Error NullPointerException", exception);
    return new ErroDto(ApplicationMessage.ERRO_NEGOCIO);
  }

  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(RuntimeException.class)
  public ErroDto handle(final RuntimeException exception) {
    this.log.error("Error RuntimeException", exception);
    return new ErroDto(ApplicationMessage.ERRO_NEGOCIO + "$" + exception.getMessage());
  }

  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(IOException.class)
  public ErroDto handle(final IOException exception) {
    this.log.error("Error IOException", exception);
    return new ErroDto(ApplicationMessage.ERRO_NEGOCIO + "$" + exception.getMessage());
  }

  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public ErroDto handle(final Exception exception) {
    this.log.error("Error Exception", exception);
    return new ErroDto(ApplicationMessage.ERRO_NEGOCIO + "$" + exception.getMessage());
  }

}
