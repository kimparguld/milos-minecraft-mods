package ru.berdinskiybear.fastxp.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FastXpConfigTest {
    @Test
    void defaultsReproduceVanillaBehavior() {
        FastXpConfig config = new FastXpConfig();

        assertTrue(config.isXpCreationEnabled());
        assertEquals(1, config.getXpCreationMultiplier());
        assertTrue(config.isXpPickupEnabled());
        assertEquals(1, config.getXpPickupOrbsPerTick());
        assertTrue(config.isAutoThrowEnabled());
        assertEquals(2, config.getAutoThrowDelayTicks());
    }

    @Test
    void defaultProjectileWhitelistIncludesExperienceBottle() {
        FastXpConfig config = new FastXpConfig();

        assertTrue(config.isProjectileEnabled("minecraft:experience_bottle"));
        assertTrue(config.isProjectileEnabled("minecraft:snowball"));
        assertFalse(config.isProjectileEnabled("minecraft:diamond"));
    }

    @Test
    void projectileEnabledStateCanBeToggled() {
        FastXpConfig config = new FastXpConfig();

        config.setProjectileEnabled("minecraft:snowball", false);
        assertFalse(config.isProjectileEnabled("minecraft:snowball"));

        config.setProjectileEnabled("minecraft:diamond", true);
        assertTrue(config.isProjectileEnabled("minecraft:diamond"));
    }
}
