package io.github.origins.platform.neoforge.net;

import net.minecraft.resources.ResourceLocation;

public final class OriginsNetworking {
    private OriginsNetworking() {}
    public static final String MODID = "origins";
    public static final ResourceLocation CHANNEL_ID = ResourceLocation.fromNamespaceAndPath(MODID, "main");
    private static final String PROTO = "1";

    public static void register() {
        // TODO: register payload handlers via RegisterPayloadHandlersEvent when packets are ported.
    }
}
