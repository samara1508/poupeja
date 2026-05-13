package com.financeiro.poupeja.controller;

import com.financeiro.poupeja.PoupejaApplication;
import com.financeiro.poupeja.util.SpringFXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import com.financeiro.poupeja.util.MessageUtils;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class VisualizacaoCategoriaController {

    private final SpringFXMLLoader fxmlLoader;

    public VisualizacaoCategoriaController(SpringFXMLLoader fxmlLoader){
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
    public void cadastrarCategoria() {
        try {
            Parent root = fxmlLoader.load("/fxml/cria_categoria.fxml");
            PoupejaApplication.getPrimaryStage().setTitle("PoupeJá! - Cadastrar Categoria");
            PoupejaApplication.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException e) {
            MessageUtils.erro("Não Foi Possível Carregar a Tela de Criação de Categorias.");
        }
    }

}
