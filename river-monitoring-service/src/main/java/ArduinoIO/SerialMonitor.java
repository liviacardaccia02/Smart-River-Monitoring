package ArduinoIO;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialMonitor implements SerialPortEventListener {
    SerialPort serialPort;

    String receivedData = "";

    public void start(String portName) {
        serialPort = new SerialPort(portName);
        try {
            serialPort.openPort();

            serialPort.setParams(SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |
                    SerialPort.FLOWCONTROL_RTSCTS_OUT);

            serialPort.addEventListener(this, SerialPort.MASK_RXCHAR);
        } catch (SerialPortException ex) {
            System.out.println("There is an error on writing string to port т: " + ex);
        }
    }

    public synchronized void close() {
        try {
            if (serialPort != null) {
                serialPort.removeEventListener();
                serialPort.closePort();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void processData(String receivedData) {

    }

    /**
     * Handle an event on the serial port. Read the data and print it.
     */
    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR() && event.getEventValue() > 0) {
            try {
                receivedData = receivedData.concat(serialPort.readString(event.getEventValue()));
                if (receivedData.contains("\n")) {
                    processData(receivedData.substring(0, receivedData.indexOf("\n")));

                    receivedData = receivedData.substring(receivedData.indexOf("\n") + 1);
                }
            } catch (SerialPortException ex) {
                System.out.println("Error in receiving string from COM-port: " + ex);
            }
        }
    }
}
