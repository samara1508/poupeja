package com.financeiro.poupeja.entity;

import java.time.LocalDate;
import java.util.Objects;

import com.financeiro.poupeja.enumeration.StatusAlerta;
import com.financeiro.poupeja.util.Utils;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

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
    @Enumerated(EnumType.STRING)
    private StatusAlerta status = StatusAlerta.PENDENTE;

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

    public StatusAlerta getStatus() {
        return status;
    }

    public void setStatus(StatusAlerta status) {
        this.status = status;
    }

    public void inicializar(Usuario usuario) {
        this.usuario = usuario;
        this.email = usuario.getEmail();
        this.ativo = true;
        this.status = StatusAlerta.PENDENTE;
        this.dataCriacao = LocalDate.now();
    }

    public boolean pertenceA(Usuario usuario) {
        if (Utils.isEmpty(this.usuario) || Utils.isEmpty(usuario)) {
            return false;
        }
        return Objects.equals(this.usuario.getId(), usuario.getId());
    }

    public boolean estaVencido(LocalDate hoje) {
        return !Utils.isEmpty(dataVencimento) && hoje.isAfter(dataVencimento);
    }

    public boolean deveLancarAlerta(LocalDate hoje) {
        if (!Utils.isEmpty(dataVencimento) || Utils.isEmpty(diasAntes)) return false;
        LocalDate dataDisparo = dataVencimento.minusDays(diasAntes);
        return !hoje.isBefore(dataDisparo);
    }

}
