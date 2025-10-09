package io.github.apace100.origins;

import com.mojang.logging.LogUtils;
import io.github.apace100.origins.util.ResourceLocationCompat;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public final class Origins {
    public static final String MOD_ID = "origins";
    public static final Logger LOGGER = LogUtils.getLogger();

    private Origins() {
    }

    public static ResourceLocation id(String path) {
        return ResourceLocationCompat.mod(path);
    }
}
