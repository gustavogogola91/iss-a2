package models;

import java.util.Date;

public class Partida {
    private int id;
    private Date data;
    private int idCampeonato;
    private int idTimeA;
    private int idTimeB;
    private String resultado; // Exemplo: "2 x 1"

    // Construtor padrão para o Jackson (Desserialização)
    public Partida() {}

    public Partida(int id, Date data, int idCampeonato, int idTimeA, int idTimeB, String resultado) {
        this.id = id;
        this.data = data;
        this.idCampeonato = idCampeonato;
        this.idTimeA = idTimeA;
        this.idTimeB = idTimeB;
        this.resultado = resultado;
    }

    // Getters e Setters (Encapsulamento)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public int getIdCampeonato() {
        return idCampeonato;
    }

    public void setIdCampeonato(int idCampeonato) {
        this.idCampeonato = idCampeonato;
    }

    public int getIdTimeA() {
        return idTimeA;
    }

    public void setIdTimeA(int idTimeA) {
        this.idTimeA = idTimeA;
    }

    public int getIdTimeB() {
        return idTimeB;
    }

    public void setIdTimeB(int idTimeB) {
        this.idTimeB = idTimeB;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
}