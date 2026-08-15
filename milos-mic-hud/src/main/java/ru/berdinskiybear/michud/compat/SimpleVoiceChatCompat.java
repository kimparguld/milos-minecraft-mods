package ru.berdinskiybear.michud.compat;

import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.ClientEvent;
import de.maxhenkel.voicechat.api.events.ClientVoicechatConnectionEvent;
import de.maxhenkel.voicechat.api.events.ClientVoicechatInitializationEvent;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophoneMuteEvent;
import de.maxhenkel.voicechat.api.events.VoicechatDisableEvent;
import ru.berdinskiybear.michud.icon.MicState;

public class SimpleVoiceChatCompat implements VoicechatPlugin {
    public static final String PLUGIN_ID = "milos-mic-hud";

    private static volatile boolean installed = false;
    private static volatile MicState state = MicState.DISCONNECTED;

    @Override
    public String getPluginId() {
        return PLUGIN_ID;
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        installed = true;
        registration.registerEvent(ClientVoicechatInitializationEvent.class, SimpleVoiceChatCompat::updateState);
        registration.registerEvent(ClientVoicechatConnectionEvent.class, SimpleVoiceChatCompat::updateState);
        registration.registerEvent(MicrophoneMuteEvent.class, SimpleVoiceChatCompat::updateState);
        registration.registerEvent(VoicechatDisableEvent.class, SimpleVoiceChatCompat::updateState);
    }

    private static void updateState(ClientEvent event) {
        var api = event.getVoicechat();
        state = MicState.fromFlags(api.isMuted(), api.isDisabled(), api.isDisconnected());
    }

    public static MicState getCurrentState() {
        return installed ? state : MicState.NOT_INSTALLED;
    }
}
