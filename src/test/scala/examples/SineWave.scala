package examples

import grapher.Grapher
import javax.swing.JFrame
import java.awt.Color

object SineWave extends App {
  def sineWave(waveFrequency: Double, magnitude: Double, sampleFrequency: Double, samples: Int): IndexedSeq[Double] = {
    val wavePeriod = 1 / waveFrequency
    val samplePeriod = 1 / sampleFrequency
    val dTheta = (samplePeriod / wavePeriod) * 2 * math.Pi
    
    (0 until samples).map(n => math.sin(dTheta * n) * magnitude)
  }
  
  val grapher = new Grapher
  val frame = new JFrame("SquareWave")
  frame.getContentPane().add(grapher)
  frame.pack
  frame.setVisible(true)
  frame.setSize(1000, 1000)
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  
  val waveFrequency = 2
  val magnitude = 2
  val sampleFrequency = 1000
  val samples = 2001
  
  val sineWaveData = sineWave(waveFrequency, magnitude, sampleFrequency, samples)
  
  grapher.setData("Sine Wave", sineWaveData, Color.BLACK, waveFrequency/sampleFrequency.toDouble)
}