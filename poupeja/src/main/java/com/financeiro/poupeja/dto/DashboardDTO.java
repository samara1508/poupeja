package com.financeiro.poupeja.dto;

import java.util.List;

public class DashboardDTO {
    private final Double totalReceitas;
    private final Double totalDespesas;
    private final Double totalMetaUtilizada;
    private final List<CategoriaDashboardDTO> categorias;

    public DashboardDTO(Double totalReceitas, Double totalDespesas, Double totalMetaUtilizada, List<CategoriaDashboardDTO> categorias) {
        this.totalReceitas = totalReceitas;
        this.totalDespesas = totalDespesas;
        this.totalMetaUtilizada = totalMetaUtilizada;
        this.categorias = categorias;
    }

    public Double getTotalReceitas() {
        return totalReceitas;
    }

    public Double getTotalDespesas() {
        return totalDespesas;
    }

    public Double getTotalMetaUtilizada() {
        return totalMetaUtilizada;
    }

    public List<CategoriaDashboardDTO> getCategorias() {
        return categorias;
    }
}
