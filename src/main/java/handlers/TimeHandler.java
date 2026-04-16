package handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import models.Time;
import repository.TimeRepository;

import java.io.IOException;
import java.io.OutputStream;

public class TimeHandler implements HttpHandler {

    private ObjectMapper _mapper = new ObjectMapper();
    private TimeRepository _repository = new TimeRepository();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String metodo = exchange.getRequestMethod();
        String caminho = exchange.getRequestURI().getPath();
        String[] partesCaminho = caminho.split("/");

        switch (metodo) {
            case "GET":

                if (partesCaminho.length == 2) {
                    buscarTimePorId(exchange);
                } else {
                    listarTimes(exchange);
                }

                break;

            case "POST":
                adicionatTime(exchange);
                break;

            case "PUT":
                alterarTime(exchange);
                break;

            case "DELETE":
                if( partesCaminho.length == 2) {
                    deletarTime(exchange, partesCaminho[1]);
                } else {
                    exchange.sendResponseHeaders(400, -1);
                }
                break;

            default:
                exchange.sendResponseHeaders(405, -1);
                break;
        }
    }

    private void listarTimes(HttpExchange exchange) {

    }

    private void buscarTimePorId(HttpExchange exchange) {

    }

    private void adicionatTime(HttpExchange exchange) throws IOException {
        Time mewTime = _mapper.readValue(exchange.getRequestBody(), Time.class);


    }

    private void alterarTime(HttpExchange exchange) throws IOException {

    }

    private void deletarTime(HttpExchange exchange, String id) {

    }

    private void enviarResposta(HttpExchange exchange, String resposta) throws IOException {

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, resposta.getBytes().length);

        OutputStream os = exchange.getResponseBody();
        os.write(resposta.getBytes());

        os.close();
    }
}
