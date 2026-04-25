package repository;

import data.ConnectionFactory;
import exceptions.NotFoundException;
import models.Inscricao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InscricaoRepository {

    public void salvarInscricao(Inscricao novaInscricao) throws SQLException, NotFoundException {

        int idTime = novaInscricao.getIdTime();
        int idCampeonato = novaInscricao.getIdCampeonato();

        String validacaoExistencia = """
                    SELECT COUNT(*) FROM (
                        SELECT 1 FROM tb_time WHERE id = ?
                        UNION ALL
                        SELECT 1 FROM tb_campeonato WHERE id = ?
                    )
                """;

        String validacaoJogadores = "SELECT COUNT(*) FROM tb_jogador WHERE id_time = ?";

        String sql = "INSERT INTO tb_inscricao (id_campeonato, id_time) VALUES (?, ?)";

        try (Connection conn = ConnectionFactory.getConnection()) {

            PreparedStatement valStmt = conn.prepareStatement(validacaoExistencia);
            valStmt.setInt(1, idTime);
            valStmt.setInt(2, idCampeonato);
            ResultSet valRs = valStmt.executeQuery();

            if (valRs.next()) {
                int totalEncontrado = valRs.getInt(1);
                if (totalEncontrado < 2) {
                    throw new NotFoundException();
                }
            }

            PreparedStatement jogStmt = conn.prepareStatement(validacaoJogadores);
            jogStmt.setInt(1, idTime);
            ResultSet jogRs = jogStmt.executeQuery();

            if (jogRs.next()) {
                int totalEncontrado = jogRs.getInt(1);
                if (totalEncontrado < 5) {
                    throw new NotFoundException();
                }
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idCampeonato);
            stmt.setInt(2, idTime);
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