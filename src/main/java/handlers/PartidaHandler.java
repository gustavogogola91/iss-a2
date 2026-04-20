package handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import models.Campeonato;
import models.Partida;
import repository.CampeonatoRepository;
import repository.PartidaRepository;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

public class PartidaHandler implements HttpHandler {

    private final ObjectMapper _mapper = new ObjectMapper();
    private final PartidaRepository _repository = new PartidaRepository();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String metodo = exchange.getRequestMethod();
        String caminho = exchange.getRequestURI().getPath();
        String[] partesCaminho = caminho.split("/");

        try {
            switch (metodo) {
                case "GET" -> {
                    if (partesCaminho.length == 3) {
                        buscarCampeonatoPorId(exchange, partesCaminho[2]);
                    } else {
                        System.out.println("Buscando campeonatos");
                        listarCampeonatos(exchange);
                    }
                }
                case "POST" -> adicionarPartida(exchange);
                case "PUT" -> {
                    if (partesCaminho.length == 3) {
                        alterarCampeonato(exchange, partesCaminho[2]);
                    } else {
                        exchange.sendResponseHeaders(400, -1);
                    }
                }
                case "DELETE" -> {
                    if (partesCaminho.length == 3) {
                        deletarCampeonato(exchange, partesCaminho[2]);
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

    private void listarCampeonatos(HttpExchange exchange) throws SQLException, IOException {
        List<Campeonato> campeonatos = _repository.listarCampeonatos();

        String resposta = _mapper.writeValueAsString(campeonatos);

        enviarResposta(exchange, resposta);
    }

    private void buscarCampeonatoPorId(HttpExchange exchange, String id)
            throws SQLException, IOException, NotFoundException {
        Campeonato campeonato = _repository.buscarCampeonatoPorId(id);

        String resposta = _mapper.writeValueAsString(campeonato);

        enviarResposta(exchange, resposta);
    }

    private void adicionarPartida(HttpExchange exchange) throws SQLException, IOException {
        Partida novaPartida = _mapper.readValue(exchange.getRequestBody(), Partida.class);

        if (novaPartida == null) {
            String mensagem = "Erro no objeto enviado";
            exchange.sendResponseHeaders(400, mensagem.getBytes().length);

            OutputStream os = exchange.getResponseBody();
            os.write(mensagem.getBytes());

            os.close();

            return;
        }

        _repository.salvarPartida(novaPartida);

        String resposta = "Partida adicionado com sucesso";

        enviarResposta(exchange, resposta);
    }

    private void alterarCampeonato(HttpExchange exchange, String id)
            throws SQLException, IOException, NotFoundException {
        Campeonato campeonatoAlterado = _mapper.readValue(exchange.getRequestBody(), Campeonato.class);

        if (campeonatoAlterado == null || campeonatoAlterado.getNome().isBlank()) {
            String mensagem = "Erro no objeto enviado";

            exchange.sendResponseHeaders(400, mensagem.getBytes().length);

            OutputStream os = exchange.getResponseBody();
            os.write(mensagem.getBytes());

            os.close();

            return;
        }

        int linhasAfetadas = _repository.alterarCampeonato(campeonatoAlterado, id);

        if (linhasAfetadas == 0) {
            throw new NotFoundException();
        }

        String resposta = "Campeonato alterado com sucesso";

        enviarResposta(exchange, resposta);

    }

    private void deletarCampeonato(HttpExchange exchange, String id)
            throws SQLException, IOException, NotFoundException {

        int linhasAfetadas = _repository.deletarCampeonato(id);

        if (linhasAfetadas == 0) {
            throw new NotFoundException();
        }

        String resposta = "Campeonato deletado com sucesso";

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
