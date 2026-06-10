package com.financeiro.poupeja.repository;

import com.financeiro.poupeja.entity.Lancamento;
import com.financeiro.poupeja.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    Page<Lancamento> findByUsuarioAndDescricaoContainingIgnoreCase(Usuario usuario, String descricao, Pageable pageable);

    Page<Lancamento> findByUsuario(Usuario usuario, Pageable pageable);
}
