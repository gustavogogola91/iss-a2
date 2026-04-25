package handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import models.Partida;
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
                    if (partesCaminho.length >= 3) {
                        if(partesCaminho[2].equals("por_campeonato") ) {
                            buscarPartidaPorCampeonatoId(exchange, partesCaminho[3]);
                        } else {
                            buscarPartidaPorId(exchange, partesCaminho[2]);
                        }
                    }

                    else {
                        System.out.println("Buscando campeonatos");
                        listarPartidas(exchange);
                    }
                }
                case "POST" -> adicionarPartida(exchange);
                case "PUT" -> {
                    if (partesCaminho.length == 3) {
                        alterarPartida(exchange, partesCaminho[2]);
                    } else {
                        exchange.sendResponseHeaders(400, -1);
                    }
                }
                case "DELETE" -> {
                    if (partesCaminho.length == 3) {
                        deletarPartida(exchange, partesCaminho[2]);
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

    private void listarPartidas(HttpExchange exchange) throws SQLException, IOException {
        List<Partida> partidas = _repository.listarPartidas();

        String resposta = _mapper.writeValueAsString(partidas);

        enviarResposta(exchange, resposta);
    }

    private void buscarPartidaPorId(HttpExchange exchange, String id)
            throws SQLException, IOException, NotFoundException {
        Partida partida = _repository.buscarPartidaPorId(id);

        String resposta = _mapper.writeValueAsString(partida);

        enviarResposta(exchange, resposta);
    }

    private void buscarPartidaPorCampeonatoId(HttpExchange exchange, String campeonatoId) throws SQLException, IOException, NotFoundException {
        List<Partida> partidas = _repository.buscarPartidaPorCampeonatoId(campeonatoId);

        String resposta = _mapper.writeValueAsString(partidas);

        enviarResposta(exchange, resposta);
    }

    

    private void adicionarPartida(HttpExchange exchange) throws SQLException, IOException, NotFoundException {
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

    private void alterarPartida(HttpExchange exchange, String id)
            throws SQLException, IOException, NotFoundException {
        Partida partidaAlterado = _mapper.readValue(exchange.getRequestBody(), Partida.class);

        if (partidaAlterado == null || partidaAlterado.getIdCampeonato() == 0) {
            String mensagem = "Erro no objeto enviado";

            exchange.sendResponseHeaders(400, mensagem.getBytes().length);

            OutputStream os = exchange.getResponseBody();
            os.write(mensagem.getBytes());

            os.close();

            return;
        }

        int linhasAfetadas = _repository.alterarPartida(partidaAlterado, id);

        if (linhasAfetadas == 0) {
            throw new NotFoundException();
        }

        String resposta = "Partida alterado com sucesso";

        enviarResposta(exchange, resposta);

    }

    private void deletarPartida(HttpExchange exchange, String id)
            throws SQLException, IOException, NotFoundException {

        int linhasAfetadas = _repository.deletarPartida(id);

        if (linhasAfetadas == 0) {
            throw new NotFoundException();
        }

        String resposta = "Partida deletado com sucesso";

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
