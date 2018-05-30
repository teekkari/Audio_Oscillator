package osc.generator

import misc.AngleTable
import audio.GlobalAudioSettings

object SineGenerator extends GlobalAudioSettings {
  val sineCycle = 2 * Math.PI
  
  // looks up the rough value of sin(x) from the pre-generated angle-table
  private def getSin(x: Double) = AngleTable.getSinRad(x)
  
  // returns the correct increment to be given to the sin function as the time (x-axis)
  private def getTime(x: Double): Double = x / (this.sampleRate)
     
  // Generates a single wave of given frequency
  def generateWave(freq: Int): Array[Short] = {
    val output = Array.ofDim[Short]( sampleRate )
    
    for ( x <- output.indices ) {
      output(x) = (this.getSin(freq*this.getTime(x)*sineCycle)*maxVolume).toShort
    }
    
    output
  }
  
}