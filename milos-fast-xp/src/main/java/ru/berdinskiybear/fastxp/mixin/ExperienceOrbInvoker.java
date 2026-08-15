package ru.berdinskiybear.fastxp.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ExperienceOrb.class)
public interface ExperienceOrbInvoker {
    @Invoker("repairPlayerItems")
    int fastXp$invokeRepairPlayerItems(ServerPlayer serverPlayer, int amount);
}
