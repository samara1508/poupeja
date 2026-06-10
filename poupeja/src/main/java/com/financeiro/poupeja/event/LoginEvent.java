package com.financeiro.poupeja.event;

import com.financeiro.poupeja.entity.Usuario;

public class LoginEvent {

    private final Usuario usuario;

    public LoginEvent(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
