package se.guldbransen.milos.fastxp.xp;

public final class XpMultiplier {
    private XpMultiplier() {
    }

    public static int scale(int amount, int multiplier) {
        if (multiplier <= 1) {
            return amount;
        }
        long scaled = (long) amount * (long) multiplier;
        return (int) Math.min(scaled, Integer.MAX_VALUE);
    }
}
