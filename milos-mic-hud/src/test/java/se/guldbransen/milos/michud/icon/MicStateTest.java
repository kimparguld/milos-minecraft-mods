package se.guldbransen.milos.michud.icon;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MicStateTest {
    @Test
    void disconnectedTakesPriorityOverEverything() {
        assertEquals(MicState.DISCONNECTED, MicState.fromFlags(true, true, true));
    }

    @Test
    void disabledTakesPriorityOverMuted() {
        assertEquals(MicState.DISABLED, MicState.fromFlags(true, true, false));
    }

    @Test
    void mutedWhenOnlyMutedIsTrue() {
        assertEquals(MicState.MUTED, MicState.fromFlags(true, false, false));
    }

    @Test
    void unmutedWhenNoFlagsSet() {
        assertEquals(MicState.UNMUTED, MicState.fromFlags(false, false, false));
    }
}
