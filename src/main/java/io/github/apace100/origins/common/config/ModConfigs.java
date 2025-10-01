package io.github.apace100.origins.common.config;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.common.network.SyncConfigS2C;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class ModConfigs {
    public static final ModConfigSpec SERVER_SPEC;
    public static final ModConfigSpec CLIENT_SPEC;

    public static final Server SERVER;
    public static final Client CLIENT;

    static {
        ModConfigSpec.Builder serverBuilder = new ModConfigSpec.Builder();
        SERVER = new Server(serverBuilder);
        SERVER_SPEC = serverBuilder.build();

        ModConfigSpec.Builder clientBuilder = new ModConfigSpec.Builder();
        CLIENT = new Client(clientBuilder);
        CLIENT_SPEC = clientBuilder.build();
    }

    private ModConfigs() {
    }

    public static void register(ModLoadingContext context) {
        context.getActiveContainer().registerConfig(ModConfig.Type.SERVER, SERVER_SPEC, Origins.MOD_ID + "-server.toml");
        context.getActiveContainer().registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC, Origins.MOD_ID + "-client.toml");
    }

    public static SyncConfigS2C createSyncPayload() {
        return new SyncConfigS2C(
            SERVER.allowOrbReuse.get(),
            CLIENT.showOriginReminder.get()
        );
    }

    public static void applySync(SyncConfigS2C payload) {
        SERVER.allowOrbReuse.set(payload.allowOrbReuse());
        CLIENT.showOriginReminder.set(payload.showOriginReminder());
    }

    public static final class Server {
        public final ModConfigSpec.BooleanValue allowOrbReuse;

        private Server(ModConfigSpec.Builder builder) {
            builder.push("gameplay");
            allowOrbReuse = builder
                .comment("Allow the Orb of Origin to be reused without clearing the player's origin.")
                .define("allowOrbReuse", false);
            builder.pop();
        }
    }

    public static final class Client {
        public final ModConfigSpec.BooleanValue showOriginReminder;

        private Client(ModConfigSpec.Builder builder) {
            builder.push("ui");
            showOriginReminder = builder
                .comment("Show a reminder toast if the player has not selected an origin yet.")
                .define("showOriginReminder", true);
            builder.pop();
        }
    }
}
