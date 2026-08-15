package ru.berdinskiybear.playerhighlight.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerHighlightConfigIOTest {
    @TempDir
    Path dir;

    @Test
    void savedConfigRoundTrips() {
        Path file = dir.resolve("milos-player-highlight.json");
        PlayerHighlightConfig config = new PlayerHighlightConfig();
        config.setEntries(List.of(new TrackedPlayer("Notch", 0xFFFF0000)));

        PlayerHighlightConfigIO.save(config, file);
        PlayerHighlightConfig loaded = PlayerHighlightConfigIO.load(file);

        assertEquals(1, loaded.getEntries().size());
        assertEquals("Notch", loaded.getEntries().get(0).getName());
        assertEquals(0xFFFF0000, loaded.getEntries().get(0).getColor());
    }

    @Test
    void missingFileFallsBackToDefaultsAndWritesThem() {
        Path file = dir.resolve("nested").resolve("milos-player-highlight.json");

        PlayerHighlightConfig loaded = PlayerHighlightConfigIO.load(file);

        assertTrue(loaded.getEntries().isEmpty());
        assertTrue(Files.isRegularFile(file), "the default config should have been written to disk");
    }

    @Test
    void corruptedFileFallsBackToDefaultsAndRewritesIt() throws IOException {
        Path file = dir.resolve("milos-player-highlight.json");
        Files.writeString(file, "{ this is not json");

        PlayerHighlightConfig loaded = PlayerHighlightConfigIO.load(file);

        assertTrue(loaded.getEntries().isEmpty());
        assertTrue(Files.readString(file).contains("entries"), "the file should have been rewritten");
    }

    @Test
    void explicitNullEntriesListIsRepaired() throws IOException {
        Path file = dir.resolve("milos-player-highlight.json");
        Files.writeString(file, "{\"entries\": null}");

        PlayerHighlightConfig loaded = PlayerHighlightConfigIO.load(file);

        assertNotNull(loaded.getEntries());
        assertTrue(loaded.getEntries().isEmpty());
    }
}
