package com.financeiro.poupeja.enumeration;

public enum StatusAlerta {
    PENDENTE("PENDENTE"),
    CONCLUIDO("CONCLUÍDO"),
    ENVIADO("ENVIADO");

    private final String label;

    StatusAlerta(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}