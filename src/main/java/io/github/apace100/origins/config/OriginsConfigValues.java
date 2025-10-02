package io.github.apace100.origins.config;

public record OriginsConfigValues(
    Phantom phantom,
    Avian avian,
    Elemental enderian,
    Elemental blazeborn,
    Merling merling,
    Feline feline,
    Elytrian elytrian,
    Shulk shulk,
    boolean debugAudit
) {
    public record Phantom(int hungerDrainIntervalTicks, int hungerDrainPerInterval, boolean allowWallPhasing) {}

    public record Avian(int sleepMaxY, boolean slowFallingEnabled) {}

    public record Elemental(double waterDamagePerSecond) {}

    public record Merling(double swimSpeedMultiplier, boolean underwaterVisionEnabled) {}

    public record Feline(double fallDamageReduction, double moveSpeedMultiplier) {}

    public record Elytrian(boolean cancelFallDamage, boolean confinedSpaceChecks) {}

    public record Shulk(boolean chestArmorAllowed) {}
}
