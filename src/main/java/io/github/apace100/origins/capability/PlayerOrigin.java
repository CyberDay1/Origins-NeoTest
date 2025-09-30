package io.github.apace100.origins.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class PlayerOrigin {
    private ResourceLocation originId;

    public Optional<ResourceLocation> getOriginId() {
        return Optional.ofNullable(originId);
    }

    public void setOriginId(ResourceLocation originId) {
        this.originId = originId;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        if (originId != null) {
            tag.putString("Origin", originId.toString());
        }
        return tag;
    }

    public void load(CompoundTag tag) {
        if (tag.contains("Origin")) {
            originId = ResourceLocation.tryParse(tag.getString("Origin"));
        } else {
            originId = null;
        }
    }
}
