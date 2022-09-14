package br.gov.es.openpmo.exception;

public class CannotAccessResourceException extends RuntimeException {

  public CannotAccessResourceException() {
  }

  public CannotAccessResourceException(final String message) {
    super(message);
  }

  public CannotAccessResourceException(
    final String message,
    final Throwable cause
  ) {
    super(message, cause);
  }

}
