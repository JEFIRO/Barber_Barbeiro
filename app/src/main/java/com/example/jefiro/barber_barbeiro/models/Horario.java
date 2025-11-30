package com.example.jefiro.barber_barbeiro.models;

import java.util.List;

public class Horario {
    private String idBarbearia;
    private DiaSemana diaSemana;
    private List<Periods> periods;
    private Boolean closed;

    public Horario(DiaSemana diaSemana, List<Periods> periods, Boolean closed) {
        this.diaSemana = diaSemana;
        this.periods = periods;
        this.closed = closed;
    }

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DiaSemana diaSemana) {
        this.diaSemana = diaSemana;
    }

    public List<Periods> getPeriods() {
        return periods;
    }

    public String getIdBarbearia() {
        return idBarbearia;
    }

    public void setIdBarbearia(String idBarbearia) {
        this.idBarbearia = idBarbearia;
    }

    public void setPeriods(List<Periods> periods) {
        this.periods = periods;
    }

    public Boolean getClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }
}
