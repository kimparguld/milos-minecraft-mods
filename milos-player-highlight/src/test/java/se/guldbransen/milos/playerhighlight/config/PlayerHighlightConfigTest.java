package se.guldbransen.milos.playerhighlight.config;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerHighlightConfigTest {
    @Test
    void findColorMatchesCaseInsensitively() {
        PlayerHighlightConfig config = new PlayerHighlightConfig();
        config.setEntries(List.of(new TrackedPlayer("Notch", 0xFFFF0000)));

        assertEquals(OptionalInt.of(0xFFFF0000), config.findColor("notch"));
        assertEquals(OptionalInt.of(0xFFFF0000), config.findColor("NOTCH"));
    }

    @Test
    void findColorReturnsEmptyForUntrackedPlayer() {
        PlayerHighlightConfig config = new PlayerHighlightConfig();
        config.setEntries(List.of(new TrackedPlayer("Notch", 0xFFFF0000)));

        assertTrue(config.findColor("Jeb_").isEmpty());
    }

    @Test
    void findColorReturnsEmptyForEmptyEntries() {
        PlayerHighlightConfig config = new PlayerHighlightConfig();

        assertTrue(config.findColor("Notch").isEmpty());
    }

    @Test
    void newConfigStartsWithEmptyMutableEntryList() {
        PlayerHighlightConfig config = new PlayerHighlightConfig();

        config.getEntries().add(new TrackedPlayer("Dinnerbone", 0xFF00FF00));

        assertEquals(1, config.getEntries().size());
    }

    @Test
    void findColorSkipsNullEntriesWithoutThrowing() {
        PlayerHighlightConfig config = new PlayerHighlightConfig();
        List<TrackedPlayer> entries = new ArrayList<>(Arrays.asList(
                null, new TrackedPlayer("Notch", 0xFFFF0000)));
        config.setEntries(entries);

        assertDoesNotThrow(() -> {
            OptionalInt result = config.findColor("Notch");
            assertEquals(OptionalInt.of(0xFFFF0000), result);
        });
    }

    @Test
    void findColorIgnoresEntryWithNullNameWithoutThrowing() {
        PlayerHighlightConfig config = new PlayerHighlightConfig();
        List<TrackedPlayer> entries = new ArrayList<>(Arrays.asList(
                new TrackedPlayer(null, 0xFF00FF00)));
        config.setEntries(entries);

        assertDoesNotThrow(() -> {
            OptionalInt result = config.findColor("Notch");
            assertTrue(result.isEmpty());
        });
    }
}
