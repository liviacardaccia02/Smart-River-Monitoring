#include "MessageHandler.h"

String MessageHandler::getMessage()
{
    String msg = "";
    if(Serial.available())
    {
        msg = Serial.readStringUntil('\n');
    }
    return msg;
}

int MessageHandler::getValue()
{
    if(Serial.available())
    {
        String msg = this->getMessage();
        if(msg.indexOf(VALUE_PREFIX) >= 0)
        {
            msg.remove(0,3);
            Serial.println("Received: " + msg);
            return msg.toInt();
        }
    }

    return -1; //Returns negative value if there is no message incoming
}

int MessageHandler::getType()
{
    //TODO
    String msg = this->getMessage();
    if(msg.indexOf(MODE_PREFIX) >= 0)
    {
        msg.remove(0,3);
        Serial.println("Received mode: " + msg);
        return msg.toInt();
    }
    
    return -1;
}