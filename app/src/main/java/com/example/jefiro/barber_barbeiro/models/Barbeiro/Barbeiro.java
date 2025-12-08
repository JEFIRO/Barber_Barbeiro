package com.example.jefiro.barber_barbeiro.models.Barbeiro;

import com.google.firebase.Timestamp;

public class Barbeiro {
    private String id;
    private String barbearia_id;
    private String nome;
    private String email;
    private String fotoBarbeiro;
    private String calendario_id;
    private boolean ativo;
    private Timestamp criadoEm;
    private Timestamp atualizadoEm;

    public Barbeiro() {
    }

    public Barbeiro(String uuid, String nome, String email, String fotoBarbeiro, String barbeariaID) {
        this.id = uuid;
        this.barbearia_id = barbeariaID;
        this.email = email;
        this.nome = nome;
        this.fotoBarbeiro = fotoBarbeiro;
        this.ativo = true;
        this.criadoEm = Timestamp.now();
        this.atualizadoEm = Timestamp.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCalendario_id() {
        return calendario_id;
    }

    public void setCalendario_id(String calendario_id) {
        this.calendario_id = calendario_id;
    }

    public String getBarbearia_id() {
        return barbearia_id;
    }

    public void setBarbearia_id(String barbearia_id) {
        this.barbearia_id = barbearia_id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFotoBarbeiro() {
        return fotoBarbeiro;
    }

    public void setFotoBarbeiro(String fotoBarbeiro) {
        this.fotoBarbeiro = fotoBarbeiro;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Timestamp getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Timestamp criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Timestamp getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(Timestamp atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
}
