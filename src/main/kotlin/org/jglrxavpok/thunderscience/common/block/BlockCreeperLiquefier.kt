package org.jglrxavpok.thunderscience.common.block

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.monster.EntityCreeper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.jglrxavpok.thunderscience.ThunderScience
import org.jglrxavpok.thunderscience.common.ThunderGuiHandler
import org.jglrxavpok.thunderscience.common.tileentity.TileEntityCreeperLiquefier
import org.jglrxavpok.thunderscience.network.S0ParticleSpawn

object BlockCreeperLiquefier: Block(Material.IRON) {

    init {
        registryName = ResourceLocation(ThunderScience.ModID, "creeper_liquefier")
        unlocalizedName = "creeper_liquefier"
        setCreativeTab(ThunderScience.CreativeTab)
    }

    override fun hasTileEntity() = true

    override fun hasTileEntity(state: IBlockState?) = true

    override fun createTileEntity(world: World?, state: IBlockState?): TileEntity {
        return TileEntityCreeperLiquefier()
    }

    override fun onEntityWalk(world: World, pos: BlockPos, entity: Entity) {
        super.onEntityWalk(world, pos, entity)
        if(world.isRemote)
            return
        if(entity is EntityCreeper) {
            val te = world.getTileEntity(pos) as? TileEntityCreeperLiquefier
            te?.let {
                if(te.liquefyCreeper(entity)) {
                    val partCount = entity.rng.nextInt(10) + 2
                    repeat(partCount) {
                        val xOff = entity.rng.nextDouble()
                        val zOff = entity.rng.nextDouble()
                        ThunderScience.network.sendToAll(S0ParticleSpawn(EnumParticleTypes.SPELL_INSTANT, pos.x.toDouble()+xOff, pos.y.toDouble()+1.0, pos.z.toDouble()+zOff, 0.0, 0.2, 0.0))
                    }
                    entity.setDead()
                }
            }
        }
    }

    override fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if(world.isRemote)
            return true
        player.openGui(ThunderScience, ThunderGuiHandler.CreeperLiquefierID, world, pos.x, pos.y, pos.z)
        return true
    }
}