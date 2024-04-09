#ifndef __MQTT_MANAGER__
#define __MQTT_MANAGER__

#include <WiFi.h>
#include <PubSubClient.h>
#include <Led.h>

class MQTTManager
{
public:
    MQTTManager(const char *mqttBroker, int mqttPort, const char *mqttUsername,
                const char *mqttPassword, WiFiClient *espClient, Led *redLed, Led *greenLed);
    void connect();
    void publish(const char *topic, const char *message);
    void subscribe(const char *topic);
    void checkConnection();
    static void callback(char *topic, byte *payload, unsigned int length);

private:
    WiFiClient espClient;
    PubSubClient client;
    Led *redLed;
    Led *greenLed;
    const char *mqttBroker;
    int mqttPort;
    const char *mqttUsername;
    const char *mqttPassword;
};

#endif