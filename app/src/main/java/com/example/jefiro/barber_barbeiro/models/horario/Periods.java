package com.example.jefiro.barber_barbeiro.models.horario;

public class Periods {
    private String open;
    private String close;

    public Periods(String open, String close) {
        this.open = open;
        this.close = close;
    }

    public Periods() {
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }
}
