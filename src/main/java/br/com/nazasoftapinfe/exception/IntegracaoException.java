package br.com.nazasoftapinfe.exception;

public class IntegracaoException extends RuntimeException{

    public IntegracaoException(String message) {
        super(message);
    }

    public IntegracaoException(String message,Throwable cause) {
        super(message,cause);
    }
}
