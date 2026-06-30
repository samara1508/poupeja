package com.financeiro.poupeja.service;

import io.mailtrap.client.MailtrapClient;
import io.mailtrap.config.MailtrapConfig;
import io.mailtrap.factory.MailtrapClientFactory;
import io.mailtrap.model.request.emails.Address;
import io.mailtrap.model.request.emails.MailtrapMail;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    private static final String TOKEN = "613d2407ada9b7b03d21fcafb04a3160";
    private final MailtrapClient client;

    public EmailService() {
        MailtrapConfig config = new MailtrapConfig.Builder()
                .token(TOKEN)
                .build();
        this.client = MailtrapClientFactory.createMailtrapClient(config);
    }

    public void enviarEmailAlerta(String destinatario, String assunto, String texto) {
        MailtrapMail mail = MailtrapMail.builder()
                .from(new Address("hello@demomailtrap.co", "PoupeJá!"))
                .to(List.of(new Address(destinatario)))
                .subject(assunto)
                .text(texto)
                .category("Alerta de Vencimento")
                .build();

        try {
            client.send(mail);
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao enviar e-mail via Mailtrap SDK: " + e.getMessage(), e);
        }
    }
}
