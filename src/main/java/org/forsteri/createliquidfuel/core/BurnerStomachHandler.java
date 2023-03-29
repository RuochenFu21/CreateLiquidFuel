package org.forsteri.createliquidfuel.core;

import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class BurnerStomachHandler extends CombinedTankWrapper {

    public BurnerStomachHandler(IFluidHandler[] handlers) {
        super(handlers);
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return LiquidBurnerFuelJsonLoader.LIQUID_BURNER_FUEL_MAP.containsKey(stack.getFluid());
    }


}
