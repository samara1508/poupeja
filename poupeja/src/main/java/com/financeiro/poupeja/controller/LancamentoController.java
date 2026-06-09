package com.financeiro.poupeja.controller;

import com.financeiro.poupeja.PoupejaApplication;
import com.financeiro.poupeja.entity.Lancamento;
import com.financeiro.poupeja.service.LancamentoService;
import com.financeiro.poupeja.util.LancamentoEdicaoContext;
import com.financeiro.poupeja.util.MessageUtils;
import com.financeiro.poupeja.util.SpringFXMLLoader;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

@Component
public class LancamentoController {

    private static final int TAM_PAG = 5;
    private static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat MOEDA_FORMATTER = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private final SpringFXMLLoader fxmlLoader;
    private final LancamentoService lancamentoService;
    private final LancamentoEdicaoContext lancamentoEdicaoContext;

    @FXML
    private TableView<Lancamento> tabelaLancamentos;

    @FXML
    private TableColumn<Lancamento, String> colDescricao;

    @FXML
    private TableColumn<Lancamento, String> colTipo;

    @FXML
    private TableColumn<Lancamento, Double> colValor;

    @FXML
    private TableColumn<Lancamento, LocalDate> colData;

    @FXML
    private TableColumn<Lancamento, String> colCategoria;

    @FXML
    private TableColumn<Lancamento, String> colFormaPagamento;

    @FXML
    private TableColumn<Lancamento, String> colRecorrencia;

    @FXML
    private TableColumn<Lancamento, String> colParcelas;

    @FXML
    private TableColumn<Lancamento, Void> colAcoes;

    @FXML
    private TextField txtBusca;

    @FXML
    private Button btnAnterior;

    @FXML
    private Button btnProximo;

    @FXML
    private Label lblPagina;

    private int paginaAtual = 0;
    private int totalPaginas = 1;

    public LancamentoController(
            SpringFXMLLoader fxmlLoader,
            LancamentoService lancamentoService,
            LancamentoEdicaoContext lancamentoEdicaoContext) {
        this.fxmlLoader = fxmlLoader;
        this.lancamentoService = lancamentoService;
        this.lancamentoEdicaoContext = lancamentoEdicaoContext;
    }

    @FXML
    public void initialize() {
        configurarColunas();
        carregarDados();
    }

    private void configurarColunas() {
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colTipo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTipo().name()));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));
        colValor.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : MOEDA_FORMATTER.format(item));
            }
        });
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colData.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(DATA_FORMATTER));
            }
        });
        colCategoria.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getCategoria() == null ? "-" : cellData.getValue().getCategoria().getDescricao()));
        colFormaPagamento.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFormaPagamento() == null ? "-" : cellData.getValue().getFormaPagamento().getDescricao()));
        colRecorrencia.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRecorrencia().name()));
        colParcelas.setCellValueFactory(cellData -> {
            Lancamento lancamento = cellData.getValue();
            int totalParcelas = lancamento.getParcelas() == null ? 0 : lancamento.getParcelas().size();
            return new SimpleStringProperty(String.valueOf(totalParcelas));
        });
        colAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnExcluir = new Button("Excluir");
            private final HBox container = new HBox(6, btnEditar, btnExcluir);

            {
                btnEditar.setOnAction(event -> editarLancamento(getTableView().getItems().get(getIndex())));
                btnExcluir.setOnAction(event -> confirmarEDeletar(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    private void editarLancamento(Lancamento lancamento) {
        lancamentoEdicaoContext.editar(lancamento.getId());
        navegarPara("/fxml/cadastro_lancamento.fxml", "PoupeJa! - Editar Lancamento");
    }

    private void carregarDados() {
        try {
            String busca = txtBusca.getText() != null ? txtBusca.getText().trim() : "";
            Page<Lancamento> pagina = lancamentoService.listarPorUsuario(busca, paginaAtual, TAM_PAG);

            tabelaLancamentos.setItems(FXCollections.observableArrayList(pagina.getContent()));

            totalPaginas = pagina.getTotalPages() > 0 ? pagina.getTotalPages() : 1;
            lblPagina.setText(String.format("Pagina %d de %d", paginaAtual + 1, totalPaginas));

            btnAnterior.setDisable(paginaAtual == 0);
            btnProximo.setDisable(paginaAtual >= totalPaginas - 1);
        } catch (Exception e) {
            MessageUtils.erro("Erro ao carregar lancamentos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void confirmarEDeletar(Lancamento lancamento) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusao");
        alert.setHeaderText("Excluir lancamento permanentemente?");
        alert.setContentText("Esta acao nao podera ser desfeita.");

        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                lancamentoService.excluirLancamento(lancamento.getId());
                MessageUtils.sucesso("Lancamento excluido com sucesso!");

                if (tabelaLancamentos.getItems().size() == 1 && paginaAtual > 0) {
                    paginaAtual--;
                }
                carregarDados();
            } catch (Exception e) {
                MessageUtils.erro("Erro ao excluir lancamento: " + e.getMessage());
            }
        }
    }

    @FXML
    public void filtrar() {
        paginaAtual = 0;
        carregarDados();
    }

    @FXML
    public void limparFiltro() {
        txtBusca.clear();
        paginaAtual = 0;
        carregarDados();
    }

    @FXML
    public void paginaAnterior() {
        if (paginaAtual > 0) {
            paginaAtual--;
            carregarDados();
        }
    }

    @FXML
    public void proximaPagina() {
        if (paginaAtual < totalPaginas - 1) {
            paginaAtual++;
            carregarDados();
        }
    }

    @FXML
    public void voltarMenu() {
        navegarPara("/fxml/menu_principal.fxml", "PoupeJa! - Menu Principal");
    }

    @FXML
    public void criarNovoLancamento() {
        lancamentoEdicaoContext.limpar();
        navegarPara("/fxml/cadastro_lancamento.fxml", "PoupeJa! - Novo Lancamento");
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
}
