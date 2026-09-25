package se.guldbransen.milos.michud.icon;

public enum MicState {
    UNMUTED,
    MUTED,
    DISABLED,
    DISCONNECTED,
    NOT_INSTALLED;

    public static MicState fromFlags(boolean muted, boolean disabled, boolean disconnected) {
        if (disconnected) return DISCONNECTED;
        if (disabled) return DISABLED;
        if (muted) return MUTED;
        return UNMUTED;
    }
}
