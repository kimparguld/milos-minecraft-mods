package se.guldbransen.milos.fastxp.mixin;

import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ExperienceOrb.class)
public interface ExperienceOrbAccessor {
    @Accessor("count")
    int fastXp$getCount();

    @Accessor("count")
    void fastXp$setCount(int count);
}
