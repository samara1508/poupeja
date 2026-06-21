package com.financeiro.poupeja.service;

import com.financeiro.poupeja.dto.DashboardDTO;
import com.financeiro.poupeja.dto.CategoriaDashboardDTO;
import com.financeiro.poupeja.dto.CategoriaGastoDTO;
import com.financeiro.poupeja.entity.Categoria;
import com.financeiro.poupeja.entity.Usuario;
import com.financeiro.poupeja.enumeration.TipoLancamento;
import com.financeiro.poupeja.repository.LancamentoRepository;
import com.financeiro.poupeja.util.Utils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final LancamentoRepository lancamentoRepository;
    private final CategoriaService categoriaService;
    private final AuthService authService;

    public DashboardService(
            LancamentoRepository lancamentoRepository,
            CategoriaService categoriaService,
            AuthService authService) {
        this.lancamentoRepository = lancamentoRepository;
        this.categoriaService = categoriaService;
        this.authService = authService;
    }

    public DashboardDTO obterDadosDashboard(LocalDate dataInicio, LocalDate dataFim) {
        Usuario usuario = authService.getUsuarioLogado();
        if (Utils.isEmpty(usuario)) {
            throw new IllegalArgumentException("Usuário precisa estar logado para acessar o dashboard.");
        }

        Double totalReceitas = lancamentoRepository.sumValorTotalByUsuarioAndTipoAndDataBetween(
                usuario, TipoLancamento.RECEITA, dataInicio, dataFim);
        if (Utils.isEmpty(totalReceitas)) totalReceitas = 0.0;

        Double totalDespesas = lancamentoRepository.sumValorTotalByUsuarioAndTipoAndDataBetween(
                usuario, TipoLancamento.DESPESA, dataInicio, dataFim);
        if (Utils.isEmpty(totalDespesas)) totalDespesas = 0.0;

        List<Categoria> todasCategorias = categoriaService.listarPorUsuario(usuario);
        List<Categoria> categoriasVigentes = Utils.isEmpty(todasCategorias) ? List.of()
                : todasCategorias.stream()
                    .filter(c -> !Boolean.FALSE.equals(c.getAtivo()))
                    .toList();

        List<CategoriaGastoDTO> gastosAgrupados = lancamentoRepository.sumValorTotalByUsuarioAndTipoAndDataBetweenGroupedByCategoria(
                usuario, TipoLancamento.DESPESA, dataInicio, dataFim);

        Map<Long, Double> mapGastos = new HashMap<>();
        if (!Utils.isEmpty(gastosAgrupados)) {
            for (CategoriaGastoDTO row : gastosAgrupados) {
                mapGastos.put(row.getCategoriaId(), Utils.isEmpty(row.getTotalGasto()) ? 0.0 : row.getTotalGasto());
            }
        }

        Double totalMetaUtilizada = 0.0;
        if (!Utils.isEmpty(todasCategorias)) {
            for (Categoria cat : todasCategorias) {
                if (mapGastos.containsKey(cat.getId())) {
                    totalMetaUtilizada += cat.getMeta() != null ? cat.getMeta() : 0.0;
                }
            }
        }

        List<CategoriaDashboardDTO> listCategoriasDTO = new ArrayList<>();
        for (Categoria cat : categoriasVigentes) {
            Double gasto = mapGastos.getOrDefault(cat.getId(), 0.0);
            Double meta = Utils.isEmpty(cat.getMeta()) ? 0.0 : cat.getMeta();
            listCategoriasDTO.add(new CategoriaDashboardDTO(cat.getDescricao(), meta, gasto));
        }

        return new DashboardDTO(totalReceitas, totalDespesas, totalMetaUtilizada, listCategoriasDTO);
    }
}
