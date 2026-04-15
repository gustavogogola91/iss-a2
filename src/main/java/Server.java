import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class Server {
    static void main() throws Exception{

        HttpServer server = HttpServer.create(new InetSocketAddress(4321), 0);
//        server.createContext("");

        server.setExecutor(null);
        server.start();

        System.out.println("API rodando na porta 4321");
    }
}
