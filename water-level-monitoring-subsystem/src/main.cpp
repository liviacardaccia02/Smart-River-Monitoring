#include <Arduino.h>
#include <Wire.h>
#include <PubSubClient.h>
#include <WiFi.h>
#include <Sonar.h>
#include <Led.h>

#define MSG_BUFFER_SIZE (500)
#define WIFI_TIMEOUT 10000
#define WIFI_SSID "Livia’s iPhone" // Replace with your own SSID
#define WIFI_PASSWORD "kitty123"   // Replace with your own password

#define RED_LED_PIN 1
#define GREEN_LED_PIN 2
#define TRIG_PIN 13
#define ECHO_PIN 14

WiFiClient espClient;
PubSubClient client(espClient);
Sonar *sonar;
Led *redLed;
Led *greenLed;

const char *mqtt_broker = "broker.hivemq.com";
const char *topic = "WaterLevel";
const char *mqtt_username = "liviacardaccia";
const char *mqtt_password = "public";
const int mqtt_port = 1883;

unsigned long lastPublishTime = 0;
char msg[MSG_BUFFER_SIZE];
int value = 0;

void connectToWifi()
{
  delay(1000);
  Serial.println("Connecting to WiFi...");
  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

  unsigned long startAttemptTime = millis();

  while (WiFi.status() != WL_CONNECTED && millis() - startAttemptTime < WIFI_TIMEOUT)
  {
    Serial.print(".");
    delay(100);
  }

  if (WiFi.status() != WL_CONNECTED)
  {
    redLed->switchOn();
    greenLed->switchOff(); // disconnected
    Serial.println("Failed to connect to WiFi.");
    Serial.println("Trying again in 5 seconds...");
    delay(5000);
    connectToWifi();
  }
  else
  {
    redLed->switchOff();
    greenLed->switchOn(); // connected
    Serial.println("Connected to WiFi.");
    Serial.println("IP address: ");
    Serial.println(WiFi.localIP());
  }
}

void connectToMQTT()
{
  while (!client.connected())
  {
    Serial.println("Connecting to MQTT...\n");

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

  connectToWifi();

  client.setServer(mqtt_broker, mqtt_port);
  client.setCallback(callback);
}

void loop()
{
  delay(500);

  while (WiFi.status() != WL_CONNECTED)
  {
    redLed->switchOn();
    greenLed->switchOff();
    connectToWifi();
  }

  if (!client.connected())
  {
    redLed->switchOn();
    greenLed->switchOff();
    connectToMQTT();
  }

  unsigned long currentTime = millis();

  if (currentTime - lastPublishTime >= 1000)
  {
    value = sonar->getDistance();

    String strValue = String(value);

    client.publish(topic, strValue.c_str());

    lastPublishTime = currentTime;
  }

  client.loop();
  delay(1000);
}
