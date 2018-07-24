package org.jglrxavpok.thunderscience.common.tileentity

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.wrapper.InvWrapper
import org.jglrxavpok.thunderscience.ThunderScience
import org.jglrxavpok.thunderscience.common.InvalidRecipe
import org.jglrxavpok.thunderscience.common.TemporalChamberRecipes

class TileEntityTemporalChamber: TileEntity(), IInventory {

    private var stackToTransform = ItemStack.EMPTY
    private val title = TextComponentTranslation(ThunderScience.ModID+".gui.temporal_chamber.title")
    private val invWrapper = InvWrapper(this)

    override fun getField(p0: Int): Int {
        return 0 // TODO
    }

    override fun hasCustomName(): Boolean {
        return false
    }

    override fun getStackInSlot(p0: Int): ItemStack {
        if(p0 != 0)
            return ItemStack.EMPTY
        return stackToTransform
    }

    override fun decrStackSize(p0: Int, p1: Int): ItemStack {
        if(p0 != 0)
            return ItemStack.EMPTY
        val itemStack = ItemStackHelper.getAndSplit(listOf(stackToTransform), 0, p1)
        if (!itemStack.isEmpty) {
            this.markDirty()
        }

        stackToTransform.count -= p1
        if(stackToTransform.count <= 0)
            stackToTransform = ItemStack.EMPTY

        return itemStack
    }

    override fun clear() {
        stackToTransform = ItemStack.EMPTY
    }

    override fun getSizeInventory(): Int {
        return 1
    }

    override fun getName(): String {
        return title.unformattedText
    }

    override fun isEmpty(): Boolean {
        return stackToTransform == ItemStack.EMPTY
    }

    override fun isItemValidForSlot(p0: Int, p1: ItemStack): Boolean {
        if(p0 != 0)
            return false
        return TemporalChamberRecipes.getCorrespondingResult(p1) != InvalidRecipe
    }

    override fun getInventoryStackLimit(): Int {
        return 64
    }

    override fun isUsableByPlayer(p0: EntityPlayer): Boolean {
        return true
    }

    override fun openInventory(p0: EntityPlayer) { }

    override fun setField(p0: Int, p1: Int) {
        // TODO
    }

    override fun closeInventory(p0: EntityPlayer) { }

    override fun setInventorySlotContents(p0: Int, p1: ItemStack) {
        if(p0 != 0)
            return
        stackToTransform = p1.copy()
    }

    override fun removeStackFromSlot(p0: Int): ItemStack {
        if(p0 != 0)
            return ItemStack.EMPTY
        val result = stackToTransform
        stackToTransform = ItemStack.EMPTY
        return result
    }

    override fun getFieldCount(): Int {
        return 0 // TODO
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true
        return super.hasCapability(capability, facing)
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return invWrapper as T
        return super.getCapability(capability, facing)
    }
}