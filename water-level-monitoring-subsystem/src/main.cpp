#include <Arduino.h>
#include <Wire.h>
#include <PubSubClient.h>
#include <WiFi.h>

#define WIFI_TIMEOUT 20000
#define WIFI_SSID "SSID" // Replace with your own SSID
#define WIFI_PASSWORD "PASSWORD"   // Replace with your own password

WiFiClient espClient;
PubSubClient client(espClient);

void connectToWifi()
{
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
    Serial.println("Restarting in 10 seconds...");
    delay(10000);
    ESP.restart();
  }
  else
  {
    Serial.println("Connected to WiFi.");
    Serial.println("IP address: ");
    Serial.println(WiFi.localIP());
  }
}

void setup()
{
  Serial.begin(115200);
  pinMode(LED_BUILTIN, OUTPUT);
  connectToWifi();
}

void loop()
{
  // Just a test
  digitalWrite(LED_BUILTIN, HIGH);
  delay(500);
  digitalWrite(LED_BUILTIN, LOW);
  delay(500);
}
