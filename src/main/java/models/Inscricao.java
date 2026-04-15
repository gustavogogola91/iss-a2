package models;

public class Inscricao {
    private int id;
    private int idCampeonato;
    private int idTime;

    // Construtor padrão (necessário para Jackson)
    public Inscricao() {}

    public Inscricao(int id, int idCampeonato, int idTime) {
        this.id = id;
        this.idCampeonato = idCampeonato;
        this.idTime = idTime;
    }

    // Getters e Setters (Encapsulamento)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCampeonato() {
        return idCampeonato;
    }

    public void setIdCampeonato(int idCampeonato) {
        this.idCampeonato = idCampeonato;
    }

    public int getIdTime() {
        return idTime;
    }

    public void setIdTime(int idTime) {
        this.idTime = idTime;
    }
}