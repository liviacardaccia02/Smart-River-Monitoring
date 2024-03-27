#ifndef __MQTT_MANAGER__
#define __MQTT_MANAGER__

#include <WiFi.h>
#include <PubSubClient.h>
#include <Led.h>

class MQTTManager
{
public:
    MQTTManager(const char *mqtt_broker, int mqtt_port, const char *mqtt_username,
                const char *mqtt_password, WiFiClient *espClient, Led *redLed, Led *greenLed);
    void connect();
    void publish(const char *topic, const char *message);
    void checkConnection();

private:
    WiFiClient espClient;
    PubSubClient client;
    Led *redLed;
    Led *greenLed;
    const char *mqtt_broker;
    int mqtt_port;
    const char *mqtt_username;
    const char *mqtt_password;
};

#endif