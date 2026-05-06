package com.financeiro.poupeja.service;

import org.springframework.stereotype.Service;

import com.financeiro.poupeja.entity.Usuario;
import com.financeiro.poupeja.exception.AcessoNegadoException;
import com.financeiro.poupeja.repository.UsuarioRepository;
import com.financeiro.poupeja.util.Utils;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario criarUsuario(String nome, String email, String senha, String confirmacaoSenha) {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(nome);
        novoUsuario.setEmail(email);
        novoUsuario.setSenha(senha);
        
        novoUsuario.confirmarSenha(confirmacaoSenha);

        if (usuarioRepository.existsByEmail(email)) {
            throw new AcessoNegadoException("E-mail já utilizado.");
        }


        return usuarioRepository.save(novoUsuario);
    }

    public Usuario login(String nome, String senha) {
        validarLogin(nome, senha);

		Usuario usuario = usuarioRepository.findByNome(nome)
				.orElseThrow(() -> new AcessoNegadoException("Usuário não encontrado."));

        if (!usuario.getSenha().equals(senha)) {
            throw new AcessoNegadoException("Senha inválida.");
        }

        return usuario;
    }

    public void alterarSenha(String nome, String novaSenha, String confirmacaoSenha) {
        validarAlteracaoSenha(nome, novaSenha, confirmacaoSenha);

		Usuario usuario = usuarioRepository.findByNome(nome)
				.orElseThrow(() -> new AcessoNegadoException("Usuário não encontrado."));

        usuario.setSenha(novaSenha);
        usuarioRepository.save(usuario);
    }

	private void validarLogin(String nome, String senha) {
		if (Utils.isEmpty(nome)) {
            throw new IllegalArgumentException("O login é obrigatório.");
        }
        if (Utils.isEmpty(senha)) {
            throw new IllegalArgumentException("Senha é obrigatório.");
        }
	}
	
	private void validarAlteracaoSenha(String nome, String novaSenha, String confirmacaoSenha) {
		if (Utils.isEmpty(nome)) {
            throw new IllegalArgumentException("Login é obrigatório.");
        }
        if (Utils.isEmpty(novaSenha)) {
            throw new IllegalArgumentException("Senha é obrigatório.");
        }
        if (Utils.isEmpty(confirmacaoSenha)) {
            throw new IllegalArgumentException("Confirmação de senha é obrigatório.");
        }
        if (!novaSenha.equals(confirmacaoSenha)) {
            throw new IllegalArgumentException("A confirmação de senha e a nova senha devem ser idênticas.");
        }
	}
}
