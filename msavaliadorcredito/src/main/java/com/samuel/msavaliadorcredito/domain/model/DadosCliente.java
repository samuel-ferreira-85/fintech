package com.samuel.msavaliadorcredito.domain.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DadosCliente {
    private Long id;
    private String nome;
    private String cpf;
    private Integer idade;
}
