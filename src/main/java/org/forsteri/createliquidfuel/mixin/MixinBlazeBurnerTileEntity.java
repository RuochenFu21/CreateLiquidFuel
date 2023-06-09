package org.forsteri.createliquidfuel.mixin;

import com.mojang.datafixers.util.Pair;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.forsteri.createliquidfuel.core.BurnerStomachHandler;
import org.forsteri.createliquidfuel.core.LiquidBurnerFuelJsonLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = BlazeBurnerBlockEntity.class, remap = false)
public abstract class MixinBlazeBurnerTileEntity extends SmartBlockEntity {

    @Shadow protected int remainingBurnTime;
    @Shadow @Final public static int MAX_HEAT_CAPACITY;

    @Shadow protected abstract void setBlockHeat(BlazeBurnerBlock.HeatLevel heat);

    @Shadow public abstract BlazeBurnerBlock.HeatLevel getHeatLevelFromBlock();

    public BurnerStomachHandler stomach;

    public MixinBlazeBurnerTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (isFluidHandlerCap(cap))
            return LazyOptional.of(() -> stomach).cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        IFluidHandler[] handlers = new IFluidHandler[10];
        for (int i = 0; i < 10; i++) {
            handlers[i] = new SmartFluidTank(1000, (FluidStack contents)->{});
        }

        stomach = new BurnerStomachHandler(
            handlers
        );
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo info) {
        if (stomach != null) {
            for (int i = 0; i < 10; i++) {
                if (stomach.getFluidInTank(i).getAmount() > 0) {
                    boolean superHeating = HeatCondition.SUPERHEATED.testBlazeBurner(
                            getHeatLevelFromBlock()
                    );

                    Pair<Integer, Boolean> superheats = LiquidBurnerFuelJsonLoader.LIQUID_BURNER_FUEL_MAP.get(
                            stomach.getFluidInTank(i).getFluid());

                    if (superheats == null)
                        return;

                    boolean fluidSuperHeats = superheats.getSecond();

                    int usingFluidAtATime = superHeating ? 1 : 10;

                    if (remainingBurnTime <= MAX_HEAT_CAPACITY) {
                        Pair<Integer, Boolean> _superheats = LiquidBurnerFuelJsonLoader.LIQUID_BURNER_FUEL_MAP.get(
                                stomach.getFluidInTank(i).getFluid());

                        if (_superheats == null)
                            return;

                        if ((remainingBurnTime + (
                                usingFluidAtATime
                                * _superheats.getFirst()) <= MAX_HEAT_CAPACITY)){
                            if (fluidSuperHeats)
                                setBlockHeat(BlazeBurnerBlock.HeatLevel.SEETHING);
                            else
                                setBlockHeat(BlazeBurnerBlock.HeatLevel.FADING);

                            remainingBurnTime += usingFluidAtATime * _superheats.getFirst();

                            stomach.getFluidInTank(i).shrink(usingFluidAtATime);
                        }

                        break;

                    }
                }
            }
        }
    }
}
