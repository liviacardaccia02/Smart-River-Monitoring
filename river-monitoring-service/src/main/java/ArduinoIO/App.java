package ArduinoIO;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class App {
    private int count = 0;

    SerialMonitor sm;


    private void connectToArduino() {
        try {
            sm = new SerialMonitor();
            sm.start("COM5");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void disconnectToArduino() {
        try {
            sm.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static void main(String[] args) {
    }
}