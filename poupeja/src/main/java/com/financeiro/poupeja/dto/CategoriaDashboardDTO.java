package com.financeiro.poupeja.dto;

public class CategoriaDashboardDTO {
    private final String descricao;
    private final Double meta;
    private final Double gastoRealizado;

    public CategoriaDashboardDTO(String descricao, Double meta, Double gastoRealizado) {
        this.descricao = descricao;
        this.meta = meta;
        this.gastoRealizado = gastoRealizado;
    }

    public String getDescricao() {
        return descricao;
    }

    public Double getMeta() {
        return meta;
    }

    public Double getGastoRealizado() {
        return gastoRealizado;
    }
}
