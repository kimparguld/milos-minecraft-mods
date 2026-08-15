package ru.berdinskiybear.michud.compat;

import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.ClientEvent;
import de.maxhenkel.voicechat.api.events.ClientVoicechatConnectionEvent;
import de.maxhenkel.voicechat.api.events.ClientVoicechatInitializationEvent;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophoneMuteEvent;
import de.maxhenkel.voicechat.api.events.VoicechatDisableEvent;
import ru.berdinskiybear.michud.icon.MicState;

/**
 * The actual {@link VoicechatPlugin} implementation, registered via the
 * {@code voicechat} entrypoint in {@code fabric.mod.json}. This class is
 * only ever loaded by Fabric Loader when Simple Voice Chat itself is
 * present and queries that entrypoint, so it is safe for it to reference
 * {@code de.maxhenkel.voicechat.api} types directly.
 *
 * <p>Deliberately kept separate from {@link SimpleVoiceChatCompat}, which
 * must have zero {@code de.maxhenkel.*} imports: touching any static
 * member of a class forces its declared interfaces to resolve (JVMS
 * §5.3.5), so if this class and the compat holder were merged, the very
 * first render-mixin call to the holder would throw
 * {@code NoClassDefFoundError} when Simple Voice Chat is absent.
 */
public class MicHudVoicechatPlugin implements VoicechatPlugin {
    public static final String PLUGIN_ID = "milos-mic-hud";

    @Override
    public String getPluginId() {
        return PLUGIN_ID;
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        SimpleVoiceChatCompat.markInstalled();
        registration.registerEvent(ClientVoicechatInitializationEvent.class, MicHudVoicechatPlugin::updateState);
        registration.registerEvent(ClientVoicechatConnectionEvent.class, MicHudVoicechatPlugin::updateState);
        registration.registerEvent(MicrophoneMuteEvent.class, MicHudVoicechatPlugin::updateState);
        registration.registerEvent(VoicechatDisableEvent.class, MicHudVoicechatPlugin::updateState);
    }

    private static void updateState(ClientEvent event) {
        var api = event.getVoicechat();
        SimpleVoiceChatCompat.setState(MicState.fromFlags(api.isMuted(), api.isDisabled(), api.isDisconnected()));
    }
}
