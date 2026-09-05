package com.forsteri.createliquidfuel.core;

import com.forsteri.createliquidfuel.CreateLiquidFuel;
import com.forsteri.createliquidfuel.util.Triplet;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class LiquidBurnerFuelJsonLoader extends SimpleJsonResourceReloadListener {
    public static final ResourceLocation IDENTIFIER = ResourceLocation.of("createliquidfuel:blaze_burner_fuel", ':');

    private static final Gson GSON = new Gson();

    public static final LiquidBurnerFuelJsonLoader INSTANCE = new LiquidBurnerFuelJsonLoader();

    public LiquidBurnerFuelJsonLoader() {
        super(GSON, "blaze_burner_fuel");
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
                fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidElement.getAsString()));
            } catch (ResourceLocationException e) {
                CreateLiquidFuel.LOGGER.warn("Skipping liquid burner fuel {}: invalid fluid {}", id, fluidElement.getAsString());
                continue;
            }

            if (fluid == null || fluid == Fluids.EMPTY) {
                CreateLiquidFuel.LOGGER.warn("Skipping liquid burner fuel {}: unknown fluid {}", id, fluidElement.getAsString());
                continue;
            }

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
