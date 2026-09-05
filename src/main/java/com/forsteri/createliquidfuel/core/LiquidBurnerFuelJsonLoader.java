package com.forsteri.createliquidfuel.core;

import com.forsteri.createliquidfuel.CreateLiquidFuel;
import com.forsteri.createliquidfuel.util.Triplet;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

/**
 * One-version compatibility loader for {@code data/<namespace>/compat/*.json}.
 * New packs should use the {@code createliquidfuel:liquid_fuel} data map.
 */
public class LiquidBurnerFuelJsonLoader extends SimpleJsonResourceReloadListener {
    public static final ResourceLocation IDENTIFIER = ResourceLocation.fromNamespaceAndPath("createliquidfuel", "drainable_fuel_loader");

    private static final Gson GSON = new Gson();

    public static final LiquidBurnerFuelJsonLoader INSTANCE = new LiquidBurnerFuelJsonLoader();

    public LiquidBurnerFuelJsonLoader() {
        super(GSON, "compat");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> entries, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        BurnerStomachHandler.LIQUID_BURNER_FUEL_MAP.entrySet()
                .removeIf(entry -> IDENTIFIER.equals(entry.getValue().getFirst()));

        for (Map.Entry<ResourceLocation, JsonElement> entry : entries.entrySet()) {
            JsonElement element = entry.getValue();
            if (!element.isJsonObject()) {
                continue;
            }

            ResourceLocation id = entry.getKey();
            JsonObject object = element.getAsJsonObject();
            JsonElement fluidElement = object.get("fluid");

            if (fluidElement == null) {
                CreateLiquidFuel.LOGGER.warn("Skipping {}: not a liquid burner fuel definition (no \"fluid\" field)", id);
                continue;
            }

            final Fluid fluid;
            try {
                Optional<Fluid> value = BuiltInRegistries.FLUID.getOptional(ResourceLocation.parse(fluidElement.getAsString()));
                if (value.isEmpty()) {
                    CreateLiquidFuel.LOGGER.warn("Skipping liquid burner fuel {}: unknown fluid {}", id, fluidElement.getAsString());
                    continue;
                }
                fluid = value.get();
            } catch (ResourceLocationException e) {
                CreateLiquidFuel.LOGGER.warn("Skipping liquid burner fuel {}: invalid fluid {}", id, fluidElement.getAsString());
                continue;
            }

            if (fluid.builtInRegistryHolder().getData(LiquidFuels.DATA_MAP) != null) {
                continue;
            }

            CreateLiquidFuel.LOGGER.warn(
                    "Liquid burner fuel {} is using the deprecated data/<namespace>/compat/ format. Move it to data/createliquidfuel/data_maps/fluid/liquid_fuel.json",
                    id
            );

            boolean superHeat = object.has("superHeat") && object.get("superHeat").getAsBoolean();
            int burnTime = object.has("burnTime")
                    ? object.get("burnTime").getAsInt()
                    : superHeat ? 32 : 20;
            int amountConsumedPerTick = object.has("amountConsumedPerTick")
                    ? object.get("amountConsumedPerTick").getAsInt()
                    : superHeat ? 10 : 1;

            BurnerStomachHandler.LIQUID_BURNER_FUEL_MAP.put(
                    fluid,
                    Pair.of(IDENTIFIER, Triplet.of(burnTime, superHeat, amountConsumedPerTick))
            );
        }
    }
}
