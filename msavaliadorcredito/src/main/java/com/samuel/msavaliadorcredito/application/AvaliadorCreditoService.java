package com.samuel.msavaliadorcredito.application;

import com.samuel.msavaliadorcredito.application.exceptions.DadosClienteNotFoundException;
import com.samuel.msavaliadorcredito.application.exceptions.ErroComunicacaoMicrosservicoException;
import com.samuel.msavaliadorcredito.application.exceptions.ErroSolicitacaoCartaoException;
import com.samuel.msavaliadorcredito.domain.model.*;
import com.samuel.msavaliadorcredito.infra.CartoesResourceClient;
import com.samuel.msavaliadorcredito.infra.ClienteResourceClient;
import com.samuel.msavaliadorcredito.infra.mqueue.SolicitacaoEmissaoCartaoPublisher;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvaliadorCreditoService {

    private final ClienteResourceClient clienteFeign;
    private final CartoesResourceClient cartoesFeign;
    private final SolicitacaoEmissaoCartaoPublisher publisher;

    public SituacaoCliente obterSituacaoCliente(String cpf)
            throws DadosClienteNotFoundException, ErroComunicacaoMicrosservicoException {
        try {
            ResponseEntity<DadosCliente> clienteResponse = clienteFeign.dadosCliente(cpf);
            ResponseEntity<List<CartaoCliente>> cartoesResponse = cartoesFeign.getCartoesByCliente(cpf);

            log.info("Lista de Cartoes Response: {}", cartoesResponse.getBody());

            var situacao = SituacaoCliente.builder()
                    .cliente(clienteResponse.getBody())
                    .cartoes(cartoesResponse.getBody())
                    .build();
            log.info("Situacao do cliente: {}", situacao);
            return situacao;
        } catch (FeignException.FeignClientException e) {
            int status = e.status();
            if (HttpStatus.NOT_FOUND.value() == status) {
                throw new DadosClienteNotFoundException();
            }
            throw new ErroComunicacaoMicrosservicoException(e.getMessage(), status);
        }
    }

    public RetornoAvaliacaoCliente realizarAvaliacao(String cpf, Long renda) throws DadosClienteNotFoundException,
            ErroComunicacaoMicrosservicoException {
        try {
            ResponseEntity<DadosCliente> dadosClienteResponse = clienteFeign.dadosCliente(cpf);
            log.info("DadosCliente Recebido: {}", dadosClienteResponse.getBody());
            ResponseEntity<List<CartaoCliente>> cartoesResponse = cartoesFeign.getCartoesRendaAte(renda);

            List<CartaoCliente> cartoesList = cartoesResponse.getBody();
            log.info("Lista de cartoes Recebido: {}", cartoesList);

            List<CartaoAprovado> cartoes = cartoesList.stream().map(cartao -> {
                DadosCliente dadosCliente = dadosClienteResponse.getBody();
                BigDecimal limiteBD = cartao.getLimite();
                BigDecimal idadeBD = BigDecimal.valueOf(dadosCliente.getIdade());

                BigDecimal fator = idadeBD.divide(BigDecimal.valueOf(10));
                BigDecimal limiteAprovado = fator.multiply(limiteBD);

                CartaoAprovado aprovado = new CartaoAprovado();
                aprovado.setCartao(cartao.getNome());
                aprovado.setBandeira(cartao.getBandeira());
                aprovado.setLimite(limiteAprovado);
                return aprovado;
            }).collect(Collectors.toList());
            return new RetornoAvaliacaoCliente(cartoes);

        } catch (FeignException.FeignClientException e) {
            int status = e.status();
            log.error("Erro no feign status: {}", status);
            if (HttpStatus.NOT_FOUND.value() == status) {
                throw new DadosClienteNotFoundException();
            }
            throw new ErroComunicacaoMicrosservicoException(e.getMessage(), status);
        }
    }

    public ProtocoloSolicitacaoCartao solicitarEmissaoCartao(DadosSolicitacaoEmissaoCartao dados) {
        try {
            log.info("DadosSolicitacaoEmissaoCartao: {}", dados);
            publisher.solicitarCartao(dados);
            var protocolo = UUID.randomUUID().toString();
            return new ProtocoloSolicitacaoCartao(protocolo);
        } catch (Exception e) {
           throw new ErroSolicitacaoCartaoException(e.getMessage());
        }
    }
}
