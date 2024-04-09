package ArduinoIO;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import thread.data.SharedMessage;
import utils.Logger;
import utils.Pair;

/*
 * MQTT Agent
 */
public class MQTTAgent extends AbstractVerticle {
    private final SharedMessage<Pair<String, Long>> waterLevel;
    private final SharedMessage<Integer> frequency;
    MqttClient mqttClient;

    public MQTTAgent(SharedMessage<Pair<String, Long>> waterLevel,
                     SharedMessage<Integer> frequency) {
        this.waterLevel = waterLevel;
        this.frequency = frequency;
        this.frequency.addFrequencyChangeListener(newMessage -> {
            if (newMessage != null) {
                publishMessage(newMessage.toString(), "FrequencyMonitoring");
            }
        });
    }

    @Override
    public void start() {
        // Create MQTT client
        mqttClient = MqttClient.create(vertx);


        // Configure MQTT client
        mqttClient.connect(1883, "broker.hivemq.com", "", ar -> {
            if (ar.succeeded()) {
                Logger.success("Connected to MQTT server");
                // Subscribe to a topic
                mqttClient.publishHandler(s -> {
                    System.out.println(s.payload().toString());
                    synchronized (waterLevel) {
                        waterLevel.setMessage(new Pair<>(s.payload().toString(), System.currentTimeMillis()));
                        Logger.success("Messaggio scritto: " +
                                waterLevel.getMessage().getFirst() + " " +
                                waterLevel.getMessage().getSecond());
                    }
                }).subscribe("WaterLevelMonitoring", MqttQoS.AT_LEAST_ONCE.value(), handler -> {
                    if (handler.succeeded()) {
                        Logger.success("Subscribed to topic: WaterLevel");
                    } else {
                        Logger.warning("Failed to subscribe to topic: WaterLevel");
                    }
                });
            } else {
                Logger.warning("Failed to connect to MQTT server");
            }
        });
    }

    public boolean isConnected() {
        return mqttClient.isConnected();
    }

    public void publishMessage(String payload, String topic) {
        Buffer buffer = Buffer.buffer(payload);
        this.mqttClient.publish(topic, buffer, MqttQoS.AT_LEAST_ONCE, false, false, handler -> {
            if (handler.succeeded()) {
                Logger.success("Published message to topic: WaterLevel");
            } else {
                Logger.warning("Failed to publish message to topic: WaterLevel");
            }
        });
    }
}