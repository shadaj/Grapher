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
  val samples = 2000
  
  val sineWaveData2 = sineWave(2, magnitude, sampleFrequency, samples)
  val sineWaveData4 = sineWave(4, magnitude, sampleFrequency, samples)
  
  grapher.addGraph("Sine Wave 2hz", sineWaveData2, Color.BLACK, 2/sampleFrequency.toDouble)
  grapher.addGraph("Sine Wave 4hz", sineWaveData4, Color.RED, 4/sampleFrequency.toDouble)
}