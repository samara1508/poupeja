package com.financeiro.poupeja.util;

import org.springframework.stereotype.Component;

@Component
public class LancamentoEdicaoContext {

    private Long lancamentoId;

    public void editar(Long lancamentoId) {
        this.lancamentoId = lancamentoId;
    }

    public void limpar() {
        this.lancamentoId = null;
    }

    public Long getLancamentoId() {
        return lancamentoId;
    }

    public boolean isEditando() {
        return lancamentoId != null;
    }
}
