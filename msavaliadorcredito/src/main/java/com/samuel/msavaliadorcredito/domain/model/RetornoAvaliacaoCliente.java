package com.samuel.msavaliadorcredito.domain.model;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RetornoAvaliacaoCliente {
    private List<CartaoAprovado> cartoes;

}
