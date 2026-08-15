package ru.berdinskiybear.fastxp.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FastXpConfig implements Serializable {
    private boolean xpCreationEnabled = true;
    private int xpCreationMultiplier = 1;
    private boolean xpPickupEnabled = true;
    private int xpPickupOrbsPerTick = 1;
    private boolean autoThrowEnabled = true;
    private int autoThrowDelayTicks = 2;
    private List<String> autoThrowProjectiles = defaultProjectiles();

    private static List<String> defaultProjectiles() {
        return new ArrayList<>(List.of(
                "minecraft:snowball",
                "minecraft:egg",
                "minecraft:ender_pearl",
                "minecraft:splash_potion",
                "minecraft:lingering_potion",
                "minecraft:trident",
                "minecraft:experience_bottle"
        ));
    }

    public boolean isProjectileEnabled(String itemId) {
        return autoThrowProjectiles.contains(itemId);
    }

    public void setProjectileEnabled(String itemId, boolean enabled) {
        if (enabled) {
            if (!autoThrowProjectiles.contains(itemId)) {
                autoThrowProjectiles.add(itemId);
            }
        } else {
            autoThrowProjectiles.remove(itemId);
        }
    }
}
