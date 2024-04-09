#include <Arduino.h>
#include <Wire.h>
#include <PubSubClient.h>
#include <WiFi.h>
#include <Sonar.h>
#include <Led.h>
#include <MQTTManager.h>
#include <WiFiManager.h>

#define MSG_BUFFER_SIZE 50
#define NORMAL_FREQUENCY 5000
#define HIGH_FREQUENCY 2000

#define RED_LED_PIN 1
#define GREEN_LED_PIN 2
#define TRIG_PIN 13
#define ECHO_PIN 14

WiFiClient espClient;
PubSubClient client(espClient);

Sonar *sonar;
Led *redLed;
Led *greenLed;
MQTTManager *mqttManager;
WiFiManager *wifiManager;

const char *mqttBroker = "broker.hivemq.com";
const char *waterLevelTopic = "WaterLevelMonitoring";
const char *frequencyTopic = "FrequencyMonitoring";
const char *mqttUsername = "liviacardaccia";
const char *mqttPassword = "public";
const int mqttPort = 1883;

const char *ssid = "Redmi 10"; // Replace with your own SSID
const char *password = "11111111";   // Replace with your own password

unsigned long lastPublishTime = 0;
char msg[MSG_BUFFER_SIZE];
int value = 0;

void setup()
{
  Serial.begin(115200);
  sonar = new Sonar(TRIG_PIN, ECHO_PIN);
  redLed = new Led(RED_LED_PIN);
  greenLed = new Led(GREEN_LED_PIN);
  mqttManager = new MQTTManager(mqttBroker, mqttPort, mqttUsername,
                                mqttPassword, &espClient, redLed, greenLed);
  wifiManager = new WiFiManager(ssid, password, redLed, greenLed);
  wifiManager->connect();
  mqttManager->connect();
  mqttManager->subscribe(frequencyTopic);
  randomSeed(micros());
}

void loop()
{
  delay(500);

  wifiManager->checkConnection();

  mqttManager->checkConnection();

  unsigned long currentTime = millis();

  if (currentTime - lastPublishTime >= 1000)
  {
    value = sonar->getDistance();

    String strValue = String(value);

    mqttManager->publish(waterLevelTopic, strValue.c_str());

    lastPublishTime = currentTime;
  }
}
