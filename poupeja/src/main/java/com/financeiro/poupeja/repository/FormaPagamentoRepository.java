package com.financeiro.poupeja.repository;

import com.financeiro.poupeja.entity.FormaPagamento;
import com.financeiro.poupeja.entity.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormaPagamentoRepository
        extends JpaRepository<FormaPagamento, Long> {

    Optional<FormaPagamento> findByDescricao(String descricao);

    boolean existsByDescricao(String descricao);

    List<FormaPagamento> findByUsuario(Usuario usuario);
}