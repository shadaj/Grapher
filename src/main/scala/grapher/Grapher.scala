package grapher

import javax.swing.JComponent
import java.awt.Graphics2D
import javax.swing.JFrame
import java.awt.Graphics
import java.awt.Color
import javax.swing.JPanel
import java.awt.geom.Line2D
import java.awt.BasicStroke
import java.awt.BorderLayout
import java.awt.geom.Rectangle2D

class Grapher extends JComponent {
  var graphDatas = collection.mutable.MutableList[GraphData]()

  override def paintComponent(g: Graphics) {
    paintComponent(g.asInstanceOf[Graphics2D])
  }

  def paintComponent(g: Graphics2D) {
    if (graphDatas.isEmpty) {
      return
    }

    val screenWidth = getSize().getWidth()
    val screenHeight = getSize().getHeight()

    g.clearRect(0, 0, screenWidth.toInt, screenHeight.toInt)

    val marginFactor = 0.1
    val graphXShift = screenWidth * marginFactor
    val graphYShift = screenHeight * marginFactor

    val graphWidth = screenWidth * (1 - 2 * marginFactor)
    val graphHeight = screenHeight * (1 - 2 * marginFactor)

    g.draw(new Rectangle2D.Double(graphXShift, graphYShift, graphWidth, graphHeight))
    g.translate(graphXShift, graphYShift)

    g.setStroke(new BasicStroke(2))

    val maxData = graphDatas.maxBy(_.data.length)

    val maxLetterCharWidth = g.getFontMetrics().stringWidth(stringToDraw(maxData.data.maxBy(_._1)._1)) * 2

    val howManyCanFit = (screenWidth.toDouble / maxLetterCharWidth).toInt

    val maxxs = maxData.data.map(_._1)
    val maxys = maxData.data.map(_._2)

    val (maxxScale, maxxShift) = scaleAndShift(maxxs, graphWidth)
    val (maxyScale, maxyShift) = scaleAndShift(maxys, graphHeight)

    (0 until maxData.data.length by (maxData.data.length.toDouble / howManyCanFit).toInt).init.foreach { v =>
      val index = v
      val xLoc = (maxData.data(index)._1 * maxxScale).toInt
      val stringNum = stringToDraw(maxData.xRate * index)
      g.drawString(stringNum, xLoc + maxxShift.toInt, graphHeight.toInt + 25)
    }

    val lastIndex = maxData.data.length - 1
    val xLoc = (maxData.data(lastIndex)._1 * maxxScale).toInt
    val stringNum = stringToDraw(maxData.xRate * lastIndex)
    g.drawString(stringNum, xLoc + maxxShift.toInt, graphHeight.toInt + 25)

    val maxY = maxData.data.maxBy(t => t._2)._2
    val yLoc = graphHeight - (maxY * maxyScale)
    g.drawString(maxY.toString, -20, yLoc.toInt)

    graphDatas.zipWithIndex.foreach { graphDataTuple =>
      val (graphData, index) = graphDataTuple

      if (graphData == null) {
        return
      }

      g.setColor(graphData.color)

      val xs = graphData.data.map(_._1)
      val ys = graphData.data.map(_._2)

      val (xMax, xMin) = (xs.max, xs.min)
      val (yMax, yMin) = (ys.max, ys.min)

      val (xScale, xShift) = scaleAndShift(xs, graphWidth)
      val (yScale, yShift) = scaleAndShift(ys, graphHeight)

      graphData.data.sliding(2).foreach { pairs =>
        val p0 = pairs(0)
        val p1 = pairs(1)

        val x0 = p0._1 * xScale + xShift
        val y0 = (p0._2 * yScale) + yShift

        val x1 = p1._1 * xScale + xShift
        val y1 = (p1._2 * yScale) + yShift

        g.draw(new Line2D.Double(x0, graphHeight - y0, x1, graphHeight - y1))
      }

      if (graphData.printMaxValue) {
        val maxValue = graphData.data.maxBy(t => t._2)
        val index = graphData.data.indexOf(maxValue)
        val xLoc = (maxValue._1 * xScale).toInt
        val stringNum = stringToDraw(graphData.xRate * index)
        g.drawString(stringNum, xLoc + xShift.toInt, graphHeight.toInt + 50)
      }

    }
  }

  def addGraph(data: GraphData) {
    if (graphDatas.exists(_.name == data.name)) {
      graphDatas(graphDatas.indexWhere(_.name == data.name)) = data
    } else {
      this.graphDatas = data +: this.graphDatas
    }
    repaint()
  }

  def addGraph(name: String, points: Seq[Double], linecolor: Color, XRate: Double, printMax: Boolean = false) {
    val data = new GraphData(name, points.zipWithIndex.map(t => ((t._2).toDouble, t._1)), linecolor, XRate, printMax)
    addGraph(data)
  }

  def slideAddPoint(name: String, point: Double) = {
    if (!graphDatas.exists(_.name == name)) {
      throw new IllegalArgumentException("Graph: " + name + " does not exist")
    } else {
      val origData = graphDatas(graphDatas.indexWhere(_.name == name))
      val pointToAdd: (Double, Double) = (origData.data.length.toDouble - 1, point)
      graphDatas(graphDatas.indexWhere(_.name == name)) = new GraphData(name, origData.data.tail :+ pointToAdd, origData.color, origData.xRate, origData.printMaxValue)
    }
  }

  private def scaleAndShift(data: Seq[Double], span: Double, includeZero: Boolean = false): (Double, Double) = {
    val min = data.min
    val max = data.max

    if (math.abs(max - min) < 10e-5) {
      (0, span / 2)
    } else if (max > 0 && min >= 0) {
      val scale = span / (max - min)
      val shift = -min
      (scale, shift * scale)
    } else {
      val scale = span / (max - min)
      val shift = scale * -min
      (scale, shift)
    }
  }

  private def stringToDraw(num: Double): String = {
    val numString = num.toString
    val (left, right) = numString.span(_ != '.')
    left + right.take(3)
  }
}