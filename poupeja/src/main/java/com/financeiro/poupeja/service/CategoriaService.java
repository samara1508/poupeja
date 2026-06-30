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


    public void criarCategoria(Long id, String descricao, double meta, boolean ativo, Usuario usuario) {

        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome da categoria é obrigatório.");
        }

        if (categoriaRepository.existsByDescricao(descricao)) {
            throw new IllegalArgumentException(
                    "Já existe uma categoria com essa descrição.");
        }

        Categoria categoria =
                new Categoria(id, descricao, meta, ativo, usuario);

        categoriaRepository.save(categoria);
    }

    public List<Categoria> listarPorUsuario(Usuario usuario) {
        return categoriaRepository.findByUsuario(usuario);
    }

    public Categoria salvar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

}
