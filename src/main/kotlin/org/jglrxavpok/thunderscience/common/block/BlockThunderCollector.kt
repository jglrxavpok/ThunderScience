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
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentBase
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.jglrxavpok.thunderscience.ThunderScience
import org.jglrxavpok.thunderscience.common.ExtendedTooltipTranslationKey
import org.jglrxavpok.thunderscience.common.tileentity.TileEntityThunderCollector
import org.lwjgl.input.Keyboard

object BlockThunderCollector: Block(Material.IRON) {

    init {
        registryName = ResourceLocation(ThunderScience.ModID, "thunder_collector")
        setCreativeTab(ThunderScience.CreativeTab)
        unlocalizedName = "thunder_collector"
    }

    override fun hasTileEntity() = true
    override fun hasTileEntity(state: IBlockState?) = true

    override fun createTileEntity(world: World, state: IBlockState): TileEntity {
        return TileEntityThunderCollector()
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, world: World?, tooltip: MutableList<String>, advanced: ITooltipFlag) {
        val sneakKeybind = Minecraft.getMinecraft().gameSettings.keyBindSneak
        if(Keyboard.isKeyDown(sneakKeybind.keyCode)) {
            tooltip.add(TextComponentTranslation(ThunderScience.ModID+".item.thunder_collector.description").formattedText)
        } else {
            val text = TextFormatting.ITALIC.toString() + TextComponentTranslation(ExtendedTooltipTranslationKey, sneakKeybind.displayName).unformattedText
            tooltip.add(text)
        }
    }
}