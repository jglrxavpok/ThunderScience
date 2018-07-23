package org.jglrxavpok.thunderscience.common.tileentity

import net.minecraft.inventory.Container
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.IEnergyStorage

abstract class TileEntityEnergy: TileEntity(), IEnergyStorage {

    internal var energy: Int = 0
    protected abstract val maxReceivableEnergy: Int
    protected abstract val maxExtractableEnergy: Int

    private val listeners = mutableListOf<Container>()
    private val listenersToAdd = mutableListOf<Container>()
    private val listenersToRemove = mutableListOf<Container>()


    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        energy = compound.getInteger("energy")
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setInteger("energy", energy)
        return super.writeToNBT(compound)
    }

    override fun getEnergyStored(): Int {
        return energy
    }

    override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int {
        if(!canReceive())
            return 0
        val maxExtracted = Math.min(energy, Math.min(maxExtract, maxExtractableEnergy))
        val newEnergyLevel = energy - maxExtracted
        val actuallyExtracted = energy - newEnergyLevel
        if(!simulate) {
            energy -= maxExtracted
            markDirty()
        }
        return actuallyExtracted
    }

    override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int {
        if(!canReceive())
            return 0
        val maxExtracted = Math.min(maxEnergyStored-energy, Math.min(maxReceive, maxReceivableEnergy))
        val newEnergyLevel = energy + maxExtracted
        val actuallyReceived = newEnergyLevel - energy
        if(!simulate) {
            energy += maxExtracted
            markDirty()
        }
        return actuallyReceived
    }

    fun pushEnergyToNeighbors(totalEnergyToSend: Int, facings: Array<EnumFacing> = EnumFacing.values()) {
        if(energy < totalEnergyToSend)
            return
        val neighborCount = countNeighbors(facings)
        if(neighborCount <= 0)
            return
        val energyToSendToASingleNeighbor = Math.ceil(totalEnergyToSend.toDouble()/neighborCount).toInt()
        var energyActuallySent = 0
        neighborsThatCanReceivePower(facings).forEach {
            energyActuallySent += it.receiveEnergy(energyToSendToASingleNeighbor, false)
        }
        energy -= energyActuallySent
        markDirty()
    }

    fun pullEnergyFromNeighbors(totalEnergyToReceive: Int, facings: Array<EnumFacing> = EnumFacing.values()) {
        if(maxEnergyStored-energy < totalEnergyToReceive)
            return
        val neighborCount = countNeighbors(facings)
        if(neighborCount <= 0)
            return
        val energyToReceiveFromASingleNeighbor = Math.ceil(totalEnergyToReceive.toDouble()/neighborCount).toInt()
        var energyActuallyReceived = 0
        neighborsThatCanReceivePower(facings).forEach {
            energyActuallyReceived += it.extractEnergy(energyToReceiveFromASingleNeighbor, false)
        }
        energy += energyActuallyReceived
        markDirty()
    }

    private fun neighborsThatCanReceivePower(facings: Array<EnumFacing> = EnumFacing.values()) =
            facings
                    .mapNotNull {
                        val neighborPos = pos.offset(it)
                        getPowerCapability(neighborPos, it.opposite)
                    }
                    .filter(IEnergyStorage::canReceive)

    private fun countNeighbors(facings: Array<EnumFacing> = EnumFacing.values()): Int {
        return neighborsThatCanReceivePower(facings).count()
    }

    private fun getPowerCapability(pos: BlockPos, facing: EnumFacing): IEnergyStorage? {
        val te = world.getTileEntity(pos)
        if(te != null) {
            if (te.hasCapability(CapabilityEnergy.ENERGY, facing)) {
                return te.getCapability(CapabilityEnergy.ENERGY, facing)
            }
        }
        return null
    }

    fun consumeEnergy(amount: Int): Boolean {
        if(amount > energy)
            return false
        energy -= amount
        markDirty()
        return true
    }

    abstract fun isEnergyFacing(facing: EnumFacing?): Boolean

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if(capability == CapabilityEnergy.ENERGY && isEnergyFacing(facing)) {
            return this as T
        }
        return super.getCapability(capability, facing)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if(capability == CapabilityEnergy.ENERGY && isEnergyFacing(facing)) {
            return true
        }
        return false
    }

    fun removeContainerListener(container: Container) {
        listenersToRemove += container
    }

    fun addContainerListener(container: Container) {
        listenersToAdd += container
    }

    fun updateListeners() {
        listeners.addAll(listenersToAdd)
        listeners.removeAll(listenersToRemove)

        listenersToAdd.clear()
        listenersToRemove.clear()
        for(listener in listeners) {
            listener.detectAndSendChanges()
        }
    }

}