package org.jglrxavpok.thunderscience.network

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import org.jglrxavpok.thunderscience.common.container.ContainerTeslaCoil
import org.jglrxavpok.thunderscience.common.tileentity.TileEntityTeslaCoilCenter

class C2ChangeTeslaMode(): IMessage {

    var killsCreeper = false
    var x = 0
    var y = 0
    var z = 0

    constructor(killsCreeper: Boolean, coilCenterPos: BlockPos): this() {
        this.killsCreeper = killsCreeper
        this.x = coilCenterPos.x
        this.y = coilCenterPos.y
        this.z = coilCenterPos.z
    }

    override fun fromBytes(buf: ByteBuf) {
        killsCreeper = buf.readBoolean()
        x = buf.readInt()
        y = buf.readInt()
        z = buf.readInt()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeBoolean(killsCreeper)
        buf.writeInt(x)
        buf.writeInt(y)
        buf.writeInt(z)
    }

    object Handler: IMessageHandler<C2ChangeTeslaMode, IMessage?> {
        override fun onMessage(message: C2ChangeTeslaMode, ctx: MessageContext): IMessage? {
            val player = ctx.serverHandler.player
            if(player.openContainer !is ContainerTeslaCoil) // trying to cheat ? or just laggy
                return null
            val world = player.serverWorld
            world.addScheduledTask {
                with(message) {
                    val pos = BlockPos.PooledMutableBlockPos.retain(x, y, z)
                    val te = world.getTileEntity(pos)
                    pos.release()
                    if(te is TileEntityTeslaCoilCenter) {
                        te.killCreepers = killsCreeper
                    }
                }
            }
            return null
        }

    }
}