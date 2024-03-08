#include "LCD.h"

LCD::LCD()
{
    this->displayLCD = new LiquidCrystal_I2C(LCD_ADDR, LCD_COLS, LCD_ROWS);
    displayLCD->init();

    this->displayOn();
}

void LCD::displayOn()
{
    displayLCD->display();
    displayLCD->backlight();
    displayLCD->clear();
}

void LCD::displayOff()
{
    displayLCD->noDisplay();
    displayLCD->noBacklight();
}

void LCD::displayClear()
{
    displayLCD->clear();
}

void LCD::clearLine(int line)
{
    displayLCD->setCursor(0, line);
    for(int i = 0; i <= LCD_COLS - 1; i++)
    {
        displayLCD->print(" ");
    }
}

void LCD::WriteOnLine(String msg, int line)
{
    if(line >= LCD_ROWS || line < 0)
    {   
        displayLCD->setCursor(0, LCD_FIRST_LINE);
    }
    else
    {
        displayLCD->setCursor(0, line);
    }

    displayLCD->print(msg);
}