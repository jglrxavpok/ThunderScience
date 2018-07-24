package org.jglrxavpok.thunderscience.common.block

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.*
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.jglrxavpok.thunderscience.ThunderScience
import org.jglrxavpok.thunderscience.common.ThunderGuiHandler
import org.jglrxavpok.thunderscience.common.tileentity.TileEntityCreeperSolidifier

val Orientation = PropertyEnum.create("facing", EnumFacing::class.java)

object BlockCreeperSolidifier: Block(Material.IRON) {

    init {
        registryName = ResourceLocation(ThunderScience.ModID, "creeper_solidifier")
        unlocalizedName = "creeper_solidifier"
        setCreativeTab(ThunderScience.CreativeTab)
        blockState.baseState.withProperty(Orientation, EnumFacing.NORTH)
    }

    override fun hasTileEntity() = true

    override fun hasTileEntity(state: IBlockState?) = true

    override fun createTileEntity(world: World?, state: IBlockState?): TileEntity {
        return TileEntityCreeperSolidifier()
    }

    // Logic taken from BlockPistonBase

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack) {
        worldIn.setBlockState(pos, state.withProperty(Orientation, EnumFacing.getDirectionFromEntityLiving(pos, placer)), 2)
    }

    override fun getStateForPlacement(worldIn: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase): IBlockState {
        return this.defaultState.withProperty(Orientation, EnumFacing.getDirectionFromEntityLiving(pos, placer))
    }

    fun getFacing(meta: Int): EnumFacing? {
        val i = meta and 7
        return if (i > 5) null else EnumFacing.getFront(i)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(Orientation).index
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultState.withProperty(Orientation, getFacing(meta)!!)
    }

    override fun withRotation(state: IBlockState, rot: Rotation): IBlockState {
        return state.withProperty(Orientation, rot.rotate(state.getValue(Orientation)))
    }

    override fun withMirror(state: IBlockState, mirrorIn: Mirror): IBlockState {
        return state.withRotation(mirrorIn.toRotation(state.getValue(Orientation)))
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, Orientation)
    }

    override fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if(world.isRemote)
            return true
        player.openGui(ThunderScience, ThunderGuiHandler.CreeperSolidifierID, world, pos.x, pos.y, pos.z)
        return true
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, world: World?, tooltip: MutableList<String>, advanced: ITooltipFlag) {
        tooltip.add(TextComponentTranslation(ThunderScience.ModID+".tile.creeper_solidifier.description").formattedText)
    }
}