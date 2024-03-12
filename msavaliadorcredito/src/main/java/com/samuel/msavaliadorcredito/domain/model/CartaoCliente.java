package com.samuel.msavaliadorcredito.domain.model;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartaoCliente {
    private String nome;
    private String bandeira;
    private BigDecimal limite;

    public CartaoCliente(Cartao cartao) {
        nome = cartao.getNome();
        bandeira = cartao.getBandeira();
        limite = cartao.getLimite();
    }
}
