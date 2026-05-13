package com.financeiro.poupeja.controller;


import com.financeiro.poupeja.PoupejaApplication;
import com.financeiro.poupeja.util.SpringFXMLLoader;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TextField;

import com.financeiro.poupeja.util.MessageUtils;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class CadastroCategoriaController {

    private final SpringFXMLLoader fxmlLoader;

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtLimite;

    public CadastroCategoriaController(SpringFXMLLoader fxmlLoader){
        this.fxmlLoader = fxmlLoader;
    }

    @FXML
    public void voltarParaLogin() {
        try {
            Parent root = fxmlLoader.load("/fxml/login.fxml");
            PoupejaApplication.getPrimaryStage().setTitle("PoupeJá! - Login");
            PoupejaApplication.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException e) {
            MessageUtils.erro("Não foi possível carregar a tela de Login.");
        }
    }

    @FXML
    public void voltarParaCategorias() {
        try {
            Parent root = fxmlLoader.load("/fxml/lista_categoria.fxml");
            PoupejaApplication.getPrimaryStage().setTitle("PoupeJá! - Login");
            PoupejaApplication.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException e) {
            MessageUtils.erro("Não Foi Possível Carregar a Tela de Categorias.");
        }
    }

    @FXML
    public void criaCategoria() {
        String nome = txtNome.getText();
        String limite = txtLimite.getText();
        MessageUtils.informacao("Nome: " + nome + "; Limite: " + limite + ".");
    }
}
