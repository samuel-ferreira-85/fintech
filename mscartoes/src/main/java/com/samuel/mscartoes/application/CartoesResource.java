package com.samuel.mscartoes.application;

import com.samuel.mscartoes.application.representantion.CartaoSaveRequest;
import com.samuel.mscartoes.application.representantion.CartoesPorClienteResponse;
import com.samuel.mscartoes.domain.Cartao;
import com.samuel.mscartoes.domain.ClienteCartao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("cartoes")
@RequiredArgsConstructor
@Slf4j
public class CartoesResource {

    private final CartaoService cartaoService;
    private final ClienteCartaoService clienteCartaoService;
    private final Environment environment;

    @Value("${spring.application.name}")
    private String applicationName;
    @GetMapping
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> retorno = new HashMap<>();
        var port = environment.getProperty("local.server.port");
        retorno.put("service", applicationName);
        retorno.put("status", "up");
        retorno.put("port", port);
        retorno.put("httpStatus", HttpStatus.OK.value());
        log.info("Requisicao na porta: {}", port);
        return ResponseEntity.ok(retorno);
    }

    @PostMapping
    public ResponseEntity cadastrar(@RequestBody CartaoSaveRequest request) {
        var cartao = request.toModel();
        cartaoService.salvar(cartao);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(cartao.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping(params = "renda")
    public ResponseEntity<List<Cartao>> getCartoesRendaAte(@RequestParam("renda") Long renda) {
        List<Cartao> list = cartaoService.getCartoesRendaMenorIgual(renda);
        return ResponseEntity.ok(list);
    }

    @GetMapping(params = "cpf")
    public ResponseEntity<List<CartoesPorClienteResponse>> getCartoesByCliente (@RequestParam("cpf") String cpf ) {
        var lista = clienteCartaoService.listarClienteCartaoByCpf(cpf);

        List<CartoesPorClienteResponse> clienteResponses = lista.stream()
                .map(CartoesPorClienteResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(clienteResponses);
    }
}
