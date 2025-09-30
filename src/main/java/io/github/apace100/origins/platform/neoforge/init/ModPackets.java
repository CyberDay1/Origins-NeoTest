package io.github.apace100.origins.platform.neoforge.init;

import io.github.apace100.origins.OriginsNeoForge;
import io.github.apace100.origins.core.network.C2SUsePower;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.simple.SimpleChannel;

public final class ModPackets {
    private static final String PROTOCOL_VERSION = "1";
    public static final ResourceLocation CHANNEL_ID = new ResourceLocation(OriginsNeoForge.MOD_ID, "main");
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(CHANNEL_ID)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    private static int index;

    private ModPackets() {
    }

    public static void register() {
        index = 0;
        CHANNEL.registerMessage(index++, C2SUsePower.class, C2SUsePower::encode, C2SUsePower::decode, C2SUsePower::handle);
    }
}
