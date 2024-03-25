package com.forsteri.createliquidfuel;


import com.forsteri.createliquidfuel.eventhandlers.ForgeEventHandler;
import com.forsteri.createliquidfuel.eventhandlers.ModEventHandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(value = CreateLiquidFuel.MOD_ID)
public class CreateLiquidFuel {

    // Directly reference a slf4j logger
    public static final String MOD_ID = "createliquidfuel";

    public CreateLiquidFuel() {
        MinecraftForge.EVENT_BUS.register(ForgeEventHandler.class);
        FMLJavaModLoadingContext.get().getModEventBus().register(ModEventHandler.class);
    }
}
