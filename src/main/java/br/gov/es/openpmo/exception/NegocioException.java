package br.gov.es.openpmo.exception;

import br.gov.es.openpmo.utils.ApplicationMessage;

public class NegocioException extends RuntimeException {

  private static final long serialVersionUID = 3228949821710114160L;
  private static final String NEGOCIO = ApplicationMessage.ERRO_NEGOCIO;

  public NegocioException() {
    super(NEGOCIO);
  }

  public NegocioException(final String mensagem) {
    super(mensagem);
  }


  public NegocioException(
    final String message,
    final Throwable cause
  ) {
    super(message, cause);
  }

}
