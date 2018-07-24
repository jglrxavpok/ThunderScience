package org.jglrxavpok.thunderscience.common.tileentity

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.NonNullList
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.wrapper.InvWrapper
import org.jglrxavpok.thunderscience.ThunderScience
import org.jglrxavpok.thunderscience.common.InvalidRecipe
import org.jglrxavpok.thunderscience.common.TemporalChamberRecipes

class TileEntityTemporalChamber: TileEntityListenable(), IInventory, ITickable {

    private val invContents = NonNullList.withSize(2, ItemStack.EMPTY)
    private val title = TextComponentTranslation(ThunderScience.ModID+".gui.temporal_chamber.title")
    private val invWrapper = InvWrapper(this)

    internal var lightingCharges = 0
    internal var transformTime = 0
    internal var nextResult = ItemStack.EMPTY
    val maxTransformTime = 5*20
    val maxLightingCharges: Int = 100

    override fun update() {
        if(world.isRemote) {
            return
        }
        updateListeners()
        if(transformTime > 0) {
            transformTime--
            if(transformTime == 0) {
                val currentResultStack = invContents[1]
                if(currentResultStack.isEmpty) {
                    invContents[1] = nextResult
                } else {
                    currentResultStack.count++
                }
            }
        } else {
            if(lightingCharges > 0) {
                if(invContents[0].isEmpty)
                    return
                val recipe = TemporalChamberRecipes.getCorrespondingResult(invContents[0])
                if(recipe != InvalidRecipe) {
                    val stackInResultSlot = invContents[1]
                    if(stackInResultSlot.isEmpty || (ItemStack.areItemsEqual(recipe.output, stackInResultSlot) && stackInResultSlot.count < stackInResultSlot.maxStackSize)) {
                        nextResult = recipe.output.copy()
                        lightingCharges--
                        transformTime = maxTransformTime
                        decrStackSize(0, 1)
                    }
                }
            }
        }
    }

    fun receiveLighting(): Boolean {
        if(lightingCharges < 100) {
            lightingCharges++
            return true
        }
        return false
    }

    override fun getField(p0: Int): Int {
        return when(p0) {
            0 -> lightingCharges
            1 -> transformTime
            else -> -1
        }
    }

    override fun hasCustomName(): Boolean {
        return false
    }

    override fun getStackInSlot(p0: Int): ItemStack {
        if(p0 == 0)
            return invContents[0]
        if(p0 == 1)
            return invContents[1]
        return ItemStack.EMPTY
    }

    override fun decrStackSize(p0: Int, p1: Int): ItemStack {
        if(p0 != 0 && p0 != 1)
            return ItemStack.EMPTY
        val itemStack = ItemStackHelper.getAndSplit(invContents, p0, p1)
        if (!itemStack.isEmpty) {
            this.markDirty()
        }

        return itemStack
    }

    override fun clear() {
        invContents.fill(ItemStack.EMPTY)
    }

    override fun getSizeInventory(): Int {
        return invContents.size
    }

    override fun getName(): String {
        return title.unformattedText
    }

    override fun isEmpty(): Boolean {
        return invContents[0] == ItemStack.EMPTY && invContents[1] == ItemStack.EMPTY
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
        when(p0) {
            0 -> lightingCharges = p1
            1 -> transformTime = p1
        }
    }

    override fun closeInventory(p0: EntityPlayer) { }

    override fun setInventorySlotContents(p0: Int, p1: ItemStack) {
        if(p0 == 0 || p0 == 1)
            invContents[p0] = p1.copy()
    }

    override fun removeStackFromSlot(p0: Int): ItemStack {
        if(p0 != 0 && p0 != 1)
            return ItemStack.EMPTY
        val result = invContents[p0]
        invContents[p0] = ItemStack.EMPTY
        return result
    }

    override fun getFieldCount(): Int {
        return 2
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        lightingCharges = compound.getInteger("lighting_charges")
        transformTime = compound.getInteger("transform_time")
        ItemStackHelper.loadAllItems(compound, invContents)
        nextResult = ItemStack(compound.getCompoundTag("next_result"))
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setTag("next_result", nextResult.writeToNBT(NBTTagCompound()))
        compound.setInteger("lighting_charges", lightingCharges)
        compound.setInteger("transform_time", transformTime)
        ItemStackHelper.saveAllItems(compound, invContents)
        return super.writeToNBT(compound)
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

    fun canReceiveLighting(): Boolean {
        return lightingCharges < 100
    }
}