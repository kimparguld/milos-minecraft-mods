package se.guldbransen.milos.fastxp.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import se.guldbransen.milos.fastxp.FastXpMod;
import se.guldbransen.milos.fastxp.config.FastXpConfig;
import se.guldbransen.milos.fastxp.xp.OrbPickupBatcher;

import java.util.List;

@Mixin(ExperienceOrb.class)
public class ExperienceOrbPickupMixin {

    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    private void fastXp$batchPickup(Player player, CallbackInfo ci) {
        FastXpConfig config = FastXpMod.config();
        if (!config.isXpPickupEnabled() || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        ExperienceOrb self = (ExperienceOrb) (Object) this;
        ServerLevel serverLevel = (ServerLevel) self.level();

        List<ExperienceOrb> nearbyOrbs = serverLevel.getEntities(
                EntityTypeTest.forClass(ExperienceOrb.class),
                player.getBoundingBox().inflate(1.0),
                orb -> !orb.isRemoved()
        );

        for (ExperienceOrb orb : OrbPickupBatcher.selectForPickup(nearbyOrbs, config.getXpPickupOrbsPerTick())) {
            fastXp$collect(orb, serverPlayer);
        }

        ci.cancel();
    }

    private void fastXp$collect(ExperienceOrb orb, ServerPlayer serverPlayer) {
        ExperienceOrbAccessor accessor = (ExperienceOrbAccessor) orb;
        ExperienceOrbInvoker invoker = (ExperienceOrbInvoker) orb;

        serverPlayer.take(orb, 1);
        int remaining = invoker.fastXp$invokeRepairPlayerItems(serverPlayer, orb.getValue());
        if (remaining > 0) {
            serverPlayer.giveExperiencePoints(remaining);
        }

        accessor.fastXp$setCount(accessor.fastXp$getCount() - 1);
        if (accessor.fastXp$getCount() <= 0) {
            orb.discard();
        }
    }
}
