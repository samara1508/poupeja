package com.financeiro.poupeja.service;

import com.financeiro.poupeja.entity.Alerta;
import com.financeiro.poupeja.entity.Usuario;
import com.financeiro.poupeja.repository.AlertaRepository;
import com.financeiro.poupeja.util.Utils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AlertaService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final AlertaRepository repository;
    private final AuthService authService;

    public AlertaService(AlertaRepository repository, AuthService authService) {
        this.repository = repository;
        this.authService = authService;
    }

    public Page<Alerta> listarPorUsuario(String descricao, int pagina, int tamanho) {
        Usuario usuario = authService.getUsuarioLogado();
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by(Sort.Direction.DESC, "id"));
        
        if (!Utils.isEmpty(descricao)) {
            return repository.findByUsuarioAndDescricaoContainingIgnoreCase(usuario, descricao, pageable);
        }
        
        return repository.findByUsuario(usuario, pageable);
    }

    @Transactional
    public void criarAlerta(String descricao, Integer diasAntes, LocalDate dataVencimento) {
        validarDadosAlerta(descricao, diasAntes, dataVencimento);

        Usuario usuario = authService.getUsuarioLogado();

        Alerta alerta = new Alerta();
        alerta.setDescricao(descricao);
        alerta.setDiasAntes(diasAntes);
        alerta.setDataVencimento(dataVencimento);
        alerta.inicializar(usuario);

        repository.save(alerta);
    }

    @Transactional
    public void excluirAlerta(Long id) {
        Alerta alerta = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alerta não encontrado."));

        Usuario usuario = authService.getUsuarioLogado();

        if (!alerta.pertenceA(usuario)) {
            throw new IllegalArgumentException("Você não tem permissão para excluir este alerta.");
        }

        repository.delete(alerta);
    }

    @Scheduled(initialDelay = 10000, fixedRate = 30000)
    @Transactional
    public void verificarEDispararAlertasAgendados() {
        System.out.println("Iniciando verificação demonstrativa de alertas agendados (intervalo: 30s)...");

        List<Alerta> alertasPendentes = repository.findByAtivoTrueAndStatus("PENDENTE");
        LocalDate hoje = LocalDate.now();

        for (Alerta alerta : alertasPendentes) {
            try {
                processar(alerta, hoje);
            } catch (Exception e) {
                System.err.println("Erro ao processar alerta ID " + alerta.getId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("Fim da verificação demonstrativa de alertas.");
    }

    private void validarDadosAlerta(String descricao, Integer diasAntes, LocalDate dataVencimento) {
        if (Utils.isEmpty(descricao)) {
            throw new IllegalArgumentException("A descrição é obrigatória.");
        }
        if (Utils.isEmpty(diasAntes) || diasAntes < 0) {
            throw new IllegalArgumentException("O espaçamento de dias deve ser maior ou igual a zero.");
        }
        if (Utils.isEmpty(dataVencimento)) {
            throw new IllegalArgumentException("A data de vencimento é obrigatória.");
        }
    }

    private void processar(Alerta alerta, LocalDate hoje) {
        if (Utils.isEmpty(alerta.getDataVencimento())) return;

        if (alerta.estaVencido(hoje)) {
            marcarComoVencido(alerta);
            return;
        }

        if (alerta.deveLancarAlerta(hoje)) {
            simularEnvioEmail(alerta, hoje);
        }
    }

    private void marcarComoVencido(Alerta alerta) {
        alerta.setStatus("CONCLUIDO");
        alerta.setAtivo(false);
        
        repository.save(alerta);
        
        System.out.println("Alerta ID " + alerta.getId() + " marcado como CONCLUIDO (vencimento ultrapassado).");
    }

    private void simularEnvioEmail(Alerta alerta, LocalDate hoje) {
        System.out.println("\n=== [MOCK EMAIL DISPATCH] ===");
        System.out.println("Enviando e-mail para: " + alerta.getEmail());
        System.out.println("Assunto: Alerta de Vencimento - PoupeJá!");
        System.out.println("Conteúdo:");
        System.out.println("--------------------------------------------------");
        System.out.println("Atenção! Sua conta está prestes a vencer!");
        System.out.println("Não esqueça de regularizar :)");
        System.out.println("Data de vencimento: " + alerta.getDataVencimento().format(FORMATTER));
        System.out.println("Descrição: " + alerta.getDescricao());
        System.out.println();
        System.out.println("Este email foi enviado automaticamente pelo sistema PoupeJá! e não precisa ser respondido.");
        System.out.println("--------------------------------------------------");
        System.out.println("==================================================\n");

        alerta.setUltimaExecucao(hoje);
        alerta.setStatus("ENVIADO");
        alerta.setAtivo(false);
        
        repository.save(alerta);
        
        System.out.println("Alerta ID " + alerta.getId() + " processado e marcado como ENVIADO no banco de dados.");
    }
}
