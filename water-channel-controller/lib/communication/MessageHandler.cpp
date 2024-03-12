#include "MessageHandler.h"

int MessageHandler::getValue()
{
    if(Serial.available())
    {
        String msg = Serial.readStringUntil('\n');
        Serial.println("Recieved " + msg);
        if(msg.indexOf("VAL") >= 0)
        {
            msg.remove(0,3);
            Serial.println("Modified " + msg);
            return msg.toInt();
        }
    }

    return -1; //Returns negative value if there is no message incoming
}