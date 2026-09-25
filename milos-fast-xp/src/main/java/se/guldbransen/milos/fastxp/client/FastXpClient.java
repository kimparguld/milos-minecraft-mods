package se.guldbransen.milos.fastxp.client;

import net.fabricmc.api.ClientModInitializer;
import se.guldbransen.milos.fastxp.client.input.AutoThrowHandler;
import se.guldbransen.milos.fastxp.client.network.FastXpClientNetwork;

public class FastXpClient implements ClientModInitializer {
    private static boolean serverDisabled;

    @Override
    public void onInitializeClient() {
        new AutoThrowHandler().register();
        FastXpClientNetwork.register();
    }

    public static boolean isServerDisabled() {
        return serverDisabled;
    }

    public static void setServerDisabled(boolean disabled) {
        serverDisabled = disabled;
    }
}
