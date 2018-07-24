package org.jglrxavpok.thunderscience.common.block

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import org.jglrxavpok.thunderscience.ThunderScience
import org.jglrxavpok.thunderscience.common.tileentity.TileEntityCreeperLiquefier
import org.jglrxavpok.thunderscience.common.tileentity.TileEntityTemporalChamber

object BlockTemporalChamber: Block(Material.IRON) {

    init {
        unlocalizedName = "temporal_chamber"
        registryName = ResourceLocation(ThunderScience.ModID, "temporal_chamber")
        setCreativeTab(ThunderScience.CreativeTab)
    }

    override fun hasTileEntity() = true

    override fun hasTileEntity(state: IBlockState?) = true

    override fun createTileEntity(world: World?, state: IBlockState?): TileEntity {
        return TileEntityTemporalChamber()
    }
}