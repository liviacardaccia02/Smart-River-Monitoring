#include <Arduino.h>
#include <Sonar.h>
#include <Led.h>

#define RED_LED_PIN 1
#define GREEN_LED_PIN 2
#define TRIG_PIN 13
#define ECHO_PIN 14

Led redLed(RED_LED_PIN);
Led greenLed(GREEN_LED_PIN);
Sonar sonar(TRIG_PIN, ECHO_PIN);

void setup()
{
  Serial.begin(115200);
}

void loop()
{ 
  redLed.switchOn();
  greenLed.switchOff();
  delay(500);
  redLed.switchOff();
  greenLed.switchOn();
  Serial.println(sonar.getDistance());
  delay(500);
}
