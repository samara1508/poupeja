package com.financeiro.poupeja.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.financeiro.poupeja.PoupejaApplication;
import com.financeiro.poupeja.entity.Alerta;
import com.financeiro.poupeja.service.AlertaService;
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

@Component
public class AlertaController {
	private static final int TAM_PAG = 5;

    private final SpringFXMLLoader fxmlLoader;
    private final AlertaService alertaService;

    @FXML
    private TableView<Alerta> tabelaAlertas;

    @FXML
    private TableColumn<Alerta, String> colDescricao;

    @FXML
    private TableColumn<Alerta, Integer> colDiasAntes;

    @FXML
    private TableColumn<Alerta, String> colEmail;

    @FXML
    private TableColumn<Alerta, LocalDate> colDataCriacao;

    @FXML
    private TableColumn<Alerta, LocalDate> colUltimaExecucao;

    @FXML
    private TableColumn<Alerta, String> colStatus;

    @FXML
    private TableColumn<Alerta, Void> colAcoes;

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

    public AlertaController(SpringFXMLLoader fxmlLoader, AlertaService alertaService) {
        this.fxmlLoader = fxmlLoader;
        this.alertaService = alertaService;
    }

    @FXML
    public void initialize() {
        configurarColunas();
        carregarDados();
    }

    private void configurarColunas() {
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colDiasAntes.setCellValueFactory(new PropertyValueFactory<>("diasAntes"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        colDataCriacao.setCellValueFactory(new PropertyValueFactory<>("dataCriacao"));
        colDataCriacao.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(formatter));
                }
            }
        });

        colUltimaExecucao.setCellValueFactory(new PropertyValueFactory<>("ultimaExecucao"));
        colUltimaExecucao.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("-");
                } else {
                    setText(item.format(formatter));
                }
            }
        });

        colStatus.setCellValueFactory(cellData -> {
            Alerta alerta = cellData.getValue();
            String statusExibicao = alerta.getStatus();
            if (alerta.getAtivo() != null) {
                statusExibicao += Boolean.TRUE.equals(alerta.getAtivo()) ? " (Ativo)" : " (Inativo)";
            }
            return new SimpleStringProperty(statusExibicao);
        });

        colAcoes.setCellFactory(col -> {
            var btn = new Button("Excluir");
            TableCell<Alerta, Void> cell = new TableCell<>() {
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : btn);
                }
            };
            btn.setOnAction(e -> confirmarEDeletar(cell.getTableView().getItems().get(cell.getIndex())));
            return cell;
        });
    }

    private void carregarDados() {
        try {
            String busca = txtBusca.getText() != null ? txtBusca.getText().trim() : "";

            Page<Alerta> pagina = alertaService.listarPorUsuario(busca, paginaAtual, TAM_PAG);

            tabelaAlertas.setItems(FXCollections.observableArrayList(pagina.getContent()));

            totalPaginas = pagina.getTotalPages() > 0 ? pagina.getTotalPages() : 1;
            lblPagina.setText(String.format("Página %d de %d", paginaAtual + 1, totalPaginas));

            btnAnterior.setDisable(paginaAtual == 0);
            btnProximo.setDisable(paginaAtual >= totalPaginas - 1);

        } catch (Exception e) {
            MessageUtils.erro("Erro ao carregar a lista de alertas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void confirmarEDeletar(Alerta alerta) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText("Excluir Alerta permanentemente?");
        alert.setContentText("Esta ação não poderá ser desfeita.");

        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                alertaService.excluirAlerta(alerta.getId());

                MessageUtils.sucesso("Alerta excluído com sucesso!");

                if (tabelaAlertas.getItems().size() == 1 && paginaAtual > 0) {
                    paginaAtual--;
                }
                carregarDados();
            } catch (Exception e) {
                MessageUtils.erro("Erro ao excluir o alerta: " + e.getMessage());
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
        navegarPara("/fxml/menu_principal.fxml", "PoupeJá! - Menu Principal");
    }

    @FXML
    public void criarNovoAlerta() {
        navegarPara("/fxml/cadastro_alerta.fxml", "PoupeJá! - Cadastrar Alerta");
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
