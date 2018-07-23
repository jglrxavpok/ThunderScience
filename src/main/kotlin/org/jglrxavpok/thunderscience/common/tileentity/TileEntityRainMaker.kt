package org.jglrxavpok.thunderscience.common.tileentity

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.IEnergyStorage
import org.jglrxavpok.thunderscience.ThunderScience
import org.jglrxavpok.thunderscience.common.k
import org.jglrxavpok.thunderscience.network.S0ParticleSpawn
import java.util.*

class TileEntityRainMaker: TileEntityEnergy(), ITickable, IEnergyStorage {

    companion object {
        const val AverageTickIntervalBetweenRain = 10 * 60 * 20 // every 10 min
        val EnergyRequired = 10.k
    }

    private val rng = Random()

    override fun update() {
        if(!world.canBlockSeeSky(pos.up())) // only the top rod actually generates lighting
            return
        if(world.isRemote)
            return
        pullEnergyFromNeighbors(200, TileEntityThunderCollector.FacingsWithoutUp)

        if(this.energyStored < EnergyRequired) {
            return
        }
        if(!world.isRainingAt(pos) && !world.isThundering) {
            if(consumeEnergy(100)) {
                if(rng.nextInt(10) == 0) {
                    ThunderScience.network.sendToAll(S0ParticleSpawn(EnumParticleTypes.CLOUD, pos.x.toDouble()+rng.nextFloat(), pos.y.toDouble()+rng.nextFloat()+1f, pos.z.toDouble()+rng.nextFloat(), 0.0, 0.1, 0.0))
                }
                if(rng.nextInt(AverageTickIntervalBetweenRain/60) == 0)
                    makeItRain()
            }
        }
    }

    private fun makeItRain() {
        val minTime = 20 * 30 // 30s
        val maxTime = 20 * 60 * 10 // 10min
        val time = minTime + rng.nextFloat() * (maxTime-minTime)
        world.worldInfo.isRaining = true
        world.worldInfo.rainTime = time.toInt()
        if(rng.nextBoolean()) {
            world.worldInfo.isThundering = true
            world.worldInfo.thunderTime = time.toInt()
        }

        world.worldInfo.cleanWeatherTime = 0
    }

    override fun canExtract() = false

    override fun getMaxEnergyStored() = 20.k

    override fun getEnergyStored(): Int {
        return energy
    }

    override fun canReceive() = true

    override val maxReceivableEnergy: Int = 500
    override val maxExtractableEnergy: Int = 0

    override fun isEnergyFacing(facing: EnumFacing?): Boolean {
        return facing != EnumFacing.UP
    }
}