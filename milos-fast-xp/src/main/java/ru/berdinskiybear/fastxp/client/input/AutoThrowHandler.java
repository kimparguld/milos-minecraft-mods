package ru.berdinskiybear.fastxp.client.input;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import ru.berdinskiybear.fastxp.FastXpMod;
import ru.berdinskiybear.fastxp.client.FastXpClient;
import ru.berdinskiybear.fastxp.config.FastXpConfig;

public final class AutoThrowHandler {
    private int tickCounter;

    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onEndTick);
    }

    private void onEndTick(Minecraft client) {
        FastXpConfig config = FastXpMod.config();
        if (!config.isAutoThrowEnabled() || FastXpClient.isServerDisabled() || client.player == null) {
            tickCounter = 0;
            return;
        }

        // A screen or overlay is up (inventory, chest, pause menu, this mod's config screen, the
        // loading overlay), or the cursor has been released for GUI interaction. Vanilla only
        // processes item-use input when the world has the mouse, so neither may we: otherwise
        // holding right-click to drag-distribute a stack of snowballs in a chest throws them.
        if (client.screen != null || client.getOverlay() != null || !client.mouseHandler.isMouseGrabbed()) {
            tickCounter = 0;
            return;
        }

        boolean rightMouseHeld = GLFW.glfwGetMouseButton(client.getWindow().handle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        InteractionHand hand = rightMouseHeld ? resolveWhitelistedHand(client, config) : null;
        if (hand == null) {
            tickCounter = 0;
            return;
        }

        tickCounter++;
        if (tickCounter >= config.getAutoThrowDelayTicks()) {
            tickCounter = 0;
            client.gameMode.useItem(client.player, hand);
        }
    }

    private InteractionHand resolveWhitelistedHand(Minecraft client, FastXpConfig config) {
        if (isWhitelisted(client.player.getMainHandItem(), config)) {
            return InteractionHand.MAIN_HAND;
        }
        if (isWhitelisted(client.player.getOffhandItem(), config)) {
            return InteractionHand.OFF_HAND;
        }
        return null;
    }

    private boolean isWhitelisted(ItemStack stack, FastXpConfig config) {
        if (stack.isEmpty()) {
            return false;
        }
        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        return config.isProjectileEnabled(itemId);
    }
}
