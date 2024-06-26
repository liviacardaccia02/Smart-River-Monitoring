package ArduinoIO;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import thread.data.SharedMessage;

import java.util.concurrent.TimeUnit;

/*
 * MQTT Agent
 */
public class MQTTespReplacement extends AbstractVerticle {
    MqttClient mqttClient;

    public MQTTespReplacement() {
    }

    @Override
    public void start() {
        // Create MQTT client
        mqttClient = MqttClient.create(vertx);


        // Configure MQTT client
        mqttClient.connect(1883, "broker.hivemq.com", "", ar -> {
            if (ar.succeeded()) {
                System.out.println("Connected to MQTT server");
                // Subscribe to a topic
                mqttClient.publishHandler(s -> {
                    System.out.println(s.payload().toString());
                }).subscribe("WaterLevel", MqttQoS.AT_LEAST_ONCE.value(), handler -> {
                    if (handler.succeeded()) {
                        System.out.println("Subscribed to topic: WaterLevel");
                    } else {
                        System.out.println("Failed to subscribe to topic: WaterLevel");
                    }
                });
            } else {
                System.out.println("Failed to connect to MQTT server");
            }
        });
    }

    public boolean isConnected() {
        return mqttClient.isConnected();
    }

    public void publishMessage(String payload) {
        Buffer buffer = Buffer.buffer(payload);
        this.mqttClient.publish("WaterLevel", buffer, MqttQoS.AT_MOST_ONCE, false, false, handler -> {
            if (handler.succeeded()) {
                System.out.println("Published message to topic: WaterLevel");
            } else {
                System.out.println("Failed to publish message to topic: WaterLevel");
            }
        });
    }
}