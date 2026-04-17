package repository;

import data.ConnectionFactory;
import exceptions.NotFoundException;
import models.Jogador;
import models.Time;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TimeRepository {

    public List<Time> listarTimes() throws SQLException {
        List<Time> times = new ArrayList<>();

        String sqlTime = "SELECT * FROM tb_time ORDER BY id ASC";

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sqlTime);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Time t = new Time(
                    rs.getInt("id"),
                    rs.getString("nome")
                );

                times.add(t);
            }
        }
        return times;
    }

    public Time buscarTimePorId(String id) throws SQLException, NotFoundException {

        Time time;

        String sql = "SELECT a.id as id_time, a.nome as nome_time, b.id as id_jogador, b.nome as nome_jogador " +
                "FROM tb_time as a LEFT JOIN tb_jogador as b ON a.id = b.id_time WHERE a.id = ?";

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);

            int idInt = Integer.parseInt(id);

            stmt.setInt(1, idInt);

            ResultSet rs = stmt.executeQuery();

            if(!rs.next()) {
                throw new NotFoundException();
            }

            time = new Time(
                    rs.getInt("id_time"),
                    rs.getString("nome_time")
            );

            List<Jogador> jogadores = new ArrayList<>();

            do {

                int jogadorId = rs.getInt("id_jogador");

                if(!rs.wasNull()) {
                    Jogador j = new Jogador(jogadorId, rs.getString("nome_jogador"), idInt);

                    jogadores.add(j);
                }
            } while (rs.next());

            time.setJogadores(jogadores);
        }

        return time;
    }

    public void salvarTime(Time novoTime) throws SQLException {

        String sql = "INSERT INTO tb_time (nome) VALUES (?)";

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, novoTime.getNome());

            stmt.executeUpdate();
        }
    }

    public int alterarTime(Time timeAlterado, String id) throws SQLException {

        String sql = "UPDATE tb_time SET nome = ? WHERE id = ?";
        int idInt = Integer.parseInt(id);

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, timeAlterado.getNome());
            stmt.setInt(2, idInt);

            return stmt.executeUpdate();
        }
    }

    public int deletarTime(String id) throws SQLException {

        String sql = "DELETE FROM tb_time WHERE id = ?";

        int idInt = Integer.parseInt(id);

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, idInt);

            return stmt.executeUpdate();
        }
    }
}
