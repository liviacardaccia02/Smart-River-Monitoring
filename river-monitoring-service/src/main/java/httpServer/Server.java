package httpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {
    MyHandler handler = new MyHandler();

    //Generate an http server that listen for incoming requests. do not use main metho. the server must support cross origin requests
    private List<String> data = new ArrayList<>();
    public void start() throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test",handler);
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        private List<String> data = new ArrayList<>();

        public void addData(String data) {
            this.data.add("{\"waterLevel\":"+ data +",\"date\": "+System.currentTimeMillis()+"}");
        }
        @Override
        public void handle(HttpExchange t) throws IOException {
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String value = data.stream().reduce((a, b) -> a +","+ b).orElse("");
            System.out.println(value);
            String response = "["+ value + "]";
            t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();


        }
    }

    public void addData(String data) {
        handler.addData(data);
    }

}