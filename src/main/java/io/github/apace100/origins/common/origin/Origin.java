package io.github.apace100.origins.common.origin;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record Origin(
    ResourceLocation id,
    Component name,
    Component description,
    List<ResourceLocation> powers,
    ItemStack icon,
    OriginImpact impact
) {
}
