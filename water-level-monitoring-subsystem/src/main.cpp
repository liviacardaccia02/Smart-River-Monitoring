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
#define THRESHOLD 200

#define RED_LED_PIN 1
#define GREEN_LED_PIN 2
#define TRIG_PIN 13
#define ECHO_PIN 14

WiFiClient espClient;

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

const char *ssid = "iPhone di Livia"; // Replace with your own SSID
const char *password = "kitty123";    // Replace with your own password

unsigned long lastPublishTime = 0;
char msg[MSG_BUFFER_SIZE];
int value = 0;
int frequency;

void callback(char *topic, u_int8_t *payload, unsigned int length)
{
  Serial.print("Message arrived on topic [");
  Serial.print(topic);
  Serial.print("]: ");
  for (int i = 0; i < length; i++)
  {
    Serial.print((char)payload[i]);
  }
  Serial.println();

  if (strcmp(topic, frequencyTopic) == 0)
  {
    char str[(sizeof(payload)) + 1];
    memcpy(str, payload, sizeof(payload));
    str[sizeof(payload)] = 0;
    frequency = atoi(str);
  }
}

void setup()
{
  Serial.begin(115200);
  sonar = new Sonar(TRIG_PIN, ECHO_PIN);
  redLed = new Led(RED_LED_PIN);
  greenLed = new Led(GREEN_LED_PIN);
  mqttManager = new MQTTManager(mqttBroker, mqttPort, mqttUsername,
                                mqttPassword, &espClient, redLed, greenLed);
  wifiManager = new WiFiManager(ssid, password, redLed, greenLed);
  frequency = NORMAL_FREQUENCY;
  wifiManager->connect();
  mqttManager->connect();
  mqttManager->subscribe(frequencyTopic, callback);
  randomSeed(micros());
}

void loop()
{
  delay(500);

  mqttManager->update();
  wifiManager->checkConnection();
  mqttManager->checkConnection();

  unsigned long currentTime = millis();

  if (currentTime - lastPublishTime >= frequency)
  {
    value = sonar->getDistance();
    if (value > THRESHOLD)
    {
      value = THRESHOLD;
    }
    String strValue = String(THRESHOLD - value);
    mqttManager->publish(waterLevelTopic, strValue.c_str());
    lastPublishTime = currentTime;
  }
}
