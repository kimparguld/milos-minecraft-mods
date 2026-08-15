package ru.berdinskiybear.fastxp.client;

import net.fabricmc.api.ClientModInitializer;
import ru.berdinskiybear.fastxp.client.input.AutoThrowHandler;
import ru.berdinskiybear.fastxp.client.network.FastXpClientNetwork;

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
