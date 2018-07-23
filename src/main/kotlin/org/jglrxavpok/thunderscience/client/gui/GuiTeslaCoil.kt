package org.jglrxavpok.thunderscience.client.gui

import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import org.jglrxavpok.thunderscience.ThunderScience
import org.jglrxavpok.thunderscience.client.gui.elements.GuiTeslaCreeperButton
import org.jglrxavpok.thunderscience.common.container.ContainerTeslaCoil
import org.jglrxavpok.thunderscience.common.tileentity.TileEntityTeslaCoilCenter
import org.jglrxavpok.thunderscience.network.C2ChangeTeslaMode
import org.lwjgl.opengl.GL11
import java.text.NumberFormat

class GuiTeslaCoil(val player: EntityPlayer, val te: TileEntityTeslaCoilCenter): GuiContainer(ContainerTeslaCoil(player, te)) {

    private val backgroundTexture = ResourceLocation(ThunderScience.ModID, "textures/gui/tesla.png")
    private val title = TextComponentTranslation(ThunderScience.ModID+".gui.tesla_coil.title")
    private val chargeCreepersTooltipText = TextComponentTranslation(ThunderScience.ModID+".gui.tesla_coil.charge_creepers")
    private val killCreepersTooltipText = TextComponentTranslation(ThunderScience.ModID+".gui.tesla_coil.kill_creepers")
    private val creeperButton = GuiTeslaCreeperButton(0, 0, 0)
    private val numberFormat = "%.2f" // up to 2 decimal places

    override fun initGui() {
        super.initGui()
        addButton(creeperButton)
        creeperButton.chargeCreepers = !te.killCreepers
        creeperButton.x = xSize-creeperButton.width-3+guiLeft
        creeperButton.y = 3+guiTop
    }

    override fun actionPerformed(button: GuiButton) {
        super.actionPerformed(button)
        when(button) {
            creeperButton -> {
                val newState = !creeperButton.chargeCreepers
                ThunderScience.network.sendToServer(C2ChangeTeslaMode(newState, te.pos))
            }
        }
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        this.mc.textureManager.bindTexture(backgroundTexture)
        val x = (this.width - this.xSize) / 2
        val y = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize)

        val energyPercentage = te.energy / te.maxEnergyStored.toFloat()
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

        fontRenderer.drawString("Cooldown: ${te.cooldown}", mouseX-guiLeft, mouseY-guiTop, 0xFFFFFFFF.toInt())
        fontRenderer.drawString("Coil size: ${te.currentCoilSize}", mouseX-guiLeft, mouseY-guiTop+10, 0xFFFFFFFF.toInt())

        if(creeperButton.isMouseOver) {
            val text = if(creeperButton.chargeCreepers) chargeCreepersTooltipText else killCreepersTooltipText
            drawHoveringText(text.unformattedText, mouseX-guiLeft, mouseY-guiTop)
        }

        if(mouseX-guiLeft in 7..(7+22) && mouseY-guiTop in 13..(13+60)) {
            val energyStr = String.format(numberFormat, te.energy/1000f)
            val maxEnergyStr = String.format(numberFormat, te.maxEnergyStored/1000f)
            drawHoveringText("$energyStr/$maxEnergyStr kRF", mouseX-guiLeft, mouseY-guiTop)
        }
    }

}