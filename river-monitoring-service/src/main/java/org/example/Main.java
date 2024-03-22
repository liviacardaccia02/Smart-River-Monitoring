package org.example;

import ArduinoIO.MQTTAgent;
import httpServer.Server;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        Vertx vertx = Vertx.vertx(new VertxOptions().setMaxEventLoopExecuteTime(Long.MAX_VALUE));
        // change vertx options

        MQTTAgent agent = new MQTTAgent();
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
                //agent.publishMessage("sukaLivia");
            } else {
                System.out.println("Failed to deploy MQTT agent");
            }
        });
    }
}
