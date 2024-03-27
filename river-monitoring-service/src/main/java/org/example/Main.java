package org.example;

import ArduinoIO.MQTTAgent;
import ArduinoIO.MQTTespReplacement;
import Service.Monitor;
import httpServer.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import thread.data.SharedMessage;
import utils.Pair;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {


    public static void main(String[] args) throws IOException {
        SharedMessage<Pair<String, Long>> waterLevel = new SharedMessage<>(new Pair<>("0", System.currentTimeMillis()));
        final SharedMessage<String> mode = new SharedMessage<>();
        SharedMessage<String> dangerLevel = new SharedMessage<>("default");
        SharedMessage<Integer> valve = new SharedMessage<>();

        HttpServer httpServer = new HttpServer(waterLevel, mode, dangerLevel, valve);
        Vertx vertx = Vertx.vertx(new VertxOptions().setMaxEventLoopExecuteTime(Long.MAX_VALUE));
        MQTTAgent agent = new MQTTAgent(waterLevel);

        Monitor monitor = new Monitor(waterLevel, dangerLevel, valve, List.of(50, 100, 150, 200));

        try {
            deployMqttAgent(vertx, agent);
        } catch (Exception e) {
            System.out.println("Failed to deploy MQTT agent");
        }
        httpServer.start();
        monitor.run();
    }

    private static void deployMqttAgent(Vertx vertx, MQTTAgent agent) {
        vertx.deployVerticle(agent).onComplete(ar -> {
            if (ar.succeeded()) {
                while (!agent.isConnected()) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                System.out.println("MQTT agent deployed");
            } else {
                System.out.println("Failed to deploy MQTT agent");
            }
        });
    }

}
