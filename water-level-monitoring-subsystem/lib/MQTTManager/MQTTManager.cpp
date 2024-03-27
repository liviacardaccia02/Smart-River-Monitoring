#include "MQTTManager.h"

MQTTManager::MQTTManager(const char *mqtt_broker, int mqtt_port, const char *mqtt_username,
                         const char *mqtt_password, WiFiClient *espClient, Led *redLed, Led *greenLed)
{
    this->mqtt_broker = mqtt_broker;
    this->mqtt_port = mqtt_port;
    this->mqtt_username = mqtt_username;
    this->mqtt_password = mqtt_password;
    this->redLed = redLed;
    this->greenLed = greenLed;

    client.setServer(mqtt_broker, mqtt_port);
    client.setClient(*espClient);
}

void MQTTManager::connect()
{
    while (!client.connected())
    {
        Serial.println("Connecting to MQTT...");

        String clientID = "ESP32Client-";
        clientID += String(random(0xffff), HEX);

        if (client.connect(clientID.c_str(), mqtt_username, mqtt_password))
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

void MQTTManager::checkConnection()
{
    if (!client.connected())
    {
        redLed->switchOn();
        greenLed->switchOff();
        this->connect();
    }
}
