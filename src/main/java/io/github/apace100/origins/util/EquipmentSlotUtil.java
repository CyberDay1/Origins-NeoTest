package io.github.apace100.origins.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.Locale;

/**
 * Utility helpers for working with equipment slot identifiers in datapack definitions.
 */
public final class EquipmentSlotUtil {
    private static final Codec<EquipmentSlot> CODEC = Codec.STRING.comapFlatMap(
        EquipmentSlotUtil::decodeSlot,
        slot -> slot.getName().toLowerCase(Locale.ROOT)
    );

    private EquipmentSlotUtil() {
    }

    /**
     * Returns a codec that encodes/decodes equipment slots using their datapack identifiers.
     */
    public static Codec<EquipmentSlot> codec() {
        return CODEC;
    }

    /**
     * Parses a datapack string identifier into an equipment slot instance.
     *
     * @param raw the slot identifier provided by a datapack entry
     * @return the corresponding equipment slot, or {@code null} if unknown
     */
    public static EquipmentSlot parse(String raw) {
        if (raw == null || raw.isEmpty()) {
            return null;
        }
        return switch (raw.toLowerCase(Locale.ROOT)) {
            case "mainhand", "main_hand", "hand" -> EquipmentSlot.MAINHAND;
            case "offhand", "off_hand" -> EquipmentSlot.OFFHAND;
            case "head", "helmet" -> EquipmentSlot.HEAD;
            case "chest", "body" -> EquipmentSlot.CHEST;
            case "legs", "pants" -> EquipmentSlot.LEGS;
            case "feet", "boots" -> EquipmentSlot.FEET;
            default -> null;
        };
    }

    private static DataResult<EquipmentSlot> decodeSlot(String raw) {
        EquipmentSlot slot = parse(raw);
        if (slot == null) {
            return DataResult.error(() -> "Unknown equipment slot '" + raw + "'");
        }
        return DataResult.success(slot);
    }
}
