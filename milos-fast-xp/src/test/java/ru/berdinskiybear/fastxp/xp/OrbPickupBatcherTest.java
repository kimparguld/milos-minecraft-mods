package ru.berdinskiybear.fastxp.xp;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrbPickupBatcherTest {
    @Test
    void selectsUpToOrbsPerTick() {
        List<String> orbs = List.of("a", "b", "c", "d");
        assertEquals(List.of("a", "b"), OrbPickupBatcher.selectForPickup(orbs, 2));
    }

    @Test
    void orbsPerTickHigherThanListSizeReturnsWholeList() {
        List<String> orbs = List.of("a", "b");
        assertEquals(List.of("a", "b"), OrbPickupBatcher.selectForPickup(orbs, 16));
    }

    @Test
    void emptyListReturnsEmpty() {
        assertEquals(List.of(), OrbPickupBatcher.selectForPickup(List.of(), 4));
    }

    @Test
    void zeroOrbsPerTickReturnsEmpty() {
        List<String> orbs = List.of("a", "b");
        assertEquals(List.of(), OrbPickupBatcher.selectForPickup(orbs, 0));
    }
}
