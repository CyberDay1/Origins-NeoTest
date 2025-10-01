package io.github.apace100.origins.common.origin;

import net.minecraft.network.chat.Component;

public enum OriginImpact {
    NONE(0, "origin.impact.none"),
    LOW(1, "origin.impact.low"),
    MEDIUM(2, "origin.impact.medium"),
    HIGH(3, "origin.impact.high"),
    VERY_HIGH(4, "origin.impact.very_high");

    private final int id;
    private final String translationKey;

    OriginImpact(int id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    public int id() {
        return id;
    }

    public String translationKey() {
        return translationKey;
    }

    public Component displayName() {
        return Component.translatable(translationKey);
    }

    public String textureKey() {
        return name();
    }

    public static OriginImpact fromId(int id) {
        for (OriginImpact impact : values()) {
            if (impact.id == id) {
                return impact;
            }
        }
        return id < NONE.id ? NONE : VERY_HIGH;
    }
}
