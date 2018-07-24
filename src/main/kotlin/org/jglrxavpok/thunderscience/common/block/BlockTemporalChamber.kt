package org.jglrxavpok.thunderscience.common.block

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryHelper
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.jglrxavpok.thunderscience.ThunderScience
import org.jglrxavpok.thunderscience.common.ExtendedTooltipTranslationKey
import org.jglrxavpok.thunderscience.common.ThunderGuiHandler
import org.jglrxavpok.thunderscience.common.tileentity.TileEntityCreeperLiquefier
import org.jglrxavpok.thunderscience.common.tileentity.TileEntityTemporalChamber
import org.lwjgl.input.Keyboard

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

    override fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if(world.isRemote)
            return true
        player.openGui(ThunderScience, ThunderGuiHandler.TemporalChamberID, world, pos.x, pos.y, pos.z)
        return true
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val tileentity = worldIn.getTileEntity(pos)
        if (tileentity is IInventory) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (tileentity as IInventory?)!!)
            worldIn.updateComparatorOutputLevel(pos, this)
        }

        super.breakBlock(worldIn, pos, state)
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, world: World?, tooltip: MutableList<String>, advanced: ITooltipFlag) {
        val sneakKeybind = Minecraft.getMinecraft().gameSettings.keyBindSneak
        if(Keyboard.isKeyDown(sneakKeybind.keyCode)) {
            tooltip.add(TextComponentTranslation(ThunderScience.ModID+".tile.temporal_chamber.description").formattedText)
        } else {
            val text = TextFormatting.ITALIC.toString() + TextComponentTranslation(ExtendedTooltipTranslationKey, sneakKeybind.displayName).unformattedText
            tooltip.add(text)
        }
    }
}