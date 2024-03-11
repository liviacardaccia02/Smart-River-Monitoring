#ifndef __SYSTEMMANAGER__
#define __SYSTEMMANAGER__

#include <Arduino.h>
#include <Button.h>
#include <LCD.h>
#include <Potentiometer.h>
#include <ServoMotor.h>

#define AUTOMATIC 0
#define MANUAL 1

class SystemManager
{
private:
    Button *button;
    LCD *lcdDisplay;
    Potentiometer *pot;
    ServoMotor *servo;
    int mode;
    int prevLevel;
    void updateBoard(int gateLevel);
    void displayMode();
public:
    SystemManager(int buttonPin, int potPin, int servoPin);
    int checkMode();
    void updateManual();
    void updateAutomatic();
};

#endif
