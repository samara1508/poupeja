package com.financeiro.poupeja.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Component;

import com.financeiro.poupeja.PoupejaApplication;
import com.financeiro.poupeja.entity.Categoria;
import com.financeiro.poupeja.entity.FormaPagamento;
import com.financeiro.poupeja.entity.Usuario;
import com.financeiro.poupeja.service.AuthService;
import com.financeiro.poupeja.service.CategoriaService;
import com.financeiro.poupeja.util.MessageUtils;
import com.financeiro.poupeja.util.SpringFXMLLoader;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


@Component
public class CategoriaController {

    private final SpringFXMLLoader fxmlLoader;

    private CategoriaService categoriaService;

    private AuthService authService;

    @FXML
    private VBox containerCategorias;

    public CategoriaController(SpringFXMLLoader fxmlLoader, CategoriaService categoriaService, AuthService authService){
        this.categoriaService = categoriaService;
        this.fxmlLoader = fxmlLoader;
        this.authService = authService;
    }

    @FXML
    public void initialize() {

        Usuario usuarioLogado = authService.getUsuarioLogado();

        List<Categoria> categorias =
                categoriaService.listarPorUsuario(usuarioLogado);

        containerCategorias.getChildren().clear();

        for (Categoria categoria : categorias) {

            HBox linha = criarLinhaCategoria(categoria);

            containerCategorias.getChildren().add(linha);
        }
    }

    private HBox criarLinhaCategoria(Categoria categoria) {

        HBox linha = new HBox(10);

        linha.setAlignment(Pos.CENTER_LEFT);

        linha.setPadding(new Insets(12));

        linha.setPrefHeight(50);

        linha.setMaxWidth(400);

        linha.setStyle(
                "-fx-background-color: #F4F4F4;" +
                        "-fx-background-radius: 4;" +
                        "-fx-border-color: #D9D9D9;" +
                        "-fx-border-radius: 4;"
        );

        Label lblNome = criarLabelNome(categoria, linha);

        lblNome.setStyle(
                "-fx-font-size: 14px;"
        );

        HBox.setHgrow(lblNome, Priority.ALWAYS);

        CheckBox chkAtivo = criarCheckBoxAtivo(categoria);

        chkAtivo.setMinWidth(80);

        chkAtivo.setStyle(
                "-fx-font-size: 13px;"
        );

        linha.getChildren().addAll(lblNome, chkAtivo);

        return linha;
    }

    private Label criarLabelNome(Categoria categoria, HBox linha) {

        Label lblNome = new Label(categoria.getDescricao());

        lblNome.setStyle("-fx-font-size: 14px;");
        lblNome.setPrefWidth(250);

        configurarEdicaoNome(lblNome, categoria, linha);

        return lblNome;
    }

    private CheckBox criarCheckBoxAtivo(Categoria categoria) {

        CheckBox chkAtivo = new CheckBox("Ativo");

        chkAtivo.setSelected(categoria.getAtivo());

        chkAtivo.setOnAction(event -> {

            categoria.setAtivo(chkAtivo.isSelected());

            categoriaService.salvar(categoria);

            System.out.println(
                    "Atualizou ativo: " + categoria.getDescricao());
        });

        return chkAtivo;
    }

    private void configurarEdicaoNome(
            Label lblNome,
            Categoria categoria,
            HBox linha) {

        lblNome.setOnMouseClicked(event -> {

            if (event.getClickCount() == 2) {

                TextField txtEdicao =
                        new TextField(categoria.getDescricao());

                txtEdicao.setPrefWidth(250);

                linha.getChildren().set(0, txtEdicao);

                txtEdicao.requestFocus();

                configurarSalvarEdicao(
                        txtEdicao,
                        lblNome,
                        categoria,
                        linha);
            }
        });
    }

    private void configurarSalvarEdicao(
            TextField txtEdicao,
            Label lblNome,
            Categoria categoria,
            HBox linha) {

        txtEdicao.setOnAction(e -> {

            String novoNome = txtEdicao.getText();

            if (novoNome != null &&
                    !novoNome.isBlank()) {

                categoria.setDescricao(novoNome);

                categoriaService.salvar(categoria);

                lblNome.setText(novoNome);

                System.out.println("Descrição alterada");
            }

            linha.getChildren().set(0, lblNome);
        });

        txtEdicao.focusedProperty().addListener(
                (obs, oldVal, newVal) -> {

                    if (!newVal) {
                        linha.getChildren().set(0, lblNome);
                    }
                });
    }

    @FXML
    public void voltarParaMenu() {
        try {
            Parent root = fxmlLoader.load("/fxml/menu_principal.fxml");
            PoupejaApplication.getPrimaryStage().setTitle("PoupeJá! - Menu Principal");
            PoupejaApplication.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException e) {
            MessageUtils.erro("Não foi possível carregar a tela de Login.");
        }
    }

    @FXML
    public void cadastrarCategoria() {
        navegarPara("/fxml/cadastro_categoria.fxml",
                "PoupeJá! - Nova Categoria");
    }

    private void navegarPara(String fxmlPath, String titulo) {
        try {
            Parent root = fxmlLoader.load(fxmlPath);
            PoupejaApplication.getPrimaryStage().setTitle(titulo);
            PoupejaApplication.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException e) {
            mostrarErro("Erro de Navegação", "Não foi possível carregar a tela.");
        }
    }

    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
