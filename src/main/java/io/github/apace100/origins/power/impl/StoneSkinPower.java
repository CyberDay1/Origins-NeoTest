package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class StoneSkinPower extends Power {
    public StoneSkinPower(PowerType<?> type) {
        super(type);
    }

    @Override
    public void tick(Player player) {
        AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
        if (armor != null && armor.getModifier(ARMOR_MODIFIER) == null) {
            armor.addTransientModifier(new AttributeModifier(ARMOR_MODIFIER, 8.0D, AttributeModifier.Operation.ADD_VALUE));
        }

        AttributeInstance toughness = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if (toughness != null && toughness.getModifier(TOUGHNESS_MODIFIER) == null) {
            toughness.addTransientModifier(new AttributeModifier(TOUGHNESS_MODIFIER, 4.0D, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    private static final ResourceLocation ARMOR_MODIFIER = Origins.id("shulk_stone_skin_armor");
    private static final ResourceLocation TOUGHNESS_MODIFIER = Origins.id("shulk_stone_skin_toughness");
}
