package com.samuel.msavaliadorcredito.domain.model;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Cartao {
    private Long id;
    private String nome;
    private String bandeira;
    private BigDecimal limite;

}
