package com.financeiro.poupeja.util;

import com.financeiro.poupeja.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class Sessao {

    private Usuario usuarioLogado;

    public void login(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    public void logout() {
        this.usuarioLogado = null;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public boolean isAutenticado() {
        return usuarioLogado != null;
    }
}
