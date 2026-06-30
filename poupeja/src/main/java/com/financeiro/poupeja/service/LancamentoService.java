package com.financeiro.poupeja.service;

import com.financeiro.poupeja.entity.Categoria;
import com.financeiro.poupeja.entity.FormaPagamento;
import com.financeiro.poupeja.entity.Lancamento;
import com.financeiro.poupeja.entity.Parcela;
import com.financeiro.poupeja.entity.Usuario;
import com.financeiro.poupeja.enumeration.TipoLancamento;
import com.financeiro.poupeja.enumeration.TipoRecorrencia;
import com.financeiro.poupeja.repository.LancamentoRepository;
import com.financeiro.poupeja.util.Utils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financeiro.poupeja.dto.LancamentoExportDTO;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class LancamentoService {

    private final LancamentoRepository repository;
    private final AuthService authService;

    public LancamentoService(LancamentoRepository repository, AuthService authService) {
        this.repository = repository;
        this.authService = authService;
    }

    public Page<Lancamento> listarPorUsuario(String descricao, int pagina, int tamanho) {
        Usuario usuario = authService.getUsuarioLogado();
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by(Sort.Direction.DESC, "data", "id"));

        if (!Utils.isEmpty(descricao)) {
            return repository.findByUsuarioAndDescricaoContainingIgnoreCase(usuario, descricao, pageable);
        }

        return repository.findByUsuario(usuario, pageable);
    }

    public List<Lancamento> listarTodosPorUsuario() {
        Usuario usuario = authService.getUsuarioLogado();
        return repository.findByUsuarioOrderByDataDesc(usuario);
    }

    public List<LancamentoExportDTO> obterLancamentosParaExportacao(String descricao) {
        Usuario usuario = authService.getUsuarioLogado();
        List<Lancamento> lancamentos;
        if (!Utils.isEmpty(descricao)) {
            lancamentos = repository.findByUsuarioAndDescricaoContainingIgnoreCase(usuario, descricao);
        } else {
            lancamentos = repository.findByUsuario(usuario);
        }

        DateTimeFormatter dataFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        NumberFormat moedaFormatter = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));

        return lancamentos.stream().map(l -> {
            String valorFormatado = !Utils.isEmpty(l.getValorTotal()) ? moedaFormatter.format(l.getValorTotal()) : "-";
            String dataFormatada = !Utils.isEmpty(l.getData()) ? l.getData().format(dataFormatter) : "-";
            String categoriaDesc = !Utils.isEmpty(l.getCategoria()) ? l.getCategoria().getDescricao() : "-";
            String formaPagamentoDesc = !Utils.isEmpty(l.getFormaPagamento()) ? l.getFormaPagamento().getDescricao() : "-";
            String tipoLabel = !Utils.isEmpty(l.getTipo()) ? l.getTipo().name() : "-";
            String recorrenciaLabel = !Utils.isEmpty(l.getRecorrencia())? l.getRecorrencia().name() : "-";
            
            int totalParcelas = Utils.isEmpty(l.getParcelas()) ? 0 : l.getParcelas().size();
            String parcelasLabel = String.valueOf(totalParcelas);

            return new LancamentoExportDTO(
                l.getDescricao(),
                tipoLabel,
                valorFormatado,
                dataFormatada,
                categoriaDesc,
                formaPagamentoDesc,
                recorrenciaLabel,
                parcelasLabel
            );
        }).collect(Collectors.toList());
    }

    public Lancamento buscarPorIdDoUsuario(Long id) {
        Lancamento lancamento = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lancamento nao encontrado."));

        validarPertenceAoUsuarioLogado(lancamento);

        return lancamento;
    }

    @Transactional
    public void criarLancamento(
            String descricao,
            Double valorTotal,
            LocalDate data,
            TipoLancamento tipo,
            TipoRecorrencia recorrencia,
            Categoria categoria,
            FormaPagamento formaPagamento,
            Integer quantidadeParcelas) {

        validarDadosLancamento(descricao, valorTotal, data, tipo, recorrencia, categoria, formaPagamento, quantidadeParcelas);

        Lancamento lancamento = new Lancamento();
        lancamento.setDescricao(descricao);
        lancamento.setValorTotal(valorTotal);
        lancamento.setData(data);
        lancamento.setTipo(tipo);
        lancamento.setRecorrencia(recorrencia);
        lancamento.setCategoria(categoria);
        lancamento.setFormaPagamento(formaPagamento);
        lancamento.setUsuario(authService.getUsuarioLogado());
        lancamento.setParcelas(criarParcelas(lancamento, valorTotal, data, quantidadeParcelas));

        repository.save(lancamento);
    }

    @Transactional
    public void atualizarLancamento(
            Long id,
            String descricao,
            Double valorTotal,
            LocalDate data,
            TipoLancamento tipo,
            TipoRecorrencia recorrencia,
            Categoria categoria,
            FormaPagamento formaPagamento,
            Integer quantidadeParcelas) {

        validarDadosLancamento(descricao, valorTotal, data, tipo, recorrencia, categoria, formaPagamento, quantidadeParcelas);

        Lancamento lancamento = buscarPorIdDoUsuario(id);
        lancamento.setDescricao(descricao);
        lancamento.setValorTotal(valorTotal);
        lancamento.setData(data);
        lancamento.setTipo(tipo);
        lancamento.setRecorrencia(recorrencia);
        lancamento.setCategoria(categoria);
        lancamento.setFormaPagamento(formaPagamento);

        if (lancamento.getParcelas() == null) {
            lancamento.setParcelas(new ArrayList<>());
        }
        lancamento.getParcelas().clear();
        lancamento.getParcelas().addAll(criarParcelas(lancamento, valorTotal, data, quantidadeParcelas));

        repository.save(lancamento);
    }

    @Transactional
    public void excluirLancamento(Long id) {
        Lancamento lancamento = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lancamento nao encontrado."));

        validarPertenceAoUsuarioLogado(lancamento);

        repository.delete(lancamento);
    }

    private void validarPertenceAoUsuarioLogado(Lancamento lancamento) {
        Usuario usuario = authService.getUsuarioLogado();
        if (lancamento.getUsuario() == null
                || usuario == null
                || !lancamento.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Voce nao tem permissao para acessar este lancamento.");
        }
    }

    private void validarDadosLancamento(
            String descricao,
            Double valorTotal,
            LocalDate data,
            TipoLancamento tipo,
            TipoRecorrencia recorrencia,
            Categoria categoria,
            FormaPagamento formaPagamento,
            Integer quantidadeParcelas) {

        if (Utils.isEmpty(descricao)) {
            throw new IllegalArgumentException("A descricao e obrigatoria.");
        }
        if (valorTotal == null || valorTotal <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero.");
        }
        if (Utils.isEmpty(data)) {
            throw new IllegalArgumentException("A data e obrigatoria.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("O tipo do lancamento e obrigatorio.");
        }
        if (recorrencia == null) {
            throw new IllegalArgumentException("A recorrencia e obrigatoria.");
        }
        if (categoria != null && !Boolean.TRUE.equals(categoria.getAtivo())) {
            throw new IllegalArgumentException("A categoria selecionada precisa estar ativa.");
        }
        if (formaPagamento != null && !Boolean.TRUE.equals(formaPagamento.getAtivo())) {
            throw new IllegalArgumentException("A forma de pagamento selecionada precisa estar ativa.");
        }
        if (quantidadeParcelas == null || quantidadeParcelas <= 0) {
            throw new IllegalArgumentException("A quantidade de parcelas deve ser maior que zero.");
        }
    }

    private List<Parcela> criarParcelas(
            Lancamento lancamento,
            Double valorTotal,
            LocalDate data,
            Integer quantidadeParcelas) {

        List<Parcela> parcelas = new ArrayList<>();
        double valorParcela = Math.round((valorTotal / quantidadeParcelas) * 100.0) / 100.0;
        double somaParcelasAnteriores = 0.0;

        for (int i = 1; i <= quantidadeParcelas; i++) {
            Parcela parcela = new Parcela();
            parcela.setNumParcela(i);
            parcela.setDataVencimento(data.plusMonths(i - 1L));
            parcela.setLancamento(lancamento);

            if (i == quantidadeParcelas) {
                parcela.setValor(Math.round((valorTotal - somaParcelasAnteriores) * 100.0) / 100.0);
            } else {
                parcela.setValor(valorParcela);
                somaParcelasAnteriores += valorParcela;
            }

            parcelas.add(parcela);
        }

        return parcelas;
    }
}
