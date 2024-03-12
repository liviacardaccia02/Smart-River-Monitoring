#include "SystemManager.h"

SystemManager::SystemManager(int buttonPin, int potPin, int servoPin)
{
    Serial.begin(9600);
    button = new Button(buttonPin);
    pot = new Potentiometer(potPin);
    servo = new ServoMotor(servoPin);
    lcdDisplay = new LCD();
    msgHandler = new MessageHandler();
    prevLevel = 0;
    mode = AUTOMATIC; //Modify to change the starting mode
    updateBoard(prevLevel);
}

void SystemManager::updateBoard(int gateLevel)
{
    displayMode();
    lcdDisplay->clearLine(2);
    lcdDisplay->WriteOnLine("Gate level: " +  (String)gateLevel +"%" , 2);
    servo->openPercent(gateLevel);
}

void SystemManager::displayMode()
{
    lcdDisplay->clearLine(1);
    if(mode == AUTOMATIC)
    {
        lcdDisplay->WriteOnLine("Mode: AUTOMATIC" , 1);
    }
    else if(mode == MANUAL)
    {
        lcdDisplay->WriteOnLine("Mode: MANUAL" , 1);
    }
}

void SystemManager::updateManual()
{
    int gateLevel = pot->getPercent();
    
    if(gateLevel != prevLevel){
        updateBoard(gateLevel);
    }
    prevLevel = gateLevel;

    //Communicate with application
    //TODO
}

void SystemManager::updateAutomatic()
{
    //check for serial input
    int msgValue = msgHandler->getValue();

    if((msgValue != prevLevel) && (msgValue != -1))
    {
        updateBoard(msgValue);
        prevLevel = msgValue;
    }

    //if value is updated 
}

int SystemManager::checkMode()
{
    if(button->isPressed())
    {
        if(mode == AUTOMATIC)
        {
            mode = MANUAL;
        }
        else
        {
            mode = AUTOMATIC;
        }
    }

    return mode;
}