package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.Origins;
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
        player.resetFallDistance();

        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null && speed.getModifier(MODIFIER_ID) == null) {
            speed.addTransientModifier(new AttributeModifier(MODIFIER_ID, 0.1D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }

    private static final ResourceLocation MODIFIER_ID = Origins.id("feline_speed");
}
