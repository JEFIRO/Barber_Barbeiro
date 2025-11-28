package com.example.jefiro.barber_barbeiro.models.Barbearia;

public class Endereco {
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;

    public Endereco() {
    }

    public Endereco(String numero, String bairro, String cidade, String estado) {
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}