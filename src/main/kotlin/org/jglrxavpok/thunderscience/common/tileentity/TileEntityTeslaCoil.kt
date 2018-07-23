package org.jglrxavpok.thunderscience.common.tileentity

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos

class TileEntityTeslaCoil: TileEntity() {
    val controllerLocation: BlockPos.MutableBlockPos = BlockPos.MutableBlockPos()

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setInteger("controllerX", controllerLocation.x)
        compound.setInteger("controllerY", controllerLocation.y)
        compound.setInteger("controllerZ", controllerLocation.z)
        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        val x = compound.getInteger("controllerX")
        val y = compound.getInteger("controllerY")
        val z = compound.getInteger("controllerZ")
        controllerLocation.setPos(x, y, z)
    }
}