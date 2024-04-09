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

int MessageHandler::getValue(String msg)
{
    msg.remove(0,3);
    return msg.toInt();
}

String MessageHandler::getType(String msg)
{
    return msg.substring(0,3);
}

void MessageHandler::sendMode(int mode)
{
    Serial.println(MODE_PREFIX + String(mode));
}

void MessageHandler::sendValue(int value)
{
    Serial.println(VALUE_PREFIX + String(value));
}