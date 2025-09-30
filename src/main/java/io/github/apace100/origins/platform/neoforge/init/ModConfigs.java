package io.github.apace100.origins.platform.neoforge.init;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ModConfigs {
    public static final ModConfigSpec COMMON_SPEC;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment("Origins common configuration").push("common");
        builder.define("enablePlaceholderPower", true);
        builder.pop();
        COMMON_SPEC = builder.build();
    }

    private ModConfigs() {
    }
}
