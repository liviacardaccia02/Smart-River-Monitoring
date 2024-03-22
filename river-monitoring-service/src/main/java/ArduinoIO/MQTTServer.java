package ArduinoIO;

import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttServer;
import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttEndpoint;

public class MQTTServer {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        MqttServer mqttServer = MqttServer.create(vertx);

        mqttServer.endpointHandler(endpoint -> {
            System.out.println("MQTT client [" + endpoint.clientIdentifier() + "] request to connect, clean session = "
                    + endpoint.isCleanSession());

            if (endpoint.auth() != null) {
                System.out.println("[username = " + endpoint.auth().getUsername() + ", password = "
                        + endpoint.auth().getPassword() + "]");
            }
            if (endpoint.will() != null) {
                System.out.println("[will topic = " + endpoint.will().getWillTopic() +
                        " QoS = " + endpoint.will().getWillQos() + " isRetain = " + endpoint.will().isWillRetain()
                        + "]");
            }

            System.out.println("[keep alive timeout = " + endpoint.keepAliveTimeSeconds() + "]");

            // accept connection from the remote client
            endpoint.accept(false);

            // message handler for the subscribed topic
            endpoint.publishHandler(message -> {
                System.out.println("Received message on topic: " + message.topicName());
                System.out.println("Message payload: " + message.payload().toString());
            });
        });

        mqttServer.listen(ar -> {
            if (ar.succeeded()) {
                System.out.println("MQTT server is listening on port " + ar.result().actualPort());
            } else {
                System.out.println("Error on starting the server");
                ar.cause().printStackTrace();
            }
        });
    }
}