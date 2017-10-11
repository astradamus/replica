package com.alexanderstrada.replica.io.swing

import java.awt._
import java.awt.event.ActionListener
import javax.swing._
import javax.swing.border.BevelBorder

import com.alexanderstrada.replica.bot.Genome
import com.alexanderstrada.replica.io.swing.ControlPanel._
import com.alexanderstrada.replica.sim.Simulator
import com.alexanderstrada.replica.world.{Data, World}

/** A companion panel to the simulation panel. Provides the
  * user with statistics and controls for the displayed world.*/
class ControlPanel(
    sim: Simulator,
    world: World,
    display: Display)
  extends JPanel(new GridBagLayout)
  with Styled {

  private val statsTable = new CpTable()

  setup()

  override def paint(g: Graphics): Unit = {
    super.paint(g)

    statsTable.labelTime.setText(sim.clock.toString)
    val tpsString = ((sim.ticksPerSecond * 100).round / 100.0).toString
    statsTable.labelTPS.setText(if (tpsString.contains('E')) "∞" else tpsString)
    updateLifeCounts()
  }

  def updateData(data: Data) = Genome.allPlantGenes.foreach(g => {
    val maybeLabels = statsTable.plantLabels.get(g)
    maybeLabels.foreach(labels => data.latest(g).foreach(d => {
      labels._2.setText(d._2._1.toString)
      labels._3.setText(d._2._2.toString)
      labels._4.setText(d._2._3.toString)
    }))
  })

  private def updateLifeCounts() = {
    val plantCount = world.plants.size
    val fruitCount = world.fruits.size

    if (plantCount + fruitCount == 0) {
      statsTable.labelPlantCount.setText("EXTINCT")
      statsTable.labelPlantCount.setForeground(Color.ORANGE)
      statsTable.labelCountFruits.setText("hit 'RESTART'")
      statsTable.labelCountFruits.setForeground(Color.ORANGE)
    }
    else {
      statsTable.labelPlantCount.setText(plantCount.toString)
      statsTable.labelCountFruits.setText(fruitCount.toString)
    }
  }

  private def setup() = {
    val gbc = new GridBagConstraints()
    gbc.fill = GridBagConstraints.NONE
    gbc.weighty = 0.0

    val simDelaySpinnerModel = new SpinnerNumberModel(sim.simDelay, 0.0, 1000.0, 0.5)
    val all = Seq(
      new CpCheck("Simulation Paused", set = true) {addActionListener(_ => sim.simPaused = isSelected)},
      new CpCheck("Rendering Paused", set = false) {addActionListener(_ => display.renderingPaused = isSelected)},
      Box.createVerticalStrut(15),

      new CpCell("Simulation Delay", hasBorder = false),
      new CpSpinner(simDelaySpinnerModel) {addChangeListener(_ => sim.simDelay = getValue.asInstanceOf[Double])},
      Box.createVerticalStrut(15),

      new CpCell("Plant Layer Alpha", hasBorder = false),
      new CpSlider(0, 20, (display.alphaPlant*20).round) {
        addChangeListener(_ => display.alphaPlant = getValue/20.0f)
      },
      new CpCell("Arable Map Alpha", hasBorder = false),
      new CpSlider(0, 20, (display.alphaArableMap*20).round) {
        addChangeListener(_ => display.alphaArableMap = getValue/20.0f)
      },
      Box.createVerticalStrut(15),
      statsTable,
      Box.createVerticalStrut(15),
      new CpButton("Restart", _ => SwingDemo.restart()))

    all.zipWithIndex.foreach(a => mk(a._2, a._1))

    def mk(y: Int, j: Component) = {
      gbc.gridy = y
      add(j, gbc)
    }
  }
}

object ControlPanel {

  private val cellMinSize = new Dimension(0, 0)

  sealed trait Styled extends Component {
    setFocusable(false)
    setForeground(Color.WHITE)
    setBackground(Color.BLACK)
  }

  private class CpSpinner(sm: SpinnerModel) extends JSpinner(sm) with Styled

  private class CpSlider(min: Int, max: Int, init: Int) extends JSlider(min, max, init) with Styled

  private class CpCheck(text: String, set: Boolean) extends JCheckBox(text, set) with Styled

  private class CpButton(text: String, listener: ActionListener) extends JButton(text) with Styled {
    setContentAreaFilled(false)
    addActionListener(listener)
  }

  private class CpCell(s: String, c: Color = Color.WHITE, hasBorder: Boolean = true) extends JLabel(s) {
    setMinimumSize(cellMinSize)
    setForeground(c)
    if (hasBorder) setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED))
    setHorizontalAlignment(SwingConstants.CENTER)
    setVerticalAlignment(SwingConstants.CENTER)
  }

  private class CpTable() extends JPanel(new GridBagLayout) with Styled {

    val labelTPS = new CpCell("0")

    val labelTime = new CpCell("0")
    val labelPlantCount = new CpCell("0")
    val labelCountFruits = new CpCell("0")

    val plantLabels = Genome.allPlantGenes.map(g => (g, (new CpCell(g.toString), new CpCell("-"), new CpCell("-"), new CpCell("-")))).toMap

    val gbc = new GridBagConstraints()
    gbc.fill = GridBagConstraints.HORIZONTAL
    gbc.weighty = 0.0

    val rows = Seq(
      ("TIME: ", labelTime),
      ("TPS: ", labelTPS),
      Box.createRigidArea(new Dimension(235, 5)),
      ("PLANTS: ", labelPlantCount),
      ("FRUITS: ", labelCountFruits),
      Box.createRigidArea(new Dimension(235, 5)),
      (new CpCell("gene"), new CpCell("low"), new CpCell("avg"), new CpCell("hi"))
    ) ++ plantLabels.values

    rows.zipWithIndex.foreach(rwi => {
      rwi._1 match {
        case (name: CpCell, low: CpCell, average: CpCell, high: CpCell) =>
          gbc.gridy = rwi._2

          gbc.weightx = 0.00
          gbc.gridx = 0
          gbc.gridwidth = 2
          add(name, gbc)

          gbc.gridwidth = 1
          gbc.weightx = 0.33

          gbc.gridx = 2
          add(low, gbc)
          gbc.gridx = 3
          add(average, gbc)
          gbc.gridx = 4
          add(high, gbc)

        case (s: String, c: CpCell) => mkRow(rwi._2, s, c)
        case (c: Component)         => mkGap(rwi._2, c)
        case _ =>
      }
    })

    private def mkGap(row: Int, comp: Component) = {
      gbc.gridx = 0
      gbc.gridy = row
      gbc.gridwidth = 5
      gbc.weightx = 0.0
      add(comp, gbc)
    }

    private def mkRow(row: Int, name: String, cell: CpCell) = {
      gbc.gridy = row

      gbc.weightx = 0.40
      gbc.gridx = 0
      gbc.gridwidth = 2
      add(new CpCell(name), gbc)

      gbc.weightx = 0.60
      gbc.gridx = 2
      gbc.gridwidth = 3
      add(cell, gbc)
    }
  }
}
