package repository;

import data.ConnectionFactory;
import exceptions.NotFoundException;
import models.Campeonato;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PartidaRepository {

    public List<Campeonato> listarCampeonatos() throws SQLException {
        List<Campeonato> campeonatos = new ArrayList<>();

        String sql = "SELECT * FROM tb_campeonato";

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Campeonato c = new Campeonato(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getFloat("prizepool")
                );

                campeonatos.add(c);
            }
        }
        return campeonatos;
    }

    public Campeonato buscarCampeonatoPorId(String id) throws SQLException, NotFoundException {

        Campeonato campeonato;

        String sql = "SELECT * FROM tb_campeonato as a WHERE a.id = (?)";

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);

            int idInt = Integer.parseInt(id);

            stmt.setInt(1, idInt);

            ResultSet rs = stmt.executeQuery();

            if(!rs.next()) {
                throw new NotFoundException();
            };

            campeonato = new Campeonato(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getFloat("prizepool")
            );
        }

        return campeonato;
    }

    public void salvarCampeonato(Campeonato novoCampeonato) throws SQLException {

        String sql = "INSERT INTO tb_campeonato (nome, prizepool) VALUES ( ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, novoCampeonato.getNome());
            stmt.setFloat(2, novoCampeonato.getPrizepool());

            stmt.executeUpdate();
        }
    }

    public int alterarCampeonato(Campeonato campeonatoAlterado, String id) throws SQLException {
        String sql = "UPDATE tb_campeonato SET nome = ?, prizepool = ? WHERE id = ?";
        int idInt = Integer.parseInt(id);

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, campeonatoAlterado.getNome());
            stmt.setFloat(2, campeonatoAlterado.getPrizepool());
            stmt.setInt(3, idInt);

            return stmt.executeUpdate();
        }
    }

    public int deletarCampeonato(String id) throws SQLException{
        String sql = "DELETE FROM tb_campeonato WHERE id = ?";

        int idInt = Integer.parseInt(id);

        try(Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, idInt);

            return stmt.executeUpdate();
        } 

    }
}
