package org.example;

import ArduinoIO.MQTTespReplacement;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DeployEspReplacement {
    public static void main(String[] args) throws IOException {

        Vertx vertx = Vertx.vertx(new VertxOptions().setMaxEventLoopExecuteTime(Long.MAX_VALUE));

        MQTTespReplacement agent = new MQTTespReplacement();


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

            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                agent.publishMessage(new Random().nextInt(40, 250)+"");
            }
        });

        System.out.println("Hello world!");
    }
}
