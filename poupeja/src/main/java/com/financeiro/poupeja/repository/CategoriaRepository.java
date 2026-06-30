package com.financeiro.poupeja.repository;

import com.financeiro.poupeja.entity.Categoria;
import com.financeiro.poupeja.entity.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    
    Optional<Categoria> findByDescricao(String descricao);

    List<Categoria> findByUsuario(Usuario usuario);

    List<Categoria> findByUsuarioAndAtivoTrue(Usuario usuario);

    boolean existsByDescricao(String descricao);
    
}
