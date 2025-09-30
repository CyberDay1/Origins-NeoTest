package io.github.apace100.origins.common.config;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.common.network.SyncConfigS2C;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.ModConfigEvent;

public final class ModConfigs {
    public static final ModConfigSpec SPEC;
    public static final Common COMMON;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        COMMON = new Common(builder);
        SPEC = builder.build();
    }

    private ModConfigs() {
    }

    public static void register(ModLoadingContext context, IEventBus modBus) {
        context.registerConfig(ModConfig.Type.COMMON, SPEC, Origins.MOD_ID + "-common.toml");
        modBus.addListener(ModConfigs::onConfigReloaded);
    }

    private static void onConfigReloaded(ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) {
            COMMON.onReload();
        }
    }

    public static void applySync(SyncConfigS2C payload) {
        COMMON.applySync(payload);
    }

    public static final class Common {
        public final ModConfigSpec.BooleanValue syncPowersOnLogin;
        public final ModConfigSpec.IntValue maxTrackedPowers;

        private Common(ModConfigSpec.Builder builder) {
            builder.push("networking");
            syncPowersOnLogin = builder
                .comment("If true, Origins requests a full power sync from the server whenever a player joins.")
                .define("syncPowersOnLogin", true);
            builder.pop();

            builder.push("gameplay");
            maxTrackedPowers = builder
                .comment("Hard limit for the number of simultaneous passive powers tracked on a player.")
                .defineInRange("maxTrackedPowers", 32, 1, 128);
            builder.pop();
        }

        private void onReload() {
            // TODO: consume updated config values.
        }

        private void applySync(SyncConfigS2C payload) {
            syncPowersOnLogin.set(payload.syncPowersOnLogin());
            maxTrackedPowers.set(payload.maxTrackedPowers());
        }
    }
}
