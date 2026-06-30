package com.financeiro.poupeja.controller;


import com.financeiro.poupeja.PoupejaApplication;
import com.financeiro.poupeja.entity.Usuario;
import com.financeiro.poupeja.exception.AcessoNegadoException;
import com.financeiro.poupeja.util.SpringFXMLLoader;
import com.financeiro.poupeja.service.CategoriaService;
import com.financeiro.poupeja.service.FormaPagamentoService;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import com.financeiro.poupeja.service.AuthService;
import com.financeiro.poupeja.service.CategoriaService;

import com.financeiro.poupeja.util.MessageUtils;
import org.springframework.stereotype.Component;
import java.io.IOException;

import javafx.scene.control.CheckBox;


@Component
public class CadastroCategoriaController {


    private final SpringFXMLLoader fxmlLoader;
    private CategoriaService categoriaService;
    private AuthService authService;


    @FXML
    private TextField txtDescricao;

    @FXML
    private TextField txtLimite;

    @FXML
    private CheckBox chkAtivo;

    public CadastroCategoriaController(CategoriaService categoriaService, SpringFXMLLoader fxmlLoader, AuthService authService){

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
            Parent root = fxmlLoader.load("/fxml/categoria.fxml");
            PoupejaApplication.getPrimaryStage().setTitle("PoupeJá! - Categorias");
            PoupejaApplication.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException e) {
            MessageUtils.erro("Não Foi Possível Carregar a Tela de Categorias.");
        }
    }

    @FXML
    public void salvar() {

        String descricao = txtDescricao.getText();
        if (descricao == null || descricao.isBlank()) {
            mostrarErro("Erro",
                    "Digite um nome para a categoria.");
            return;
        }

        Double limite = parseLimite(txtLimite.getText());
        if(limite == null){
            mostrarErro("Erro",
                    "Digite um limite valido para a categoria.");
            return;
        }
        if(limite < 0){
            mostrarErro("Erro",
                "Numeros menores que zero nao sao aceitos.");
            return;
        }


        try {

            Usuario usuarioLogado = authService.getUsuarioLogado();

            categoriaService.criarCategoria(
                    null,
                    descricao,
                    limite,
                    chkAtivo.isSelected(),
                    usuarioLogado
            );

            Alert alert = new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Categoria cadastrada com sucesso!");

            alert.showAndWait();

            voltar();

        } catch (IllegalArgumentException e) {

            mostrarErro("Erro", e.getMessage());
        }
    }

    private Double parseLimite(String limiteTexto) {
        if (limiteTexto == null || limiteTexto.isBlank()) {
            return null;
        }

        try {
            return Double.parseDouble(limiteTexto.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }



    @FXML
    public void voltar() {

        navegarPara("/fxml/categoria.fxml",
                "PoupeJá! - Categorias");
    }

    private void navegarPara(String fxmlPath, String titulo) {

        try {

            Parent root = fxmlLoader.load(fxmlPath);

            PoupejaApplication.getPrimaryStage().setTitle(titulo);
            PoupejaApplication.getPrimaryStage().getScene().setRoot(root);

        } catch (IOException e) {

            System.out.println(e);
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
