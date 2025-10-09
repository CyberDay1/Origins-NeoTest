package io.github.apace100.origins.power.action.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

/**
 * Datapack action that clears loot generated from a configured loot table.
 */
public final class PreventLootAction implements Action<Player> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("prevent_loot");
    private static final Codec<PreventLootAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("loot_table").forGetter(PreventLootAction::lootTable)
    ).apply(instance, PreventLootAction::new));

    private final ResourceLocation lootTable;

    private PreventLootAction(ResourceLocation lootTable) {
        this.lootTable = lootTable;
    }

    private ResourceLocation lootTable() {
        return lootTable;
    }

    @Override
    public void execute(Player player) {
        if (player == null) {
            return;
        }

        Optional<LootActionContext.Context> context = LootActionContext.current();
        if (context.isEmpty()) {
            Origins.LOGGER.warn(
                "Prevent loot action '{}' executed without loot context for player {}",
                lootTable,
                player.getGameProfile().getName()
            );
            return;
        }

        LootActionContext.Context current = context.get();
        if (!current.table().equals(lootTable)) {
            return;
        }

        List<ItemStack> loot = current.loot();
        loot.clear();
    }

    public static PreventLootAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("loot_table")) {
            Origins.LOGGER.warn("Prevent loot action '{}' is missing required 'loot_table' field", id);
            return null;
        }

        try {
            return new PreventLootAction(ResourceLocation.parse(GsonHelper.getAsString(json, "loot_table")));
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Prevent loot action '{}' has invalid loot table id '{}': {}", id, json.get("loot_table"), exception.getMessage());
            return null;
        }
    }

    public static Codec<PreventLootAction> codec() {
        return CODEC;
    }

    public static void withContext(ResourceLocation lootTable, List<ItemStack> loot, Runnable runnable) {
        LootActionContext.withContext(lootTable, loot, runnable);
    }
}
