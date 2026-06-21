package com.financeiro.poupeja.dto;

import com.financeiro.poupeja.util.Utils;

public class FormaPagamentoRelatorioDTO {
    private final String descricaoFormaPagamento;
    private final Double valorTotal;
    private final Long quantidadeLancamentos;

    public FormaPagamentoRelatorioDTO(String descricaoFormaPagamento, Double valorTotal, Long quantidadeLancamentos) {
        this.descricaoFormaPagamento = descricaoFormaPagamento;
        this.valorTotal = !Utils.isEmpty(valorTotal) ? valorTotal : 0.0;
        this.quantidadeLancamentos = !Utils.isEmpty(quantidadeLancamentos) ? quantidadeLancamentos : 0L;
    }

    public String getDescricaoFormaPagamento() {
        return descricaoFormaPagamento;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public Long getQuantidadeLancamentos() {
        return quantidadeLancamentos;
    }
}
