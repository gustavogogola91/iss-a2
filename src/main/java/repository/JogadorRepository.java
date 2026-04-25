package repository;

import data.ConnectionFactory;
import exceptions.NotFoundException;
import models.Jogador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JogadorRepository {

    public List<Jogador> listarJogadores() throws SQLException {
        List<Jogador> jogadores = new ArrayList<>();

        String sql = "SELECT * FROM tb_jogador";

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Jogador j = new Jogador(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getInt("id_time"));
                jogadores.add(j);
            }
        }
        return jogadores;
    }

    public Jogador buscarJogadorPorId(String id) throws SQLException, NotFoundException {
 
        Jogador jogador;
 
        String sql = "SELECT * FROM tb_jogador as a WHERE a.id = (?)";
 
        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
 
            int idInt = Integer.parseInt(id);
 
            stmt.setInt(1, idInt);
 
            ResultSet rs = stmt.executeQuery();
 
            if (!rs.next()) {
                throw new NotFoundException();
            }
 
            jogador = new Jogador(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getInt("id_time")
            );
        }
 
        return jogador;
    }

    public void adicionarJogador(Jogador jogador) throws SQLException, NotFoundException {
        String sql = "INSERT INTO tb_jogador (nome, id_time) VALUES (?, ?)";

        String time_validacao = "SELECT COUNT(*) FROM tb_time WHERE id = (?)";
 
        try (Connection conn = ConnectionFactory.getConnection()) {

            PreparedStatement valStmt = conn.prepareStatement(time_validacao);
            valStmt.setInt(1, jogador.getTimeId());

            ResultSet valRs = valStmt.executeQuery();

            if (valRs.next()) {
                int totalEncontrado = valRs.getInt(1);
                if (totalEncontrado < 1) {
                    throw new NotFoundException();
                }
            }

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, jogador.getNome());
            stmt.setInt(2, jogador.getTimeId());

            stmt.executeUpdate();
        }
    }

    public int alterarJogador(Jogador jogador, String id) throws SQLException {
        String sql = "UPDATE tb_jogador SET nome = ?, id_time = ? WHERE id = ?";
        int idInt = Integer.parseInt(id);
 
        try (Connection conn = ConnectionFactory.getConnection()) {

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, jogador.getNome());
            stmt.setInt(2, jogador.getTimeId());
            stmt.setInt(3, idInt);

            return stmt.executeUpdate();
        }
    }

    public int deletarJogador(String id) throws SQLException {
        String sql = "DELETE FROM tb_jogador WHERE id = ?";
        int idInt = Integer.parseInt(id);
 
        try (Connection conn = ConnectionFactory.getConnection()) {

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, idInt);
            
            return stmt.executeUpdate();
        }
    }
}
