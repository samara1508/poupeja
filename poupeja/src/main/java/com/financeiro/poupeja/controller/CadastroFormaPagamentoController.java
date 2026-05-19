package com.financeiro.poupeja.controller;

import java.io.IOException;

import javafx.scene.control.CheckBox;
import org.springframework.stereotype.Component;

import com.financeiro.poupeja.PoupejaApplication;
import com.financeiro.poupeja.entity.Usuario;
import com.financeiro.poupeja.service.AuthService;
import com.financeiro.poupeja.service.FormaPagamentoService;
import com.financeiro.poupeja.util.SpringFXMLLoader;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

@Component
public class CadastroFormaPagamentoController {

    private final SpringFXMLLoader fxmlLoader;
    private final FormaPagamentoService formaPagamentoService;
    private final AuthService authService;

    @FXML
    private TextField txtNome;

    @FXML
    private CheckBox chkAtivo;

    public CadastroFormaPagamentoController(
            SpringFXMLLoader fxmlLoader,
            FormaPagamentoService formaPagamentoService,
            AuthService authService) {

        this.fxmlLoader = fxmlLoader;
        this.formaPagamentoService = formaPagamentoService;
        this.authService = authService;
    }

    @FXML
    public void salvar() {

        String nome = txtNome.getText();
        boolean ativo = chkAtivo.isSelected();

        if (nome == null || nome.isBlank()) {
            mostrarErro("Erro",
                    "Digite um nome para a forma de pagamento.");
            return;
        }

        try {
            Usuario usuarioLogado = authService.getUsuarioLogado();

            formaPagamentoService.criarFormaPagamento(
                    nome,
                    ativo,
                    usuarioLogado
            );

            Alert alert = new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Forma de pagamento cadastrada com sucesso!");

            alert.showAndWait();

            voltar();

        } catch (IllegalArgumentException e) {

            mostrarErro("Erro", e.getMessage());
        }
    }

    @FXML
    public void voltar() {

        navegarPara("/fxml/forma_pagamento.fxml",
                "PoupeJá! - Formas de Pagamento");
    }

    private void navegarPara(String fxmlPath, String titulo) {

        try {

            Parent root = fxmlLoader.load(fxmlPath);

            PoupejaApplication.getPrimaryStage().setTitle(titulo);
            PoupejaApplication.getPrimaryStage().getScene().setRoot(root);

        } catch (IOException e) {

            mostrarErro("Erro de Navegação",
                    "Não foi possível carregar a tela.");
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