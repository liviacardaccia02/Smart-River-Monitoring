#include <Arduino.h>
#include <Wire.h>
#include <PubSubClient.h>
#include <WiFi.h>
#include <Sonar.h>
#include <Led.h>
#include <MQTTManager.h>

#define MSG_BUFFER_SIZE 50
#define WIFI_TIMEOUT 10000

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

const char *mqtt_broker = "broker.hivemq.com";
const char *topic = "WaterLevel";
const char *mqtt_username = "liviacardaccia";
const char *mqtt_password = "public";
const int mqtt_port = 1883;

const char *ssid = "Livia’s iPhone"; // Replace with your own SSID
const char *password = "kitty123";   // Replace with your own password

unsigned long lastPublishTime = 0;
char msg[MSG_BUFFER_SIZE];
int value = 0;

void connectToWifi()
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
      Serial.println("IP address: ");
      Serial.println(WiFi.localIP());
    }
  }
}

void callback(char *topic, byte *payload, unsigned int length)
{
  Serial.print("Message received on topic: ");
  Serial.println(topic);
  Serial.print("Content: ");
  for (int i = 0; i < length; i++)
  {
    Serial.print((char)payload[i]);
  }
  Serial.println();
}

void setup()
{
  Serial.begin(115200);
  sonar = new Sonar(TRIG_PIN, ECHO_PIN);
  redLed = new Led(RED_LED_PIN);
  greenLed = new Led(GREEN_LED_PIN);
  mqttManager = new MQTTManager(mqtt_broker, mqtt_port, mqtt_username,
                                mqtt_password, &espClient, redLed, greenLed);
  connectToWifi();
  mqttManager->connect();
  randomSeed(micros());
  client.setServer(mqtt_broker, mqtt_port);
  client.setCallback(callback);
}

void loop()
{
  delay(500);

  if (WiFi.status() != WL_CONNECTED)
  {
    redLed->switchOn();
    greenLed->switchOff();
    connectToWifi();
  }

  mqttManager->checkConnection();

  unsigned long currentTime = millis();

  if (currentTime - lastPublishTime >= 1000)
  {
    value = sonar->getDistance();

    String strValue = String(value);

    mqttManager->publish(topic, strValue.c_str());

    lastPublishTime = currentTime;
  }
}
