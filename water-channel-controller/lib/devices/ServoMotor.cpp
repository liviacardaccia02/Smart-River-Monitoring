#include "ServoMotor.h"

ServoMotor::ServoMotor(int pin)
{
    servo.attach(pin);
    closeGate();
}

void ServoMotor::openGate()
{
    servo.write(OPEN_DEGREES);
}

void ServoMotor::closeGate()
{
    servo.write(CLOSED_DEGREES);
}