package httpServer;

import com.sun.net.httpserver.HttpExchange;
import thread.data.SharedMessage;
import utils.Logger;
import utils.Pair;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {

    private final SharedMessage<Pair<String, Long>> waterLevel;
    private final SharedMessage<String> mode;
    private final SharedMessage<String> dangerLevel;
    private final SharedMessage<Integer> valve;
    private final SharedMessage<Integer> frequency;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public HttpServer(SharedMessage<Pair<String, Long>> waterLevel,
                      SharedMessage<String> mode,
                      SharedMessage<String> dangerLevel,
                      SharedMessage<Integer> valve,
                      SharedMessage<Integer> frequency) {
        this.waterLevel = waterLevel;
        this.mode = mode;
        this.dangerLevel = dangerLevel;
        this.valve = valve;
        this.frequency = frequency;
    }

    public void start() throws IOException {
        Logger.info("Starting HTTP server");
        com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test", new AbstractHandler() {
            @Override
            void handleGetRequest(HttpExchange t) throws IOException {
                synchronized (dangerLevel) {
                    synchronized (waterLevel) {
                        synchronized (valve) {
                            synchronized (frequency) {
                                // JSON format: {"waterLevel": 0, "date": 0, "dangerLevel": "disconnected", "valve": 0, "freq": 5000}
                                String data = "{\"waterLevel\":" + (waterLevel.getMessage().getFirst() == null ? "0" : waterLevel.getMessage().getFirst())
                                        + ",\"date\":" + (waterLevel.getMessage().getSecond() == null ? "0" : waterLevel.getMessage().getSecond())
                                        + ",\"dangerLevel\":\"" + (dangerLevel.getMessage() == null ? "disconnected" : dangerLevel.getMessage()) + "\""
                                        + ",\"valve\":" + (valve.getMessage() == null ? 0 : valve.getMessage())
                                        + ", \"freq\":" + (frequency.getMessage() == null ? 5000 : frequency.getMessage())
                                        + ", \"mode\":" + (mode.getMessage() == null ? "{\"mode\":\"auto\"}" : mode.getMessage()) + "}";
                                sendResponse(t, data);
                            }
                        }
                    }
                }
            }

            @Override
            void handlePostRequest(HttpExchange t) throws IOException {
                String modeData = new String(t.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                synchronized (mode) {
                    mode.setMessage(modeData);
                    mode.notifyAll();
                    Logger.info("Mode: " + mode.getMessage());
                }
                sendResponse(t, "POST received");
            }
        });
        server.createContext("/valve", new AbstractHandler() {
            @Override
            void handleGetRequest(HttpExchange t) {
            }

            @Override
            void handlePostRequest(HttpExchange t) throws IOException {
                Integer valveData = Integer.parseInt(new String(t.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
                synchronized (valve) {
                    valve.setMessage(valveData);
                    valve.notifyAll();
                    Logger.info("Valve opening: " + valve.getMessage());
                }
                sendResponse(t, "POST received");
            }
        });

        server.setExecutor(executor);
        server.start();
        Logger.success("HTTP server started");
    }
}
