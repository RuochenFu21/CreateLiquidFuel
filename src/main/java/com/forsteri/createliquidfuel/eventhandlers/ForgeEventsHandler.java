package com.forsteri.createliquidfuel.eventhandlers;

import com.forsteri.createliquidfuel.core.LiquidBurnerFuelJsonLoader;

import net.neoforged.neoforge.event.AddReloadListenerEvent;

public class ForgeEventsHandler {
    public static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(LiquidBurnerFuelJsonLoader.INSTANCE);
    }
}