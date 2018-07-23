package org.jglrxavpok.thunderscience.common.tileentity

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.IEnergyStorage
import org.jglrxavpok.thunderscience.common.k
import org.jglrxavpok.thunderscience.common.m

class TileEntityThunderCollector: TileEntityEnergy(), ITickable, IEnergyStorage {

    companion object {
        val FacingsWithoutUp = arrayOf(EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST, EnumFacing.DOWN)
    }

    override fun update() {
        if(world.isRemote)
            return
        pushEnergyToNeighbors(100, FacingsWithoutUp)
    }

    fun receiveLightingShock() {
        energy += 1_210.k
        energy = energy.coerceAtMost(maxEnergyStored)
        markDirty()
    }

    override fun isEnergyFacing(facing: EnumFacing?): Boolean {
        return facing != EnumFacing.UP
    }

    override val maxReceivableEnergy: Int = 0
    override val maxExtractableEnergy: Int = 1.k
    override fun getMaxEnergyStored() = 3.m
    override fun canExtract() = true
    override fun canReceive() = false

}