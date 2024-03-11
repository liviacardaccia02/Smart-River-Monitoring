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

void ServoMotor::openDegrees(int degrees)
{
    servo.write(degrees);
}

void ServoMotor::openPercent(int percentage)
{
    servo.write(map(percentage, 0, 100, 0, 180));
}