package com.financeiro.poupeja.controller;

import com.financeiro.poupeja.PoupejaApplication;
import com.financeiro.poupeja.util.SpringFXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MenuPrincipalController {

    private final SpringFXMLLoader fxmlLoader;

    public MenuPrincipalController(SpringFXMLLoader fxmlLoader) {
        this.fxmlLoader = fxmlLoader;
    }

    @FXML
    public void sair() {
        try {
            Parent root = fxmlLoader.load("/fxml/login.fxml");
            PoupejaApplication.getPrimaryStage().setTitle("PoupeJá! - Login");
            PoupejaApplication.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException e) {
            mostrarErro("Erro de Navegação", "Não foi possível carregar a tela de Login.");
        }
    }

    @FXML
    public void acaoNaoImplementada() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Em Construção");
        alert.setHeaderText(null);
        alert.setContentText("Esta funcionalidade ainda não foi implementada.");
        alert.showAndWait();
    }

    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
