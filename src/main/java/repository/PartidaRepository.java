package repository;

import data.ConnectionFactory;
import exceptions.NotFoundException;
import models.Campeonato;
import models.Partida;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PartidaRepository {

    public List<Partida> listarPartidas() throws SQLException {
        List<Partida> partidas = new ArrayList<>();

        String sql = "SELECT * FROM tb_partida";

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Partida p = new Partida(
                        rs.getInt("id"),
                        rs.getDate("data_partida"),
                        rs.getInt("id_campeonato"),
                        rs.getInt("id_time_a"),
                        rs.getInt("id_time_b"),
                        rs.getString("resultado"));

                partidas.add(p);
            }
        }
        return partidas;
    }

    public Partida buscarPartidaPorId(String id) throws SQLException, NotFoundException {

        Partida partida;

        String sql = "SELECT * FROM tb_partida as a WHERE a.id = (?)";

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);

            int idInt = Integer.parseInt(id);

            stmt.setInt(1, idInt);

            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                throw new NotFoundException();
            }
            ;

            partida = new Partida(
                    rs.getInt("id"),
                    rs.getDate("data_partida"),
                    rs.getInt("id_campeonato"),
                    rs.getInt("id_time_a"),
                    rs.getInt("id_time_b"),
                    rs.getString("resultado"));
        }

        return partida;
    }

    public List<Partida> buscarPartidaPorCampeonatoId(String idCampeonato) throws SQLException, NotFoundException {

        List<Partida> partidas = new ArrayList<>();

        String sql = "SELECT * FROM tb_partida WHERE id_campeonato = (?)";

        int intIdCampeonato = Integer.parseInt(idCampeonato);

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, intIdCampeonato);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Partida p = new Partida(
                        rs.getInt("id"),
                        rs.getDate("data_partida"),
                        rs.getInt("id_campeonato"),
                        rs.getInt("id_time_a"),
                        rs.getInt("id_time_b"),
                        rs.getString("resultado"));
                partidas.add(p);
            }
        }
        return partidas;
    }

    public void salvarPartida(Partida novoPartida) throws SQLException {

        //fazer validação

        String sql = "INSERT INTO tb_campeonato (id_campeonato, id_time_a, id_time_b, data_partida, resultado) VALUES ( ?, ?, ? , ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, novoPartida.getIdCampeonato());
            stmt.setInt(2, novoPartida.getIdTimeA());
            stmt.setInt(3, novoPartida.getIdTimeB());
            stmt.setString(4, novoPartida.getData().toString());
            stmt.setString(5, novoPartida.getResultado());
            
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

    public int deletarCampeonato(String id) throws SQLException {
        String sql = "DELETE FROM tb_campeonato WHERE id = ?";

        int idInt = Integer.parseInt(id);

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, idInt);

            return stmt.executeUpdate();
        }

    }
}
