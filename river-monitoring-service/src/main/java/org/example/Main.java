package org.example;

import ArduinoIO.MQTTAgent;
import httpServer.Server;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import thread.data.SharedMessage;

import java.util.concurrent.TimeUnit;

public class Main {


    public static void main(String[] args) {
        SharedMessage sharedMessage = new SharedMessage();
        System.out.println("Hello world!");

        Server server = new Server(sharedMessage);
        System.out.println("Hello world!");

        Vertx vertx = Vertx.vertx(new VertxOptions().setMaxEventLoopExecuteTime(Long.MAX_VALUE));
        // change vertx options
        System.out.println("Hello world!");

        MQTTAgent agent = new MQTTAgent(sharedMessage);
        System.out.println("Hello world!");



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

        server.run();
        System.out.println("Hello world!");
    }

}
