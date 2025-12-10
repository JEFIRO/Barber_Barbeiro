package com.example.jefiro.barber_barbeiro.models.agendamento;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class Agendamento {
    private String id;
    private String idBarbearia;
    private String idBarbeiro;
    private String idServico;
    private String idCliente;
    private Timestamp data_agendada;
    private boolean confirmado;
    private Timestamp criadoem;
    private Boolean status;

    public Agendamento() {
    }

    public Agendamento(String idBarbearia, String idBarbeiro, String idServico, String idCliente) {
        this.idBarbearia = idBarbearia;
        this.idBarbeiro = idBarbeiro;
        this.idServico = idServico;
        this.idCliente = idCliente;
        this.criadoem = Timestamp.now();
    }

    public Agendamento(String idBarbearia, String idBarbeiro, String idServico, String idCliente,  boolean confirmado, Timestamp criadoem) {
        this.idBarbearia = idBarbearia;
        this.idBarbeiro = idBarbeiro;
        this.idServico = idServico;
        this.idCliente = idCliente;
        this.confirmado = confirmado;
        this.criadoem = criadoem;
    }

    public String juntarDataHora() {
        LocalDateTime data = LocalDateTime.ofInstant(data_agendada.toDate().toInstant(), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy 'às' HH:mm", new Locale("pt", "BR"));
        String dataFormatada = data.format(formatter);


        return dataFormatada;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getData_agendada() {
        return data_agendada;
    }

    public void setData_agendada(Timestamp data_agendada) {
        this.data_agendada = data_agendada;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
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
