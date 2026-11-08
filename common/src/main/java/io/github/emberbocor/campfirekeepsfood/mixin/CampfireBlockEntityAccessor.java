package io.github.emberbocor.campfirekeepsfood.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.level.block.entity.CampfireBlockEntity;

@Mixin(CampfireBlockEntity.class)
public interface CampfireBlockEntityAccessor {
    @Accessor("cookingProgress")
    int[] campfirekeepsfood$getCookingProgress();

    @Accessor("cookingTime")
    int[] campfirekeepsfood$getCookingTime();
}
