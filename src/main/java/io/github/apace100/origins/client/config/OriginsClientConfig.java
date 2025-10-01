package io.github.apace100.origins.client.config;

import io.github.apace100.origins.Origins;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class OriginsClientConfig {
    public static final ModConfigSpec SPEC;
    public static final Client CLIENT;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        CLIENT = new Client(builder);
        SPEC = builder.build();
    }

    private OriginsClientConfig() {
    }

    public static void register() {
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.CLIENT, SPEC, Origins.MOD_ID + "-client.toml");
    }

    public static final class Client {
        public final ModConfigSpec.BooleanValue showOriginReminder;

        private Client(ModConfigSpec.Builder builder) {
            builder.push("ui");
            showOriginReminder = builder
                .comment("If true, Origins displays a reminder tooltip when holding the Orb of Origin.")
                .define("showOriginReminder", true);
            builder.pop();
        }
    }
}
