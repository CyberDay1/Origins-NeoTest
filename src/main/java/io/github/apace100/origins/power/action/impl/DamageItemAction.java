package io.github.apace100.origins.power.action.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import io.github.apace100.origins.util.EquipmentSlotUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Datapack action that damages an equipped item by a configured amount.
 */
public final class DamageItemAction implements Action<Player> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("damage_item");
    private static final Codec<DamageItemAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        EquipmentSlotUtil.codec().fieldOf("slot").forGetter(DamageItemAction::slot),
        Codec.INT.fieldOf("amount").forGetter(DamageItemAction::amount)
    ).apply(instance, DamageItemAction::new));

    private final EquipmentSlot slot;
    private final int amount;

    private DamageItemAction(EquipmentSlot slot, int amount) {
        this.slot = slot;
        this.amount = amount;
    }

    private EquipmentSlot slot() {
        return slot;
    }

    private int amount() {
        return amount;
    }

    @Override
    public void execute(Player player) {
        if (player == null || amount <= 0) {
            return;
        }

        ItemStack stack = player.getItemBySlot(slot);
        if (stack.isEmpty() || !stack.isDamageableItem()) {
            return;
        }

        stack.hurtAndBreak(amount, player, slot);
    }

    public static DamageItemAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("slot")) {
            Origins.LOGGER.warn("Damage item action '{}' is missing required 'slot' field", id);
            return null;
        }
        if (!json.has("amount")) {
            Origins.LOGGER.warn("Damage item action '{}' is missing required 'amount' field", id);
            return null;
        }

        String rawSlot = GsonHelper.getAsString(json, "slot");
        EquipmentSlot slot = EquipmentSlotUtil.parse(rawSlot);
        if (slot == null) {
            Origins.LOGGER.warn("Damage item action '{}' references unknown slot '{}'", id, rawSlot);
            return null;
        }

        int amount = GsonHelper.getAsInt(json, "amount", 0);
        if (amount <= 0) {
            Origins.LOGGER.warn("Damage item action '{}' has non-positive amount {}", id, amount);
            return null;
        }

        return new DamageItemAction(slot, amount);
    }

    public static Codec<DamageItemAction> codec() {
        return CODEC;
    }
}
