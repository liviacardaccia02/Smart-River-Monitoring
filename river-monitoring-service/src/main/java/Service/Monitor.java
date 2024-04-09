package Service;

import thread.data.SharedMessage;
import utils.Pair;

import java.util.List;
import java.util.Map;

public class Monitor implements Runnable {
    private final SharedMessage<Pair<String, Long>> waterLevel;
    private final SharedMessage<String> dangerLevel;
    private final SharedMessage<Integer> valve;
    private final SharedMessage<Integer> frequency;

    private final List<Integer> waterLevelBreakpoints;
    private final Map<String, Integer> dangerLevelValveMapping;
    private final Map<String, Integer> dangerLevelFrequencyMapping;

    public Monitor(SharedMessage<Pair<String, Long>> waterLevel,
                   SharedMessage<String> dangerLevel,
                   SharedMessage<Integer> valve,
                   List<Integer> waterLevelBreakpoints,
                   SharedMessage<Integer> frequency) {
        this.waterLevel = waterLevel;
        this.waterLevelBreakpoints = waterLevelBreakpoints;
        this.dangerLevel = dangerLevel;
        this.valve = valve;
        this.frequency = frequency;

        this.dangerLevelValveMapping = Map.of(
                "ALARM-TOO-LOW", 0,
                "NORMAL", 25,
                "PRE-ALARM-TOO-HIGH", 25,
                "ALARM-TOO-HIGH", 50,
                "ALARM-TOO-HIGH-CRITIC", 100
        );

        this.dangerLevelFrequencyMapping = Map.of(
                "ALARM-TOO-LOW", 5000,
                "NORMAL", 5000,
                "PRE-ALARM-TOO-HIGH", 2000,
                "ALARM-TOO-HIGH", 2000,
                "ALARM-TOO-HIGH-CRITIC", 2000
        );
    }

    private String getDangerLevel() {
        int currentWaterLevel = Integer.parseInt(waterLevel.getMessage().getFirst());
        if (currentWaterLevel < waterLevelBreakpoints.get(0)) {
            return "ALARM-TOO-LOW";
        } else if (currentWaterLevel > waterLevelBreakpoints.get(3)) {
            return "ALARM-TOO-HIGH-CRITIC";
        } else if (currentWaterLevel <= waterLevelBreakpoints.get(1)) {
            return "NORMAL";
        } else if (currentWaterLevel <= waterLevelBreakpoints.get(2)) {
            return "PRE-ALARM-TOO-HIGH";
        } else {
            return "ALARM-TOO-HIGH";
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                synchronized (waterLevel) {
                    waterLevel.wait();
                    String newDangerLevel = getDangerLevel();
                    if (!dangerLevel.getMessage().equals(newDangerLevel)) {
                        synchronized (dangerLevel) {
                            dangerLevel.setMessage(newDangerLevel);
                            valve.setMessage(dangerLevelValveMapping.get(newDangerLevel));
                            frequency.setMessage(dangerLevelFrequencyMapping.get(newDangerLevel));
                            dangerLevel.notifyAll();
                        }
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}