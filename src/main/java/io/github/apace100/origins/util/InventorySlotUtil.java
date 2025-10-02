package io.github.apace100.origins.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;

/**
 * Utility helpers for working with player inventory slot identifiers in datapacks.
 */
public final class InventorySlotUtil {
    private static final Map<String, Integer> ARMOR_SLOT_LOOKUP = Map.of(
        "feet", 36,
        "boots", 36,
        "legs", 37,
        "leggings", 37,
        "chest", 38,
        "chestplate", 38,
        "head", 39,
        "helmet", 39
    );

    private InventorySlotUtil() {
    }

    public static final Codec<Integer> CODEC = Codec.STRING.comapFlatMap(
        InventorySlotUtil::decode,
        InventorySlotUtil::encodeToString
    );

    public static OptionalInt parse(String raw, Consumer<String> onError) {
        DataResult<Integer> result = decode(raw);
        Optional<Integer> value = result.resultOrPartial(onError);
        return value.map(OptionalInt::of).orElseGet(OptionalInt::empty);
    }

    public static DataResult<Integer> decode(String raw) {
        if (raw == null || raw.isBlank()) {
            return DataResult.error(() -> "Inventory slot identifier cannot be empty");
        }

        String lower = raw.toLowerCase(Locale.ROOT);
        String[] parts = lower.split("\\.", 2);
        String section = parts[0];
        String suffix = parts.length > 1 ? parts[1] : "";

        return switch (section) {
            case "hotbar" -> parseIndexedSlot("hotbar", suffix, 0, 8, index -> index);
            case "inventory" -> parseIndexedSlot("inventory", suffix, 0, 35, index -> index);
            case "main" -> parseIndexedSlot("main", suffix, 0, 26, index -> index + 9);
            case "armor" -> parseArmorSlot(suffix);
            case "offhand" -> parseOffhandSlot(suffix);
            default -> DataResult.error(() -> "Unknown inventory section '" + section + "'");
        };
    }

    private static DataResult<Integer> parseArmorSlot(String suffix) {
        if (suffix.isBlank()) {
            return DataResult.error(() -> "Armor slot requires an index or slot name");
        }

        Integer mapped = ARMOR_SLOT_LOOKUP.get(suffix);
        if (mapped != null) {
            return DataResult.success(mapped);
        }

        return parseIndexedSlot("armor", suffix, 0, 3, index -> 36 + index);
    }

    private static DataResult<Integer> parseOffhandSlot(String suffix) {
        if (suffix.isBlank() || "0".equals(suffix)) {
            return DataResult.success(40);
        }
        return DataResult.error(() -> "Offhand slot does not support index '" + suffix + "'");
    }

    private static DataResult<Integer> parseIndexedSlot(String section, String suffix, int min, int maxInclusive, IntUnaryOperator mapper) {
        if (suffix.isBlank()) {
            return DataResult.error(() -> "Inventory section '" + section + "' requires an index");
        }

        int parsed;
        try {
            parsed = Integer.parseInt(suffix);
        } catch (NumberFormatException exception) {
            return DataResult.error(() -> "Inventory section '" + section + "' has non-numeric index '" + suffix + "'");
        }

        if (parsed < min || parsed > maxInclusive) {
            return DataResult.error(() -> "Inventory section '" + section + "' index " + parsed
                + " is out of bounds (" + min + "-" + maxInclusive + ")");
        }

        return DataResult.success(mapper.applyAsInt(parsed));
    }

    private static DataResult<String> encode(int slot) {
        if (slot >= 0 && slot <= 8) {
            return DataResult.success("hotbar." + slot);
        }
        if (slot >= 9 && slot <= 35) {
            return DataResult.success("inventory." + slot);
        }
        return switch (slot) {
            case 36 -> DataResult.success("armor.feet");
            case 37 -> DataResult.success("armor.legs");
            case 38 -> DataResult.success("armor.chest");
            case 39 -> DataResult.success("armor.head");
            case 40 -> DataResult.success("offhand");
            default -> DataResult.error(() -> "Unsupported inventory slot index " + slot);
        };
    }

    private static String encodeToString(int slot) {
        return encode(slot).getOrThrow(message -> new IllegalStateException(message));
    }
}
