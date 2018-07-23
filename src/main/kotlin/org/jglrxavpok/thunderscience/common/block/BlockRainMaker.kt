package org.jglrxavpok.thunderscience.common.block

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import org.jglrxavpok.thunderscience.ThunderScience
import org.jglrxavpok.thunderscience.common.tileentity.TileEntityRainMaker

object BlockRainMaker: Block(Material.IRON) {

    init {
        registryName = ResourceLocation(ThunderScience.ModID, "rain_maker")
        setCreativeTab(ThunderScience.CreativeTab)
        unlocalizedName = "rain_maker"
    }

    override fun hasTileEntity() = true
    override fun hasTileEntity(state: IBlockState?) = true

    override fun createTileEntity(world: World, state: IBlockState): TileEntity {
        return TileEntityRainMaker()
    }
}