package org.jglrxavpok.thunderscience.network

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.util.EnumParticleTypes
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class S0ParticleSpawn(): IMessage {

    private lateinit var particleType: EnumParticleTypes
    private var x = 0.0
    private var y = 0.0
    private var z = 0.0
    private var xSpeed = 0.0
    private var ySpeed = 0.0
    private var zSpeed = 0.0
    private lateinit var params: IntArray

    constructor(particleType: EnumParticleTypes, x: Double, y: Double, z: Double, xSpeed: Double, ySpeed: Double, zSpeed: Double, vararg params: Int): this() {
        this.particleType = particleType
        this.x = x
        this.y = y
        this.z = z
        this.xSpeed = xSpeed
        this.ySpeed = ySpeed
        this.zSpeed = zSpeed
        this.params = params
    }

    override fun fromBytes(buf: ByteBuf) {
        particleType = EnumParticleTypes.values()[buf.readInt()]
        x = buf.readDouble()
        y = buf.readDouble()
        z = buf.readDouble()
        xSpeed = buf.readDouble()
        ySpeed = buf.readDouble()
        zSpeed = buf.readDouble()
        val size = buf.readInt()
        params = IntArray(size) { _ -> buf.readInt() }
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(particleType.ordinal)
        buf.writeDouble(x)
        buf.writeDouble(y)
        buf.writeDouble(z)
        buf.writeDouble(xSpeed)
        buf.writeDouble(ySpeed)
        buf.writeDouble(zSpeed)
        buf.writeInt(params.size)
        params.forEach { buf.writeInt(it) }
    }

    object Handler: IMessageHandler<S0ParticleSpawn, IMessage?> {
        override fun onMessage(message: S0ParticleSpawn, ctx: MessageContext): IMessage? {
            with(message) {
                Minecraft.getMinecraft().world.spawnParticle(particleType, x, y, z, xSpeed, ySpeed, zSpeed, *params)
            }
            return null
        }

    }
}