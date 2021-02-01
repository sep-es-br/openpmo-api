package br.gov.es.openpmo.exception;

import br.gov.es.openpmo.utils.ApplicationMessage;

public class AutenticacaoException extends RuntimeException {

    private static final long serialVersionUID = -8284042860476981373L;
    private static final String AUTHENTICATION = ApplicationMessage.INVALID_TOKEN;

    public AutenticacaoException() {
        super(AUTHENTICATION);
    }

    public AutenticacaoException(String mensagem) {
        super(mensagem);
    }
}
