package examples

import grapher.Grapher
import javax.swing.JFrame
import java.awt.Color

object SquareWave extends App {
	val squareWave = (0 to 1000).map(n => if (n % 100 <= 50) 0D else 100).toSeq
	
	val grapher = new Grapher
	val frame = new JFrame("SquareWave")
    frame.getContentPane().add(grapher)
    frame.pack
    frame.setVisible(true)
    frame.setSize(1000, 1000)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    
    grapher.addGraph("SquareWave", squareWave, Color.BLACK, 1)
}