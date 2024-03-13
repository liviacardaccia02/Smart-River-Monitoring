#include <Arduino.h>
#include <Wire.h>
#include <PubSubClient.h>
#include <WiFi.h>
#include <Sonar.h>

#define MSG_BUFFER_SIZE (500)
#define WIFI_TIMEOUT 20000
#define WIFI_SSID "Livia’s iPhone" // Replace with your own SSID
#define WIFI_PASSWORD "kitty123"   // Replace with your own password

#define RED_LED_PIN 1
#define GREEN_LED_PIN 2
#define TRIG_PIN 13
#define ECHO_PIN 14

WiFiClient espClient;
PubSubClient client(espClient);
Sonar* sonar;

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
  delay(200);
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
    digitalWrite(RED_LED_PIN, HIGH);
    digitalWrite(GREEN_LED_PIN, LOW); // disconnesso
    Serial.println("Failed to connect to WiFi.");
    Serial.println("Restarting in 5 seconds...");
    delay(5000);
    ESP.restart(); // TODO: add a counter to restart only after a certain number of attempts
  }
  else
  {
    digitalWrite(RED_LED_PIN, LOW);
    digitalWrite(GREEN_LED_PIN, HIGH); // connesso
    Serial.println("Connected to WiFi.");
    Serial.println("IP address: ");
    Serial.println(WiFi.localIP());
  }
}

void reconnect()
{
  while (!client.connected())
  {
    Serial.println("Connecting to MQTT...\n");

    String clientID = "ESP32Client-";
    clientID += String(random(0xffff), HEX);

    if (client.connect(clientID.c_str(), mqtt_username, mqtt_password))
    {
      digitalWrite(RED_LED_PIN, LOW);
      digitalWrite(GREEN_LED_PIN, HIGH); // mqtt okay
      Serial.println("Connected to MQTT");
    }
    else
    {
      digitalWrite(RED_LED_PIN, HIGH);
      digitalWrite(GREEN_LED_PIN, LOW); // mqtt not okay
      Serial.println("Failed, code = ");
      Serial.println(client.state());
      Serial.println("Try again in 5 seconds");
      delay(5000);
    }
  }
}

void callback(char *topic, byte *payload, unsigned int length)
{
  Serial.print("Messaggio ricevuto su topic: ");
  Serial.println(topic);
  Serial.print("Contenuto: ");
  for (int i = 0; i < length; i++)
  {
    Serial.print((char)payload[i]);
  }
  Serial.println();
}

void setup()
{
  Serial.begin(115200);
  pinMode(RED_LED_PIN, OUTPUT);
  pinMode(GREEN_LED_PIN, OUTPUT);
  connectToWifi();
  client.setServer(mqtt_broker, mqtt_port); 
  client.setCallback(callback);
}

void loop()
{
  if (WiFi.status() != WL_CONNECTED)
  {
    digitalWrite(RED_LED_PIN, HIGH);
    digitalWrite(GREEN_LED_PIN, LOW);
    Serial.println("WiFi connection lost. Reconnecting...");
    connectToWifi();
  }

  if (!client.connected())
  {
    digitalWrite(RED_LED_PIN, HIGH);
    digitalWrite(GREEN_LED_PIN, LOW);
    Serial.println("MQTT connection lost. Reconnecting...");
    reconnect();
  }

  unsigned long currentTime = millis();

  if (currentTime - lastPublishTime >= 1000) {  
    // Publishing every 1000 milliseconds (1 second)
    // TODO: add code to read the distance from the sensor

    Serial.println("Publishing distance...");
    
    lastPublishTime = currentTime;
  }

  client.loop();
  delay(1000);
}
