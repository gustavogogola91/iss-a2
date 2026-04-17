package repository;

import data.ConnectionFactory;
import exceptions.NotFoundException;
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

        String sql = "SELECT * FROM tb_time ORDER BY id ASC";

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
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

        String sql = "SELECT * FROM tb_time WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);

            int idInt = Integer.parseInt(id);

            stmt.setInt(1, idInt);

            ResultSet rs = stmt.executeQuery();

            if(!rs.next()) {
                throw new NotFoundException();
            }

            time = new Time(
                    rs.getInt("id"),
                    rs.getString("nome")
            );
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
