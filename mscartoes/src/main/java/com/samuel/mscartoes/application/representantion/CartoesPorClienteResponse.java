package com.samuel.mscartoes.application.representantion;

import com.samuel.mscartoes.domain.ClienteCartao;

import java.math.BigDecimal;

public record CartoesPorClienteResponse(String nome, String bandeira, BigDecimal limite) {
    public CartoesPorClienteResponse (ClienteCartao model) {
        this (model.getCartao().getNome(), model.getCartao().getBandeira().toString(), model.getLimite());
    }
}
