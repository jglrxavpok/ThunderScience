package org.jglrxavpok.thunderscience.common.block

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import org.jglrxavpok.thunderscience.ThunderScience
import org.jglrxavpok.thunderscience.common.tileentity.TileEntityLightingRod

private val Top = PropertyBool.create("top")

object BlockLightingRod: Block(Material.IRON) {

    init {
        registryName = ResourceLocation(ThunderScience.ModID, "lighting_rod")
        setCreativeTab(ThunderScience.CreativeTab)
        unlocalizedName = "lighting_rod"
        defaultState = blockState.baseState.withProperty(Top, false)
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return defaultState.withProperty(Top, meta != 0)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return if(state.getValue(Top)) 1 else 0
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, Top)
    }

    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        val state = defaultState
        if(worldIn.getBlockState(pos.up()).block !is BlockLightingRod) {
            return state.withProperty(Top, true)
        }
        return state
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntity {
        return TileEntityLightingRod()
    }

    override fun hasTileEntity() = true

    override fun hasTileEntity(state: IBlockState?) = true

    override fun getBlockLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }
}