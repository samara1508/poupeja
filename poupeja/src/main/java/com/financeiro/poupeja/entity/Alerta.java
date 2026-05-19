package com.financeiro.poupeja.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer diasAntes;
    private Boolean ativo;
    private String email;
    private LocalDate ultimaExecucao;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private String descricao;
    private LocalDate dataVencimento;
    private LocalDate dataCriacao = LocalDate.now();
    private String status = "PENDENTE";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDiasAntes() {
        return diasAntes;
    }

    public void setDiasAntes(Integer diasAntes) {
        this.diasAntes = diasAntes;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getUltimaExecucao() {
        return ultimaExecucao;
    }

    public void setUltimaExecucao(LocalDate ultimaExecucao) {
        this.ultimaExecucao = ultimaExecucao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // --- Métodos de negócio ---

    /**
     * Inicializa os campos padrão do alerta vinculando-o ao usuário logado.
     */
    public void inicializar(Usuario usuario) {
        this.usuario = usuario;
        this.email = usuario.getEmail();
        this.ativo = true;
        this.status = "PENDENTE";
        this.dataCriacao = LocalDate.now();
    }

    /**
     * Verifica se este alerta pertence ao usuário informado.
     */
    public boolean pertenceA(Usuario usuario) {
        return usuario != null
                && this.usuario != null
                && this.usuario.getId() != null
                && this.usuario.getId().equals(usuario.getId());
    }

    /**
     * Verifica se a data de vencimento já foi ultrapassada sem disparo.
     */
    public boolean estaVencido(LocalDate hoje) {
        return dataVencimento != null && hoje.isAfter(dataVencimento);
    }

    /**
     * Verifica se hoje é o momento de disparar o alerta (dataVencimento - diasAntes).
     */
    public boolean deveLancarAlerta(LocalDate hoje) {
        if (dataVencimento == null || diasAntes == null) return false;
        LocalDate dataDisparo = dataVencimento.minusDays(diasAntes);
        return !hoje.isBefore(dataDisparo);
    }

}
