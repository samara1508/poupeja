package com.financeiro.poupeja.dto;

import com.financeiro.poupeja.util.Utils;

public class CategoriaRelatorioDTO {
    private final String descricaoCategoria;
    private final Double valorTotal;
    private final Long quantidadeLancamentos;

    public CategoriaRelatorioDTO(String descricaoCategoria, Double valorTotal, Long quantidadeLancamentos) {
        this.descricaoCategoria = descricaoCategoria;
        this.valorTotal = !Utils.isEmpty(valorTotal) ? valorTotal : 0.0;
        this.quantidadeLancamentos = !Utils.isEmpty(quantidadeLancamentos) ? quantidadeLancamentos : 0L;
    }

    public String getDescricaoCategoria() {
        return descricaoCategoria;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public Long getQuantidadeLancamentos() {
        return quantidadeLancamentos;
    }
}
