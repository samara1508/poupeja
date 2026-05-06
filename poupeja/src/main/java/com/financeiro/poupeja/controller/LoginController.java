package com.financeiro.poupeja.controller;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.financeiro.poupeja.PoupejaApplication;
import com.financeiro.poupeja.exception.AcessoNegadoException;
import com.financeiro.poupeja.service.AuthService;
import com.financeiro.poupeja.util.SpringFXMLLoader;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import com.financeiro.poupeja.util.MessageUtils;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

@Component
public class LoginController {

    private final AuthService authService;
    private final SpringFXMLLoader fxmlLoader;

    @FXML
    private TextField txtLogin;

    @FXML
    private PasswordField txtSenha;

    public LoginController(AuthService authService, SpringFXMLLoader fxmlLoader) {
        this.authService = authService;
        this.fxmlLoader = fxmlLoader;
    }

    @FXML
    public void entrar() {
        String login = txtLogin.getText();
        String senha = txtSenha.getText();

        try {
            authService.login(login, senha);
            navegarPara("/fxml/menu_principal.fxml", "PoupeJá! - Menu");
        } catch (AcessoNegadoException e) {
            MessageUtils.erro(e.getMessage());
        }
    }

    @FXML
    public void esqueciSenha() {
        navegarPara("/fxml/alterar_senha.fxml", "PoupeJá! - Alterar Senha");
    }

    @FXML
    public void cadastrar() {
        navegarPara("/fxml/cadastro_usuario.fxml", "PoupeJá! - Cadastre-se");
    }

    private void navegarPara(String fxmlPath, String titulo) {
        try {
            Parent root = fxmlLoader.load(fxmlPath);
            PoupejaApplication.getPrimaryStage().setTitle(titulo);
            PoupejaApplication.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException e) {
            MessageUtils.erro("Não foi possível carregar a tela.");
        }
    }


}
