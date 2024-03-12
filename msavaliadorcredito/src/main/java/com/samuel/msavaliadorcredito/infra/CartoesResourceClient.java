package com.samuel.msavaliadorcredito.infra;

import com.samuel.msavaliadorcredito.domain.model.Cartao;
import com.samuel.msavaliadorcredito.domain.model.CartaoCliente;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "mscartoes", path = "/cartoes")
public interface CartoesResourceClient {

    @GetMapping(params = "cpf")
    ResponseEntity<List<CartaoCliente>> getCartoesByCliente(@RequestParam("cpf") String cpf );

    @GetMapping(params = "renda")
    ResponseEntity<List<CartaoCliente>> getCartoesRendaAte(@RequestParam("renda") Long renda);
}
