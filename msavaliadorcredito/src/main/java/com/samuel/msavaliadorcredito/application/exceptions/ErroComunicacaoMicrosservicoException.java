package com.samuel.msavaliadorcredito.application.exceptions;

import lombok.Getter;

public class ErroComunicacaoMicrosservicoException extends Exception {
    @Getter
    public Integer status;
    public ErroComunicacaoMicrosservicoException(String message, Integer status) {
        super(message);
        this.status = status;
    }
}
