package ArduinoIO;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import thread.data.SharedMessage;
import utils.Logger;

public class SerialMonitor implements SerialPortEventListener {
    private final SharedMessage<String> mode;
    private final SharedMessage<Integer> valve;
    SerialPort serialPort;

    String receivedData = "";

    public SerialMonitor(SharedMessage<String> mode, SharedMessage<Integer> valve) {
        this.mode = mode;
        this.valve = valve;

        this.valve.addFrequencyChangeListener(valveOpening -> {
            try {
                if (this.serialPort.isOpened()){
                    serialPort.writeString("VAL"+valveOpening.toString()+"\n");
                    Logger.success("Valve opening set to: "+valveOpening);
                }
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        });

        this.mode.addFrequencyChangeListener(newMode -> {
            try {
                if (this.serialPort.isOpened()){
                    serialPort.writeString("MOD"+(newMode.equals("{\"mode\":\"auto\"}") ? 0: 1)+"\n");
                    Logger.success("mode changed in arduino "+"MOD"+(newMode.equals("{\"mode\":\"auto\"}") ? 0: 1));
                }
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        });
    }

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
            Logger.error("There is an error on writing string to port т: " + ex);
        }

        Logger.success("Serial port opened");
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

        Logger.info("Message from controller: "+receivedData);
        if(receivedData.contains("VAL")){
            valve.setMessage(Integer.parseInt(receivedData.substring(3, receivedData.length()-1)));
        } else if (receivedData.contains("MOA")){
            mode.setMessage("{\"mode\":"+(receivedData.substring(3, receivedData.length()-1).equals("0") ? "\"auto\"": "\"manual\"")+"}");
        }
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
                Logger.error("Error in receiving string from COM-port: " + ex);
            }
        }
    }
}
