package io.github.apace100.origins.neoforge.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class PlayerOrigin {
    private ResourceLocation originId; // e.g. origins:elytrian
    private final Set<ResourceLocation> powers = new HashSet<>();

    public ResourceLocation getOriginId() {
        return originId;
    }

    public void setOriginId(ResourceLocation id) {
        this.originId = id;
    }

    public Set<ResourceLocation> getPowers() {
        return powers;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        if (originId != null) tag.putString("Origin", originId.toString());
        // powers serialization later
        return tag;
    }

    public void load(CompoundTag tag) {
        if (tag.contains("Origin")) {
            this.originId = new ResourceLocation(tag.getString("Origin"));
        }
    }
}
