package se.guldbransen.milos.playerhighlight;

import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ModInitializer;
import se.guldbransen.milos.playerhighlight.config.PlayerHighlightConfig;
import se.guldbransen.milos.playerhighlight.config.PlayerHighlightConfigIO;

@Slf4j
public class PlayerHighlightMod implements ModInitializer {
    public static final String MOD_ID = "milos-player-highlight";

    private static PlayerHighlightConfig config = PlayerHighlightConfigIO.load();

    @Override
    public void onInitialize() {
        config();
        log.info("Milo's Player Highlight initialized");
    }

    public static PlayerHighlightConfig config() {
        return config;
    }

    public static void reloadConfig() {
        config = PlayerHighlightConfigIO.load();
    }
}
