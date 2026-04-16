package repository;

import data.ConnectionFactory;
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
}
