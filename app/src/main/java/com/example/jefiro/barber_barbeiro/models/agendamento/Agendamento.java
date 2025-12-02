package com.example.jefiro.barber_barbeiro.models.agendamento;

import com.google.firebase.Timestamp;

public class Agendamento {
    private String idBarbearia;
    private String idBarbeiro;
    private String idServico;
    private String idCliente;
    private Timestamp data;
    private Timestamp hora;
    private boolean confirmado;
    private Timestamp criadoem;

    public Agendamento() {
    }

    public Agendamento(String idBarbearia, String idBarbeiro, String idServico, String idCliente, Timestamp data, Timestamp hora) {
        this.idBarbearia = idBarbearia;
        this.idBarbeiro = idBarbeiro;
        this.idServico = idServico;
        this.idCliente = idCliente;
        this.data = data;
        this.hora = hora;
        this.criadoem = Timestamp.now();
    }

    public Agendamento(String idBarbearia, String idBarbeiro, String idServico, String idCliente, Timestamp data, Timestamp hora, boolean confirmado, Timestamp criadoem) {
        this.idBarbearia = idBarbearia;
        this.idBarbeiro = idBarbeiro;
        this.idServico = idServico;
        this.idCliente = idCliente;
        this.data = data;
        this.hora = hora;
        this.confirmado = confirmado;
        this.criadoem = criadoem;
    }

    public String getIdBarbearia() {
        return idBarbearia;
    }

    public void setIdBarbearia(String idBarbearia) {
        this.idBarbearia = idBarbearia;
    }

    public String getIdBarbeiro() {
        return idBarbeiro;
    }

    public void setIdBarbeiro(String idBarbeiro) {
        this.idBarbeiro = idBarbeiro;
    }

    public String getIdServico() {
        return idServico;
    }

    public void setIdServico(String idServico) {
        this.idServico = idServico;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public Timestamp getData() {
        return data;
    }

    public void setData(Timestamp data) {
        this.data = data;
    }

    public Timestamp getHora() {
        return hora;
    }

    public void setHora(Timestamp hora) {
        this.hora = hora;
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public void setConfirmado(boolean confirmado) {
        this.confirmado = confirmado;
    }

    public Timestamp getCriadoem() {
        return criadoem;
    }

    public void setCriadoem(Timestamp criadoem) {
        this.criadoem = criadoem;
    }
}
