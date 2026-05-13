package com.financeiro.poupeja.entity;

import jakarta.persistence.*;
import java.util.List;
import java.util.regex.Pattern;
import com.financeiro.poupeja.util.Utils;

@Entity
public class Usuario {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
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
        if (Utils.isEmpty(nome)) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        if (Utils.isEmpty(senha)) {
            throw new IllegalArgumentException("Senha é obrigatório.");
        }
        if (senha.length() < 8) {
            throw new IllegalArgumentException("A senha deve conter no mínimo 8 caracteres.");
        }
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (Utils.isEmpty(email)) {
            throw new IllegalArgumentException("E-mail é obrigatório.");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("O formato do e-mail é inválido.");
        }
        this.email = email;
    }
    
    public void confirmarSenha(String confirmacaoSenha) {
    	if(Utils.isEmpty(confirmacaoSenha)) {
    		throw new IllegalArgumentException("Confirmação de senha é obrigatório.");
    	}
    	if (!senha.equals(confirmacaoSenha)) {
            throw new IllegalArgumentException("A confirmação de senha e a senha devem ser idênticas.");
        }
    }
}