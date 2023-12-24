package com.forsteri.createliquidfuel;


import com.forsteri.createliquidfuel.core.DrainableFuelLoader;
import com.forsteri.createliquidfuel.core.LiquidBurnerFuelJsonLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(value = CreateLiquidFuel.MOD_ID)
public class CreateLiquidFuel {

    // Directly reference a slf4j logger
    public static final String MOD_ID = "createliquidfuel";

    public CreateLiquidFuel() {
        MinecraftForge.EVENT_BUS.register(this);

        DrainableFuelLoader.load();
    }

    @Mod.EventBusSubscriber
    public static class ForgeEvents {
        @SubscribeEvent
        public static void addReloadListeners(AddReloadListenerEvent event) {
            event.addListener(LiquidBurnerFuelJsonLoader.INSTANCE);
        }
    }
}
