package com.example.jefiro.barber_barbeiro.models;

import java.time.LocalDateTime;

public class Servicos {
    private String barbearia_id;
    private String nome;
    private String duracao;
    private Double preco;
    private boolean ativo;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public Servicos() {
    }

    public Servicos(String barbearia_id, String nome, String duracao, Double preco) {
        this.barbearia_id = barbearia_id;
        this.nome = nome;
        this.duracao = duracao;
        this.preco = preco;

        this.ativo = true;
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
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

    public String getDuracao() {
        return duracao;
    }

    public void setDuracao(String duracao) {
        this.duracao = duracao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
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
