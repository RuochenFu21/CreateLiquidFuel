package com.forsteri.createliquidfuel.core;

import com.forsteri.createliquidfuel.CreateLiquidFuel;
import com.forsteri.createliquidfuel.util.Triplet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jetbrains.annotations.Nullable;

public final class LiquidFuels {
    public static final DataMapType<Fluid, LiquidFuel> DATA_MAP = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath(CreateLiquidFuel.MOD_ID, "liquid_fuel"),
            Registries.FLUID,
            LiquidFuel.CODEC
    ).synced(LiquidFuel.CODEC, false).build();

    private LiquidFuels() {}

    public static @Nullable LiquidFuel get(Fluid fluid) {
        LiquidFuel mapped = fluid.builtInRegistryHolder().getData(DATA_MAP);
        if (mapped != null) {
            return mapped;
        }

        Pair<ResourceLocation, Triplet<Integer, Boolean, Integer>> legacy =
                BurnerStomachHandler.LIQUID_BURNER_FUEL_MAP.get(fluid);
        if (legacy == null || legacy.getSecond() == null) {
            return null;
        }

        Triplet<Integer, Boolean, Integer> properties = legacy.getSecond();
        return new LiquidFuel(properties.getFirst(), properties.getSecond(), properties.getThird());
    }

    public static boolean isFuel(Fluid fluid) {
        return get(fluid) != null;
    }
}
