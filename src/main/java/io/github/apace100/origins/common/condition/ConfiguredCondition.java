package io.github.apace100.origins.common.condition;

import io.github.apace100.origins.api.Condition;
import net.minecraft.resources.ResourceLocation;

public record ConfiguredCondition(ResourceLocation type, Condition<Void> condition) {
    public boolean test() {
        return condition.test(null);
    }
}
