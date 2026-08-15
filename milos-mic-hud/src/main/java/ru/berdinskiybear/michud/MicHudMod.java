package ru.berdinskiybear.michud;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.uku3lig.ukulib.config.ConfigManager;
import net.uku3lig.ukulib.utils.Ukutils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import ru.berdinskiybear.michud.config.MicHudConfig;

@Slf4j
public final class MicHudMod implements ModInitializer {
    public static final String MOD_ID = "milos-mic-hud";

    @Getter
    private static final ConfigManager<MicHudConfig> manager = ConfigManager.createDefault(MicHudConfig.class, MOD_ID);

    @Nullable
    public static Player getCameraPlayer() {
        return Minecraft.getInstance().getCameraEntity() instanceof Player player ? player : null;
    }

    @Override
    public void onInitialize() {
        Ukutils.registerToggleBind(
                new KeyMapping("michud.keybind.toggle", GLFW.GLFW_KEY_UNKNOWN, KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "key"))),
                () -> manager.getConfig().isEnabled(),
                b -> manager.getConfig().setEnabled(b),
                Component.translatable("michud.keybind.toggle.msg"));
    }
}
