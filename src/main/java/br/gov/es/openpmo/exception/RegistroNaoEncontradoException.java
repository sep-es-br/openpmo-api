package br.gov.es.openpmo.exception;

import br.gov.es.openpmo.utils.ApplicationMessage;

public class RegistroNaoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 3099562554978368818L;
    private static final String NOT_FOUND = ApplicationMessage.REGISTRO_NOT_FOUND;

    public RegistroNaoEncontradoException() {
        super(NOT_FOUND);
    }

    public RegistroNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
