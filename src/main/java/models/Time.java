package models;
import java.util.ArrayList;
import java.util.List;

public class Time {
    private int id;
    private String nome;
    private List<Jogador> jogadores = new ArrayList<>();

    public Time() {}
    public Time(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public List<Jogador> getJogadores() { return jogadores; }
    public void setJogadores(List<Jogador> jogadores) { this.jogadores = jogadores; }
}