package org.jglrxavpok.thunderscience.common.container

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IContainerListener
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import org.jglrxavpok.thunderscience.common.tileentity.TileEntityCreeperSolidifier

class ContainerCreeperSolidifier(val player: EntityPlayer, val te: TileEntityCreeperSolidifier): Container() {

    private var energy = -1

    init {
        te.addContainerListener(this)
        addPlayerInventory(player.inventory)
    }

    private fun addPlayerInventory(inventory: InventoryPlayer) {
        // main inventory
        for (line in 0..2) {
            for (column in 0..8) {
                this.addSlotToContainer(Slot(inventory, column + line * 9 + 9, 8 + column * 18, 84 + line * 18))
            }
        }

        // hotbar
        for (column in 0..8) {
            this.addSlotToContainer(Slot(inventory, column, 8 + column * 18, 142))
        }
    }

    override fun addListener(listener: IContainerListener) {
        super.addListener(listener)
        listener.sendWindowProperty(this, 0, te.energy)
    }

    override fun onContainerClosed(playerIn: EntityPlayer) {
        te.removeContainerListener(this)
    }

    override fun transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack {
        var itemstack = ItemStack.EMPTY
        val slot = this.inventorySlots[index]

        if (slot != null && slot.hasStack) {
            val itemstack1 = slot.stack
            itemstack = itemstack1.copy()

            if (index in 0..26) {
                if (!this.mergeItemStack(itemstack1, 27, 36, false)) {
                    return ItemStack.EMPTY
                } else if (index in 27..35 && !this.mergeItemStack(itemstack1, 0, 27, false)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 36, false)) {
                return ItemStack.EMPTY
            }

            if (itemstack1.isEmpty) {
                slot.putStack(ItemStack.EMPTY)
            } else {
                slot.onSlotChanged()
            }

            if (itemstack1.count == itemstack.count) {
                return ItemStack.EMPTY
            }

            slot.onTake(playerIn, itemstack1)
        }

        return itemstack
    }

    override fun detectAndSendChanges() {
        super.detectAndSendChanges()

        for (listener in listeners) {
            if (energy != te.energy)
                listener.sendWindowProperty(this, 0, te.energy)
        }

        energy = te.energy
    }

    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return true
    }

    override fun updateProgressBar(id: Int, data: Int) {
        super.updateProgressBar(id, data)
        when (id) {
            0 -> {
                te.energy = data
            }
        }
        energy = te.energy
    }
}