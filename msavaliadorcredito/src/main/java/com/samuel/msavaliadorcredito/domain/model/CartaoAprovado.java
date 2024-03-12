package com.samuel.msavaliadorcredito.domain.model;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartaoAprovado {
    private String cartao;
    private String bandeira;
    private BigDecimal limite;
}
