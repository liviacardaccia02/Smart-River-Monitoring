package org.example;

import ArduinoIO.MQTTAgent;
import ArduinoIO.SerialMonitor;
import Service.Monitor;
import httpServer.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import thread.data.SharedMessage;
import utils.Logger;
import utils.Pair;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException {
        SharedMessage<Pair<String, Long>> waterLevel = new SharedMessage<>(new Pair<>("0", System.currentTimeMillis()));
        SharedMessage<String> mode = new SharedMessage<>("{\"mode\":\"auto\"}");
        SharedMessage<String> dangerLevel = new SharedMessage<>("default");
        SharedMessage<Integer> valve = new SharedMessage<>();
        SharedMessage<Integer> frequency = new SharedMessage<>(5000);

        HttpServer httpServer = new HttpServer(waterLevel, mode, dangerLevel, valve, frequency);
        Vertx vertx = Vertx.vertx(new VertxOptions().setMaxEventLoopExecuteTime(Long.MAX_VALUE));
        MQTTAgent agent = new MQTTAgent(waterLevel, frequency);
        Monitor monitor = new Monitor(waterLevel, dangerLevel, valve, Arrays.asList(170, 175, 180, 195), frequency, mode);
        SerialMonitor serialMonitor = new SerialMonitor(mode, valve);

        serialMonitor.start("/dev/cu.usbmodem14101");
        deployMqttAgent(vertx, agent);
        httpServer.start();
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void deployMqttAgent(Vertx vertx, MQTTAgent agent) {
        vertx.deployVerticle(agent).onComplete(ar -> {
            if (ar.succeeded()) {
                while (!agent.isConnected()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                Logger.success("MQTT agent deployed");
            } else {
                Logger.error("Failed to deploy MQTT agent");
            }
        });
    }
}