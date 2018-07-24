package org.jglrxavpok.thunderscience.common.block

import net.minecraft.block.Block
import net.minecraft.block.material.Material
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
import org.jglrxavpok.thunderscience.common.tileentity.TileEntityTeslaCoil
import org.lwjgl.input.Keyboard

object BlockTeslaCoil: Block(Material.IRON) {

    init {
        registryName = ResourceLocation(ThunderScience.ModID, "tesla_coil")
        setCreativeTab(ThunderScience.CreativeTab)
        unlocalizedName = "tesla_coil"
    }

    override fun getBlockLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
    }

    override fun shouldSideBeRendered(blockState: IBlockState, blockAccess: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
        val otherBlock = blockAccess.getBlockState(pos.offset(side))
        if(otherBlock.block is BlockTeslaCoil)
            return false
        return super.shouldSideBeRendered(blockState, blockAccess, pos, side)
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntity {
        return TileEntityTeslaCoil()
    }

    override fun hasTileEntity() = true

    override fun hasTileEntity(state: IBlockState?) = true

    override fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if(world.isRemote)
            return true
        val coilTE = world.getTileEntity(pos)
        if(coilTE !is TileEntityTeslaCoil)
            return true
        val controllerPos = coilTE.controllerLocation
        player.openGui(ThunderScience, ThunderGuiHandler.TeslaCoilID, world, controllerPos.x, controllerPos.y, controllerPos.z)
        return true
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, world: World?, tooltip: MutableList<String>, advanced: ITooltipFlag) {
        tooltip.add(TextComponentTranslation(ThunderScience.ModID+".tile.tesla_coil.description").formattedText)
    }
}