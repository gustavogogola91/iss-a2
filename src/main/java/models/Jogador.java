package models;

public class Jogador {
    private int id;
    private String nome;
    private int timeId;

    public Jogador() {}
    public Jogador(int id, String nome, int timeId) {
        this.id = id;
        this.nome = nome;
        this.timeId = timeId;
    }

    // Getters e Setters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public int getTimeId() { return timeId; }
}