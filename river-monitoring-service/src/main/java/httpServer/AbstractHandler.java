package httpServer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public abstract class AbstractHandler implements HttpHandler{

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

        abstract void handleGetRequest(HttpExchange t) throws IOException;

        abstract void handlePostRequest(HttpExchange t) throws IOException;

        private void handleUnsupportedMethod(HttpExchange t) throws IOException {
            t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            t.sendResponseHeaders(405, -1);
            t.close();
        }

        protected void sendResponse(HttpExchange t, String response) throws IOException {
            t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            t.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = t.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

}
