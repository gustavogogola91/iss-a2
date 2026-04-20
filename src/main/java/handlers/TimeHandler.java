package handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import models.Time;
import repository.TimeRepository;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

public class TimeHandler implements HttpHandler {

    private final ObjectMapper _mapper = new ObjectMapper();
    private final TimeRepository _repository = new TimeRepository();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String metodo = exchange.getRequestMethod();
        String caminho = exchange.getRequestURI().getPath();
        String[] partesCaminho = caminho.split("/");

        try {
            switch (metodo) {
                case "GET" -> {
                    if (partesCaminho.length == 3) {
                        buscarTimePorId(exchange, partesCaminho[2]);
                    } else {
                        System.out.println("Buscando times");
                        listarTimes(exchange);
                    }
                }
                case "POST" -> adicionarTime(exchange);
                case "PUT" -> {
                    if (partesCaminho.length == 3) {
                        alterarTime(exchange, partesCaminho[2]);
                    } else {
                        exchange.sendResponseHeaders(400, -1);
                    }
                }
                case "DELETE" -> {
                    if (partesCaminho.length == 3) {
                        deletarTime(exchange, partesCaminho[2]);
                    } else {
                        exchange.sendResponseHeaders(400, -1);
                    }
                }
                default -> exchange.sendResponseHeaders(405, -1);
            }

        } catch(NotFoundException e) {

            enviarErro(exchange, 404, "Nenhum item encontrado. ");
        } catch(Exception e) {

            enviarErro(exchange, 500, "Ocorreu um erro no servidor. " + e.getMessage());
        }
    }

    private void listarTimes(HttpExchange exchange) throws SQLException, IOException {
        List<Time> times = _repository.listarTimes();

        String resposta = _mapper.writeValueAsString(times);

        enviarResposta(exchange, resposta);
    }

    private void buscarTimePorId(HttpExchange exchange, String id) throws SQLException, IOException, NotFoundException {
        Time time = _repository.buscarTimePorId(id);

        String resposta = _mapper.writeValueAsString(time);

        enviarResposta(exchange, resposta);

    }

    private void adicionarTime(HttpExchange exchange) throws SQLException, IOException {
        Time movoTime = _mapper.readValue(exchange.getRequestBody(), Time.class);

        if(movoTime == null || movoTime.getNome().isBlank()) {

            enviarErro(exchange, 400, "Erro no objeto enviado");
            return;
        }

        _repository.salvarTime(movoTime);

        String resposta = "Time adicionado com sucesso";

        enviarResposta(exchange, resposta);
    }

    private void alterarTime(HttpExchange exchange, String id) throws SQLException, IOException, NotFoundException {
        Time timeAlterado = _mapper.readValue(exchange.getRequestBody(), Time.class);

        if(timeAlterado == null || timeAlterado.getNome().isBlank()) {

            enviarErro(exchange, 400, "Erro no objeto enviado");
            return;
        }

        int linhasAfetadas  = _repository.alterarTime(timeAlterado, id);

        if(linhasAfetadas == 0) {
            throw new NotFoundException();
        }

        String resposta = "Time alterado com sucesso";

        enviarResposta(exchange, resposta);
    }

    private void deletarTime(HttpExchange exchange, String id) throws SQLException, IOException, NotFoundException {
        int linhasAfetadas  = _repository.deletarTime(id);

        if(linhasAfetadas == 0) {
            throw new NotFoundException();
        }

        String resposta = "Time deletado com sucesso";

        enviarResposta(exchange, resposta);
    }

    private void enviarResposta(HttpExchange exchange, String resposta) throws IOException {

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, resposta.getBytes().length);

        OutputStream os = exchange.getResponseBody();
        os.write(resposta.getBytes());

        os.close();
    }

    private void enviarErro(HttpExchange exchange, int statusCode, String mensagem) throws IOException {

        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, mensagem.getBytes("UTF-8").length);

        OutputStream os = exchange.getResponseBody();
        os.write(mensagem.getBytes());

        os.close();
    }
}
