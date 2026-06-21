package com.financeiro.poupeja.service;

import org.springframework.stereotype.Service;

import com.financeiro.poupeja.entity.Categoria;
import com.financeiro.poupeja.entity.Usuario;
import com.financeiro.poupeja.repository.CategoriaRepository;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public Categoria cadastrarCategoria(Long id, String descricao, double meta, boolean ativo, Usuario usuario) {

        Categoria novaCategoria = new Categoria();
        novaCategoria.setId(id);
        novaCategoria.setDescricao(descricao);
        novaCategoria.setMeta(meta);
        novaCategoria.setAtivo(ativo);
        novaCategoria.setUsuario(usuario);

        return categoriaRepository.save(novaCategoria);
    }

    public List<Categoria> listarPorUsuario(Usuario usuario) {
        return categoriaRepository.findByUsuarioOrUsuarioIsNull(usuario);
    }
}
