#include "WiFiManager.h"

WiFiManager::WiFiManager(const char *ssid, const char *password, Led *redLed, Led *greenLed)
{
    this->ssid = ssid;
    this->password = password;
    this->redLed = redLed;
    this->greenLed = greenLed;
}

void WiFiManager::connect()
{
    delay(100);

    while (WiFi.status() != WL_CONNECTED)
    {
        Serial.println("Connecting to WiFi...");

        WiFi.mode(WIFI_STA);
        WiFi.begin(ssid, password);

        if (WiFi.status() != WL_CONNECTED)
        {
            redLed->switchOn();
            greenLed->switchOff(); // disconnected
            Serial.println("Failed to connect to WiFi.");
            Serial.println("Trying again in 5 seconds...");
            delay(5000);
        }
        else
        {
            redLed->switchOff();
            greenLed->switchOn(); // connected
            Serial.println("");
            Serial.println("Connected to WiFi.");
        }
    }
}

void WiFiManager::checkConnection()
{
    if (WiFi.status() != WL_CONNECTED)
    {
        redLed->switchOn();
        greenLed->switchOff();
        this->connect();
    }
}
