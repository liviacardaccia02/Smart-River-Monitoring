#include <Arduino.h>
#include <Wire.h>
#include <PubSubClient.h>
#include <WiFi.h>

#define MSG_BUFFER_SIZE (500)
#define WIFI_TIMEOUT 20000
#define WIFI_SSID "Livia’s iPhone" // Replace with your own SSID
#define WIFI_PASSWORD "kitty123"   // Replace with your own password

WiFiClient espClient;
PubSubClient client(espClient);

const char *mqtt_broker = "broker.hivemq.com";
const char *topic = "testTopic";
const char *mqtt_username = "liviacardaccia";
const char *mqtt_password = "public";
const int mqtt_port = 1883;

long lastMsg = 0;
char msg[MSG_BUFFER_SIZE];
int value = 0;

void connectToWifi()
{
  delay(100);
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
    Serial.println("Failed to connect to WiFi.");
    Serial.println("Restarting in 5 seconds...");
    delay(5000);
    ESP.restart();
  }
  else
  {
    Serial.println("Connected to WiFi.");
    Serial.println("IP address: ");
    Serial.print(WiFi.localIP());
  }
}

void reconnect()
{
  while (!client.connected())
  {
    Serial.println("Connecting to MQTT...");

    String clientID = "ESP32Client-";
    clientID += String(random(0xffff), HEX);

    if (client.connect(clientID.c_str(), mqtt_username, mqtt_password))
    {
      Serial.println("Connected to MQTT");
    }
    else
    {
      Serial.println("Failed, code = ");
      Serial.print(client.state());
      Serial.println("Try again in 5 seconds");
      delay(5000);
    }
  }
}

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Messaggio ricevuto su topic: ");
  Serial.println(topic);
  Serial.print("Contenuto: ");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();
}

void setup()
{
  Serial.begin(115200);
  pinMode(LED_BUILTIN, OUTPUT);
  connectToWifi();
  client.setServer(mqtt_broker, mqtt_port);  // Port 1883 for MQTT
  client.setCallback(callback);
  client.publish(topic, "hello world");
  client.subscribe(topic);
}

void loop()
{
  if (!client.connected())
  {
    reconnect();
  }
  client.loop();

  unsigned long now = millis();
  if (now - lastMsg > 2000) {
    lastMsg = now;
    ++value;
    snprintf (msg, MSG_BUFFER_SIZE, "hello world #%ld", value);
    Serial.print("Publish message: ");
    Serial.println(msg);
    client.publish("testTopic", msg);
  }
}
