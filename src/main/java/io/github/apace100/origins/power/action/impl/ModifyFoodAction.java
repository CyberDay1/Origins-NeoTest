package io.github.apace100.origins.power.action.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

/**
 * Datapack action that modifies a player's hunger and saturation values.
 */
public final class ModifyFoodAction implements Action<Player> {
    private static final int MIN_DELTA = -20;
    private static final int MAX_DELTA = 20;
    private static final float MIN_SATURATION_DELTA = -20.0F;
    private static final float MAX_SATURATION_DELTA = 20.0F;

    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("modify_food");
    private static final Codec<ModifyFoodAction> CODEC = RecordCodecBuilder.<ModifyFoodAction>create(instance -> instance.group(
        Codec.INT.optionalFieldOf("hunger", 0).forGetter(ModifyFoodAction::hungerDelta),
        Codec.FLOAT.optionalFieldOf("saturation", 0.0F).forGetter(ModifyFoodAction::saturationDelta)
    ).apply(instance, (hunger, saturation) -> new ModifyFoodAction(hunger, saturation))).flatXmap(ModifyFoodAction::validate, ModifyFoodAction::validate);

    private final int hungerDelta;
    private final float saturationDelta;

    private ModifyFoodAction(int hungerDelta, float saturationDelta) {
        this.hungerDelta = hungerDelta;
        this.saturationDelta = saturationDelta;
    }

    private int hungerDelta() {
        return hungerDelta;
    }

    private float saturationDelta() {
        return saturationDelta;
    }

    @Override
    public void execute(Player player) {
        if (player == null) {
            return;
        }

        FoodData data = player.getFoodData();
        if (hungerDelta != 0) {
            int adjusted = Mth.clamp(data.getFoodLevel() + hungerDelta, 0, 20);
            data.setFoodLevel(adjusted);
        }

        if (saturationDelta != 0.0F) {
            float cappedMax = Math.max(0.0F, (float) data.getFoodLevel());
            float adjusted = Mth.clamp(data.getSaturationLevel() + saturationDelta, 0.0F, cappedMax);
            data.setSaturation(adjusted);
        }
    }

    public static ModifyFoodAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("hunger") && !json.has("saturation")) {
            Origins.LOGGER.warn("Modify food action '{}' is missing both 'hunger' and 'saturation' fields", id);
            return null;
        }

        int hunger = json.has("hunger") ? GsonHelper.getAsInt(json, "hunger") : 0;
        float saturation = json.has("saturation") ? GsonHelper.getAsFloat(json, "saturation") : 0.0F;

        ModifyFoodAction action = new ModifyFoodAction(hunger, saturation);
        DataResult<ModifyFoodAction> validation = validate(action);
        return validation.resultOrPartial(message -> Origins.LOGGER.warn("Modify food action '{}' is invalid: {}", id, message))
            .orElse(null);
    }

    public static Codec<ModifyFoodAction> codec() {
        return CODEC;
    }

    private static DataResult<ModifyFoodAction> validate(ModifyFoodAction action) {
        if (action.hungerDelta == 0 && action.saturationDelta == 0.0F) {
            return DataResult.error(() -> "modify_food action must specify a non-zero hunger or saturation change");
        }
        if (action.hungerDelta < MIN_DELTA || action.hungerDelta > MAX_DELTA) {
            return DataResult.error(() -> "hunger change " + action.hungerDelta + " is outside the allowed range ["
                + MIN_DELTA + ", " + MAX_DELTA + "]");
        }
        if (!Float.isFinite(action.saturationDelta)) {
            return DataResult.error(() -> "saturation change must be a finite number");
        }
        if (action.saturationDelta < MIN_SATURATION_DELTA || action.saturationDelta > MAX_SATURATION_DELTA) {
            return DataResult.error(() -> "saturation change " + action.saturationDelta
                + " is outside the allowed range [" + MIN_SATURATION_DELTA + ", " + MAX_SATURATION_DELTA + "]");
        }
        return DataResult.success(action);
    }
}
