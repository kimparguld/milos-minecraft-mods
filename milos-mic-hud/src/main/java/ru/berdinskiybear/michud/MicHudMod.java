package ru.berdinskiybear.michud;

import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ModInitializer;

@Slf4j
public final class MicHudMod implements ModInitializer {
    public static final String MOD_ID = "milos-mic-hud";

    @Override
    public void onInitialize() {
        log.info("Milo's Mic HUD initialized");
    }
}
