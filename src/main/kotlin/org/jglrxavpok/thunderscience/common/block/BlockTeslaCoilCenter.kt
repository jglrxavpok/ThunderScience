package org.jglrxavpok.thunderscience.common.block

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.jglrxavpok.thunderscience.ThunderScience
import org.jglrxavpok.thunderscience.common.ExtendedTooltipTranslationKey
import org.jglrxavpok.thunderscience.common.ThunderGuiHandler
import org.jglrxavpok.thunderscience.common.tileentity.TileEntityTeslaCoilCenter
import org.lwjgl.input.Keyboard
import java.util.*

val Powered = PropertyBool.create("powered")

object BlockTeslaCoilCenter: Block(Material.IRON) {

    init {
        registryName = ResourceLocation(ThunderScience.ModID, "tesla_coil_center")
        setCreativeTab(ThunderScience.CreativeTab)
        unlocalizedName = "tesla_coil_center"
        defaultState = blockState.baseState.withProperty(Powered, false)
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, Powered)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return if(state.getValue(Powered)) 1 else 0
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return defaultState.withProperty(Powered, meta != 0)
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntity {
        return TileEntityTeslaCoilCenter()
    }

    override fun hasTileEntity() = true

    override fun hasTileEntity(state: IBlockState?) = true

    /**
     * Called after the block is set in the Chunk data, but before the Tile Entity is set
     */
    override fun onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState) {
        if (!worldIn.isRemote) {
            if (state.getValue(Powered) && !worldIn.isBlockPowered(pos)) {
                worldIn.setBlockState(pos, defaultState.withProperty(Powered, false), 2)
            } else if (!state.getValue(Powered) && worldIn.isBlockPowered(pos)) {
                worldIn.setBlockState(pos, defaultState.withProperty(Powered, true), 2)
            }
        }
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        if (!worldIn.isRemote) {
            if (state.getValue(Powered) && !worldIn.isBlockPowered(pos)) {
                worldIn.scheduleUpdate(pos, this, 4)
            } else if (!state.getValue(Powered) && worldIn.isBlockPowered(pos)) {
                worldIn.setBlockState(pos, defaultState.withProperty(Powered, true), 2)
            }
        }
    }

    override fun updateTick(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random) {
        if (!worldIn.isRemote) {
            if (state.getValue(Powered) && !worldIn.isBlockPowered(pos)) {
                worldIn.setBlockState(pos, defaultState.withProperty(Powered, false), 2)
            }
        }
    }

    override fun getBlockLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
    }

    override fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if(world.isRemote)
            return true
        player.openGui(ThunderScience, ThunderGuiHandler.TeslaCoilID, world, pos.x, pos.y, pos.z)
        return true
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, world: World?, tooltip: MutableList<String>, advanced: ITooltipFlag) {
        tooltip.add(TextComponentTranslation(ThunderScience.ModID+".tile.tesla_coil_center.description").formattedText)
    }
}
