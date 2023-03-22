package org.forsteri123.createliquidfuel.core;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class LiquidBurnerFuelJsonLoader extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new Gson();

    public static Map<Fluid, Pair<Integer, Boolean>> LIQUID_BURNER_FUEL_MAP = new HashMap<>();

    public static final LiquidBurnerFuelJsonLoader INSTANCE = new LiquidBurnerFuelJsonLoader();

    public LiquidBurnerFuelJsonLoader() {
        super(GSON, "potato_cannon_projectile_types");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> p_10793_, @NotNull ResourceManager p_10794_, @NotNull ProfilerFiller p_10795_) {
        for (Map.Entry<ResourceLocation, JsonElement> entry : p_10793_.entrySet()) {
            JsonElement element = entry.getValue();
            if (element.isJsonObject()) {
                ResourceLocation id = entry.getKey();
                JsonObject object = element.getAsJsonObject();
                JsonElement fluidElement = object.get("fluid");

                if (fluidElement != null) {
                    try {
                        Fluid value = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidElement.getAsString()));
                        if (value != null) {
                            LIQUID_BURNER_FUEL_MAP.put(value,
                                    new Pair<>(
                                        object.has("burnTime") ?
                                                object.get("burnTime").getAsInt() :
                                                20 // Lava Burn Time per ML
                                            , object.has("superHeat") && object.get("superHeat").getAsBoolean() // default not to superheat
                                    )
                                    );
                        }
                    } catch (ResourceLocationException e) {
                        throw new RuntimeException("Fluid liquid burner fuel " + id + " has invalid fluid: " + fluidElement.getAsString());
                    }
                } else {
                    throw new RuntimeException("No fluid specified for liquid burner fuel: " + id);
                }
            }
        }

    }
}
