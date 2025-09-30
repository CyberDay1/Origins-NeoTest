package io.github.apace100.origins.platform.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.capabilities.ICapabilitySerializable;
import net.neoforged.neoforge.capabilities.CapabilityToken;
import net.neoforged.neoforge.capabilities.Capability;
import net.neoforged.neoforge.capabilities.CapabilityManager;

public class PlayerOriginProvider implements ICapabilitySerializable<CompoundTag> {

    public static final Capability<PlayerOrigin> ORIGIN_CAP = CapabilityManager.get(new CapabilityToken<>() {});

    private final PlayerOrigin backend = new PlayerOrigin();

    @Override
    public <T> T getCapability(Capability<T> cap, Direction side) {
        return cap == ORIGIN_CAP ? ORIGIN_CAP.orEmpty(cap, backend) : null;
    }

    @Override
    public CompoundTag serializeNBT() {
        return backend.saveNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        backend.loadNBT(nbt);
    }
}
