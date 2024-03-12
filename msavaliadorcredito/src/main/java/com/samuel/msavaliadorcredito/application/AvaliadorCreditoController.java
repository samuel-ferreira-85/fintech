package com.samuel.msavaliadorcredito.application;

import com.samuel.msavaliadorcredito.application.exceptions.DadosClienteNotFoundException;
import com.samuel.msavaliadorcredito.application.exceptions.ErroComunicacaoMicrosservicoException;
import com.samuel.msavaliadorcredito.application.exceptions.ErroSolicitacaoCartaoException;
import com.samuel.msavaliadorcredito.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("avaliacoes-credito")
@RequiredArgsConstructor
@Slf4j
public class AvaliadorCreditoController {

    private final Environment environment;

    @Value("${spring.application.name}")
    private String applicationName;
    private final AvaliadorCreditoService avaliadorCreditoService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> retorno = new HashMap<>();
        var port = environment.getProperty("local.server.port");
        retorno.put("service", applicationName);
        retorno.put("status", "up");
        retorno.put("port", port);
        retorno.put("httpStatus", HttpStatus.OK.value());
        return ResponseEntity.ok(retorno);
    }

    @GetMapping(value = "situacao-cliente", params = "cpf")
    public ResponseEntity consultaSituacaoCliente(@RequestParam("cpf") String cpf) {
        try {
            SituacaoCliente situacaoCliente = avaliadorCreditoService.obterSituacaoCliente(cpf);
            return ResponseEntity.ok(situacaoCliente);
        } catch (DadosClienteNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ErroComunicacaoMicrosservicoException e) {
            return ResponseEntity.status(HttpStatus.resolve(e.getStatus()))
                    .body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity realizarAvaliacao(@RequestBody DadosAvaliacao dados) {
        try {
            RetornoAvaliacaoCliente retornoAvaliacaoCliente = avaliadorCreditoService
                    .realizarAvaliacao(dados.getCpf(), dados.getRenda());
            log.info("Retorno dados da avaliacao do cliente: {}", retornoAvaliacaoCliente);
            return ResponseEntity.ok(retornoAvaliacaoCliente);
        } catch (DadosClienteNotFoundException e) {
            log.error("Dados do clente não encontrado");
            return ResponseEntity.notFound().build();
        } catch (ErroComunicacaoMicrosservicoException e) {
            return ResponseEntity.status(HttpStatus.resolve(e.getStatus()))
                    .body(e.getMessage());
        }
    }

    @PostMapping("solicitacoes-cartao")
    public ResponseEntity solicitarCartao(@RequestBody DadosSolicitacaoEmissaoCartao dados) {
        try {
            log.info("Dados Solicitacao Cartao: {}", dados);
            ProtocoloSolicitacaoCartao protocoloSolicitacaoCartao =
                    avaliadorCreditoService.solicitarEmissaoCartao(dados);
            return ResponseEntity.ok(protocoloSolicitacaoCartao);

        }catch (ErroSolicitacaoCartaoException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
