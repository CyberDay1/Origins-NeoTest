package io.github.apace100.origins.power.impl;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

public class PumpkinAllergyPower extends Power {
    public PumpkinAllergyPower(PowerType<?> type) {
        super(type);
    }

    @Override
    public void tick(Player player) {
        if (player.getItemBySlot(EquipmentSlot.HEAD).is(Items.CARVED_PUMPKIN)) {
            if (!player.hasEffect(MobEffects.BLINDNESS) || player.getEffect(MobEffects.BLINDNESS).getDuration() <= 20) {
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0, false, false, true));
            }
        }
    }
}
