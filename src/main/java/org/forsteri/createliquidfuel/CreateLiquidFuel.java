package org.forsteri.createliquidfuel;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.forsteri.createliquidfuel.core.LiquidBurnerFuelJsonLoader;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CreateLiquidFuel.MOD_ID)
public class CreateLiquidFuel {

    // Directly reference a slf4j logger
    public static final String MOD_ID = "createliquidfuel";

    public CreateLiquidFuel() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventBusSubscriber
    public static class ForgeEvents {
        @SubscribeEvent
        public static void addReloadListeners(AddReloadListenerEvent event) {
            event.addListener(LiquidBurnerFuelJsonLoader.INSTANCE);
        }
    }
}
