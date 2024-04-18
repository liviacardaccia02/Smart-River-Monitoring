package ArduinoIO;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import thread.data.SharedMessage;
import utils.Logger;
import utils.Pair;

public class MQTTAgent extends AbstractVerticle {
    private final SharedMessage<Pair<String, Long>> waterLevel;
    private final SharedMessage<Integer> frequency;
    private MqttClient mqttClient;

    public MQTTAgent(SharedMessage<Pair<String, Long>> waterLevel,
                     SharedMessage<Integer> frequency) {
        this.waterLevel = waterLevel;
        this.frequency = frequency;
        this.frequency.addFrequencyChangeListener(newMessage -> this.publishMessage(String.valueOf(newMessage)));
    }

    @Override
    public void start() {
        // Create MQTT client
        mqttClient = MqttClient.create(vertx);


        // Configure MQTT client
        mqttClient.connect(1883, "broker.hivemq.com", "", ar -> {
            if (ar.succeeded()) {
                Logger.success("Connected to MQTT server");
                subscribeToTopic("WaterLevelMonitoring");
            } else {
                Logger.warning("Failed to connect to MQTT server");
            }
        });
    }

    private void subscribeToTopic(String topic) {
        mqttClient.publishHandler(s -> handlePublish(s.payload().toString()))
                .subscribe(topic, MqttQoS.AT_LEAST_ONCE.value(), this::handleSubscription);
    }

    private void handlePublish(String message) {
        synchronized (waterLevel) {
            waterLevel.setMessage(new Pair<>(message, System.currentTimeMillis()));
            //Logger.info("Message written: " + waterLevel.getMessage().getFirst() + " " + waterLevel.getMessage().getSecond());
        }
    }

    private void handleSubscription(AsyncResult<Integer> handler) {
        if (handler.succeeded()) {
            Logger.success("Subscribed to topic: WaterLevel");
        } else {
            Logger.warning("Failed to subscribe to topic: WaterLevel");
        }
    }

    public boolean isConnected() {
        return mqttClient.isConnected();
    }

    public void publishMessage(String newMessage) {
        if (newMessage != null) {
            Buffer buffer = Buffer.buffer(newMessage);
            mqttClient.publish("FrequencyMonitoring", buffer, MqttQoS.AT_MOST_ONCE, false, false, this::handlePublish);
        }
    }

    private void handlePublish(AsyncResult<Integer> handler) {
        if (handler.succeeded()) {
            Logger.success("Published message to topic: WaterLevel");
        } else {
            Logger.warning("Failed to publish message to topic: WaterLevel");
        }
    }
}