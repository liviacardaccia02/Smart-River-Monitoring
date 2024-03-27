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
    String msg = msgHandler->getMessage();
    String msgType = msgHandler->getType(msg);
    int msgValue = msgHandler->getValue(msg);
    int gateLevel = pot->getPercent();

    if(msgType == MODE_PREFIX)
    {
        changeMode(msgValue);
    }

    
    if(gateLevel != prevLevel){
        updateBoard(gateLevel);
    }
    prevLevel = gateLevel;
}

void SystemManager::updateAutomatic()
{
    //check for serial input
    String msg = msgHandler->getMessage();
    String msgType = msgHandler->getType(msg);
    int msgValue = msgHandler->getValue(msg);

    if(msgType == MODE_PREFIX)
    {
        changeMode(msgValue);
    }
    else if(msgType == VALUE_PREFIX)
    {
        if((msgValue != prevLevel) && (msgValue > -1))
        {
            updateBoard(msgValue);
            prevLevel = msgValue;
        }
    }



    //if value is updated 
}

int SystemManager::checkButton()
{
    if(button->isPressed())
    {
        changeMode();
    }

    return mode;
}

void SystemManager::changeMode()
{
    if(mode == AUTOMATIC)
    {
        mode = MANUAL;
        msgHandler->sendMode(MANUAL);
    }
    else if(mode == MANUAL)
    {
        mode = AUTOMATIC;
        msgHandler->sendMode(AUTOMATIC);
    }
    
    displayMode();
}

void SystemManager::changeMode(int newMode)
{
    if(newMode != mode)
    {
        if(newMode == AUTOMATIC)
        {
            mode = AUTOMATIC;
            msgHandler->sendMode(AUTOMATIC);
        }
        else if(newMode == MANUAL)
        {
            mode = MANUAL;
            msgHandler->sendMode(MANUAL);
        }
    }
}