package handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import exceptions.NotFoundException;
import models.Jogador;
import repository.JogadorRepository;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

public class JogadorHandler implements HttpHandler {

    private final ObjectMapper _mapper = new ObjectMapper();
    private final JogadorRepository _repository = new JogadorRepository();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String metodo = exchange.getRequestMethod();
        String caminho = exchange.getRequestURI().getPath();
        String[] partesCaminho = caminho.split("/");

        try {
            switch (metodo) {
                case "GET" -> {
                    if (partesCaminho.length == 3) {
                        buscarJogadorPorId(exchange, partesCaminho[2]);
                    } else {
                        listarJogadores(exchange);
                    }
                }
                case "POST" -> adicionarJogador(exchange);
                case "PUT" -> {
                    if (partesCaminho.length == 3) {
                        alterarJogador(exchange, partesCaminho[2]);
                    } else {
                        exchange.sendResponseHeaders(400, -1);
                    }
                }
                case "DELETE" -> {
                    if (partesCaminho.length == 3) {
                        deletarJogador(exchange, partesCaminho[2]);
                    } else {
                        exchange.sendResponseHeaders(400, -1);
                    }
                }
                default -> exchange.sendResponseHeaders(405, -1);
            }

        } catch (NotFoundException e) {
            String mensagem = "Nenhum item encontrado. ";
            exchange.sendResponseHeaders(404, mensagem.getBytes().length);

            OutputStream os = exchange.getResponseBody();
            os.write(mensagem.getBytes());

            os.close();

        } catch (Exception e) {
            String mensagem = "Ocorreu um erro no servidor. " + e.getMessage();
            exchange.sendResponseHeaders(500, mensagem.getBytes().length);

            OutputStream os = exchange.getResponseBody();
            os.write(mensagem.getBytes());

            os.close();
        }

    }

    private void listarJogadores(HttpExchange exchange) throws SQLException, IOException, NotFoundException {
        List<Jogador> jogadores = _repository.listarJogadores();

        String resposta = _mapper.writeValueAsString(jogadores);

        enviarResposta(exchange, resposta);
    }

     private void buscarJogadorPorId(HttpExchange exchange, String id)
            throws SQLException, IOException, NotFoundException {
        Jogador jogador = _repository.buscarJogadorPorId(id);
 
        String resposta = _mapper.writeValueAsString(jogador);
 
        enviarResposta(exchange, resposta);
    }

    private void adicionarJogador(HttpExchange exchange) throws IOException, SQLException, NotFoundException {
        Jogador novoJogador = _mapper.readValue(exchange.getRequestBody(), Jogador.class);

        if (novoJogador == null || novoJogador.getNome().isBlank()) {
            String mensagem = "Erro no objeto enviado";
            exchange.sendResponseHeaders(400, mensagem.getBytes().length);

            OutputStream os = exchange.getResponseBody();
            os.write(mensagem.getBytes());

            os.close();

            return;
        }

        _repository.adicionarJogador(novoJogador);

        String resposta = "Jogador " + novoJogador.getNome() + " adicionado com sucesso";

        enviarResposta(exchange, resposta);
    }

    private void alterarJogador(HttpExchange exchange, String id)
            throws SQLException, IOException, NotFoundException {
        Jogador jogadorAlterado = _mapper.readValue(exchange.getRequestBody(), Jogador.class);
 
        if (jogadorAlterado == null || jogadorAlterado.getNome().isBlank()) {
            String mensagem = "Erro no objeto enviado";
 
            exchange.sendResponseHeaders(400, mensagem.getBytes().length);
 
            OutputStream os = exchange.getResponseBody();
            os.write(mensagem.getBytes());
 
            os.close();
 
            return;
        }
 
        int linhasAfetadas = _repository.alterarJogador(jogadorAlterado, id);
 
        if (linhasAfetadas == 0) {
            throw new NotFoundException();
        }
 
        String resposta = "Jogador alterado com sucesso";
 
        enviarResposta(exchange, resposta);
    }

    private void deletarJogador(HttpExchange exchange, String id)
            throws SQLException, IOException, NotFoundException {
 
        int linhasAfetadas = _repository.deletarJogador(id);
 
        if (linhasAfetadas == 0) {
            throw new NotFoundException();
        }
 
        String resposta = "Jogador deletado com sucesso";
 
        enviarResposta(exchange, resposta);
    }

    private void enviarResposta(HttpExchange exchange, String resposta) throws IOException {
 
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, resposta.getBytes().length);
 
        OutputStream os = exchange.getResponseBody();
        os.write(resposta.getBytes());
 
        os.close();
    }
}
