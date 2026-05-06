package com.financeiro.poupeja.controller;

import com.financeiro.poupeja.PoupejaApplication;
import com.financeiro.poupeja.exception.AcessoNegadoException;
import com.financeiro.poupeja.service.AuthService;
import com.financeiro.poupeja.util.SpringFXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import com.financeiro.poupeja.util.MessageUtils;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AlterarSenhaController {

    private final AuthService authService;
    private final SpringFXMLLoader fxmlLoader;

    @FXML
    private TextField txtLogin;

    @FXML
    private PasswordField txtNovaSenha;

    @FXML
    private PasswordField txtConfirmacaoSenha;

    public AlterarSenhaController(AuthService authService, SpringFXMLLoader fxmlLoader) {
        this.authService = authService;
        this.fxmlLoader = fxmlLoader;
    }

    @FXML
    public void alterarSenha() {
        String login = txtLogin.getText();
        String novaSenha = txtNovaSenha.getText();
        String confirmacaoSenha = txtConfirmacaoSenha.getText();

        try {
            authService.alterarSenha(login, novaSenha, confirmacaoSenha);
            MessageUtils.sucesso("Senha alterada com sucesso!");
            voltarParaLogin();
        } catch (IllegalArgumentException | AcessoNegadoException e) {
            MessageUtils.erro( e.getMessage());
        }
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


}
