package org.jglrxavpok.thunderscience.common.tileentity

import net.minecraft.entity.effect.EntityLightningBolt
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ITickable
import org.jglrxavpok.thunderscience.common.block.BlockLightingRod
import org.jglrxavpok.thunderscience.common.block.BlockThunderCollector
import java.util.*

class TileEntityLightingRod: TileEntity(), ITickable {

    companion object {
        const val AverageTickIntervalBetweenLighting = 20 * 20 // every 20s
        const val ChancePerTick = 1f/AverageTickIntervalBetweenLighting
    }

    val rng = Random()

    override fun update() {
        if(world.isRemote)
            return
        if(!world.canBlockSeeSky(pos.up())) // only the top rod actually generates lighting
            return
        val rodHeight = calculateRodHeight()
        var multiplier = rodHeight.coerceAtMost(3).toFloat()
        if(world.isRainingAt(pos) || world.isThundering) {
            val p = rng.nextFloat()
            if(world.isThundering)
                multiplier += 1
            if(p/multiplier < ChancePerTick) {
                triggerLighting()
                fillPotentialCollector(rodHeight)
            }
        }
    }

    private fun fillPotentialCollector(rodHeight: Int) {
        val collectorPos = pos.down(rodHeight)
        val block = world.getBlockState(collectorPos).block
        if(block is BlockThunderCollector) {
            val te = world.getTileEntity(collectorPos) as? TileEntityThunderCollector
            te?.receiveLightingShock()
        }
    }

    private fun calculateRodHeight(): Int {
        var yOffset = 1
        while(world.getBlockState(pos.down(yOffset++)).block === BlockLightingRod);
        return yOffset-1
    }

    private fun triggerLighting() {
        world.addWeatherEffect(EntityLightningBolt(world, pos.x.toDouble(), pos.y.toDouble()+1, pos.z.toDouble(), false))
    }

}