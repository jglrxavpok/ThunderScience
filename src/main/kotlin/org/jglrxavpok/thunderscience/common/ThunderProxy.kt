package org.jglrxavpok.thunderscience.common

import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.thunderscience.ThunderScience
import org.jglrxavpok.thunderscience.network.C2ChangeTeslaMode
import org.jglrxavpok.thunderscience.network.S0ParticleSpawn
import org.jglrxavpok.thunderscience.network.S1Sound

abstract class ThunderProxy {

    fun registerPackets() {
        ThunderScience.network.registerMessage(S0ParticleSpawn.Handler, S0ParticleSpawn::class.java, 0, Side.CLIENT)
        ThunderScience.network.registerMessage(S1Sound.Handler, S1Sound::class.java, 1, Side.CLIENT)
        ThunderScience.network.registerMessage(C2ChangeTeslaMode.Handler, C2ChangeTeslaMode::class.java, 2, Side.SERVER)
    }

    open fun preInit() {}
}