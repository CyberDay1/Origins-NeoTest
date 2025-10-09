package io.github.apace100.origins.util;

import io.github.apace100.origins.Origins;
import net.minecraft.resources.ResourceLocation;

/**
 * Utility helpers that hide `ResourceLocation` API drift between 1.21.1 and later
 * Minecraft releases. Keeping the logic centralized avoids duplicate overlays
 * across variant directories and lets the Stonecutter preprocessor decide which
 * implementation to splice when generating merged sources.
 */
public final class ResourceLocationCompat {
    private ResourceLocationCompat() {
    }

    public static ResourceLocation mod(String path) {
        return of(Origins.MOD_ID, path);
    }

    public static ResourceLocation of(String namespace, String path) {
        //#if mc == 1.21.1
        return new ResourceLocation(namespace, path);
        //#else
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
        //#endif
    }
}
