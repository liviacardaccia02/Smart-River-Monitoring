#include "Sonar.h"
#include "Arduino.h"

Sonar::Sonar(int trigPin, int echoPin)
{
    this->trigPin = trigPin;
    this->echoPin = echoPin;
    pinMode(trigPin, OUTPUT);
    pinMode(echoPin, INPUT);
}

void Sonar::trigger()
{
    digitalWrite(trigPin, LOW);
    delayMicroseconds(2);
    digitalWrite(trigPin, HIGH);
    delayMicroseconds(10);
    digitalWrite(trigPin, LOW);
}

float Sonar::getDistance()
{
    /* Sending the initial impulse */
    trigger();

    /* Measuring the duration of the receiver impulse */
    unsigned long pulseDuration = pulseIn(echoPin, HIGH);

    /* Calculating the distance of the object detected */
    float distance = pulseDuration * 0.0343 / 2;

    return distance;
}
