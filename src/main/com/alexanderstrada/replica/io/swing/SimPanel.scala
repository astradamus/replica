package com.alexanderstrada.replica.io.swing

import java.awt._
import javax.swing.JPanel

import com.alexanderstrada.replica.space2d.Vector2d
import com.alexanderstrada.replica.world.World

private class SimPanel(world: World, display: Display) extends JPanel() {

  private val cam = new Camera(this)

  private var compositePlants = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, display.alphaPlant)
  private var compositeArable = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, display.alphaArableMap)

  override def paint(g: Graphics) = {
    val g2d = g.asInstanceOf[Graphics2D]
    val stdComp = g2d.getComposite

    fillBlack(g)
    drawArableMap(g2d)
    drawWorldBounds(g2d, stdComp)
    drawPlantsAndFruits(g, g2d)
  }

  private def scale(d: Double) = (d * cam.zoomLevel).round.toInt

  private def fillBlack(g: Graphics) = {
    g.setColor(Color.BLACK)
    g.fillRect(0, 0, getWidth, getHeight)
  }

  private def drawArableMap(g2d: Graphics2D) = if (display.alphaArableMap > 0.0f) {
    val (w, h) = (world.arableMap.cellWidth, world.arableMap.cellHeight)
    g2d.setColor(Color.GRAY)
    world.arableMap.forEachArableCell((x, y) => {
      val sX = scale(x * w + cam.offset.x)
      val sY = scale(y * h + cam.offset.y)
      g2d.drawRect(sX, sY, scale(w), scale(h))
    })

    // Apply grid alpha after drawing grid to ensure even color of lines.
    compositeArable = compositeArable.derive(1.0f - display.alphaArableMap)
    g2d.setComposite(compositeArable)
    fillBlack(g2d)
  }

  private def drawWorldBounds(g2d: Graphics2D, stdComp: Composite) = {
    g2d.setComposite(stdComp)
    g2d.setColor(Color.DARK_GRAY)
    val pos = world.bounds.topLeft + cam.offset
    g2d.drawRect(scale(pos.x), scale(pos.y), scale(world.bounds.size.w), scale(world.bounds.size.h))
  }

  private def drawPlantsAndFruits(g: Graphics, g2d: Graphics2D) = if (display.alphaPlant > 0.0f) {
    compositePlants = compositePlants.derive(display.alphaPlant)
    g2d.setComposite(compositePlants)
    world.plants.foreach(p => drawPlantOrFruit(g, p.color, p.pos + cam.offset, p.size, hasCanopy = true))
    world.fruits.foreach(f => drawPlantOrFruit(g, f.color, f.pos + cam.offset, f.size))
  }

  private def drawPlantOrFruit(g: Graphics, color: Color, pos: Vector2d, size: Double, hasCanopy: Boolean = false) = {
    val (x, y, s) = (scale(pos.x), scale(pos.y), scale(size))
    val halfS = s / 2
    g.setColor(color)
    g.drawRect(x - halfS, y - halfS, s, s)
    if (hasCanopy) {
      val canopySize = scale(size * world.rules.plantCanopyMultiplier)
      val halfCS = canopySize / 2
      g.drawRect(x - halfCS, y - halfCS, canopySize, canopySize)
    }
  }
}
