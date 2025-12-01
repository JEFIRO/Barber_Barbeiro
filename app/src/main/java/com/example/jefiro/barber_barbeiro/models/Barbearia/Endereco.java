package com.example.jefiro.barber_barbeiro.models.Barbearia;

public class Endereco {
    private String idBarbearia;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;
    private Double lat;
    private Double log;

    public Endereco() {
    }

    public Endereco(String numero, String bairro, String cidade, String estado, Double lat, Double log) {
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.lat = lat;
        this.log = log;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLog() {
        return log;
    }

    public String getIdBarbearia() {
        return idBarbearia;
    }

    public void setIdBarbearia(String idBarbearia) {
        this.idBarbearia = idBarbearia;
    }

    public void setLog(Double log) {
        this.log = log;
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