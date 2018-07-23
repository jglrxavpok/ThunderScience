package org.jglrxavpok.thunderscience.network

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvent
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class S1Sound(): IMessage {

    private lateinit var soundEvent: ResourceLocation
    private lateinit var category: SoundCategory
    private var pitch: Float = 0f
    private var x = 0.0
    private var y = 0.0
    private var z = 0.0

    constructor(soundEvent: SoundEvent, category: SoundCategory, x: Double, y: Double, z: Double, pitch: Float): this() {
        this.soundEvent = soundEvent.soundName
        this.category = category
        this.pitch = pitch
        this.x = x
        this.y = y
        this.z = z
    }

    override fun fromBytes(buf: ByteBuf) {
        soundEvent = ResourceLocation(ByteBufUtils.readUTF8String(buf))
        x = buf.readDouble()
        y = buf.readDouble()
        z = buf.readDouble()
        category = SoundCategory.values()[buf.readInt()]
        pitch = buf.readFloat()
    }

    override fun toBytes(buf: ByteBuf) {
        ByteBufUtils.writeUTF8String(buf, soundEvent.toString())
        buf.writeDouble(x)
        buf.writeDouble(y)
        buf.writeDouble(z)
        buf.writeInt(category.ordinal)
        buf.writeFloat(pitch)
    }

    object Handler: IMessageHandler<S1Sound, IMessage?> {
        override fun onMessage(message: S1Sound, ctx: MessageContext): IMessage? {
            with(message) {
                Minecraft.getMinecraft().addScheduledTask {
                    val event = SoundEvent.REGISTRY.getObject(soundEvent)
                    if(event != null) {
                        val volume = Minecraft.getMinecraft().gameSettings.getSoundLevel(category)
                        Minecraft.getMinecraft().world.playSound(x, y, z, event, category, volume, pitch, true)
                    }

                }
            }
            return null
        }

    }
}