package repository;

import data.ConnectionFactory;
import exceptions.NotFoundException;
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

        String sql = "SELECT * FROM tb_partida WHERE id = (?)";

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);

            int idInt = Integer.parseInt(id);

            stmt.setInt(1, idInt);

            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                throw new NotFoundException();
            }

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

    public void salvarPartida(Partida novoPartida) throws SQLException, NotFoundException {

        String sql = "INSERT INTO tb_partida (id_campeonato, id_time_a, id_time_b, data_partida, resultado) VALUES ( ?, ?, ? , ?, ?)";
        String campeoanto_validacao = "SELECT COUNT(*) FROM tb_campeonato WHERE id = (?)";

        String timeA_inscrito_validacao = "SELECT COUNT(*) FROM tb_inscricao WHERE id_campeonato = (?) AND id_time = (?)";
        String timeB_inscrito_validacao = "SELECT COUNT(*) FROM tb_inscricao WHERE id_campeonato = (?) AND id_time = (?)";


        try (Connection conn = ConnectionFactory.getConnection()) {

            PreparedStatement valCampStmt = conn.prepareStatement(campeoanto_validacao);
            valCampStmt.setInt(1, novoPartida.getIdCampeonato());

            PreparedStatement valTimeAStmt = conn.prepareStatement(timeA_inscrito_validacao);
            PreparedStatement valTimeBStmt = conn.prepareStatement(timeB_inscrito_validacao);

            valTimeAStmt.setInt(1, novoPartida.getIdCampeonato());
            valTimeAStmt.setInt(2, novoPartida.getIdTimeA());

            valTimeBStmt.setInt(1, novoPartida.getIdCampeonato());
            valTimeBStmt.setInt(2, novoPartida.getIdTimeB());

            ResultSet valRsCamp = valCampStmt.executeQuery();

            if (valRsCamp.next()) {
                int totalEncontrado = valRsCamp.getInt(1);
                if (totalEncontrado < 1) {
                    throw new NotFoundException();
                }
            }

            ResultSet valTimeA = valTimeAStmt.executeQuery();

            if (valTimeA.next()) {
                int totalEncontrado = valTimeA.getInt(1);
                if (totalEncontrado < 1) {
                    throw new NotFoundException();
                }
            }

            ResultSet valTimeB = valTimeBStmt.executeQuery();

            if (valTimeB.next()) {
                int totalEncontrado = valTimeB.getInt(1);
                if (totalEncontrado < 1) {
                    throw new NotFoundException();
                }
            }

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, novoPartida.getIdCampeonato());
            stmt.setInt(2, novoPartida.getIdTimeA());
            stmt.setInt(3, novoPartida.getIdTimeB());
            stmt.setDate(4, novoPartida.getData());
            stmt.setString(5, novoPartida.getResultado());
            
            stmt.executeUpdate();
        }
    }

    public int alterarPartida(Partida partidaAlterado, String id) throws SQLException {
        String sql = "UPDATE tb_partida SET id_campeonato = ?, id_time_a = ?, id_time_b = ?, " +
                "data_partida = ?, resultado = ? WHERE id = ?";
        int idInt = Integer.parseInt(id);

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, partidaAlterado.getIdCampeonato());
            stmt.setInt(2, partidaAlterado.getIdTimeA());
            stmt.setInt(3, partidaAlterado.getIdTimeB());
            stmt.setDate(4, partidaAlterado.getData());
            stmt.setString(5, partidaAlterado.getResultado());
            stmt.setInt(6, idInt);

            return stmt.executeUpdate();
        }
    }

    public int deletarPartida(String id) throws SQLException {
        String sql = "DELETE FROM tb_partida WHERE id = ?";

        int idInt = Integer.parseInt(id);

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, idInt);

            return stmt.executeUpdate();
        }

    }
}
