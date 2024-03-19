#include <Arduino.h>
#include <Potentiometer.h>

#include <LCD.h>
#include <SystemManager.h>

LCD *lcd;
SystemManager *man;
int currentMode;

void setup() {
  Serial.begin(9600);
  man = new SystemManager(2,A0,8);
}

void loop() {
  currentMode = man->checkButton();

  if(currentMode == AUTOMATIC)
  {
    man->updateAutomatic();
  }
  else if(currentMode == MANUAL)
  {
    man->updateManual();
  }
  
  delay(50);//Makes the lcd stop flickering
}