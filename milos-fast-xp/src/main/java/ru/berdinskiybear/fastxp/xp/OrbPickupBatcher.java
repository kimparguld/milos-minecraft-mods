package ru.berdinskiybear.fastxp.xp;

import java.util.List;

public final class OrbPickupBatcher {
    private OrbPickupBatcher() {
    }

    public static <T> List<T> selectForPickup(List<T> nearbyOrbs, int orbsPerTick) {
        if (orbsPerTick <= 0 || nearbyOrbs.isEmpty()) {
            return List.of();
        }
        int count = Math.min(nearbyOrbs.size(), orbsPerTick);
        return nearbyOrbs.subList(0, count);
    }
}
