package models;

import java.util.ArrayList;
import java.util.List;

public class Campeonato {
    private int id;
    private String nome;
    private float prizepool;
    

    // Uso de Collections conforme requisito do projeto 
    private List<Time> times;
    private List<Partida> partidas;

    public Campeonato() {
        this.times = new ArrayList<>();
        this.partidas = new ArrayList<>();
    }

    public Campeonato(int id, String nome, float prizepool) {
        this.id = id;
        this.nome = nome;
        this.prizepool = prizepool;
        this.times = new ArrayList<>();
        this.partidas = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Time> getTimes() {
        return times;
    }

    public void setTimes(List<Time> times) {
        this.times = times;
    }

    public List<Partida> getPartidas() {
        return partidas;
    }

    public void setPartidas(List<Partida> partidas) {
        this.partidas = partidas;
    }

    public float getPrizepool() {
        return prizepool;
    }

    public void setPrizepool(float prizepool) {
        this.prizepool = prizepool;
    }

    public void adicionarTime(Time time) {
        this.times.add(time);
    }
}