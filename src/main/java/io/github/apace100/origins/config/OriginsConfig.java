package io.github.apace100.origins.config;

import io.github.apace100.origins.util.OriginsConstants;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = OriginsConstants.MODID)
public final class OriginsConfig {
    public static final ModConfigSpec COMMON_SPEC;
    public static final ModConfigSpec.BooleanValue ENABLE_ORB_OF_ORIGIN;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        ENABLE_ORB_OF_ORIGIN = builder.comment("Allows the Orb of Origin item to function.").define("enableOrbOfOrigin", true);
        COMMON_SPEC = builder.build();
    }

    private OriginsConfig() {
    }

    @SubscribeEvent
    public static void onLoad(ModConfigEvent event) {
        // Placeholder for config sync hooks
    }
}
