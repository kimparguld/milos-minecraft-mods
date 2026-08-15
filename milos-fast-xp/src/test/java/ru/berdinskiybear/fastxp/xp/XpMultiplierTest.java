package ru.berdinskiybear.fastxp.xp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XpMultiplierTest {
    @Test
    void multiplierOfOneReturnsAmountUnchanged() {
        assertEquals(7, XpMultiplier.scale(7, 1));
    }

    @Test
    void multiplierScalesAmount() {
        assertEquals(50, XpMultiplier.scale(10, 5));
    }

    @Test
    void zeroAmountStaysZero() {
        assertEquals(0, XpMultiplier.scale(0, 10));
    }

    @Test
    void overflowClampsToIntegerMax() {
        assertEquals(Integer.MAX_VALUE, XpMultiplier.scale(Integer.MAX_VALUE, 10));
    }

    @Test
    void multiplierBelowOneIsTreatedAsPassthrough() {
        assertEquals(7, XpMultiplier.scale(7, 0));
    }
}
