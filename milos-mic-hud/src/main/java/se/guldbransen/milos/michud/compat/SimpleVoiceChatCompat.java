package se.guldbransen.milos.michud.compat;

import se.guldbransen.milos.michud.icon.MicState;

/**
 * Holder for the current mic state, read every HUD frame by
 * {@code MixinGui}. This class must have zero {@code de.maxhenkel.*}
 * imports: touching any static member of a class forces the JVM to
 * resolve its declared interfaces (JVMS §5.3.5), and this holder is
 * touched unconditionally on every frame regardless of whether Simple
 * Voice Chat is installed. The actual {@code VoicechatPlugin}
 * implementation lives in {@link MicHudVoicechatPlugin}, which is only
 * loaded by Fabric Loader when Simple Voice Chat is present and queries
 * the {@code voicechat} entrypoint.
 */
public final class SimpleVoiceChatCompat {
    private static volatile boolean installed = false;
    private static volatile MicState state = MicState.DISCONNECTED;

    private SimpleVoiceChatCompat() {
    }

    public static void markInstalled() {
        installed = true;
    }

    public static void setState(MicState newState) {
        state = newState;
    }

    public static MicState getCurrentState() {
        return installed ? state : MicState.NOT_INSTALLED;
    }
}
