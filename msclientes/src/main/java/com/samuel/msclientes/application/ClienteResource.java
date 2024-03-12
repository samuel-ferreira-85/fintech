package com.samuel.msclientes.application;

import com.samuel.msclientes.application.representation.ClienteSaveRequest;
import com.samuel.msclientes.domain.Cliente;
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
import java.util.Map;

@RestController
@RequestMapping("clientes")
@RequiredArgsConstructor
@Slf4j
public class ClienteResource {
    private final ClienteService clienteService;
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
    public ResponseEntity save(@RequestBody ClienteSaveRequest request) {
        Cliente cliente = request.toModel();
        clienteService.save(cliente);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .query("cpf={cpf}")
                .buildAndExpand(cliente.getCpf())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping(params = "cpf")
    public ResponseEntity dadosCliente(@RequestParam("cpf") String cpf) {
        var cliente = clienteService.getByCpf(cpf);
        if (cliente.isEmpty()) return ResponseEntity.notFound().build();

//        var clienteSaveRequest = cliente.get().toClienteSaveRequest();
        return ResponseEntity.ok(cliente);
    }
}
