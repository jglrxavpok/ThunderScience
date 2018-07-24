package org.jglrxavpok.thunderscience.common

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fluids.BlockFluidFinite
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import org.jglrxavpok.thunderscience.ThunderScience
import org.jglrxavpok.thunderscience.common.block.*
import org.jglrxavpok.thunderscience.common.fluid.FluidLiquefiedCreeper
import org.jglrxavpok.thunderscience.common.item.ItemLightingConductor
import org.jglrxavpok.thunderscience.common.tileentity.*

object RegistryEvents {

    val blockList = arrayListOf(BlockLightingRod, BlockRainMaker, BlockThunderCollector, BlockTeslaCoil, BlockTeslaCoilCenter, BlockCreeperLiquefier, BlockCreeperSolidifier, BlockTemporalChamber)
    val itemList = arrayListOf(ItemLightingConductor)

    @SubscribeEvent
    fun registerBlocks(event: RegistryEvent.Register<Block>) {
        for(block in blockList)
            event.registry.register(block)
        val liquefiedCreeperFluidBlock = BlockFluidFinite(FluidLiquefiedCreeper, ThunderScience.MaterialLiquefiedCreeper)
                .setQuantaPerBlock(16)
                .setMaxScaledLight(FluidLiquefiedCreeper.luminosity)
                .setUnlocalizedName("fluid_block_liquefied_creeper")
                .setRegistryName(ResourceLocation(ThunderScience.ModID, "fluid_block_liquefied_creeper"))
                .setLightLevel(8f)
        event.registry.register(liquefiedCreeperFluidBlock)
        GameRegistry.registerTileEntity(TileEntityLightingRod::class.java, BlockLightingRod.registryName)
        GameRegistry.registerTileEntity(TileEntityRainMaker::class.java, BlockRainMaker.registryName)
        GameRegistry.registerTileEntity(TileEntityThunderCollector::class.java, BlockThunderCollector.registryName)
        GameRegistry.registerTileEntity(TileEntityTeslaCoilCenter::class.java, BlockTeslaCoilCenter.registryName)
        GameRegistry.registerTileEntity(TileEntityTeslaCoil::class.java, BlockTeslaCoil.registryName)
        GameRegistry.registerTileEntity(TileEntityCreeperLiquefier::class.java, BlockCreeperLiquefier.registryName)
        GameRegistry.registerTileEntity(TileEntityCreeperSolidifier::class.java, BlockCreeperSolidifier.registryName)
        GameRegistry.registerTileEntity(TileEntityTemporalChamber::class.java, BlockTemporalChamber.registryName)
    }

    @SubscribeEvent
    fun registerItems(event: RegistryEvent.Register<Item>) {
        for(block in blockList) {
            event.registry.register(ItemBlock(block).setRegistryName(block.registryName).setUnlocalizedName(block.unlocalizedName))
        }
        event.registry.registerAll(*itemList.toTypedArray())
    }
}