package se.guldbransen.milos.fastxp.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import se.guldbransen.milos.fastxp.client.FastXpClient;
import se.guldbransen.milos.fastxp.network.FastXpNetwork;

public final class FastXpClientNetwork {
    private FastXpClientNetwork() {
    }

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(FastXpNetwork.OptOutPayload.TYPE, (payload, context) ->
                context.client().execute(() -> FastXpClient.setServerDisabled(true)));

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            FastXpClient.setServerDisabled(false);
            if (ClientPlayNetworking.canSend(FastXpNetwork.JoinPayload.TYPE)) {
                ClientPlayNetworking.send(new FastXpNetwork.JoinPayload());
            }
        });
    }
}
