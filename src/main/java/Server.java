import com.sun.net.httpserver.HttpServer;

import handlers.CampeonatoHandler;
import handlers.TimeHandler;

import java.net.InetSocketAddress;

public class Server {
    static void main() throws Exception{

        HttpServer server = HttpServer.create(new InetSocketAddress(4321), 0);
        server.createContext("/time", new TimeHandler());
        server.createContext("/campeonato", new CampeonatoHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("API rodando na porta 4321");
    }
}
