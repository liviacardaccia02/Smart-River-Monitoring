#ifndef __POTENTIOMETER__
#define __POTENTIOMETER__

#include <Arduino.h>

#define MIN_REAL_VALUE 0
#define MAX_REAL_VALUE 1023
#define MIN_PERCENT 0
#define MAX_PERCENT 100

class Potentiometer
{
private:
    int pin;

public:
    Potentiometer(int pin);
    int getValue();
    int getPercent();
};

#endif