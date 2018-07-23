package org.jglrxavpok.thunderscience.common.tileentity

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.effect.EntityLightningBolt
import net.minecraft.entity.monster.EntityCreeper
import net.minecraft.entity.monster.EntityEnderman
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.monster.EntityPigZombie
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.DamageSource
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.IEnergyStorage
import org.jglrxavpok.thunderscience.ThunderScience
import org.jglrxavpok.thunderscience.common.block.BlockTeslaCoil
import org.jglrxavpok.thunderscience.common.block.BlockTeslaCoilCenter
import org.jglrxavpok.thunderscience.common.k
import org.jglrxavpok.thunderscience.network.S1Sound
import java.util.*
class TileEntityTeslaCoilCenter: TileEntityEnergy(), ITickable, IEnergyStorage {

    companion object {
        val ConsumptionPerTick = 100
        val ConsumptionPerShock = 25.k
        val EnergyRequired = ConsumptionPerShock
        val MaxCooldown = 200
    }

    private val tmpPos = BlockPos.MutableBlockPos()
    private val targets = mutableListOf<Pair<Vec3d, Int>>()
    /**
     * id: 1
     */
    internal var killCreepers = true
    /**
     * id: 2
     */
    internal var cooldown = 0
    /**
     * id: 3
     */
    internal var currentCoilSize = 0

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newState: IBlockState): Boolean {
        return newState.block !is BlockTeslaCoilCenter
    }

    override val maxReceivableEnergy: Int = 1.k
    override val maxExtractableEnergy: Int = 0

    override fun isEnergyFacing(facing: EnumFacing?): Boolean {
        return facing != EnumFacing.UP
    }

    override fun update() {
        val coilSize = fetchCoilFromWorld()
        currentCoilSize = coilSize
        val radius = (coilSize+1)*8.toDouble()
        getEntitiesAround(radius).forEach {
            targets += Pair(it.positionVector, 10)
        }

        if(world.isRemote)
            return
        updateListeners()
        pullEnergyFromNeighbors(500, TileEntityThunderCollector.FacingsWithoutUp)

        if(cooldown > 0) {
            if(cooldown-- > 0)
                return
        }

        if(!world.isSidePowered(pos, EnumFacing.UP))
            return

        if(this.energyStored < EnergyRequired) {
            return
        }
        if(consumeEnergy(ConsumptionPerTick)) {
            var fired = false
            getEntitiesAround(radius).forEach {
                if(consumeEnergy(ConsumptionPerShock)) {
                    if(it is EntityCreeper && !killCreepers) {
                        if(!it.powered) {
                            fired = true
                            it.onStruckByLightning(EntityLightningBolt(world, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), true))
                            it.setFire(0)
                        }
                    } else {
                        fired = true
                        shock(it)
                    }
                }
            }

            if(fired) {
                cooldown = MaxCooldown
            }
        }
    }

    fun getEntitiesAround(): List<EntityMob> {
        val coilSize = fetchCoilFromWorld()
        val radius = (coilSize+1)*5.toDouble()
        return getEntitiesAround(radius)
    }

    fun getEntitiesAround(radius: Double): List<EntityMob> {
        val aabb = AxisAlignedBB(pos).grow(radius)
        return world.getEntitiesWithinAABB(EntityMob::class.java, aabb)
                .filter {
                    it !is EntityEnderman && it !is EntityPigZombie // don't aggro them
                }
                .sortedBy { it.getDistanceSq(pos) }
    }

    private fun shock(mob: EntityMob) {
        ThunderScience.network.sendToAll(S1Sound(SoundEvents.ENTITY_LIGHTNING_IMPACT, SoundCategory.BLOCKS, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), pitch = 1.5f))
        ThunderScience.network.sendToAll(S1Sound(SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.BLOCKS, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), pitch = 1.5f))
        mob.attackEntityFrom(DamageSource.LIGHTNING_BOLT, 50.0f)
    }

    /**
     * Calculates the coil size and updates the coil tile entities at the same time
     */
    private fun fetchCoilFromWorld(): Int {
        return maximumDistance(pos.x, pos.z) { partPos ->
            val te = world.getTileEntity(partPos)
            if(te is TileEntityTeslaCoil) {
                te.controllerLocation.setPos(pos)
            }
        }
    }

    private tailrec fun maximumDistance(x: Int, z: Int, radius: Int = 0, actionForParts: (BlockPos) -> Unit): Int = when {
        circleFit(radius, x, z, actionForParts) -> maximumDistance(x, z, radius+1, actionForParts)
        else -> radius-1
    }

    private fun circleFit(radius: Int, x0: Int, z0: Int, actionForParts: (BlockPos) -> Unit): Boolean {
        // adapted from https://en.wikipedia.org/wiki/Midpoint_circle_algorithm
        fun foundIntersection(dx: Int, dz: Int): Boolean {
            tmpPos.setPos(x0+dx, pos.y, z0+dz)
            val state = world.getBlockState(tmpPos)
            val invalid = state != BlockTeslaCoil.defaultState && state.block !is BlockTeslaCoilCenter
            if(!invalid) {
                actionForParts(tmpPos)
            }
            return invalid
        }
        var x = radius-1
        var z = 0
        var dx = 1
        var dz = 1
        var err = dx - (radius shl 1)
        while(x >= z) {
            if(foundIntersection(x, z))
                return false
            if(foundIntersection(x, -z))
                return false
            if(foundIntersection(-x, z))
                return false
            if(foundIntersection(-x, -z))
                return false

            if(foundIntersection(z, x))
                return false
            if(foundIntersection(-z, x))
                return false
            if(foundIntersection(z, -x))
                return false
            if(foundIntersection(-z, -x))
                return false

            if(err <= 0) {
                z++
                err += dz
                dz += 2
            }

            if(err > 0) {
                x--
                dx += 2
                err += dx - (radius shl 1)
            }
        }
        return true
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setInteger("cooldown", cooldown)
        compound.setBoolean("killsCreepers", killCreepers)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        cooldown = compound.getInteger("cooldown")
        killCreepers = compound.getBoolean("killsCreepers")
    }

    override fun canExtract() = false

    override fun getMaxEnergyStored() = 500.k

    override fun getEnergyStored(): Int {
        return energy
    }

    override fun canReceive() = true

}

