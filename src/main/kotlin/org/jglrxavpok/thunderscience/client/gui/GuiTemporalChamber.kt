package org.jglrxavpok.thunderscience.client.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.client.ForgeHooksClient
import net.minecraftforge.client.model.ModelLoader
import org.jglrxavpok.thunderscience.ThunderScience
import org.jglrxavpok.thunderscience.common.container.ContainerCreeperSolidifier
import org.jglrxavpok.thunderscience.common.container.ContainerTemporalChamber
import org.jglrxavpok.thunderscience.common.tileentity.TileEntityCreeperSolidifier
import org.jglrxavpok.thunderscience.common.tileentity.TileEntityTemporalChamber
import org.lwjgl.opengl.GL11

class GuiTemporalChamber(val player: EntityPlayer, val te: TileEntityTemporalChamber): GuiContainer(ContainerTemporalChamber(player, te)) {

    private val backgroundTexture = ResourceLocation(ThunderScience.ModID, "textures/gui/temporal_chamber.png")
    private val title = TextComponentTranslation(ThunderScience.ModID+".gui.temporal_chamber.title")

    override fun initGui() {
        super.initGui()
    }

    override fun actionPerformed(button: GuiButton) {
        super.actionPerformed(button)
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        this.mc.textureManager.bindTexture(backgroundTexture)
        val x = (this.width - this.xSize) / 2
        val y = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize)

        val energyPercentage = te.lightingCharges / te.maxLightingCharges.toFloat()
        val tess = Tessellator.getInstance()
        val buffer = tess.buffer

        val boltLeft = 7.0
        val boltRight = 7.0+22.0
        val boltTop = 13.0
        val boltBottom = 13.0+60.0

        val leftU = 176.0/256.0
        val rightU = (176.0+22.0)/256.0
        val topV = (32.0)/256.0
        val bottomV = (32.0+60.0)/256.0

        val renderTop = (boltTop-boltBottom) * energyPercentage + boltBottom
        val renderTopV = (topV-bottomV) * energyPercentage + bottomV

        GlStateManager.pushMatrix()
        GlStateManager.translate(guiLeft.toFloat(), guiTop.toFloat(), 0f)
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

        buffer.pos(boltLeft, boltBottom, 0.0).tex(leftU, bottomV).endVertex()
        buffer.pos(boltRight, boltBottom, 0.0).tex(rightU, bottomV).endVertex()
        buffer.pos(boltRight, renderTop, 0.0).tex(rightU, renderTopV).endVertex()
        buffer.pos(boltLeft, renderTop, 0.0).tex(leftU, renderTopV).endVertex()
        tess.draw()
        GlStateManager.popMatrix()
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY)
        drawCenteredString(fontRenderer, title.unformattedText, xSize/2, 5, 0xFFFFFFFF.toInt())

        renderTransitionArrow(te.transformTime.toDouble()/te.maxTransformTime)

        if(mouseX-guiLeft in 7..(7+22) && mouseY-guiTop in 13..(13+60)) {
            val energyStr = te.lightingCharges
            val maxEnergyStr = te.maxLightingCharges
            drawHoveringText("$energyStr/$maxEnergyStr kRF", mouseX-guiLeft, mouseY-guiTop)
        }
    }

    private fun renderTransitionArrow(alpha: Double) {
        this.mc.textureManager.bindTexture(backgroundTexture)
        val arrowWidth = 10
        val arrowHeight = 10
        drawTexturedModalRect(0,0, 0, 0, (arrowWidth*(1.0-alpha)).toInt(), arrowHeight)
    }

}