package org.jglrxavpok.thunderscience.common.block

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.jglrxavpok.thunderscience.ThunderScience
import org.jglrxavpok.thunderscience.common.ExtendedTooltipTranslationKey
import org.jglrxavpok.thunderscience.common.tileentity.TileEntityLightingRod
import org.lwjgl.input.Keyboard

private val Top = PropertyBool.create("top")

object BlockLightingRod: Block(Material.IRON) {

    val rodBB = AxisAlignedBB(6.5/16.0, 0.0, 6.5/16.0, 9.5/16.0, 16.0/16.0, 9.5/16.0)

    init {
        registryName = ResourceLocation(ThunderScience.ModID, "lighting_rod")
        setCreativeTab(ThunderScience.CreativeTab)
        unlocalizedName = "lighting_rod"
        defaultState = blockState.baseState.withProperty(Top, false)
    }

    override fun getCollisionBoundingBox(blockState: IBlockState, worldIn: IBlockAccess, pos: BlockPos): AxisAlignedBB? {
        return getBoundingBox(blockState, worldIn, pos)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return rodBB
    }

    override fun getSelectedBoundingBox(state: IBlockState, worldIn: World, pos: BlockPos): AxisAlignedBB {
        return super.getSelectedBoundingBox(state, worldIn, pos)
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

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, world: World?, tooltip: MutableList<String>, advanced: ITooltipFlag) {
        tooltip.add(TextComponentTranslation(ThunderScience.ModID+".tile.lighting_rod.description").formattedText)
    }
}