package com.financeiro.poupeja.dto;

import java.util.List;

public class DashboardDTO {
    private final Double totalReceitas;
    private final Double totalDespesas;
    private final List<CategoriaDashboardDTO> categorias;

    public DashboardDTO(Double totalReceitas, Double totalDespesas, List<CategoriaDashboardDTO> categorias) {
        this.totalReceitas = totalReceitas;
        this.totalDespesas = totalDespesas;
        this.categorias = categorias;
    }

    public Double getTotalReceitas() {
        return totalReceitas;
    }

    public Double getTotalDespesas() {
        return totalDespesas;
    }

    public List<CategoriaDashboardDTO> getCategorias() {
        return categorias;
    }
}
