package com.financeiro.poupeja.service;

import com.financeiro.poupeja.dto.FormaPagamentoRelatorioDTO;
import com.financeiro.poupeja.entity.Usuario;
import com.financeiro.poupeja.repository.LancamentoRepository;
import com.financeiro.poupeja.util.Utils;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RelatorioService {

    private final LancamentoRepository lancamentoRepository;
    private final AuthService authService;

    public RelatorioService(LancamentoRepository lancamentoRepository, AuthService authService) {
        this.lancamentoRepository = lancamentoRepository;
        this.authService = authService;
    }

    public List<FormaPagamentoRelatorioDTO> obterRelatorioFormaPagamento() {
        Usuario usuario = authService.getUsuarioLogado();
        if (Utils.isEmpty(usuario)) {
            throw new IllegalArgumentException("Usuário precisa estar logado para acessar o relatório.");
        }
        return lancamentoRepository.findTotaisByFormaPagamento(usuario);
    }
}
