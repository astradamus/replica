package com.alexanderstrada.replica.io.swing

import java.awt.Container
import java.awt.event.{MouseAdapter, MouseEvent, MouseWheelEvent}

import com.alexanderstrada.replica.space2d.{Calc, Vector2d}

/** Enables scrolling and zooming a Swing container with a mouse. Assigns itself to the container's mouse
  * listeners and tells the container to repaint when scrolled or zoomed, but does not otherwise alter
  * the container. The container is responsible for using the offset and zoom levels provided.*/
private class Camera(container: Container) extends MouseAdapter {

  private var _zoomLevel = 0.005
  private var _offset = Vector2d.ZERO
  private var was = Vector2d(0, 0)

  def zoomLevel: Double = _zoomLevel
  def offset: Vector2d = _offset

  override def mousePressed(e: MouseEvent): Unit = {
    was = Vector2d(e.getX, e.getY)
  }

  override def mouseDragged(e: MouseEvent): Unit = {
    val ev = Vector2d(e.getX, e.getY)
    _offset = _offset + (ev - was)*5/_zoomLevel
    was = ev
    container.repaint()
  }

  override def mouseWheelMoved(e: MouseWheelEvent): Unit = {
    val ev = e.getPreciseWheelRotation * -0.1
    _zoomLevel = Calc.clamp(_zoomLevel * (1+ev), 0.001, 1000.0)
    container.repaint()
  }

  // Register with container.
  container.addMouseListener(this)
  container.addMouseMotionListener(this)
  container.addMouseWheelListener(this)
}
