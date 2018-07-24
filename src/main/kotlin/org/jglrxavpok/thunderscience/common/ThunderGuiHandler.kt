package org.jglrxavpok.thunderscience.common

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler
import org.jglrxavpok.thunderscience.client.gui.GuiCreeperSolidifier
import org.jglrxavpok.thunderscience.client.gui.GuiCreeperLiquefier
import org.jglrxavpok.thunderscience.client.gui.GuiTemporalChamber
import org.jglrxavpok.thunderscience.client.gui.GuiTeslaCoil
import org.jglrxavpok.thunderscience.common.container.ContainerCreeperSolidifier
import org.jglrxavpok.thunderscience.common.container.ContainerCreeperLiquefier
import org.jglrxavpok.thunderscience.common.container.ContainerTemporalChamber
import org.jglrxavpok.thunderscience.common.container.ContainerTeslaCoil
import org.jglrxavpok.thunderscience.common.tileentity.TileEntityCreeperSolidifier
import org.jglrxavpok.thunderscience.common.tileentity.TileEntityCreeperLiquefier
import org.jglrxavpok.thunderscience.common.tileentity.TileEntityTemporalChamber
import org.jglrxavpok.thunderscience.common.tileentity.TileEntityTeslaCoilCenter

object ThunderGuiHandler: IGuiHandler {
    val TeslaCoilID: Int = 1
    val CreeperLiquefierID: Int = 2
    val CreeperSolidifierID: Int = 3
    val TemporalChamberID: Int = 4

    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        return when(ID) {
            TeslaCoilID -> {
                val pos = BlockPos.PooledMutableBlockPos.retain(x, y, z)
                val coreTileEntity = world.getTileEntity(pos)
                pos.release()
                if(coreTileEntity !is TileEntityTeslaCoilCenter)
                    return null
                GuiTeslaCoil(player, coreTileEntity)
            }
            CreeperSolidifierID -> {
                val pos = BlockPos.PooledMutableBlockPos.retain(x, y, z)
                val tileEntity = world.getTileEntity(pos)
                pos.release()
                if(tileEntity !is TileEntityCreeperSolidifier)
                    return null
                GuiCreeperSolidifier(player, tileEntity)
            }
            CreeperLiquefierID -> {
                val pos = BlockPos.PooledMutableBlockPos.retain(x, y, z)
                val tileEntity = world.getTileEntity(pos)
                pos.release()
                if(tileEntity !is TileEntityCreeperLiquefier)
                    return null
                GuiCreeperLiquefier(player, tileEntity)
            }
            TemporalChamberID -> {
                val pos = BlockPos.PooledMutableBlockPos.retain(x, y, z)
                val tileEntity = world.getTileEntity(pos)
                pos.release()
                if(tileEntity !is TileEntityTemporalChamber)
                    return null
                GuiTemporalChamber(player, tileEntity)
            }
            else -> null
        }
    }

    override fun getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        return when(ID) {
            TeslaCoilID -> {
                val pos = BlockPos.PooledMutableBlockPos.retain(x, y, z)
                val coreTileEntity = world.getTileEntity(pos)
                pos.release()
                if(coreTileEntity !is TileEntityTeslaCoilCenter)
                    return null
                ContainerTeslaCoil(player, coreTileEntity)
            }
            CreeperSolidifierID -> {
                val pos = BlockPos.PooledMutableBlockPos.retain(x, y, z)
                val tileEntity = world.getTileEntity(pos)
                pos.release()
                if(tileEntity !is TileEntityCreeperSolidifier)
                    return null
                ContainerCreeperSolidifier(player, tileEntity)
            }
            CreeperLiquefierID -> {
                val pos = BlockPos.PooledMutableBlockPos.retain(x, y, z)
                val tileEntity = world.getTileEntity(pos)
                pos.release()
                if(tileEntity !is TileEntityCreeperLiquefier)
                    return null
                ContainerCreeperLiquefier(player, tileEntity)
            }
            TemporalChamberID -> {
                val pos = BlockPos.PooledMutableBlockPos.retain(x, y, z)
                val tileEntity = world.getTileEntity(pos)
                pos.release()
                if(tileEntity !is TileEntityTemporalChamber)
                    return null
                ContainerTemporalChamber(player, tileEntity)
            }
            else -> null
        }
    }
}