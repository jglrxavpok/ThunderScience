package org.jglrxavpok.thunderscience.common.fluid

import net.minecraft.item.EnumRarity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import org.jglrxavpok.thunderscience.ThunderScience

object FluidLiquefiedCreeper: Fluid("liquefied_creeper",
        ResourceLocation(ThunderScience.ModID, "blocks/fluid/liquefied_creeper_still"),
        ResourceLocation(ThunderScience.ModID, "blocks/fluid/liquefied_creeper_flowing")) {

    init {
        temperature = 400
        rarity = EnumRarity.RARE
        density = 2000
        viscosity = 2000
        luminosity = 8
        isGaseous = false
    }

}