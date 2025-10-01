package io.github.apace100.origins.common.power;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record ConfiguredPower(
    ResourceLocation type,
    Component name,
    Component description,
    List<ResourceLocation> actions,
    ResourceLocation condition
) {
}
