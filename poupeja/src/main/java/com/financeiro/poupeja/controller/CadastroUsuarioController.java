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
public class CadastroUsuarioController {

    private final AuthService authService;
    private final SpringFXMLLoader fxmlLoader;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtLogin;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private PasswordField txtConfirmacaoSenha;

    public CadastroUsuarioController(AuthService authService, SpringFXMLLoader fxmlLoader) {
        this.authService = authService;
        this.fxmlLoader = fxmlLoader;
    }

    @FXML
    public void cadastrar() {
        String email = txtEmail.getText();
        String login = txtLogin.getText();
        String senha = txtSenha.getText();
        String confirmacaoSenha = txtConfirmacaoSenha.getText();

        try {
            authService.criarUsuario(login, email, senha, confirmacaoSenha);
            MessageUtils.sucesso("Usuário cadastrado com sucesso!");
            voltarParaLogin();
        } catch (IllegalArgumentException | AcessoNegadoException e) {
            MessageUtils.erro(e.getMessage());
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
