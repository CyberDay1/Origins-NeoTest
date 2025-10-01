package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class SwimSpeedPower extends Power {
    public SwimSpeedPower(PowerType<?> type) {
        super(type);
    }

    @Override
    public void tick(Player player) {
        AttributeInstance attribute = player.getAttribute(Attributes.WATER_MOVEMENT_EFFICIENCY);
        if (attribute == null) {
            return;
        }

        boolean underwater = player.isEyeInFluid(FluidTags.WATER);
        AttributeModifier modifier = new AttributeModifier(MODIFIER_ID, 0.5D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        if (underwater) {
            if (attribute.getModifier(MODIFIER_ID) == null) {
                attribute.addTransientModifier(modifier);
            }
        } else if (attribute.getModifier(MODIFIER_ID) != null) {
            attribute.removeModifier(MODIFIER_ID);
        }
    }

    private static final ResourceLocation MODIFIER_ID = Origins.id("merling_swim_speed");
}
