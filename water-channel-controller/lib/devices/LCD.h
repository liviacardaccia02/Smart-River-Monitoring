#ifndef __LCD__
#define __LCD__

#include <LiquidCrystal_I2C.h>

#define LCD_ADDR 0x27
#define LCD_COLS 20
#define LCD_ROWS 4
#define LCD_FIRST_LINE 1

class LCD
{
private:
    LiquidCrystal_I2C *displayLCD;
public:
    LCD();
    void displayOn();
    void displayOff();
    void displayClear();
    void WriteOnLine(String msg, int line);
};

#endif