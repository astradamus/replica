package com.alexanderstrada.replica.io.swing

import java.awt._
import javax.swing._

import com.alexanderstrada.replica.sim.Simulator
import com.alexanderstrada.replica.world.World

class Display(sim: Simulator,
              world: World,
              targetFPS: Int = 30) {

  @volatile var renderingPaused = false
  @volatile var alphaArableMap = 0.15f
  @volatile var alphaPlant = 1.00f

  private val frame = new JFrame("replica")
  private val splitPane = new JSplitPane()
  private val simPanel = new SimPanel(world, this)
  private val controlPanel = new ControlPanel(sim, world, this)

  private val renderTimer = new Timer(1000 / targetFPS, _ => if (!renderingPaused) {
    simPanel.repaint()
    controlPanel.repaint()
  })

  // Set up Swing components.
  splitPane.setPreferredSize(new Dimension(1600, 900))
  splitPane.setResizeWeight(1.0)
  splitPane.setDividerSize(2)
  splitPane.setContinuousLayout(true)
  splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT)
  splitPane.setLeftComponent(simPanel)
  splitPane.setRightComponent(controlPanel)
  frame.add(splitPane)
  frame.pack()
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  splitPane.setDividerLocation(0.85)
  frame.setVisible(true)

  /** Start the rendering loop.*/
  def start() = renderTimer.start()
}
