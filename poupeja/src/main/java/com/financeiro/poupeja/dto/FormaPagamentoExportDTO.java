package com.financeiro.poupeja.dto;

public class FormaPagamentoExportDTO {
    private String descricaoFormaPagamento;
    private String valorTotal;
    private String quantidadeLancamentos;

    public FormaPagamentoExportDTO() {}

    public FormaPagamentoExportDTO(String descricaoFormaPagamento, String valorTotal, String quantidadeLancamentos) {
        this.descricaoFormaPagamento = descricaoFormaPagamento;
        this.valorTotal = valorTotal;
        this.quantidadeLancamentos = quantidadeLancamentos;
    }

    public String getDescricaoFormaPagamento() {
        return descricaoFormaPagamento;
    }

    public void setDescricaoFormaPagamento(String descricaoFormaPagamento) {
        this.descricaoFormaPagamento = descricaoFormaPagamento;
    }

    public String getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(String valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getQuantidadeLancamentos() {
        return quantidadeLancamentos;
    }

    public void setQuantidadeLancamentos(String quantidadeLancamentos) {
        this.quantidadeLancamentos = quantidadeLancamentos;
    }
}
