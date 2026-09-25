package se.guldbransen.milos.fastxp.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FastXpConfigIOTest {
    @TempDir
    Path dir;

    @Test
    void savedConfigRoundTrips() {
        Path file = dir.resolve("milos-fast-xp.json");
        FastXpConfig config = new FastXpConfig();
        config.setXpCreationMultiplier(7);
        config.setXpPickupOrbsPerTick(12);
        config.setAutoThrowEnabled(false);
        config.setProjectileEnabled("minecraft:snowball", false);

        FastXpConfigIO.save(config, file);
        FastXpConfig loaded = FastXpConfigIO.load(file);

        assertEquals(7, loaded.getXpCreationMultiplier());
        assertEquals(12, loaded.getXpPickupOrbsPerTick());
        assertFalse(loaded.isAutoThrowEnabled());
        assertFalse(loaded.isProjectileEnabled("minecraft:snowball"));
        assertTrue(loaded.isProjectileEnabled("minecraft:egg"));
    }

    @Test
    void missingFileFallsBackToDefaultsAndWritesThem() {
        Path file = dir.resolve("nested").resolve("milos-fast-xp.json");

        FastXpConfig loaded = FastXpConfigIO.load(file);

        assertEquals(1, loaded.getXpCreationMultiplier());
        assertEquals(2, loaded.getAutoThrowDelayTicks());
        assertTrue(Files.isRegularFile(file), "the default config should have been written to disk");
    }

    @Test
    void corruptedFileFallsBackToDefaultsAndRewritesIt() throws IOException {
        Path file = dir.resolve("milos-fast-xp.json");
        Files.writeString(file, "{ this is not json");

        FastXpConfig loaded = FastXpConfigIO.load(file);

        assertEquals(1, loaded.getXpCreationMultiplier());
        assertTrue(loaded.isProjectileEnabled("minecraft:trident"));
        assertEquals(loaded.getXpPickupOrbsPerTick(), FastXpConfigIO.load(file).getXpPickupOrbsPerTick());
        assertTrue(Files.readString(file).contains("xpCreationMultiplier"), "the file should have been rewritten");
    }

    @Test
    void partialFileKeepsDefaultsForMissingKeysAndStaysEditable() throws IOException {
        Path file = dir.resolve("milos-fast-xp.json");
        Files.writeString(file, "{\"xpCreationMultiplier\": 5}");

        FastXpConfig loaded = FastXpConfigIO.load(file);

        assertEquals(5, loaded.getXpCreationMultiplier());
        assertEquals(1, loaded.getXpPickupOrbsPerTick());
        assertTrue(loaded.isProjectileEnabled("minecraft:egg"));

        loaded.setProjectileEnabled("minecraft:egg", false);
        assertFalse(loaded.isProjectileEnabled("minecraft:egg"));
    }

    @Test
    void explicitNullProjectileListIsRepaired() throws IOException {
        Path file = dir.resolve("milos-fast-xp.json");
        Files.writeString(file, "{\"autoThrowProjectiles\": null}");

        FastXpConfig loaded = FastXpConfigIO.load(file);

        assertTrue(loaded.isProjectileEnabled("minecraft:snowball"));
    }
}
