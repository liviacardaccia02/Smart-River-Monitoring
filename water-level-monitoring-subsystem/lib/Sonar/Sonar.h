#ifndef __SONAR__
#define __SONAR__

class Sonar
{
private:
    int trigPin;
    int echoPin;
    void trigger();

public:
    Sonar(int trigPin, int echoPin);
    float getDistance();
};

#endif
