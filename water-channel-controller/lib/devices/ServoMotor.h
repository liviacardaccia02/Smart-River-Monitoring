#ifndef __SERVOMOTOR__
#define __SERVOMOTOR__

#include <Servo.h>

#define OPEN_DEGREES 0
#define CLOSED_DEGREES 90

class ServoMotor
{
private:
    Servo servo;

public:
    ServoMotor(int pin);
    void openGate();
    void closeGate();
};

#endif