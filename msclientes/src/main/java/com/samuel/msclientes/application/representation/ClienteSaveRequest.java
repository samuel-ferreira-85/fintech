package com.samuel.msclientes.application.representation;

import com.samuel.msclientes.domain.Cliente;

public record ClienteSaveRequest(String cpf, String nome, Integer idade) {
    public Cliente toModel() {
        return new Cliente(cpf(), nome(), idade());
    }
}
