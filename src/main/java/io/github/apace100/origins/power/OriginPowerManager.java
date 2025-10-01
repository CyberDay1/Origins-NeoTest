package io.github.apace100.origins.power;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.neoforge.capability.PlayerOrigin;
import io.github.apace100.origins.neoforge.capability.PlayerOriginManager;
import io.github.apace100.origins.power.impl.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class OriginPowerManager {
    private static final Map<ResourceLocation, Power> POWERS = new HashMap<>();

    public static final ResourceLocation ELYTRA_FLIGHT = Origins.id("elytra_flight");
    public static final ResourceLocation LAUNCH = Origins.id("launch_into_air");
    public static final ResourceLocation WATER_VULNERABILITY = Origins.id("water_vulnerability");
    public static final ResourceLocation FIRE_IMMUNITY = Origins.id("fire_immunity");
    public static final ResourceLocation PHASE = Origins.id("phantomize");
    public static final ResourceLocation AVIAN = Origins.id("avian");
    public static final ResourceLocation AQUATIC = Origins.id("water_breathing");
    public static final ResourceLocation UNDERWATER_VISION = Origins.id("underwater_vision");
    public static final ResourceLocation SWIM_SPEED = Origins.id("swim_speed");
    public static final ResourceLocation SHULKER_INVENTORY = Origins.id("shulk_inventory");
    public static final ResourceLocation HEAVY_STONE = Origins.id("shulk_heavy_stone");
    public static final ResourceLocation STONE_SKIN = Origins.id("shulk_stone_skin");
    public static final ResourceLocation CAT_REFLEXES = Origins.id("cat_fall_immunity");
    public static final ResourceLocation CREEPER_REPELLENT = Origins.id("scare_creepers");
    public static final ResourceLocation FEATHERWEIGHT = Origins.id("slow_falling");
    public static final ResourceLocation TAILWIND = Origins.id("tailwind");
    public static final ResourceLocation ENDER_STEP = Origins.id("throw_pearl");
    public static final ResourceLocation PUMPKIN_ALLERGY = Origins.id("pumpkin_hate");
    public static final ResourceLocation WEAK_ARMS = Origins.id("weak_arms");
    public static final ResourceLocation BURNING_WRATH = Origins.id("burning_wrath");
    public static final ResourceLocation CLAUSTROPHOBIA = Origins.id("claustrophobia");
    public static final ResourceLocation FRESH_AIR = Origins.id("fresh_air");
    public static final ResourceLocation NETHER_SPAWN = Origins.id("nether_spawn");

    private OriginPowerManager() {
    }

    public static void init() {
        register(new ElytraFlightPower(new PowerType<>(ELYTRA_FLIGHT)));
        register(new LaunchIntoAirPower(new PowerType<>(LAUNCH)));
        register(new WaterVulnerabilityPower(new PowerType<>(WATER_VULNERABILITY)));
        register(new FireImmunityPower(new PowerType<>(FIRE_IMMUNITY)));
        register(new PhantomizePower(new PowerType<>(PHASE)));
        register(new AvianPower(new PowerType<>(AVIAN)));
        register(new AquaticPower(new PowerType<>(AQUATIC)));
        register(new UnderwaterVisionPower(new PowerType<>(UNDERWATER_VISION)));
        register(new SwimSpeedPower(new PowerType<>(SWIM_SPEED)));
        register(new ShulkerInventoryPower(new PowerType<>(SHULKER_INVENTORY)));
        register(new HeavyStonePower(new PowerType<>(HEAVY_STONE)));
        register(new StoneSkinPower(new PowerType<>(STONE_SKIN)));
        register(new CatlikeReflexesPower(new PowerType<>(CAT_REFLEXES)));
        register(new CreeperRepellentPower(new PowerType<>(CREEPER_REPELLENT)));
        register(new FeatherweightPower(new PowerType<>(FEATHERWEIGHT)));
        register(new TailwindPower(new PowerType<>(TAILWIND)));
        register(new EnderStepPower(new PowerType<>(ENDER_STEP)));
        register(new PumpkinAllergyPower(new PowerType<>(PUMPKIN_ALLERGY)));
        register(new WeakArmsPower(new PowerType<>(WEAK_ARMS)));
        register(new BurningWrathPower(new PowerType<>(BURNING_WRATH)));
        register(new ClaustrophobiaPower(new PowerType<>(CLAUSTROPHOBIA)));
        register(new FreshAirPower(new PowerType<>(FRESH_AIR)));
        register(new NetherSpawnPower(new PowerType<>(NETHER_SPAWN)));

        NeoForge.EVENT_BUS.addListener(OriginPowerManager::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(OriginPowerManager::onLivingFall);
        NeoForge.EVENT_BUS.addListener(OriginPowerManager::onLivingDamage);
        NeoForge.EVENT_BUS.addListener(OriginPowerManager::onChangeTarget);
    }

    private static void register(Power power) {
        POWERS.put(power.getType().id(), power);
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }

        withPowers(player, power -> power.tick(player));
    }

    private static void onLivingFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) {
            return;
        }

        if (hasPower(player, ELYTRA_FLIGHT) || hasPower(player, CAT_REFLEXES)) {
            event.setDistance(0.0F);
            event.setDamageMultiplier(0.0F);
        }
    }

    private static void onChangeTarget(LivingChangeTargetEvent event) {
        if (event.getNewAboutToBeSetTarget() instanceof Player player && hasPower(player, CREEPER_REPELLENT)) {
            event.setNewAboutToBeSetTarget(null);
        }
    }

    private static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) {
            return;
        }

        if (event.getSource().is(DamageTypes.IN_WALL)
            && hasPower(player, PHASE)) {
            PlayerOrigin origin = PlayerOriginManager.get(player);
            if (origin != null && origin.isPhantomized() && player.isShiftKeyDown()) {
                event.setNewDamage(0.0F);
            }
        }
    }

    private static void withPowers(Player player, Consumer<Power> consumer) {
        PlayerOrigin origin = PlayerOriginManager.get(player);
        if (origin == null) {
            return;
        }

        Set<ResourceLocation> powers = origin.getPowers();
        if (powers.isEmpty()) {
            return;
        }

        for (ResourceLocation id : powers) {
            Power power = POWERS.get(id);
            if (power != null) {
                consumer.accept(power);
            }
        }
    }

    public static boolean hasPower(Player player, ResourceLocation id) {
        PlayerOrigin origin = PlayerOriginManager.get(player);
        return origin != null && origin.getPowers().contains(id);
    }
}
