package com.forsteri.createliquidfuel;

import com.forsteri.createliquidfuel.eventhandlers.ForgeEventsHandler;
import com.forsteri.createliquidfuel.eventhandlers.ModEventHandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(value = CreateLiquidFuel.MOD_ID)
public class CreateLiquidFuel {

    public static final String MOD_ID = "createliquidfuel";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public CreateLiquidFuel() {
        MinecraftForge.EVENT_BUS.register(ForgeEventsHandler.class);
        FMLJavaModLoadingContext.get().getModEventBus().register(ModEventHandler.class);
    }
}
