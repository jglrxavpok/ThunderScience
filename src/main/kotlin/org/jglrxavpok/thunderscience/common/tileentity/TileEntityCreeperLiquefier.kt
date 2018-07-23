package org.jglrxavpok.thunderscience.common.tileentity

import net.minecraft.entity.monster.EntityCreeper
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidTankProperties
import org.jglrxavpok.thunderscience.common.fluid.FluidLiquefiedCreeper
import org.jglrxavpok.thunderscience.common.k

class TileEntityCreeperLiquefier: TileEntityEnergy(), IFluidHandler, IFluidTankProperties {
    private var creeperBuckets = 0

    override val maxReceivableEnergy: Int = 100
    override val maxExtractableEnergy: Int = 0

    override fun isEnergyFacing(facing: EnumFacing?): Boolean {
        return true
    }

    override fun canExtract(): Boolean {
        return false
    }

    override fun getMaxEnergyStored(): Int {
        return 5.k
    }

    override fun canReceive(): Boolean {
        return true
    }

    fun liquefyCreeper(creeper: EntityCreeper): Boolean {
        if(consumeEnergy(200) && creeperBuckets < capacity) {
            val chargeAmount = if(creeper.powered) 1000 else 100
            creeperBuckets += chargeAmount
            markDirty()
            return true
        }

        return false
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        creeperBuckets = compound.getInteger("fluid_level")
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setInteger("fluid_level", creeperBuckets)
        return super.writeToNBT(compound)
    }

    override fun drain(amount: FluidStack, dontSimulate: Boolean): FluidStack {
        if(amount.fluid == FluidLiquefiedCreeper)
            return drain(amount.amount, dontSimulate)
        return FluidStack(FluidLiquefiedCreeper, 0)
    }

    override fun drain(amount: Int, dontSimulate: Boolean): FluidStack {
        val newLevel = Math.max(creeperBuckets - amount, 0)
        val drained = creeperBuckets - newLevel
        if(dontSimulate) {
            creeperBuckets -= drained
            markDirty()
        }
        return FluidStack(FluidLiquefiedCreeper, drained)
    }

    override fun fill(amount: FluidStack, simulate: Boolean): Int {
        return 0
    }

    override fun getTankProperties(): Array<IFluidTankProperties> {
        return arrayOf(this)
    }

    override fun canDrainFluidType(p0: FluidStack): Boolean {
        return false
    }

    override fun getContents(): FluidStack {
        return FluidStack(FluidLiquefiedCreeper, creeperBuckets)
    }

    override fun canFillFluidType(p0: FluidStack): Boolean {
        return p0.fluid == FluidLiquefiedCreeper
    }

    override fun getCapacity(): Int {
        return 50000
    }

    override fun canFill(): Boolean {
        return false
    }

    override fun canDrain(): Boolean {
        return true
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return this as T
        return super.getCapability(capability, facing)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return true
        return super.hasCapability(capability, facing)
    }
}