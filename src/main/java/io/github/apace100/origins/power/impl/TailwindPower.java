package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class TailwindPower extends Power {
    public TailwindPower(PowerType<?> type) {
        super(type);
    }

    @Override
    public void tick(Player player) {
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) {
            return;
        }

        AttributeModifier modifier = new AttributeModifier(MODIFIER_ID, 0.2D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        if (player.isSprinting()) {
            if (speed.getModifier(MODIFIER_ID) == null) {
                speed.addTransientModifier(modifier);
            }
        } else if (speed.getModifier(MODIFIER_ID) != null) {
            speed.removeModifier(MODIFIER_ID);
        }
    }

    private static final ResourceLocation MODIFIER_ID = Origins.id("tailwind_speed");
}
