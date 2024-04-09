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
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {


    public static void main(String[] args) throws IOException {
        SharedMessage<Pair<String, Long>> waterLevel = new SharedMessage<>(new Pair<>("0", System.currentTimeMillis()));
        final SharedMessage<String> mode = new SharedMessage<>();
        SharedMessage<String> dangerLevel = new SharedMessage<>("default");
        SharedMessage<Integer> valve = new SharedMessage<>();
        SharedMessage<Integer> frequency = new SharedMessage<>(5000);

        HttpServer httpServer = new HttpServer(waterLevel, mode, dangerLevel, valve, frequency);
        Vertx vertx = Vertx.vertx(new VertxOptions().setMaxEventLoopExecuteTime(Long.MAX_VALUE));
        MQTTAgent agent = new MQTTAgent(waterLevel, frequency);

        Monitor monitor = new Monitor(waterLevel, dangerLevel, valve, List.of(50, 70, 80, 100), frequency);

        SerialMonitor serialMonitor = new SerialMonitor(mode, valve);

        serialMonitor.start("/dev/cu.usbmodem14101");



        try {
            deployMqttAgent(vertx, agent);
        } catch (Exception e) {
            Logger.error("Failed to deploy MQTT agent");
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

                Logger.success("MQTT agent deployed");
            } else {
                Logger.error("Failed to deploy MQTT agent");
            }
        });

    }



}
