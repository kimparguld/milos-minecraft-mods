package se.guldbransen.milos.fastxp.mixin;

import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import se.guldbransen.milos.fastxp.FastXpMod;
import se.guldbransen.milos.fastxp.config.FastXpConfig;
import se.guldbransen.milos.fastxp.xp.XpMultiplier;

@Mixin(ExperienceOrb.class)
public class ExperienceOrbValueMixin {
    @ModifyVariable(
            method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;I)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private static int fastXp$scaleAwardedValue(int amount) {
        FastXpConfig config = FastXpMod.config();
        if (config.isXpCreationEnabled()) {
            return XpMultiplier.scale(amount, config.getXpCreationMultiplier());
        }
        return amount;
    }
}
