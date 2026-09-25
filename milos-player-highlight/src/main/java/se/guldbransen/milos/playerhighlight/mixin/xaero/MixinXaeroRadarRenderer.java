package se.guldbransen.milos.playerhighlight.mixin.xaero;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import se.guldbransen.milos.playerhighlight.PlayerHighlightMod;
import xaero.hud.minimap.element.render.MinimapElementReader;
import xaero.hud.minimap.element.render.MinimapElementRenderProvider;
import xaero.hud.minimap.element.render.MinimapElementRenderer;
import xaero.hud.minimap.radar.render.element.RadarRenderContext;
import xaero.hud.minimap.radar.render.element.RadarRenderer;

import java.util.OptionalInt;

@Mixin(value = RadarRenderer.class, remap = false)
public abstract class MixinXaeroRadarRenderer extends MinimapElementRenderer<Entity, RadarRenderContext> {
    @Unique
    private OptionalInt playerhighlight$pendingColor = OptionalInt.empty();

    public MixinXaeroRadarRenderer(MinimapElementReader<Entity, RadarRenderContext> elementReader,
                                    MinimapElementRenderProvider<Entity, RadarRenderContext> provider,
                                    RadarRenderContext context) {
        super(elementReader, provider, context);
    }

    @Inject(method = "setupRenderForEntity", at = @At("RETURN"))
    private void playerhighlight$resolveHighlight(Entity entity, CallbackInfo ci) {
        playerhighlight$pendingColor = entity instanceof Player player
                ? PlayerHighlightMod.config().findColor(player.getGameProfile().name())
                : OptionalInt.empty();

        if (playerhighlight$pendingColor.isPresent()) {
            context.icon = false;
        }
    }

    @ModifyVariable(method = "renderDot", at = @At(value = "STORE", ordinal = 0))
    private int playerhighlight$overrideDotColor(int color) {
        return playerhighlight$pendingColor.isPresent() ? 0xFF000000 | playerhighlight$pendingColor.getAsInt() : color;
    }
}
