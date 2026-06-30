package com.financeiro.poupeja.service;

import com.financeiro.poupeja.entity.FormaPagamento;
import com.financeiro.poupeja.entity.Usuario;
import com.financeiro.poupeja.repository.FormaPagamentoRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormaPagamentoService {

    private final FormaPagamentoRepository repository;

    public FormaPagamentoService(FormaPagamentoRepository repository) {
        this.repository = repository;
    }

    public void criarFormaPagamento(String descricao, Boolean ativo, Usuario usuario) {

        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome da forma de pagamento é obrigatório.");
        }

        if (repository.existsByDescricao(descricao)) {
            throw new IllegalArgumentException(
                    "Já existe uma forma de pagamento com essa descrição.");
        }

        FormaPagamento formaPagamento =
                new FormaPagamento(descricao, ativo, usuario);

        repository.save(formaPagamento);
    }

    public List<FormaPagamento> listarPorUsuario(Usuario usuario) {
        return repository.findByUsuario(usuario);
    }

    public List<FormaPagamento> listarAtivasPorUsuario(Usuario usuario) {
        return repository.findByUsuarioAndAtivoTrue(usuario);
    }

    public FormaPagamento salvar(FormaPagamento formaPagamento) {
        return repository.save(formaPagamento);
    }
}
