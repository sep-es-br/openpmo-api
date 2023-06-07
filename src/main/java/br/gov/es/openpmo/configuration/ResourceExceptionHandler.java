package br.gov.es.openpmo.configuration;

import br.gov.es.openpmo.dto.ErroDto;
import br.gov.es.openpmo.dto.FormValidationErrorDto;
import br.gov.es.openpmo.exception.AutenticacaoException;
import br.gov.es.openpmo.exception.CannotAccessResourceException;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ResourceExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(ResourceExceptionHandler.class);

  private static FormValidationErrorDto getFormValidationErrorDto(final FieldError error) {
    final String message = error.getDefaultMessage();
    return new FormValidationErrorDto(error.getField(), message);
  }

  private static String getFormattedError(final Exception exception) {
    return MessageFormat.format("{0}${1}", ApplicationMessage.ERRO_NEGOCIO, exception.getMessage());
  }

  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public List<FormValidationErrorDto> handle(final MethodArgumentNotValidException exception) {
    return exception.getBindingResult().getFieldErrors().stream()
      .map(ResourceExceptionHandler::getFormValidationErrorDto)
      .collect(Collectors.toList());
  }

  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(IllegalArgumentException.class)
  public ErroDto handle(final IllegalArgumentException exception) {
    log.error("Error IllegalArgumentException", exception);
    return new ErroDto(exception.getMessage());
  }

  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(NegocioException.class)
  public ErroDto handle(final NegocioException exception) {
    log.error("Error NegocioException", exception);
    return new ErroDto(exception.getMessage());
  }

  @ResponseStatus(code = HttpStatus.NOT_FOUND)
  @ExceptionHandler(RegistroNaoEncontradoException.class)
  public ErroDto handle(final RegistroNaoEncontradoException exception) {
    log.error("Error RegistroNaoEncontradoException", exception);
    return new ErroDto(exception.getMessage());
  }

  @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(AutenticacaoException.class)
  public ErroDto handle(final AutenticacaoException exception) {
    log.error("Error AutenticacaoException", exception);
    return new ErroDto(exception.getMessage());
  }

  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(NullPointerException.class)
  public ErroDto handle(final NullPointerException exception) {
    log.error("Error NullPointerException", exception);
    return new ErroDto(ApplicationMessage.ERRO_NEGOCIO);
  }

  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(RuntimeException.class)
  public ErroDto handle(final RuntimeException exception) {
    log.error("Error RuntimeException", exception);
    return new ErroDto(getFormattedError(exception));
  }

  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(IOException.class)
  public ErroDto handle(final IOException exception) {
    log.error("Error IOException", exception);
    return new ErroDto(getFormattedError(exception));
  }

  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public ErroDto handle(final Exception exception) {
    log.error("Error Exception", exception);
    return new ErroDto(getFormattedError(exception));
  }


  @ResponseStatus(code = HttpStatus.FORBIDDEN)
  @ExceptionHandler(CannotAccessResourceException.class)
  public ErroDto handle(final CannotAccessResourceException exception) {
    log.error("Error Exception", exception);
    return new ErroDto(getFormattedError(exception));
  }

}
