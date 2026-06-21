package com.financeiro.poupeja.controller;


import com.financeiro.poupeja.PoupejaApplication;
import com.financeiro.poupeja.entity.Usuario;
import com.financeiro.poupeja.exception.AcessoNegadoException;
import com.financeiro.poupeja.util.SpringFXMLLoader;
import com.financeiro.poupeja.service.AuthService;
import com.financeiro.poupeja.service.CategoriaService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TextField;

import com.financeiro.poupeja.util.MessageUtils;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class CadastroCategoriaController {

    private final SpringFXMLLoader fxmlLoader;
    private final CategoriaService categoriaService;
    private final AuthService authService;

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtLimite;

    public CadastroCategoriaController(
            CategoriaService categoriaService,
            SpringFXMLLoader fxmlLoader,
            AuthService authService){
        this.categoriaService = categoriaService;
        this.fxmlLoader = fxmlLoader;
        this.authService = authService;
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
        String descricao = txtNome.getText();
        double meta = Double.parseDouble(txtLimite.getText());
        MessageUtils.informacao("Nome: " + descricao + "; Limite: " + meta + ".");

        try {
            Usuario usuarioLogado = authService.getUsuarioLogado();
            categoriaService.cadastrarCategoria(null, descricao, meta, true, usuarioLogado);
            MessageUtils.sucesso("Categoria cadastrada com sucesso!");
            voltarParaLogin();
        } catch (IllegalArgumentException | AcessoNegadoException e) {
            MessageUtils.erro(e.getMessage());
        }
    }
}
