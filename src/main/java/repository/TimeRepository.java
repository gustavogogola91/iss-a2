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

        String sql = "SELECT * FROM tb_time";

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

        String sql = "SELECT * FROM tb_time as a WHERE a.id = (?)";

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);

            int idInt = Integer.parseInt(id);

            stmt.setInt(1, idInt);

            ResultSet rs = stmt.executeQuery();

            if(!rs.next()) {
                throw new NotFoundException();
            };

            time = new Time(
                    rs.getInt("id"),
                    rs.getString("nome")
            );
        }

        return time;
    }

    public void salvarTime(Time novoTime) throws SQLException {

        String sql = "INSERT INTO tb_time (id, nome) VALUES (?, ?)";

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, novoTime.getId());
            stmt.setString(2, novoTime.getNome());

            stmt.executeUpdate();
        }
    }
}
