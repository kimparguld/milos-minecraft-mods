package se.guldbransen.milos.fastxp;

import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ModInitializer;
import se.guldbransen.milos.fastxp.config.FastXpConfig;
import se.guldbransen.milos.fastxp.config.FastXpConfigIO;
import se.guldbransen.milos.fastxp.network.FastXpNetwork;

@Slf4j
public class FastXpMod implements ModInitializer {
    public static final String MOD_ID = "milos-fast-xp";

    /**
     * The live config instance the mixins and the client input handler read from. Written by the
     * client config screen (through {@link #reloadConfig()}) and read from the server thread, hence
     * volatile.
     */
    private static volatile FastXpConfig config = FastXpConfigIO.load();

    @Override
    public void onInitialize() {
        FastXpNetwork.registerPayloadTypes();
        FastXpNetwork.registerServer();
        log.info("Milo's Fast XP initialized");
    }

    public static FastXpConfig config() {
        return config;
    }

    /**
     * Re-reads the config from disk into the live instance, so edits made in the client config
     * screen take effect without a restart.
     */
    public static void reloadConfig() {
        config = FastXpConfigIO.load();
    }
}
