package io.github.apace100.origins.neoforge.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PlayerOrigin {
    private static final String NBT_ORIGIN = "Origin";
    private static final String NBT_POWERS = "Powers";

    private ResourceLocation originId; // e.g. origins:elytrian
    private final Set<ResourceLocation> powers = new HashSet<>();

    public ResourceLocation getOriginId() {
        return originId;
    }

    public Optional<ResourceLocation> getOriginIdOptional() {
        return Optional.ofNullable(originId);
    }

    public void setOriginId(ResourceLocation id) {
        this.originId = id;
    }

    public boolean hasOrigin() {
        return originId != null;
    }

    public void clear() {
        this.originId = null;
        this.powers.clear();
    }

    public Set<ResourceLocation> getPowers() {
        return powers;
    }

    public void setPowers(Set<ResourceLocation> newPowers) {
        powers.clear();
        powers.addAll(newPowers);
    }

    public CompoundTag saveNBT() {
        CompoundTag tag = new CompoundTag();
        if (originId != null) {
            tag.putString(NBT_ORIGIN, originId.toString());
        }

        if (!powers.isEmpty()) {
            ListTag list = new ListTag();
            for (ResourceLocation powerId : powers) {
                list.add(StringTag.valueOf(powerId.toString()));
            }
            tag.put(NBT_POWERS, list);
        }

        return tag;
    }

    public void loadNBT(CompoundTag tag) {
        originId = null;
        if (tag.contains(NBT_ORIGIN, Tag.TAG_STRING)) {
            ResourceLocation parsed = ResourceLocation.tryParse(tag.getString(NBT_ORIGIN));
            if (parsed != null) {
                originId = parsed;
            }
        }

        powers.clear();
        if (tag.contains(NBT_POWERS, Tag.TAG_LIST)) {
            ListTag list = tag.getList(NBT_POWERS, Tag.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                ResourceLocation parsedPower = ResourceLocation.tryParse(list.getString(i));
                if (parsedPower != null) {
                    powers.add(parsedPower);
                }
            }
        }
    }

    public void copyFrom(PlayerOrigin other) {
        this.originId = other.originId;
        this.powers.clear();
        this.powers.addAll(other.powers);
    }

    public CompoundTag save() {
        return saveNBT();
    }

    public void load(CompoundTag tag) {
        loadNBT(tag);
    }
}
