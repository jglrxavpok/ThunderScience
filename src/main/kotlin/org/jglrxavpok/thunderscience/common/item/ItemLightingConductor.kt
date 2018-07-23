package org.jglrxavpok.thunderscience.common.item

import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.thunderscience.ThunderScience

object ItemLightingConductor: Item() {

    init {
        registryName = ResourceLocation(ThunderScience.ModID, "lighting_conductor")
        unlocalizedName = "lighting_conductor"
        creativeTab = ThunderScience.CreativeTab
    }
}