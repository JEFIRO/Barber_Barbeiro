package com.example.jefiro.barber_barbeiro.models.Barbearia;

import com.example.jefiro.barber_barbeiro.models.horario.Horario;

import java.time.LocalDateTime;
import java.util.List;

public class Barbearia {
    private String id;
    private String nome;
    private String telefone;
    private String email;
    private String fotoUrl;
    private List<Horario> horarioFuncionamento;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public Barbearia(String nome, String telefone, String email, String fotoUrl, List<Horario> horarioFuncionamento) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.fotoUrl = fotoUrl;
        this.horarioFuncionamento = horarioFuncionamento;
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    public Barbearia() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public List<Horario> getHorarioFuncionamento() {
        return horarioFuncionamento;
    }

    public void setHorarioFuncionamento(List<Horario> horarioFuncionamento) {
        this.horarioFuncionamento = horarioFuncionamento;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
}
