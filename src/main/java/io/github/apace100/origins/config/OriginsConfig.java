package io.github.apace100.origins.config;

import io.github.apace100.origins.Origins;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class OriginsConfig {
    public static final ModConfigSpec COMMON_SPEC;

    private static final ModConfigSpec.BooleanValue DEBUG_AUDIT;
    private static final PhantomCategory PHANTOM;
    private static final AvianCategory AVIAN;
    private static final ElementalCategory ENDERIAN;
    private static final ElementalCategory BLAZEBORN;
    private static final MerlingCategory MERLING;
    private static final FelineCategory FELINE;
    private static final ElytrianCategory ELYTRIAN;
    private static final ShulkCategory SHULK;

    private static volatile OriginsConfigValues cachedValues;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        DEBUG_AUDIT = builder
            .comment("Enable detailed parity auditing logs during datapack reloads.")
            .define("debugAudit", false);

        builder.comment("Configuration for built-in Origins powers").push("powers");
        PHANTOM = new PhantomCategory(builder);
        AVIAN = new AvianCategory(builder);
        ENDERIAN = new ElementalCategory(builder, "enderian", 2.0D, "Damage applied every second when touching water.");
        BLAZEBORN = new ElementalCategory(builder, "blazeborn", 2.0D, "Damage applied every second when touching water.");
        MERLING = new MerlingCategory(builder);
        FELINE = new FelineCategory(builder);
        ELYTRIAN = new ElytrianCategory(builder);
        SHULK = new ShulkCategory(builder);
        builder.pop();

        COMMON_SPEC = builder.build();
        cachedValues = readValues();
    }

    private OriginsConfig() {
    }

    public static void register(ModLoadingContext context) {
        context.getActiveContainer().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC, Origins.MOD_ID + "-common.toml");
    }

    public static void registerListeners(IEventBus modBus) {
        modBus.addListener(OriginsConfig::onLoading);
        modBus.addListener(OriginsConfig::onReloading);
    }

    private static void onLoading(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == COMMON_SPEC) {
            cachedValues = readValues();
        }
    }

    private static void onReloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == COMMON_SPEC) {
            cachedValues = readValues();
        }
    }

    public static OriginsConfigValues get() {
        return cachedValues;
    }

    private static OriginsConfigValues readValues() {
        boolean debugAuditOverride = Boolean.getBoolean("origins.debugAudit");
        boolean debugAudit = DEBUG_AUDIT.get();
        if (debugAuditOverride) {
            debugAudit = true;
        }
        return new OriginsConfigValues(
            new OriginsConfigValues.Phantom(
                PHANTOM.hungerDrainIntervalTicks.get(),
                PHANTOM.hungerDrainPerInterval.get(),
                PHANTOM.allowWallPhasing.get()
            ),
            new OriginsConfigValues.Avian(
                AVIAN.sleepMaxY.get(),
                AVIAN.slowFallingEnabled.get()
            ),
            new OriginsConfigValues.Elemental(ENDERIAN.waterDamagePerSecond.get()),
            new OriginsConfigValues.Elemental(BLAZEBORN.waterDamagePerSecond.get()),
            new OriginsConfigValues.Merling(
                MERLING.swimSpeedMultiplier.get(),
                MERLING.underwaterVisionEnabled.get()
            ),
            new OriginsConfigValues.Feline(
                FELINE.fallDamageReduction.get(),
                FELINE.moveSpeedMultiplier.get()
            ),
            new OriginsConfigValues.Elytrian(
                ELYTRIAN.cancelFallDamage.get(),
                ELYTRIAN.confinedSpaceChecks.get()
            ),
            new OriginsConfigValues.Shulk(SHULK.chestArmorAllowed.get()),
            debugAudit
        );
    }

    public static boolean debugAuditEnabled() {
        return cachedValues.debugAudit();
    }

    private static final class PhantomCategory {
        private final ModConfigSpec.IntValue hungerDrainIntervalTicks;
        private final ModConfigSpec.IntValue hungerDrainPerInterval;
        private final ModConfigSpec.BooleanValue allowWallPhasing;

        private PhantomCategory(ModConfigSpec.Builder builder) {
            builder.push("phantom");
            hungerDrainIntervalTicks = builder
                .comment("Number of ticks between hunger drain while phantomized.")
                .defineInRange("hungerDrainIntervalTicks", 80, 1, Integer.MAX_VALUE);
            hungerDrainPerInterval = builder
                .comment("Amount of hunger drained every interval while phantomized.")
                .defineInRange("hungerDrainPerInterval", 1, 0, 20);
            allowWallPhasing = builder
                .comment("Allow phantomized players to phase through blocks when sneaking.")
                .define("allowWallPhasing", true);
            builder.pop();
        }
    }

    private static final class AvianCategory {
        private final ModConfigSpec.IntValue sleepMaxY;
        private final ModConfigSpec.BooleanValue slowFallingEnabled;

        private AvianCategory(ModConfigSpec.Builder builder) {
            builder.push("avian");
            sleepMaxY = builder
                .comment("Maximum Y level Avian players can sleep at.")
                .defineInRange("sleepMaxY", 86, Integer.MIN_VALUE, Integer.MAX_VALUE);
            slowFallingEnabled = builder
                .comment("Enable the Avian slow falling passive effect.")
                .define("slowFallingEnabled", true);
            builder.pop();
        }
    }

    private static final class ElementalCategory {
        private final ModConfigSpec.DoubleValue waterDamagePerSecond;

        private ElementalCategory(ModConfigSpec.Builder builder, String path, double defaultValue, String damageComment) {
            builder.push(path);
            waterDamagePerSecond = builder
                .comment(damageComment)
                .defineInRange("waterDamagePerSecond", defaultValue, 0.0D, 20.0D);
            builder.pop();
        }
    }

    private static final class MerlingCategory {
        private final ModConfigSpec.DoubleValue swimSpeedMultiplier;
        private final ModConfigSpec.BooleanValue underwaterVisionEnabled;

        private MerlingCategory(ModConfigSpec.Builder builder) {
            builder.push("merling");
            swimSpeedMultiplier = builder
                .comment("Movement speed multiplier applied while underwater.")
                .defineInRange("swimSpeedMultiplier", 1.35D, 0.0D, 5.0D);
            underwaterVisionEnabled = builder
                .comment("Enable the underwater night vision effect.")
                .define("underwaterVisionEnabled", true);
            builder.pop();
        }
    }

    private static final class FelineCategory {
        private final ModConfigSpec.DoubleValue fallDamageReduction;
        private final ModConfigSpec.DoubleValue moveSpeedMultiplier;

        private FelineCategory(ModConfigSpec.Builder builder) {
            builder.push("feline");
            fallDamageReduction = builder
                .comment("Fraction of fall damage prevented (1.0 = no damage, 0 = vanilla damage).")
                .defineInRange("fallDamageReduction", 1.0D, 0.0D, 1.0D);
            moveSpeedMultiplier = builder
                .comment("Movement speed multiplier applied to feline players.")
                .defineInRange("moveSpeedMultiplier", 1.10D, 0.0D, 5.0D);
            builder.pop();
        }
    }

    private static final class ElytrianCategory {
        private final ModConfigSpec.BooleanValue cancelFallDamage;
        private final ModConfigSpec.BooleanValue confinedSpaceChecks;

        private ElytrianCategory(ModConfigSpec.Builder builder) {
            builder.push("elytrian");
            cancelFallDamage = builder
                .comment("Cancel all Elytrian fall damage.")
                .define("cancelFallDamage", true);
            confinedSpaceChecks = builder
                .comment("Apply weakness and slowness when the Elytrian is in a confined space.")
                .define("confinedSpaceChecks", false);
            builder.pop();
        }
    }

    private static final class ShulkCategory {
        private final ModConfigSpec.BooleanValue chestArmorAllowed;

        private ShulkCategory(ModConfigSpec.Builder builder) {
            builder.push("shulk");
            chestArmorAllowed = builder
                .comment("Allow Shulk players to equip chest armor instead of storing it in their shell.")
                .define("chestArmorAllowed", false);
            builder.pop();
        }
    }
}
