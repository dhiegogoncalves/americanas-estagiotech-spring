package br.com.americanas.estagiotech.libraryapi.api.exceptions;

public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
