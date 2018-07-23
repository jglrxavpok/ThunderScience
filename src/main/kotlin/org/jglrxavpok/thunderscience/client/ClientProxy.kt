package org.jglrxavpok.thunderscience.client

import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.StateMapperBase
import net.minecraft.item.ItemBlock
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.event.RenderTooltipEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.jglrxavpok.thunderscience.ThunderScience
import org.jglrxavpok.thunderscience.common.ExtendedTooltipTranslationKey
import org.jglrxavpok.thunderscience.common.RegistryEvents
import org.jglrxavpok.thunderscience.common.ThunderProxy
import org.jglrxavpok.thunderscience.common.block.BlockThunderCollector
import org.jglrxavpok.thunderscience.common.fluid.FluidLiquefiedCreeper

class ClientProxy: ThunderProxy() {

    override fun preInit() {
        super.preInit()
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    fun registerModels(event: ModelRegistryEvent) {
        for(item in RegistryEvents.itemList) {
            ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(item.registryName.toString(), "inventory"))
        }

        for(block in RegistryEvents.blockList) {
            ModelLoader.setCustomModelResourceLocation(ItemBlock.getItemFromBlock(block), 0, ModelResourceLocation(block.registryName.toString(), "inventory"))
        }

        val modelResourceLocation = ModelResourceLocation(ThunderScience.ModID+":fluid/${FluidLiquefiedCreeper.name}", "normal")
        ModelLoader.setCustomStateMapper(FluidLiquefiedCreeper.block, object: StateMapperBase() {
            override fun getModelResourceLocation(p0: IBlockState?): ModelResourceLocation {
                return modelResourceLocation
            }
        })
    }
}