package com.samuel.mscartoes.application;

import com.samuel.mscartoes.domain.ClienteCartao;
import com.samuel.mscartoes.infra.repository.ClienteCartaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteCartaoService {

    private final ClienteCartaoRepository clienteCartaoRepository;

    public List<ClienteCartao> listarClienteCartaoByCpf(String cpf) {
        return clienteCartaoRepository.findByCpf(cpf);
    }

}
