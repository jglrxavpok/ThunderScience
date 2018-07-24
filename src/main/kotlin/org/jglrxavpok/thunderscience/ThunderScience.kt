package org.jglrxavpok.thunderscience

import net.minecraft.block.material.MapColor
import net.minecraft.block.material.MaterialLiquid
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.util.DamageSource
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import org.apache.logging.log4j.Logger
import org.jglrxavpok.thunderscience.common.RegistryEvents
import org.jglrxavpok.thunderscience.common.TemporalChamberRecipes
import org.jglrxavpok.thunderscience.common.ThunderGuiHandler
import org.jglrxavpok.thunderscience.common.ThunderProxy
import org.jglrxavpok.thunderscience.common.event.EntityEventHandlers
import org.jglrxavpok.thunderscience.common.fluid.FluidLiquefiedCreeper

@Mod(modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter", modid = ThunderScience.ModID, dependencies = "required-after:forgelin;",
        name = "Thunder Science", version = "1.0.0")
object ThunderScience {

    const val ModID = "thunderscience"

    @SidedProxy(modId = ModID, clientSide = "org.jglrxavpok.thunderscience.client.ClientProxy", serverSide = "org.jglrxavpok.thunderscience.server.ServerProxy")
    lateinit var proxy: ThunderProxy

    val network = SimpleNetworkWrapper(ModID)
    val MaterialLiquefiedCreeper = MaterialLiquid(MapColor.GREEN)
    val DamageSourceElectric: DamageSource = DamageSource("$ModID:electric")
    lateinit var logger: Logger

    val CreativeTab = object: CreativeTabs(ModID) {
        override fun getTabIconItem(): ItemStack {
            return ItemStack(Items.REDSTONE)
        }
    }

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        logger = event.modLog
        FluidRegistry.enableUniversalBucket()
        MinecraftForge.EVENT_BUS.register(RegistryEvents)
        MinecraftForge.EVENT_BUS.register(EntityEventHandlers)
        NetworkRegistry.INSTANCE.registerGuiHandler(this, ThunderGuiHandler)
        proxy.preInit()

        // Init Fluid stuff
        FluidRegistry.registerFluid(FluidLiquefiedCreeper)
        FluidRegistry.addBucketForFluid(FluidLiquefiedCreeper)
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        proxy.registerPackets()
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        TemporalChamberRecipes.registerRecipesFromOreDict()
    }
}