package com.financeiro.poupeja.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String senha;

    @OneToMany(mappedBy = "usuario")
    private List<Lancamento> lancamentos;

    @OneToMany(mappedBy = "usuario")
    private List<Categoria> categorias;

    @OneToMany(mappedBy = "usuario")
    private List<FormaPagamento> formasPagamento;

    @OneToMany(mappedBy = "usuario")
    private List<Alerta> alertas;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}