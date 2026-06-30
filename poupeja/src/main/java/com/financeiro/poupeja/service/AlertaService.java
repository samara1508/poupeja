package com.financeiro.poupeja.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financeiro.poupeja.entity.Alerta;
import com.financeiro.poupeja.entity.Lancamento;
import com.financeiro.poupeja.entity.Usuario;
import com.financeiro.poupeja.enumeration.StatusAlerta;
import com.financeiro.poupeja.event.LoginEvent;
import com.financeiro.poupeja.repository.AlertaRepository;
import com.financeiro.poupeja.util.Utils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AlertaService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final AlertaRepository repository;
    private final AuthService authService;
    private final EmailService emailService;
    private final TaskScheduler taskScheduler;
    private ScheduledFuture<?> agendamentoFuturo;

    public AlertaService(AlertaRepository repository, AuthService authService, EmailService emailService, TaskScheduler taskScheduler) {
        this.repository = repository;
        this.authService = authService;
        this.emailService = emailService;
        this.taskScheduler = taskScheduler;
    }

    @EventListener
    public void iniciarVerificacaoDeAlertas(LoginEvent event) {
        Usuario usuario = event.getUsuario();
        boolean temPendentes = repository.existsByUsuarioAndAtivoTrueAndStatus(usuario, StatusAlerta.PENDENTE);
        
        if (temPendentes && agendamentoFuturo == null) {
            log.info("Alertas pendentes encontrados para o usuário logado. Iniciando agendamento (intervalo: 1min)...");
            agendamentoFuturo = taskScheduler.scheduleAtFixedRate(this::verificarEDispararAlertasAgendados, Duration.ofMinutes(1));
        }
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
    public void criarAlerta(String descricao, Integer diasAntes, LocalDate dataVencimento, Lancamento lancamento) {
        validarDadosAlerta(descricao, diasAntes, dataVencimento, lancamento);

        Usuario usuario = authService.getUsuarioLogado();

        Alerta alerta = new Alerta();
        alerta.setDescricao(descricao);
        alerta.setDiasAntes(diasAntes);
        alerta.setDataVencimento(dataVencimento);
        alerta.setLancamento(lancamento);
        alerta.inicializar(usuario);

        repository.save(alerta);

        if (agendamentoFuturo == null) {
            log.info("Novo alerta pendente cadastrado. Iniciando agendamento periódico...");
            agendamentoFuturo = taskScheduler.scheduleAtFixedRate(this::verificarEDispararAlertasAgendados, Duration.ofMinutes(1));
        }
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

    @Transactional(readOnly = true)
    public void verificarEDispararAlertasAgendados() {
        Usuario usuario = authService.getUsuarioLogado();
        if (Utils.isEmpty(usuario)) {
            if (agendamentoFuturo != null) {
                agendamentoFuturo.cancel(false);
                agendamentoFuturo = null;
                log.warn("Nenhum usuário logado. Agendamento suspenso.");
            }
            return;
        }

        List<Alerta> alertasPendentes = repository.findByUsuarioAndAtivoTrueAndStatus(usuario, StatusAlerta.PENDENTE);
        
        if (alertasPendentes.isEmpty()) {
            if (agendamentoFuturo != null) {
                agendamentoFuturo.cancel(false);
                agendamentoFuturo = null;
                log.warn("Sem mais alertas pendentes. Agendamento suspenso.");
            }
            return;
        }

        log.info("Verificando alertas agendados para o usuário logado...");
        LocalDate hoje = LocalDate.now();

        for (Alerta alerta : alertasPendentes) {
            try {
                processar(alerta, hoje);
            } catch (Exception e) {
            	log.error("Erro ao processar alerta ID " + alerta.getId() + ": " + e.getMessage(), e);
                throw e;
            }
        }
    }

    private void validarDadosAlerta(String descricao, Integer diasAntes, LocalDate dataVencimento, Lancamento lancamento) {
        if (Utils.isEmpty(descricao)) {
            throw new IllegalArgumentException("A descrição é obrigatória.");
        }
        if (Utils.isEmpty(diasAntes) || diasAntes < 0) {
            throw new IllegalArgumentException("O espaçamento de dias deve ser maior ou igual a zero.");
        }
        if (Utils.isEmpty(dataVencimento)) {
            throw new IllegalArgumentException("A data de vencimento é obrigatória.");
        }
        if (Utils.isEmpty(lancamento)) {
            throw new IllegalArgumentException("O lançamento é obrigatório.");
        }
    }

    private void processar(Alerta alerta, LocalDate hoje) {
        if (Utils.isEmpty(alerta.getDataVencimento())) return;

        if (alerta.estaVencido(hoje)) {
            marcarComoVencido(alerta);
            return;
        }

        if (alerta.deveLancarAlerta(hoje)) {
            enviarEmail(alerta, hoje);
        }
    }

    private void marcarComoVencido(Alerta alerta) {
        alerta.setStatus(StatusAlerta.CONCLUIDO);
        alerta.setAtivo(false);
        
        repository.save(alerta);
        
        log.warn("Alerta ID " + alerta.getId() + " marcado como CONCLUIDO (vencimento ultrapassado).");
    }

    private void enviarEmail(Alerta alerta, LocalDate hoje) {
        log.info("Enviando e-mail para: " + alerta.getEmail());
        
        String assunto = "Alerta de Vencimento - PoupeJá!";
        String texto = "Atenção! Sua conta está prestes a vencer!\n" +
                "Não esqueça de regularizar :)\n" +
                "Data de vencimento: " + alerta.getDataVencimento().format(FORMATTER) + "\n" +
                "Descrição: " + alerta.getDescricao() + "\n\n" +
                "Este email foi enviado automaticamente pelo sistema PoupeJá! e não precisa ser respondido.";
        
        emailService.enviarEmailAlerta(alerta.getEmail(), assunto, texto);

        alerta.setUltimaExecucao(hoje);
        alerta.setStatus(StatusAlerta.ENVIADO);
        alerta.setAtivo(false);
        
        repository.save(alerta);
        
        log.info("Alerta ID " + alerta.getId() + " processado e marcado como ENVIADO no banco de dados.");
    }
}
