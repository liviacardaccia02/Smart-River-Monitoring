#include "Potentiometer.h"

Potentiometer::Potentiometer(int pin)
{
    this->pin = pin;
    pinMode(pin, INPUT);
}

int Potentiometer::getValue()
{
    return analogRead(pin);
}

int Potentiometer::getPercent()
{
    return map(this->getValue(),MIN_REAL_VALUE, MAX_REAL_VALUE, MIN_PERCENT, MAX_PERCENT);
}