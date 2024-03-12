package com.samuel.mscartoes.infra.mqueue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.samuel.mscartoes.domain.ClienteCartao;
import com.samuel.mscartoes.domain.DadosSolicitacaoEmissaoCartao;
import com.samuel.mscartoes.infra.repository.CartaoRepository;
import com.samuel.mscartoes.infra.repository.ClienteCartaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmissaoCartaoSubscriber {

    private final ClienteCartaoRepository clienteCartaoRepository;
    private final CartaoRepository cartaoRepository;
    private final ObjectMapper mapper;

    @RabbitListener(queues = "${mq.queues.emissao-cartoes}")
    public void receberSolicitacaoEmissao(@Payload String payload) {
        try {
            var dados =
                    mapper.readValue(payload, DadosSolicitacaoEmissaoCartao.class);

            var cartao = cartaoRepository.findById(dados.getIdCartao()).orElseThrow();

            var clienteCartao = new ClienteCartao();
            clienteCartao.setCartao(cartao);
            clienteCartao.setCpf(dados.getCpf());
            clienteCartao.setLimite(dados.getLimite());

            clienteCartaoRepository.save(clienteCartao);
        } catch (Exception e) {
            log.error("Erro ao receber solicitação de cartão: {}", e.getMessage());
        }
    }
}
