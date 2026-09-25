package se.guldbransen.milos.playerhighlight.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import se.guldbransen.milos.playerhighlight.PlayerHighlightMod;

@Mixin(Minecraft.class)
public abstract class MixinMinecraftGlow {
    @Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true)
    private void playerhighlight$forceGlow(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof Player player
                && PlayerHighlightMod.config().findColor(player.getGameProfile().name()).isPresent()) {
            cir.setReturnValue(true);
        }
    }
}
