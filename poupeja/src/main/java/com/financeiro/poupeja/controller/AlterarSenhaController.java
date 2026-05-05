package com.financeiro.poupeja.controller;

import com.financeiro.poupeja.PoupejaApplication;
import com.financeiro.poupeja.service.AuthService;
import com.financeiro.poupeja.util.SpringFXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
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

        if (login.isEmpty() || novaSenha.isEmpty() || confirmacaoSenha.isEmpty()) {
            mostrarErro("Validação", "Todos os campos são obrigatórios.");
            return;
        }

        try {
            authService.alterarSenha(login, novaSenha, confirmacaoSenha);
            mostrarSucesso("Sucesso", "Senha alterada com sucesso!");
            voltarParaLogin();
        } catch (IllegalArgumentException | com.financeiro.poupeja.exception.AcessoNegadoException e) {
            mostrarErro("Erro ao Alterar Senha", e.getMessage());
        }
    }

    @FXML
    public void voltarParaLogin() {
        try {
            Parent root = fxmlLoader.load("/fxml/login.fxml");
            PoupejaApplication.getPrimaryStage().setTitle("PoupeJá! - Login");
            PoupejaApplication.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException e) {
            mostrarErro("Erro de Navegação", "Não foi possível carregar a tela de Login.");
        }
    }

    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarSucesso(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
