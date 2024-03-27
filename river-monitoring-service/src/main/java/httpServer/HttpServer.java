package httpServer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import thread.data.SharedMessage;
import utils.Pair;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {

    private final SharedMessage<Pair<String, Long>> waterLevel;
    private final SharedMessage<String> mode;
    private final SharedMessage<String> dangerLevel;
    private final SharedMessage<Integer> valve;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public HttpServer(SharedMessage<Pair<String, Long>> waterLevel, SharedMessage<String> mode, SharedMessage<String> dangerLevel, SharedMessage<Integer> valve) {
        this.waterLevel = waterLevel;
        this.mode = mode;
        this.dangerLevel = dangerLevel;
        this.valve = valve;
    }

    public void start() throws IOException {
        System.out.println("Starting HTTP server");
        com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test", new MyHandler());
        server.setExecutor(executor);
        server.start();
        System.out.println("HTTP server started");
    }

    class MyHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            if (t.getRequestMethod().equals("OPTIONS")) {
                handleOptionsRequest(t);
            } else if (t.getRequestMethod().equals("GET")) {
                handleGetRequest(t);
            } else if (t.getRequestMethod().equals("POST")) {
                handlePostRequest(t);
            } else {
                handleUnsupportedMethod(t);
            }
        }

        private void handleOptionsRequest(HttpExchange t) throws IOException {
            t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            t.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            t.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
            t.sendResponseHeaders(204, -1);
            t.close();
        }

        private void handleGetRequest(HttpExchange t) throws IOException {
            synchronized (dangerLevel) {
                synchronized (waterLevel) {
                    synchronized (valve) {
                        String data = "{\"waterLevel\":" + (waterLevel.getMessage().getFirst() == null ? "0" : waterLevel.getMessage().getFirst())
                                + ",\"date\":" + (waterLevel.getMessage().getSecond() == null ? "0" : waterLevel.getMessage().getSecond())
                                + ",\"dangerLevel\":\"" + (dangerLevel.getMessage() == null ? "disconnected" : dangerLevel.getMessage()) + "\""
                                + ",\"valve\":" + (valve.getMessage() == null ? 0 : valve.getMessage())
                                + ", \"freq\": 5000}";
                        sendResponse(t, 200, data);
                    }
                }
            }
        }

        private void handlePostRequest(HttpExchange t) throws IOException {
            String modeData = new String(t.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            synchronized (mode) {
                mode.setMessage(modeData);
                mode.notifyAll();
                System.out.println("Mode: " + mode.getMessage());
            }
            sendResponse(t, 200, "POST received");
        }

        private void handleUnsupportedMethod(HttpExchange t) throws IOException {
            t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            t.sendResponseHeaders(405, -1);
            t.close();
        }

        private void sendResponse(HttpExchange t, int statusCode, String response) throws IOException {
            t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            t.sendResponseHeaders(statusCode, response.getBytes().length);
            try (OutputStream os = t.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
