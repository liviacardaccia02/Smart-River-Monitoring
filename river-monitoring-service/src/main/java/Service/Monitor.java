package Service;

import thread.data.SharedMessage;
import utils.Logger;
import utils.Pair;

import java.util.List;
import java.util.Map;

public class Monitor {
    private final SharedMessage<Pair<String, Long>> waterLevel;
    private final SharedMessage<String> dangerLevel;
    private final SharedMessage<Integer> valve;
    private final SharedMessage<Integer> frequency;
    private final SharedMessage<String> mode;

    private final List<Integer> waterLevelBreakpoints;
    private final Map<String, Integer> dangerLevelValveMapping;
    private final Map<String, Integer> dangerLevelFrequencyMapping;

    public Monitor(SharedMessage<Pair<String, Long>> waterLevel,
                   SharedMessage<String> dangerLevel,
                   SharedMessage<Integer> valve,
                   List<Integer> waterLevelBreakpoints,
                   SharedMessage<Integer> frequency, SharedMessage<String> mode) {
        this.waterLevel = waterLevel;
        this.waterLevelBreakpoints = waterLevelBreakpoints;
        this.dangerLevel = dangerLevel;
        this.valve = valve;
        this.frequency = frequency;
        this.mode = mode;


        this.dangerLevelValveMapping = Map.of(
                "ALARM-TOO-LOW", 1,
                "NORMAL", 25,
                "PRE-ALARM-TOO-HIGH", 25,
                "ALARM-TOO-HIGH", 50,
                "ALARM-TOO-HIGH-CRITIC", 97
        );

        this.dangerLevelFrequencyMapping = Map.of(
                "ALARM-TOO-LOW", 5000,
                "NORMAL", 5000,
                "PRE-ALARM-TOO-HIGH", 2000,
                "ALARM-TOO-HIGH", 2000,
                "ALARM-TOO-HIGH-CRITIC", 2000
        );

        waterLevel.addFrequencyChangeListener(newMessage -> {
            String newDangerLevel = getDangerLevel();

            synchronized (this.dangerLevel) {
                synchronized (this.mode) {
                    this.dangerLevel.setMessage(newDangerLevel);
                    if (this.mode.getMessage().equals("{\"mode\":\"auto\"}")) {
                        this.valve.setMessage(dangerLevelValveMapping.get(newDangerLevel));
                    }
                    this.frequency.setMessage(dangerLevelFrequencyMapping.get(newDangerLevel));
                    this.dangerLevel.notifyAll();
                }
            }

        });
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
}