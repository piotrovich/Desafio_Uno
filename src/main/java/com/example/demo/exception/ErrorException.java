package com.example.demo.exception;

public class ErrorException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final String codigo;

    public ErrorException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return this.codigo;
    }

    public String getMensaje() {
        return this.getMessage();
    }
}
