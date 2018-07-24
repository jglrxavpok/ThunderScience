package org.jglrxavpok.thunderscience.common.container

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IContainerListener
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import org.jglrxavpok.thunderscience.common.TemporalChamberRecipes
import org.jglrxavpok.thunderscience.common.tileentity.TileEntityTemporalChamber

class ContainerTemporalChamber(val player: EntityPlayer, val te: TileEntityTemporalChamber): Container() {

    private var lightingCharges = -1
    private var transformTime = -1

    init {
        te.addContainerListener(this)
        this.addSlotToContainer(SlotTemporalInput(te, 0, 56, 17))
        this.addSlotToContainer(SlotTemporalOutput(te, 1, 86, 17))
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
        listener.sendWindowProperty(this, 0, te.lightingCharges)
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
            if (lightingCharges != te.lightingCharges)
                listener.sendWindowProperty(this, 0, te.lightingCharges)
            if (transformTime != te.transformTime)
                listener.sendWindowProperty(this, 1, te.transformTime)
        }

        lightingCharges = te.lightingCharges
        transformTime = te.transformTime
    }

    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return true
    }

    override fun updateProgressBar(id: Int, data: Int) {
        super.updateProgressBar(id, data)
        when (id) {
            0 -> te.lightingCharges = data
            1 -> te.transformTime = data
        }
        lightingCharges = te.lightingCharges
        transformTime = te.transformTime
    }
}

class SlotTemporalOutput(val inv: IInventory, val index: Int, val x: Int, val y: Int): Slot(inv, index, x, y) {
    override fun isItemValid(stack: ItemStack?): Boolean {
        return false
    }
}

class SlotTemporalInput(val inv: IInventory, val index: Int, val x: Int, val y: Int): Slot(inv, index, x, y) {
    override fun isItemValid(stack: ItemStack): Boolean {
        return TemporalChamberRecipes.isRecipeInput(stack)
    }
}