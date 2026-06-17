package com.financeiro.poupeja.controller;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.financeiro.poupeja.PoupejaApplication;
import com.financeiro.poupeja.dto.CategoriaDashboardDTO;
import com.financeiro.poupeja.dto.DashboardDTO;
import com.financeiro.poupeja.service.DashboardService;
import com.financeiro.poupeja.util.MessageUtils;
import com.financeiro.poupeja.util.SpringFXMLLoader;
import com.financeiro.poupeja.util.Utils;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

@Component
public class DashboardController {

    private static final NumberFormat MOEDA_FORMATTER = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));

    private final SpringFXMLLoader fxmlLoader;
    private final DashboardService dashboardService;

    @FXML
    private DatePicker dpDataInicio;

    @FXML
    private DatePicker dpDataFim;

    @FXML
    private Label lblTotalReceitas;

    @FXML
    private Label lblTotalDespesas;

    @FXML
    private Label lblTotalGastoMeta;

    @FXML
    private Label lblTotalMetaCategorias;

    @FXML
    private BarChart<String, Number> chartGastos;

    @FXML
    private CategoryAxis xAxisCategorias;

    @FXML
    private NumberAxis yAxisValores;

    public DashboardController(SpringFXMLLoader fxmlLoader, DashboardService dashboardService) {
        this.fxmlLoader = fxmlLoader;
        this.dashboardService = dashboardService;
    }

    @FXML
    public void initialize() {
        LocalDate hoje = LocalDate.now();
        dpDataInicio.setValue(hoje.with(TemporalAdjusters.firstDayOfMonth()));
        dpDataFim.setValue(hoje.with(TemporalAdjusters.lastDayOfMonth()));

        buscarEDispararAtualizacao();
    }

    @FXML
    public void aplicarFiltros() {
        if (Utils.isEmpty(dpDataInicio.getValue()) || Utils.isEmpty(dpDataFim.getValue())) {
            MessageUtils.erro("Ambos os campos de data (Início e Fim) são obrigatórios para aplicar o filtro.");
            return;
        }

        if (dpDataInicio.getValue().isAfter(dpDataFim.getValue())) {
            MessageUtils.erro("A data de início não pode ser posterior à data de fim.");
            return;
        }

        buscarEDispararAtualizacao();
    }

    @FXML
    public void atualizarDados() {
        aplicarFiltros();
    }

    private void buscarEDispararAtualizacao() {
        try {
            LocalDate inicio = dpDataInicio.getValue();
            LocalDate fim = dpDataFim.getValue();

            DashboardDTO dados = dashboardService.obterDadosDashboard(inicio, fim);
            atualizarInterface(dados);
        } catch (Exception e) {
            MessageUtils.erro("Erro ao carregar dados do dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void atualizarInterface(DashboardDTO dados) {
        lblTotalReceitas.setText(MOEDA_FORMATTER.format(dados.getTotalReceitas()));
        lblTotalDespesas.setText(MOEDA_FORMATTER.format(dados.getTotalDespesas()));

        double totalGasto = dados.getCategorias().stream()
                .mapToDouble(CategoriaDashboardDTO::getGastoRealizado)
                .sum();
        double totalMeta = dados.getCategorias().stream()
                .mapToDouble(CategoriaDashboardDTO::getMeta)
                .sum();

        lblTotalGastoMeta.setText(MOEDA_FORMATTER.format(totalGasto));
        lblTotalMetaCategorias.setText(MOEDA_FORMATTER.format(totalMeta));

        chartGastos.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Gasto Realizado");

        for (CategoriaDashboardDTO catDTO : dados.getCategorias()) {
            XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(catDTO.getDescricao(), catDTO.getGastoRealizado());
            
            dataPoint.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    double gasto = catDTO.getGastoRealizado();
                    double meta = catDTO.getMeta();
                    if (gasto > meta && meta > 0) {
                        newNode.setStyle("-fx-bar-fill: #c62828;"); // Vermelho (Estouro da cota)
                    } else {
                        newNode.setStyle("-fx-bar-fill: #002244;"); // Azul Marinho (Padrão)
                    }
                }
            });

            series.getData().add(dataPoint);
        }

        chartGastos.getData().add(series);
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
            MessageUtils.erro("Não foi possível carregar a tela.");
            e.printStackTrace();
        }
    }
}
