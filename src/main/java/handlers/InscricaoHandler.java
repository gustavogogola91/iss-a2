package handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import models.Inscricao;
import models.Time;
import repository.CampeonatoRepository;
import repository.InscricaoRepository;
import repository.TimeRepository;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

public class InscricaoHandler implements HttpHandler {

    private final ObjectMapper _mapper = new ObjectMapper();
    private final InscricaoRepository _inscricaoRepository = new InscricaoRepository();
    private final TimeRepository _timeRepository = new TimeRepository();
    private final CampeonatoRepository _campeonatoRepository = new CampeonatoRepository();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String metodo = exchange.getRequestMethod();
        String caminho = exchange.getRequestURI().getPath();
        String[] partesCaminho = caminho.split("/");

        try {
            switch (metodo) {
                case "GET" -> listarInscricoes(exchange);
                case "POST" -> adicionarInscricao(exchange);
                case "DELETE" -> {
                    if (partesCaminho.length == 3) {
                        deletarInscricao(exchange, partesCaminho[2]);
                    } else {
                        enviarErro(exchange, 400, "ID não informado na URL.");
                    }
                }
                default -> exchange.sendResponseHeaders(405, -1);
            }

        } catch (NotFoundException e) {
            enviarErro(exchange, 404, "Campeonato ou Time não encontrados no banco de dados.");
        } catch (IllegalArgumentException e) {
            enviarErro(exchange, 400, e.getMessage());
        } catch (Exception e) {
            enviarErro(exchange, 500, "Ocorreu um erro no servidor: " + e.getMessage());
        }
    }

    private void listarInscricoes(HttpExchange exchange) throws SQLException, IOException {
        List<Inscricao> inscricoes = _inscricaoRepository.listarInscricoes();
        String resposta = _mapper.writeValueAsString(inscricoes);
        enviarResposta(exchange, 200, resposta);
    }

    private void adicionarInscricao(HttpExchange exchange) throws SQLException, IOException, NotFoundException {
        Inscricao novaInscricao = _mapper.readValue(exchange.getRequestBody(), Inscricao.class);

        if (novaInscricao == null) {
            enviarErro(exchange, 400, "Erro no objeto JSON enviado");
            return;
        }

        _campeonatoRepository.buscarCampeonatoPorId(String.valueOf(novaInscricao.getIdCampeonato()));

        Time time = _timeRepository.buscarTimePorId(String.valueOf(novaInscricao.getIdTime()));

        if (time.getJogadores().size() < 5) {
            throw new IllegalArgumentException("Regra de Negocio: O time precisa ter pelo menos 5 jogadores cadastrados para se inscrever.");
        }

        _inscricaoRepository.salvarInscricao(novaInscricao);
        enviarResposta(exchange, 201, "Inscrição realizada com sucesso!");
    }

    private void deletarInscricao(HttpExchange exchange, String id) throws SQLException, IOException, NotFoundException {
        int linhasAfetadas = _inscricaoRepository.deletarInscricao(id);

        if (linhasAfetadas == 0) {
            throw new NotFoundException();
        }
        enviarResposta(exchange, 200, "Inscrição deletada com sucesso!");
    }

    private void enviarResposta(HttpExchange exchange, int statusCode, String resposta) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, resposta.getBytes("UTF-8").length);

        OutputStream os = exchange.getResponseBody();
        os.write(resposta.getBytes("UTF-8"));
        os.close();
    }

    private void enviarErro(HttpExchange exchange, int statusCode, String mensagem) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, mensagem.getBytes("UTF-8").length);

        OutputStream os = exchange.getResponseBody();
        os.write(mensagem.getBytes("UTF-8"));
        os.close();
    }
}