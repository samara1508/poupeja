package com.financeiro.poupeja.dto;

public class CategoriaGastoDTO {
    private final Long categoriaId;
    private final Double totalGasto;

    public CategoriaGastoDTO(Long categoriaId, Double totalGasto) {
        this.categoriaId = categoriaId;
        this.totalGasto = totalGasto;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public Double getTotalGasto() {
        return totalGasto;
    }
}
