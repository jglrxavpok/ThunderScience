package org.jglrxavpok.thunderscience.common.tileentity

import net.minecraft.entity.monster.EntityCreeper
import net.minecraft.init.SoundEvents
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.ITickable
import net.minecraft.util.SoundCategory
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidTankProperties
import org.jglrxavpok.thunderscience.ThunderScience
import org.jglrxavpok.thunderscience.common.block.Orientation
import org.jglrxavpok.thunderscience.common.fluid.FluidLiquefiedCreeper
import org.jglrxavpok.thunderscience.common.k
import org.jglrxavpok.thunderscience.network.S0ParticleSpawn
import org.jglrxavpok.thunderscience.network.S1Sound

class TileEntityCreeperSolidifier: TileEntityEnergy(), IFluidTankProperties, IFluidHandler, ITickable {

    private var creeperAmount = 0

    override val maxReceivableEnergy: Int = 20.k
    override val maxExtractableEnergy: Int = 0

    override fun update() {
        if(world.isRemote) {
            return
        }
        if(creeperAmount > 200) {
            creeperAmount -= 200
            spawnCreeper()
            markDirty()
        }
    }

    fun spawnCreeper() {
        val facing = getBlockFacing() ?: EnumFacing.UP
        val spawningPos = pos.offset(facing)
        val creeper = EntityCreeper(world)
        creeper.setPositionAndRotation(spawningPos.x+0.5, spawningPos.y.toDouble(), spawningPos.z+0.5, creeper.rng.nextFloat() * 360f, 0f)
        world.spawnEntity(creeper)

        val speed = 0.1
        ThunderScience.network.sendToAll(S0ParticleSpawn(EnumParticleTypes.SMOKE_NORMAL,
                spawningPos.x.toDouble()+0.5 - facing.directionVec.x*0.5,
                spawningPos.y.toDouble()+0.5 - facing.directionVec.y*0.5,
                spawningPos.z.toDouble()+0.5 - facing.directionVec.z*0.5,
                facing.directionVec.x*speed, facing.directionVec.y*speed, facing.directionVec.z*speed))


        ThunderScience.network.sendToAll(S1Sound(SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), pitch = 1.2f))
    }

    fun getBlockFacing(): EnumFacing? {
        val state = world.getBlockState(pos)
        return state.getValue(Orientation)
    }

    override fun isEnergyFacing(facing: EnumFacing?): Boolean {
        val blockFacing = getBlockFacing()
        return facing != blockFacing
    }

    override fun canExtract() = false

    override fun getMaxEnergyStored() = 20.k

    override fun canReceive() = true

    override fun canDrainFluidType(p0: FluidStack?): Boolean {
        return false
    }

    override fun getContents(): FluidStack {
        return FluidStack(FluidLiquefiedCreeper, creeperAmount)
    }

    override fun canFillFluidType(p0: FluidStack): Boolean {
        return p0.fluid == FluidLiquefiedCreeper
    }

    override fun getCapacity(): Int {
        return 50000
    }

    override fun canFill() = true

    override fun canDrain() = false

    override fun drain(p0: FluidStack, perform: Boolean): FluidStack {
        return FluidStack(FluidLiquefiedCreeper, 0)
    }

    override fun drain(p0: Int, perform: Boolean): FluidStack {
        return FluidStack(FluidLiquefiedCreeper, 0)
    }

    override fun fill(p0: FluidStack, perform: Boolean): Int {
        if(p0.fluid != FluidLiquefiedCreeper)
            return 0
        val newLevel = Math.min(capacity, p0.amount+creeperAmount)
        val accepted = newLevel-creeperAmount
        if(perform) {
            creeperAmount += accepted
            markDirty()
        }

        return accepted
    }

    override fun getTankProperties(): Array<IFluidTankProperties> {
        return arrayOf(this)
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