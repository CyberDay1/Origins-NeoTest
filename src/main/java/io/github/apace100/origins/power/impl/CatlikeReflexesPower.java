package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.config.OriginsConfig;
import io.github.apace100.origins.config.OriginsConfigValues;
import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class CatlikeReflexesPower extends Power {
    public CatlikeReflexesPower(PowerType<?> type) {
        super(type);
    }

    @Override
    public void tick(Player player) {
        OriginsConfigValues.Feline config = OriginsConfig.get().feline();
        if (config.fallDamageReduction() >= 1.0D) {
            player.resetFallDistance();
        }

        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            double bonus = Math.max(0.0D, config.moveSpeedMultiplier() - 1.0D);
            AttributeModifier current = speed.getModifier(MODIFIER_ID);
            if (bonus <= 0.0D) {
                if (current != null) {
                    speed.removeModifier(MODIFIER_ID);
                }
            } else if (current == null || current.amount() != bonus) {
                if (current != null) {
                    speed.removeModifier(MODIFIER_ID);
                }
                speed.addTransientModifier(new AttributeModifier(MODIFIER_ID, bonus, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
        }
    }

    private static final ResourceLocation MODIFIER_ID = Origins.id("feline_speed");
}
