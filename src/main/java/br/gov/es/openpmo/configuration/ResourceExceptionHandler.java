package br.gov.es.openpmo.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.gov.es.openpmo.dto.ErroDto;
import br.gov.es.openpmo.dto.FormValidationErrorDto;
import br.gov.es.openpmo.exception.AutenticacaoException;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.utils.ApplicationMessage;

@RestControllerAdvice
public class ResourceExceptionHandler {

	@Autowired
	private Logger log;
	
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<FormValidationErrorDto> handle(MethodArgumentNotValidException exception) {
        List<FormValidationErrorDto> errors = new ArrayList<>();
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();

        fieldErrors.forEach(error -> {
            String message = error.getDefaultMessage();
            FormValidationErrorDto formValidationErrorDto = new FormValidationErrorDto(error.getField(), message);

            errors.add(formValidationErrorDto);
        });

        return errors;
    }

	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(IllegalArgumentException.class)
	public ErroDto handle(IllegalArgumentException exception) {
		return new ErroDto(exception.getMessage());
	}

	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(NegocioException.class)
	public ErroDto handle(NegocioException exception) {
		return new ErroDto(exception.getMessage());
	}

	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	@ExceptionHandler(RegistroNaoEncontradoException.class)
	public ErroDto handle(RegistroNaoEncontradoException exception) {
		return new ErroDto(exception.getMessage());
	}

	@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(AutenticacaoException.class)
	public ErroDto handle(AutenticacaoException exception) {
		return new ErroDto(exception.getMessage());
	}

	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(NullPointerException.class)
	public ErroDto handle(NullPointerException exception) {
		log.error("Error NullPointerException", exception);
		return new ErroDto(ApplicationMessage.ERRO_NEGOCIO);
	}

	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(RuntimeException.class)
	public ErroDto handle(RuntimeException exception) {
		log.error("Error RuntimeException", exception);
		return new ErroDto(ApplicationMessage.ERRO_NEGOCIO + "$" + exception.getMessage());
	}

	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(IOException.class)
	public ErroDto handle(IOException exception) {
		log.error("Error IOException", exception);
		return new ErroDto(ApplicationMessage.ERRO_NEGOCIO + "$" + exception.getMessage());
	}

	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public ErroDto handle(Exception exception) {
		log.error("Error Exception", exception);
		return new ErroDto(ApplicationMessage.ERRO_NEGOCIO + "$" + exception.getMessage());
	}

}
