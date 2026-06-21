package com.financeiro.poupeja.dto;

public class LancamentoExportDTO {
    private String descricao;
    private String tipo;
    private String valorTotal;
    private String data;
    private String categoria;
    private String formaPagamento;
    private String recorrencia;
    private String parcelas;

    public LancamentoExportDTO() {}

    public LancamentoExportDTO(String descricao, String tipo, String valorTotal, String data, 
                               String categoria, String formaPagamento, String recorrencia, String parcelas) {
        this.descricao = descricao;
        this.tipo = tipo;
        this.valorTotal = valorTotal;
        this.data = data;
        this.categoria = categoria;
        this.formaPagamento = formaPagamento;
        this.recorrencia = recorrencia;
        this.parcelas = parcelas;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(String valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public String getRecorrencia() {
        return recorrencia;
    }

    public void setRecorrencia(String recorrencia) {
        this.recorrencia = recorrencia;
    }

    public String getParcelas() {
        return parcelas;
    }

    public void setParcelas(String parcelas) {
        this.parcelas = parcelas;
    }
}
