package org.jglrxavpok.thunderscience.common.event

import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.jglrxavpok.thunderscience.ThunderScience

object EntityEventHandlers {

    @SubscribeEvent
    fun onLivingInFluid(event: LivingEvent.LivingUpdateEvent) {
        val entity = event.entityLiving
        val inLiquefiedCreeper = entity.world.isMaterialInBB(entity.entityBoundingBox, ThunderScience.MaterialLiquefiedCreeper)
        if (inLiquefiedCreeper) {
            entity.attackEntityFrom(ThunderScience.DamageSourceElectric, 2f)
        }
    }
}