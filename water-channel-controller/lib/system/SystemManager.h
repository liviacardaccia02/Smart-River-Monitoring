#ifndef __SYSTEMMANAGER__
#define __SYSTEMMANAGER__

#include <Arduino.h>
#include <Button.h>
#include <LCD.h>
#include <Potentiometer.h>
#include <ServoMotor.h>
#include <MessageHandler.h>

#define AUTOMATIC 0
#define MANUAL 1

class SystemManager
{
private:
    Button *button;
    LCD *lcdDisplay;
    Potentiometer *pot;
    ServoMotor *servo;
    MessageHandler *msgHandler;
    int mode;
    int prevLevel;
    int prevLevelPot;
    void updateBoard(int gateLevel);
    void displayMode();
public:
    SystemManager(int buttonPin, int potPin, int servoPin);
    int checkButton();
    void changeMode();
    void changeMode(int newMode);
    void updateManual();
    void updateAutomatic();
};

#endif
