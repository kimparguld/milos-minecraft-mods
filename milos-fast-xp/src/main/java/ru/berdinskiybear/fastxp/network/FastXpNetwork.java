package ru.berdinskiybear.fastxp.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import ru.berdinskiybear.fastxp.FastXpMod;

public final class FastXpNetwork {
    private FastXpNetwork() {
    }

    public record JoinPayload() implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<JoinPayload> TYPE =
                new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(FastXpMod.MOD_ID, "join"));
        public static final StreamCodec<RegistryFriendlyByteBuf, JoinPayload> CODEC = StreamCodec.unit(new JoinPayload());

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record OptOutPayload() implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<OptOutPayload> TYPE =
                new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(FastXpMod.MOD_ID, "opt_out"));
        public static final StreamCodec<RegistryFriendlyByteBuf, OptOutPayload> CODEC = StreamCodec.unit(new OptOutPayload());

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void registerPayloadTypes() {
        PayloadTypeRegistry.playC2S().register(JoinPayload.TYPE, JoinPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(OptOutPayload.TYPE, OptOutPayload.CODEC);
    }

    /**
     * No default reaction: this just accepts the client's "join" packet instead of the server
     * rejecting an unregistered channel. Server-side administration tooling can call
     * ServerPlayNetworking.send(player, new OptOutPayload()) in response to this event to
     * disable Auto-Throw for that player; this mod ships no such policy itself.
     */
    public static void registerServer() {
        ServerPlayNetworking.registerGlobalReceiver(JoinPayload.TYPE, (payload, context) -> {
        });
    }
}
