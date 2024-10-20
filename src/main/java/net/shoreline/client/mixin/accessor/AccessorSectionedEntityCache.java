package net.shoreline.client.mixin.accessor;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import net.minecraft.world.entity.EntityLike;
import net.minecraft.world.entity.EntityTrackingSection;
import net.minecraft.world.entity.SectionedEntityCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SectionedEntityCache.class)
public interface AccessorSectionedEntityCache {
    @Accessor("trackedPositions")
    LongSortedSet getTrackedPositions();

    @Accessor("trackingSections")
    <T extends EntityLike> Long2ObjectMap<EntityTrackingSection<T>> getTrackingSections();
}
