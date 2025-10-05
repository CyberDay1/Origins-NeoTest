package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.config.OriginsConfig;
import io.github.apace100.origins.config.OriginsConfigValues;
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
    @SuppressWarnings("deprecation")
    public void tick(Player player) {
        AttributeInstance attribute = player.getAttribute(Attributes.WATER_MOVEMENT_EFFICIENCY);
        if (attribute == null) {
            return;
        }

        OriginsConfigValues.Merling config = OriginsConfig.get().merling();
        double bonus = Math.max(0.0D, config.swimSpeedMultiplier() - 1.0D);

        AttributeModifier existing = attribute.getModifier(MODIFIER_ID);
        if (bonus <= 0.0D) {
            if (existing != null) {
                attribute.removeModifier(MODIFIER_ID);
            }
            return;
        }

        boolean underwater = player.isEyeInFluid(FluidTags.WATER);
        AttributeModifier modifier = new AttributeModifier(MODIFIER_ID, bonus, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        if (underwater) {
            if (existing == null || existing.amount() != bonus) {
                if (existing != null) {
                    attribute.removeModifier(MODIFIER_ID);
                }
                attribute.addTransientModifier(modifier);
            }
        } else if (existing != null) {
            attribute.removeModifier(MODIFIER_ID);
        }
    }

    private static final ResourceLocation MODIFIER_ID = Origins.id("merling_swim_speed");
}
