package com.financeiro.poupeja.service;

import com.financeiro.poupeja.entity.Usuario;
import com.financeiro.poupeja.exception.AcessoNegadoException;
import com.financeiro.poupeja.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Autowired
    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario criarUsuario(String nome, String email, String senha, String confirmacaoSenha) {
        if (senha == null || senha.length() < 8) {
            throw new IllegalArgumentException("A senha deve conter no mínimo 8 caracteres.");
        }

        if (!senha.equals(confirmacaoSenha)) {
            throw new IllegalArgumentException("A senha e a confirmação de senha não conferem.");
        }

        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("O formato do e-mail é inválido.");
        }

        if (usuarioRepository.existsByEmail(email)) {
            throw new AcessoNegadoException("E-mail já utilizado.");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(nome);
        novoUsuario.setEmail(email);
        novoUsuario.setSenha(senha);

        return usuarioRepository.save(novoUsuario);
    }

    public Usuario login(String nome, String senha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNome(nome);

        if (usuarioOpt.isEmpty()) {
            throw new AcessoNegadoException("Usuário não encontrado.");
        }

        Usuario usuario = usuarioOpt.get();

        if (!usuario.getSenha().equals(senha)) {
            throw new AcessoNegadoException("Senha inválida.");
        }

        return usuario;
    }

    public void alterarSenha(String nome, String novaSenha, String confirmacaoSenha) {
        if (novaSenha == null || novaSenha.length() < 8) {
            throw new IllegalArgumentException("A nova senha deve conter no mínimo 8 caracteres.");
        }

        if (!novaSenha.equals(confirmacaoSenha)) {
            throw new IllegalArgumentException("A nova senha e a confirmação não conferem.");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByNome(nome);
        if (usuarioOpt.isEmpty()) {
            throw new AcessoNegadoException("Usuário não encontrado.");
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setSenha(novaSenha);
        usuarioRepository.save(usuario);
    }
}
