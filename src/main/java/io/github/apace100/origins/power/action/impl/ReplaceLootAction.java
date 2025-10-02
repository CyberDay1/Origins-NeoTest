package io.github.apace100.origins.power.action.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

/**
 * Datapack action that replaces loot generated from a configured loot table with a different item.
 */
public final class ReplaceLootAction implements Action<Player> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "replace_loot");
    private static final Codec<ReplaceLootAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("loot_table").forGetter(ReplaceLootAction::lootTable),
        BuiltInRegistries.ITEM.byNameCodec().fieldOf("replacement").forGetter(ReplaceLootAction::replacement)
    ).apply(instance, ReplaceLootAction::new));

    private final ResourceLocation lootTable;
    private final Item replacement;

    private ReplaceLootAction(ResourceLocation lootTable, Item replacement) {
        this.lootTable = lootTable;
        this.replacement = replacement;
    }

    private ResourceLocation lootTable() {
        return lootTable;
    }

    private Item replacement() {
        return replacement;
    }

    @Override
    public void execute(Player player) {
        if (player == null) {
            return;
        }

        Optional<LootActionContext.Context> context = LootActionContext.current();
        if (context.isEmpty()) {
            Origins.LOGGER.warn(
                "Replace loot action '{}' executed without loot context for player {}",
                lootTable,
                player.getGameProfile().getName()
            );
            return;
        }
        if (!context.get().table().equals(lootTable)) {
            return;
        }

        List<ItemStack> loot = context.get().loot();
        for (int i = 0; i < loot.size(); i++) {
            ItemStack original = loot.get(i);
            if (original.isEmpty()) {
                continue;
            }
            loot.set(i, new ItemStack(replacement, Math.max(1, original.getCount())));
        }
    }

    public static ReplaceLootAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("loot_table")) {
            Origins.LOGGER.warn("Replace loot action '{}' is missing required 'loot_table' field", id);
            return null;
        }
        if (!json.has("replacement")) {
            Origins.LOGGER.warn("Replace loot action '{}' is missing required 'replacement' field", id);
            return null;
        }

        ResourceLocation lootTableId;
        try {
            lootTableId = ResourceLocation.parse(GsonHelper.getAsString(json, "loot_table"));
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Replace loot action '{}' has invalid loot table id '{}': {}", id, json.get("loot_table"), exception.getMessage());
            return null;
        }

        String rawItem = GsonHelper.getAsString(json, "replacement");
        ResourceLocation itemId;
        try {
            itemId = ResourceLocation.parse(rawItem);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Replace loot action '{}' has invalid item id '{}': {}", id, rawItem, exception.getMessage());
            return null;
        }

        Optional<Item> replacement = BuiltInRegistries.ITEM.getOptional(itemId);
        if (replacement.isEmpty()) {
            Origins.LOGGER.warn("Replace loot action '{}' references unknown item '{}'", id, itemId);
            return null;
        }

        return new ReplaceLootAction(lootTableId, replacement.get());
    }

    public static Codec<ReplaceLootAction> codec() {
        return CODEC;
    }

    public static void withContext(ResourceLocation lootTable, List<ItemStack> loot, Runnable runnable) {
        LootActionContext.withContext(lootTable, loot, runnable);
    }
}
