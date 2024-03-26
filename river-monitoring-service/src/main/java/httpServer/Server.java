package httpServer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import thread.data.SharedMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Server implements Runnable {
    private final SharedMessage sharedMessage;
    MyHandler handler = new MyHandler();

    public Server(SharedMessage sharedMessage) {
        this.sharedMessage = sharedMessage;
    }

    public void start() throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test", handler);
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    @Override
    public void run() {
        try {
            start();
            while (true) {
            System.out.println("server started");
                synchronized (sharedMessage) {
                    try {
                        sharedMessage.wait(); // Aspetta fino a quando il messaggio cambia
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.addData(sharedMessage.getMessaggio());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class MyHandler implements HttpHandler {
        private String data = "{\"waterLevel\":" + 0 + ",\"date\": " + 0 + ", \"freq\": 5000}";

        public void addData(String data) {
            this.data = "{\"waterLevel\":" + data + ",\"date\": " + System.currentTimeMillis() + ", \"freq\": 5000}";
        }

        @Override
        public void handle(HttpExchange t) throws IOException {
            String value = data;
            t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            t.sendResponseHeaders(200, value.length());
            OutputStream os = t.getResponseBody();
            os.write(value.getBytes());
            os.close();
        }
    }

}