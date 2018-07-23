package org.jglrxavpok.thunderscience.client.gui.elements

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.thunderscience.ThunderScience
import org.lwjgl.opengl.GL11.GL_QUADS

class GuiTeslaCreeperButton(id: Int, x: Int, y: Int): GuiButton(id, x, y, 16, 16, "") {

    private val texture = ResourceLocation(ThunderScience.ModID, "textures/gui/tesla.png")
    var chargeCreepers = false

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float) {
        if(this.visible) {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height
            val xOffset = if(hovered) 16.0 else 0.0
            val minU = (176.0+xOffset)/256.0
            val maxU = (176.0+xOffset+16.0)/256.0
            val minV = if(chargeCreepers) 16.0/256.0 else 0.0
            val maxV = minV + 16.0/256.0
            mc.textureManager.bindTexture(texture)

            val tess = Tessellator.getInstance()
            val buffer = tess.buffer
            buffer.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            buffer.pos(x.toDouble(), (y+height).toDouble(), zLevel.toDouble()).tex(minU, maxV).endVertex()
            buffer.pos((x+width).toDouble(), (y+height).toDouble(), zLevel.toDouble()).tex(maxU, maxV).endVertex()
            buffer.pos((x+width).toDouble(), y.toDouble(), zLevel.toDouble()).tex(maxU, minV).endVertex()
            buffer.pos(x.toDouble(), y.toDouble(), zLevel.toDouble()).tex(minU, minV).endVertex()
            tess.draw()
        }
    }

    override fun mousePressed(mc: Minecraft, mouseX: Int, mouseY: Int): Boolean {
        if(super.mousePressed(mc, mouseX, mouseY)) {
            chargeCreepers = !chargeCreepers
            return true
        }
        return false
    }
}