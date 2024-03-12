package com.samuel.mscartoes.application.representantion;

import com.samuel.mscartoes.domain.BandeiraCartao;
import com.samuel.mscartoes.domain.Cartao;

import java.math.BigDecimal;

public record CartaoSaveRequest(String nome,
                                BandeiraCartao bandeira,
                                BigDecimal renda,
                                BigDecimal limite) {
    public Cartao toModel() {
        return new Cartao(nome(), bandeira(), renda(), limite());
    }
}
