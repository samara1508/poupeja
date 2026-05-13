package com.financeiro.poupeja.controller;

import java.io.IOException;
import java.util.List;

import com.financeiro.poupeja.entity.FormaPagamento;
import com.financeiro.poupeja.entity.Usuario;
import com.financeiro.poupeja.service.AuthService;
import com.financeiro.poupeja.service.FormaPagamentoService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.springframework.stereotype.Component;

import com.financeiro.poupeja.PoupejaApplication;
import com.financeiro.poupeja.util.SpringFXMLLoader;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

@Component
public class FormaPagamentoController {

    private final SpringFXMLLoader fxmlLoader;

    @FXML
    private VBox containerFormasPagamento;

    private final FormaPagamentoService formaPagamentoService;
    private final AuthService authService;

    public FormaPagamentoController(
            SpringFXMLLoader fxmlLoader,
            FormaPagamentoService formaPagamentoService,
            AuthService authService) {

        this.fxmlLoader = fxmlLoader;
        this.formaPagamentoService = formaPagamentoService;
        this.authService = authService;
    }

    @FXML
    public void initialize() {

        Usuario usuarioLogado = authService.getUsuarioLogado();

        List<FormaPagamento> formasPagamento =
                formaPagamentoService.listarPorUsuario(usuarioLogado);

        containerFormasPagamento.getChildren().clear();

        for (FormaPagamento forma : formasPagamento) {

            HBox linha = criarLinhaFormaPagamento(forma);

            containerFormasPagamento.getChildren().add(linha);
        }
    }

    private HBox criarLinhaFormaPagamento(FormaPagamento forma) {

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

        Label lblNome = criarLabelNome(forma, linha);

        lblNome.setStyle(
                "-fx-font-size: 14px;"
        );

        HBox.setHgrow(lblNome, Priority.ALWAYS);

        CheckBox chkAtivo = new CheckBox("Ativo");

        chkAtivo.setMinWidth(80);

        chkAtivo.setStyle(
                "-fx-font-size: 13px;"
        );

        linha.getChildren().addAll(lblNome, chkAtivo);

        return linha;
    }

    private Label criarLabelNome(FormaPagamento forma, HBox linha) {

        Label lblNome = new Label(forma.getDescricao());

        lblNome.setStyle("-fx-font-size: 14px;");
        lblNome.setPrefWidth(250);

        configurarEdicaoNome(lblNome, forma, linha);

        return lblNome;
    }

    private CheckBox criarCheckBoxAtivo(FormaPagamento forma) {

        CheckBox chkAtivo = new CheckBox("Ativo");

        chkAtivo.setSelected(forma.getAtivo());

        chkAtivo.setOnAction(event -> {

            forma.setAtivo(chkAtivo.isSelected());

            formaPagamentoService.salvar(forma);

            System.out.println(
                    "Atualizou ativo: " + forma.getDescricao());
        });

        return chkAtivo;
    }

    private void configurarEdicaoNome(
            Label lblNome,
            FormaPagamento forma,
            HBox linha) {

        lblNome.setOnMouseClicked(event -> {

            if (event.getClickCount() == 2) {

                TextField txtEdicao =
                        new TextField(forma.getDescricao());

                txtEdicao.setPrefWidth(250);

                linha.getChildren().set(0, txtEdicao);

                txtEdicao.requestFocus();

                configurarSalvarEdicao(
                        txtEdicao,
                        lblNome,
                        forma,
                        linha);
            }
        });
    }

    private void configurarSalvarEdicao(
            TextField txtEdicao,
            Label lblNome,
            FormaPagamento forma,
            HBox linha) {

        txtEdicao.setOnAction(e -> {

            String novoNome = txtEdicao.getText();

            if (novoNome != null &&
                    !novoNome.isBlank()) {

                forma.setDescricao(novoNome);

                formaPagamentoService.salvar(forma);

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
    public void voltarMenu() {
        navegarPara("/fxml/menu_principal.fxml", "PoupeJá! - Menu");
    }

    @FXML
    public void selecionarDinheiro() {
        System.out.println("Selecionou: Dinheiro");
    }

    @FXML
    public void selecionarPix() {
        System.out.println("Selecionou: Pix");
    }

    @FXML
    public void selecionarCartao() {
        System.out.println("Selecionou: Cartão");
    }

    @FXML
    public void criarNovaForma() {
        navegarPara("/fxml/cadastro_forma_pagamento.fxml",
                "PoupeJá! - Nova Forma de Pagamento");
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