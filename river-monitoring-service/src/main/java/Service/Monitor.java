package Service;

import thread.data.SharedMessage;
import utils.Pair;

import java.util.List;
import java.util.Map;

public class Monitor implements Runnable {
    private final SharedMessage<Pair<String, Long>> waterLevel;
    private final SharedMessage<String> dangerLevel;
    private final SharedMessage<Integer> valve;

    private final List<Integer> waterLevelBreakpoints;
    private final Map<String, Integer> dangerLevelValveMapping = Map.of(
            "ALARM-TOO-LOW", 0,
            "NORMAL", 25,
            "PRE-ALARM-TOO-HIGH", 25,
            "ALARM-TOO-HIGH", 50,
            "ALARM-TOO-HIGH-CRITIC", 100
    );


    public Monitor(SharedMessage<Pair<String, Long>> waterLevel, SharedMessage<String> dangerLevel, SharedMessage<Integer> valve, List<Integer> waterLevelBreakpoints) {
        this.waterLevel = waterLevel;
        this.waterLevelBreakpoints = waterLevelBreakpoints;
        this.dangerLevel = dangerLevel;
        this.valve = valve;
    }


    private String getDangerLevel() {
        synchronized (waterLevel) {
            if (Integer.parseInt(waterLevel.getMessage().getFirst()) < waterLevelBreakpoints.get(0)) {
                return "ALARM-TOO-LOW";
            } else if (Integer.parseInt(waterLevel.getMessage().getFirst()) > waterLevelBreakpoints.get(3)) {
                return "ALARM-TOO-HIGH-CRITIC";
            } else if (Integer.parseInt(waterLevel.getMessage().getFirst()) <= waterLevelBreakpoints.get(1)) {
                return "NORMAL";
            } else if (Integer.parseInt(waterLevel.getMessage().getFirst()) <= waterLevelBreakpoints.get(2)) {
                return "PRE-ALARM-TOO-HIGH";
            } else {
                return "ALARM-TOO-HIGH";
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            synchronized (waterLevel) {
                try {
                    waterLevel.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("nuovo livello");
                if (!dangerLevel.getMessage().equals(getDangerLevel())) {
                    synchronized (dangerLevel) {
                        dangerLevel.setMessage(getDangerLevel());
                        dangerLevel.notifyAll();
                        valve.setMessage(dangerLevelValveMapping.get(dangerLevel.getMessage()));
                    }
                }
            }
        }
    }
}
