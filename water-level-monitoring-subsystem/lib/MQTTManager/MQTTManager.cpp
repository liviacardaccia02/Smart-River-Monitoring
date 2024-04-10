#include "MQTTManager.h"

MQTTManager::MQTTManager(const char *mqttBroker, int mqttPort, const char *mqttUsername,
                         const char *mqttPassword, WiFiClient *espClient, Led *redLed, Led *greenLed)
{
    this->mqttBroker = mqttBroker;
    this->mqttPort = mqttPort;
    this->mqttUsername = mqttUsername;
    this->mqttPassword = mqttPassword;
    this->redLed = redLed;
    this->greenLed = greenLed;

    client.setServer(mqttBroker, mqttPort);
    client.setClient(*espClient);
}

void MQTTManager::connect()
{
    while (!client.connected())
    {
        Serial.println("Connecting to MQTT...");

        String clientID = "ESP32Client-";
        clientID += String(random(0xffff), HEX);

        if (client.connect(clientID.c_str(), mqttUsername, mqttPassword))
        {
            redLed->switchOff();
            greenLed->switchOn(); // mqtt ok
            Serial.println("Connected to MQTT");
        }
        else
        {
            redLed->switchOn();
            greenLed->switchOff(); // mqtt ko
            Serial.println("Failed to connect to MQTT.");
            Serial.println(client.state());
            Serial.println("Trying again in 5 seconds...");
            delay(5000);
        }
    }
}

void MQTTManager::publish(const char *topic, const char *message)
{
    client.publish(topic, message);
}

void MQTTManager::subscribe(const char *topic, std::function<void(char*, uint8_t*, unsigned int)> callback)
{
    client.setCallback(callback);
    client.subscribe(topic);
}

void MQTTManager::checkConnection()
{
    if (!client.connected())
    {
        redLed->switchOn();
        greenLed->switchOff();
        this->connect();
    }
}

void MQTTManager::update() {
    client.loop();
}
