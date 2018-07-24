package org.jglrxavpok.thunderscience.common.block

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.jglrxavpok.thunderscience.ThunderScience
import org.jglrxavpok.thunderscience.common.ExtendedTooltipTranslationKey
import org.jglrxavpok.thunderscience.common.tileentity.TileEntityRainMaker
import org.lwjgl.input.Keyboard

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

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, world: World?, tooltip: MutableList<String>, advanced: ITooltipFlag) {
        tooltip.add(TextComponentTranslation(ThunderScience.ModID+".tile.rain_maker.description").formattedText)
    }
}