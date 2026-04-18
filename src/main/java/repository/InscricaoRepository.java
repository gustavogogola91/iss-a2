package repository;

import data.ConnectionFactory;
import models.Inscricao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InscricaoRepository {

    public void salvarInscricao(Inscricao inscricao) throws SQLException {
        String sql = "INSERT INTO tb_inscricao (id_campeonato, id_time) VALUES (?, ?)";

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, inscricao.getIdCampeonato());
            stmt.setInt(2, inscricao.getIdTime());
            stmt.executeUpdate();
        }
    }

    public List<Inscricao> listarInscricoes() throws SQLException {
        List<Inscricao> inscricoes = new ArrayList<>();
        String sql = "SELECT * FROM tb_inscricao";

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Inscricao i = new Inscricao(
                        rs.getInt("id"),
                        rs.getInt("id_campeonato"),
                        rs.getInt("id_time")
                );
                inscricoes.add(i);
            }
        }
        return inscricoes;
    }

    public int deletarInscricao(String id) throws SQLException {
        String sql = "DELETE FROM tb_inscricao WHERE id = ?";
        int idInt = Integer.parseInt(id);

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idInt);
            return stmt.executeUpdate();
        }
    }
}