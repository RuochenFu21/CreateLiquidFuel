package org.forsteri123.createliquidfuel.core;

import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;

public class BurnerStomachHandler extends CombinedTankWrapper {

    ItemStackHandler inventory;
    public BurnerStomachHandler(IFluidHandler[] handlers) {
        super(handlers);
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return LiquidBurnerFuelJsonLoader.LIQUID_BURNER_FUEL_MAP.containsKey(stack.getFluid());
    }


}
