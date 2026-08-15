package ru.berdinskiybear.playerhighlight.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.berdinskiybear.playerhighlight.PlayerHighlightMod;

import java.util.OptionalInt;

@Mixin(Entity.class)
public abstract class MixinEntityTeamColor {
    @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
    private void playerhighlight$overrideOutlineColor(CallbackInfoReturnable<Integer> cir) {
        Entity self = (Entity) (Object) this;
        if (self instanceof Player player) {
            OptionalInt color = PlayerHighlightMod.config().findColor(player.getGameProfile().name());
            if (color.isPresent()) {
                cir.setReturnValue(color.getAsInt() & 0xFFFFFF);
            }
        }
    }
}
