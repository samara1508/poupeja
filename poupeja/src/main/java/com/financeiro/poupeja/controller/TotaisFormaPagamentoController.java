package com.financeiro.poupeja.controller;

import com.financeiro.poupeja.PoupejaApplication;
import com.financeiro.poupeja.dto.FormaPagamentoRelatorioDTO;
import com.financeiro.poupeja.service.RelatorioService;
import com.financeiro.poupeja.util.MessageUtils;
import com.financeiro.poupeja.util.SpringFXMLLoader;
import com.financeiro.poupeja.util.Utils;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.financeiro.poupeja.dto.FormaPagamentoExportDTO;
import com.financeiro.poupeja.service.ExportacaoService;
import javafx.stage.FileChooser;

@Component
public class TotaisFormaPagamentoController {

    private static final NumberFormat MOEDA_FORMATTER = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
    private static final int TAM_PAG = 5;

    private final SpringFXMLLoader fxmlLoader;
    private final RelatorioService relatorioService;
    private final ExportacaoService exportacaoPdfService;

    @FXML
    private TableView<FormaPagamentoRelatorioDTO> tabelaRelatorio;

    @FXML
    private TableColumn<FormaPagamentoRelatorioDTO, String> colFormaPagamento;

    @FXML
    private TableColumn<FormaPagamentoRelatorioDTO, Double> colValor;

    @FXML
    private TableColumn<FormaPagamentoRelatorioDTO, Long> colQuantidade;

    @FXML
    private Button btnAnterior;

    @FXML
    private Button btnProximo;

    @FXML
    private Label lblPagina;

    private int paginaAtual = 0;
    private int totalPaginas = 1;
    private List<FormaPagamentoRelatorioDTO> todosDados;

    public TotaisFormaPagamentoController(SpringFXMLLoader fxmlLoader, RelatorioService relatorioService, ExportacaoService exportacaoPdfService) {
        this.fxmlLoader = fxmlLoader;
        this.relatorioService = relatorioService;
        this.exportacaoPdfService = exportacaoPdfService;
    }

    @FXML
    public void initialize() {
        configurarColunas();
        carregarDados();
    }

    private void configurarColunas() {
        colFormaPagamento.setCellValueFactory(new PropertyValueFactory<>("descricaoFormaPagamento"));
        
        colValor.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));
        colValor.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(MOEDA_FORMATTER.format(item));
                }
            }
        });

        colQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidadeLancamentos"));
    }

    private void carregarDados() {
        try {
            todosDados = relatorioService.obterRelatorioFormaPagamento();
            paginaAtual = 0;
            atualizarTabelaEPaginacao();
        } catch (Exception e) {
            MessageUtils.erro("Erro ao carregar relatorio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void atualizarTabelaEPaginacao() {
        if (Utils.isEmpty(todosDados)) {
            todosDados = Collections.emptyList();
        }

        totalPaginas = (int) Math.ceil((double) todosDados.size() / TAM_PAG);
        if (totalPaginas == 0) {
            totalPaginas = 1;
        }

        if (paginaAtual >= totalPaginas) {
            paginaAtual = totalPaginas - 1;
        }
        if (paginaAtual < 0) {
            paginaAtual = 0;
        }

        int deIndice = paginaAtual * TAM_PAG;
        int ateIndice = Math.min(deIndice + TAM_PAG, todosDados.size());

        List<FormaPagamentoRelatorioDTO> paginaItens;
        if (deIndice < todosDados.size()) {
            paginaItens = todosDados.subList(deIndice, ateIndice);
        } else {
            paginaItens = Collections.emptyList();
        }

        tabelaRelatorio.setItems(FXCollections.observableArrayList(paginaItens));
        lblPagina.setText(String.format("Página %d de %d", paginaAtual + 1, totalPaginas));

        btnAnterior.setDisable(paginaAtual == 0);
        btnProximo.setDisable(paginaAtual >= totalPaginas - 1);
    }

    @FXML
    public void paginaAnterior() {
        if (paginaAtual > 0) {
            paginaAtual--;
            atualizarTabelaEPaginacao();
        }
    }

    @FXML
    public void proximaPagina() {
        if (paginaAtual < totalPaginas - 1) {
            paginaAtual++;
            atualizarTabelaEPaginacao();
        }
    }

    @FXML
    public void voltarMenu() {
        navegarPara("/fxml/menu_principal.fxml", "PoupeJá! - Menu Principal");
    }

    private void navegarPara(String fxmlPath, String titulo) {
        try {
            Parent root = fxmlLoader.load(fxmlPath);
            PoupejaApplication.getPrimaryStage().setTitle(titulo);
            PoupejaApplication.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException e) {
            MessageUtils.erro("Nao foi possivel carregar a tela.");
            e.printStackTrace();
        }
    }

    @FXML
    public void exportarParaPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Relatório de Totais por Forma de Pagamento");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos PDF (*.pdf)", "*.pdf"));
        fileChooser.setInitialFileName("totais_forma_pagamento.pdf");
        File file = fileChooser.showSaveDialog(PoupejaApplication.getPrimaryStage());
        
        if (!Utils.isEmpty(file)) {
            try {
                if (Utils.isEmpty(todosDados) || todosDados.isEmpty()) {
                    MessageUtils.alerta("Não há dados para exportar.");
                    return;
                }
                
                List<FormaPagamentoExportDTO> dadosExport = todosDados.stream().map(d -> new FormaPagamentoExportDTO(
                    d.getDescricaoFormaPagamento(),
                    MOEDA_FORMATTER.format(d.getValorTotal()),
                    String.valueOf(d.getQuantidadeLancamentos())
                )).collect(Collectors.toList());
                
                try (InputStream reportStream = getClass().getResourceAsStream("/reports/totais_forma_pagamento.jrxml")) {
                    exportacaoPdfService.exportarPdf(reportStream, dadosExport, file.getAbsolutePath());
                    MessageUtils.sucesso("Relatório exportado com sucesso!");
                }
            } catch (Exception e) {
                MessageUtils.erro("Erro ao exportar relatório: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void exportarParaExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Relatório de Totais por Forma de Pagamento em Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos Excel (*.xlsx)", "*.xlsx"));
        fileChooser.setInitialFileName("totais_forma_pagamento.xlsx");
        File file = fileChooser.showSaveDialog(PoupejaApplication.getPrimaryStage());
        
        if (!Utils.isEmpty(file)) {
            try {
                if (Utils.isEmpty(todosDados) || todosDados.isEmpty()) {
                    MessageUtils.alerta("Não há dados para exportar.");
                    return;
                }
                
                List<FormaPagamentoExportDTO> dadosExport = todosDados.stream().map(d -> new FormaPagamentoExportDTO(
                    d.getDescricaoFormaPagamento(),
                    MOEDA_FORMATTER.format(d.getValorTotal()),
                    String.valueOf(d.getQuantidadeLancamentos())
                )).collect(Collectors.toList());
                
                try (InputStream reportStream = getClass().getResourceAsStream("/reports/totais_forma_pagamento.jrxml")) {
                    exportacaoPdfService.exportarExcel(reportStream, dadosExport, file.getAbsolutePath());
                    MessageUtils.sucesso("Relatório exportado com sucesso!");
                }
            } catch (Exception e) {
                MessageUtils.erro("Erro ao exportar relatório: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
