package com.financeiro.poupeja.repository;

import com.financeiro.poupeja.entity.Alerta;
import com.financeiro.poupeja.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    Page<Alerta> findByUsuarioAndDescricaoContainingIgnoreCase(Usuario usuario, String descricao, Pageable pageable);

    Page<Alerta> findByUsuario(Usuario usuario, Pageable pageable);

    List<Alerta> findByAtivoTrueAndStatus(String status);
}
