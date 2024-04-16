#ifndef __MESSAGEHANDLER__
#define __MESSAGEHANDLER__

#include <Arduino.h>


#define VALUE_PREFIX "VAL"
#define MODE_IN_PREFIX "MOD"
#define MODE_OUT_PREFIX "MOA"
#define DASH_PREFIX "DAS"
#define MSG_VAL 0
#define MSG_MOD 1
#define MODE_AUTOMATIC 0
#define MODE_MANUAL 1

class MessageHandler
{
private:
    
public:
    String getMessage();
    int getValue(String msg);
    String getType(String msg);
    void sendMode(int mode);
    void sendValue(int value);
};

#endif
