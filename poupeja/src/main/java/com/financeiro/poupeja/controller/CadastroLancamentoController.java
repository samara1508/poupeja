package com.financeiro.poupeja.controller;

import com.financeiro.poupeja.PoupejaApplication;
import com.financeiro.poupeja.entity.Categoria;
import com.financeiro.poupeja.entity.FormaPagamento;
import com.financeiro.poupeja.entity.Lancamento;
import com.financeiro.poupeja.entity.Usuario;
import com.financeiro.poupeja.enumeration.TipoLancamento;
import com.financeiro.poupeja.enumeration.TipoRecorrencia;
import com.financeiro.poupeja.service.AuthService;
import com.financeiro.poupeja.service.CategoriaService;
import com.financeiro.poupeja.service.FormaPagamentoService;
import com.financeiro.poupeja.service.LancamentoService;
import com.financeiro.poupeja.util.LancamentoEdicaoContext;
import com.financeiro.poupeja.util.MessageUtils;
import com.financeiro.poupeja.util.SpringFXMLLoader;
import com.financeiro.poupeja.util.Utils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;

@Component
public class CadastroLancamentoController {

    private final SpringFXMLLoader fxmlLoader;
    private final LancamentoService lancamentoService;
    private final CategoriaService categoriaService;
    private final FormaPagamentoService formaPagamentoService;
    private final AuthService authService;
    private final LancamentoEdicaoContext lancamentoEdicaoContext;

    @FXML
    private Label lblTitulo;

    @FXML
    private TextField txtDescricao;

    @FXML
    private TextField txtValor;

    @FXML
    private TextField txtQuantidadeParcelas;

    @FXML
    private DatePicker dpData;

    @FXML
    private ComboBox<TipoLancamento> cbTipo;

    @FXML
    private ComboBox<TipoRecorrencia> cbRecorrencia;

    @FXML
    private ComboBox<Categoria> cbCategoria;

    @FXML
    private ComboBox<FormaPagamento> cbFormaPagamento;

    public CadastroLancamentoController(
            SpringFXMLLoader fxmlLoader,
            LancamentoService lancamentoService,
            CategoriaService categoriaService,
            FormaPagamentoService formaPagamentoService,
            AuthService authService,
            LancamentoEdicaoContext lancamentoEdicaoContext) {
        this.fxmlLoader = fxmlLoader;
        this.lancamentoService = lancamentoService;
        this.categoriaService = categoriaService;
        this.formaPagamentoService = formaPagamentoService;
        this.authService = authService;
        this.lancamentoEdicaoContext = lancamentoEdicaoContext;
    }

    @FXML
    public void initialize() {
        Usuario usuario = authService.getUsuarioLogado();

        cbTipo.setItems(FXCollections.observableArrayList(TipoLancamento.values()));
        cbRecorrencia.setItems(FXCollections.observableArrayList(TipoRecorrencia.values()));
        cbCategoria.setItems(FXCollections.observableArrayList(categoriaService.listarAtivasPorUsuario(usuario)));
        cbFormaPagamento.setItems(FXCollections.observableArrayList(formaPagamentoService.listarAtivasPorUsuario(usuario)));

        cbTipo.setValue(TipoLancamento.DESPESA);
        cbRecorrencia.setValue(TipoRecorrencia.VARIAVEL);
        dpData.setValue(LocalDate.now());
        txtQuantidadeParcelas.setText("1");

        configurarConversores();

        if (lancamentoEdicaoContext.isEditando()) {
            carregarLancamentoParaEdicao();
        }
    }

    private void carregarLancamentoParaEdicao() {
        try {
            Lancamento lancamento = lancamentoService.buscarPorIdDoUsuario(lancamentoEdicaoContext.getLancamentoId());
            lblTitulo.setText("Editar Lancamento");
            txtDescricao.setText(lancamento.getDescricao());
            txtValor.setText(String.valueOf(lancamento.getValorTotal()).replace(".", ","));
            txtQuantidadeParcelas.setText(String.valueOf(
                    lancamento.getParcelas() == null || lancamento.getParcelas().isEmpty()
                            ? 1
                            : lancamento.getParcelas().size()));
            dpData.setValue(lancamento.getData());
            cbTipo.setValue(lancamento.getTipo());
            cbRecorrencia.setValue(lancamento.getRecorrencia());
            selecionarCategoria(lancamento.getCategoria());
            selecionarFormaPagamento(lancamento.getFormaPagamento());
        } catch (IllegalArgumentException e) {
            MessageUtils.erro(e.getMessage());
            voltar();
        }
    }

    private void selecionarCategoria(Categoria categoria) {
        if (categoria == null || categoria.getId() == null) {
            cbCategoria.setValue(categoria);
            return;
        }

        cbCategoria.getItems().stream()
                .filter(item -> categoria.getId().equals(item.getId()))
                .findFirst()
                .ifPresentOrElse(cbCategoria::setValue, () -> cbCategoria.setValue(categoria));
    }

    private void selecionarFormaPagamento(FormaPagamento formaPagamento) {
        if (formaPagamento == null || formaPagamento.getId() == null) {
            cbFormaPagamento.setValue(formaPagamento);
            return;
        }

        cbFormaPagamento.getItems().stream()
                .filter(item -> formaPagamento.getId().equals(item.getId()))
                .findFirst()
                .ifPresentOrElse(cbFormaPagamento::setValue, () -> cbFormaPagamento.setValue(formaPagamento));
    }

    private void configurarConversores() {
        cbCategoria.setConverter(new StringConverter<>() {
            @Override
            public String toString(Categoria categoria) {
                return categoria == null ? "" : categoria.getDescricao();
            }

            @Override
            public Categoria fromString(String string) {
                return null;
            }
        });

        cbFormaPagamento.setConverter(new StringConverter<>() {
            @Override
            public String toString(FormaPagamento formaPagamento) {
                return formaPagamento == null ? "" : formaPagamento.getDescricao();
            }

            @Override
            public FormaPagamento fromString(String string) {
                return null;
            }
        });
    }

    @FXML
    public void salvar() {
        String descricao = txtDescricao.getText();
        Double valor = parseValor(txtValor.getText());
        Integer quantidadeParcelas = parseQuantidadeParcelas(txtQuantidadeParcelas.getText());
        LocalDate data = dpData.getValue();

        if (Utils.isEmpty(descricao)) {
            MessageUtils.erro("A descricao e obrigatoria.");
            return;
        }
        if (valor == null || valor <= 0) {
            MessageUtils.erro("Informe um valor maior que zero.");
            return;
        }
        if (quantidadeParcelas == null || quantidadeParcelas <= 0) {
            MessageUtils.erro("Informe uma quantidade de parcelas maior que zero.");
            return;
        }

        try {
            if (lancamentoEdicaoContext.isEditando()) {
                lancamentoService.atualizarLancamento(
                        lancamentoEdicaoContext.getLancamentoId(),
                        descricao,
                        valor,
                        data,
                        cbTipo.getValue(),
                        cbRecorrencia.getValue(),
                        cbCategoria.getValue(),
                        cbFormaPagamento.getValue(),
                        quantidadeParcelas);
                MessageUtils.sucesso("Lancamento atualizado com sucesso!");
            } else {
                lancamentoService.criarLancamento(
                        descricao,
                        valor,
                        data,
                        cbTipo.getValue(),
                        cbRecorrencia.getValue(),
                        cbCategoria.getValue(),
                        cbFormaPagamento.getValue(),
                        quantidadeParcelas);
                MessageUtils.sucesso("Lancamento cadastrado com sucesso!");
            }

            lancamentoEdicaoContext.limpar();
            voltar();
        } catch (IllegalArgumentException e) {
            MessageUtils.erro(e.getMessage());
        }
    }

    private Integer parseQuantidadeParcelas(String quantidadeTexto) {
        if (Utils.isEmpty(quantidadeTexto)) {
            return null;
        }

        try {
            return Integer.parseInt(quantidadeTexto.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseValor(String valorTexto) {
        if (Utils.isEmpty(valorTexto)) {
            return null;
        }

        try {
            return Double.parseDouble(valorTexto.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @FXML
    public void voltar() {
        lancamentoEdicaoContext.limpar();
        navegarPara("/fxml/lancamentos.fxml", "PoupeJa! - Lancamentos");
    }

    private void navegarPara(String fxmlPath, String titulo) {
        try {
            Parent root = fxmlLoader.load(fxmlPath);
            PoupejaApplication.getPrimaryStage().setTitle(titulo);
            PoupejaApplication.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException e) {
            MessageUtils.erro("Nao foi possivel carregar a tela.");
            e.printStackTrace();
        }
    }
}
