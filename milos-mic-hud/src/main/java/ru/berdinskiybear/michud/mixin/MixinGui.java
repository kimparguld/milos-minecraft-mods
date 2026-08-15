package ru.berdinskiybear.michud.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.berdinskiybear.michud.MicHudMod;
import ru.berdinskiybear.michud.compat.SimpleVoiceChatCompat;
import ru.berdinskiybear.michud.config.MicHudConfig;
import ru.berdinskiybear.michud.icon.IconLoader;
import ru.berdinskiybear.michud.icon.MicState;

@Mixin(Gui.class)
public abstract class MixinGui {
    // Half of vanilla's 182px-wide hotbar sprite, plus a small gap so the icon
    // doesn't touch the hotbar edge.
    @Unique
    private static final int HOTBAR_OFFSET = 91 + 4;

    @Inject(method = "renderItemHotbar", at = @At("TAIL"))
    public void renderMicHud(GuiGraphics graphics, DeltaTracker tickCounter, CallbackInfo ci) {
        MicHudConfig config = MicHudMod.getManager().getConfig();
        if (!config.isEnabled()) return;

        Player player = MicHudMod.getCameraPlayer();
        if (player == null) return;

        MicState state = SimpleVoiceChatCompat.getCurrentState();
        if (state == MicState.NOT_INSTALLED) return;

        IconLoader.IconTexture icon = IconLoader.resolve(state);
        int size = config.getIconSize();

        final int sideMultiplier, sideOffsetMultiplier;
        if ((config.getAnchor() == MicHudConfig.Anchor.HOTBAR && config.getSide() == MicHudConfig.Side.LEFT)
                || (config.getAnchor() != MicHudConfig.Anchor.HOTBAR && config.getSide() == MicHudConfig.Side.RIGHT)) {
            sideMultiplier = -1;
            sideOffsetMultiplier = -1;
        } else {
            sideMultiplier = 1;
            sideOffsetMultiplier = 0;
        }

        int x = config.getOffsetX() * sideMultiplier + switch (config.getAnchor()) {
            case TOP_CENTER -> (graphics.guiWidth() - size) / 2;
            case TOP, BOTTOM -> (size - graphics.guiWidth()) * sideOffsetMultiplier;
            case HOTBAR -> graphics.guiWidth() / 2 + (HOTBAR_OFFSET * sideMultiplier) + (size * sideOffsetMultiplier);
        };

        int y = switch (config.getAnchor()) {
            case TOP, TOP_CENTER -> config.getOffsetY();
            case BOTTOM, HOTBAR -> graphics.guiHeight() - size - config.getOffsetY();
        };

        graphics.blit(RenderPipelines.GUI_TEXTURED, icon.id(), x, y, 0, 0, size, size, icon.width(), icon.height(), icon.width(), icon.height());
    }
}
