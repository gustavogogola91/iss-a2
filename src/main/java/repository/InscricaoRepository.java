package repository;

import data.ConnectionFactory;
import exceptions.NotFoundException;
import models.Campeonato;
import models.Inscricao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InscricaoRepository {

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
                        rs.getFloat("prizepool"));

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

            if (!rs.next()) {
                throw new NotFoundException();
            }
            ;

            campeonato = new Campeonato(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getFloat("prizepool"));
        }

        return campeonato;
    }

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

        String valicadaoJogadores = "SELECT COUNT(*) FROM tb_jogador WHERE id = " + idTime;

        String sql = "INSERT INTO tb_inscricao (id_campeonato, id_time) VALUES ( ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement valStmt = conn.prepareStatement(validacaoExistencia);
            ResultSet valRs = valStmt.executeQuery();

            valStmt.setInt(1, idTime);
            valStmt.setInt(2, idCampeonato);

            if (valRs.next()) {
                int totalEncontrado = valRs.getInt(1);
                if (totalEncontrado < 2) {
                    throw new NotFoundException(); // mandar erro que não pode cadastrar uma inscriao a time ou
                                                   // campeonatos inexistentes
                }
            }

            PreparedStatement jogStmt = conn.prepareStatement(valicadaoJogadores);
            ResultSet jogRs = jogStmt.executeQuery();

            if (jogRs.next()) {
                int totalEncontrado = jogRs.getInt(1);
                if (totalEncontrado < 5) {
                    throw new NotFoundException(); // mandar erro que não pode cadastrar time sem pelo menos 5 jogadores
                }
            }

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, idCampeonato);
            stmt.setInt(2, idTime);

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
