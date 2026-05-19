package com.financeiro.poupeja.controller;

import com.financeiro.poupeja.PoupejaApplication;
import com.financeiro.poupeja.service.AlertaService;
import com.financeiro.poupeja.util.MessageUtils;
import com.financeiro.poupeja.util.SpringFXMLLoader;
import com.financeiro.poupeja.util.Utils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;

@Component
public class CadastroAlertaController {

    private final SpringFXMLLoader fxmlLoader;
    private final AlertaService alertaService;

    @FXML
    private TextField txtDescricao;

    @FXML
    private TextField txtDiasAntes;

    @FXML
    private DatePicker dpDataVencimento;

    public CadastroAlertaController(SpringFXMLLoader fxmlLoader, AlertaService alertaService) {
        this.fxmlLoader = fxmlLoader;
        this.alertaService = alertaService;
    }

    @FXML
    public void salvar() {
        String descricao = txtDescricao.getText();
        String diasAntesText = txtDiasAntes.getText();
        LocalDate dataVencimento = dpDataVencimento.getValue();

        if (Utils.isEmpty(descricao)) {
            MessageUtils.erro("A descrição é obrigatória.");
            return;
        }

        if (Utils.isEmpty(diasAntesText)) {
            MessageUtils.erro("O espaçamento de dias antecedente é obrigatório.");
            return;
        }

        Integer diasAntes;
        try {
            diasAntes = Integer.parseInt(diasAntesText.trim());
            if (diasAntes < 0) {
                MessageUtils.erro("O espaçamento de dias deve ser maior ou igual a zero.");
                return;
            }
        } catch (NumberFormatException e) {
            MessageUtils.erro("O espaçamento de dias deve ser um número inteiro válido.");
            return;
        }

        if (Utils.isEmpty(dataVencimento)) {
            MessageUtils.erro("A data de vencimento é obrigatória.");
            return;
        }

        try {
            alertaService.criarAlerta(descricao, diasAntes, dataVencimento);
            MessageUtils.sucesso("Alerta cadastrado com sucesso!");
            voltar();
        } catch (IllegalArgumentException e) {
            MessageUtils.erro(e.getMessage());
        }
    }

    @FXML
    public void voltar() {
        navegarPara("/fxml/alertas.fxml", "PoupeJá! - Alertas");
    }

    private void navegarPara(String fxmlPath, String titulo) {
        try {
            Parent root = fxmlLoader.load(fxmlPath);
            PoupejaApplication.getPrimaryStage().setTitle(titulo);
            PoupejaApplication.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException e) {
            MessageUtils.erro("Não foi possível carregar a tela.");
            e.printStackTrace();
        }
    }
}
