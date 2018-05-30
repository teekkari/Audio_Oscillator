package osc.generator

import audio.GlobalAudioSettings

object TriangleGenerator extends GlobalAudioSettings{
    
  private def getSample(x: Double, mod: Int): Double = Math.abs( (x % mod) - mod / 2 ) / mod
  
  // returns the length of the wave in samples
  private def findCycleLen(freq: Int) = (sampleRate.toDouble / freq).toInt
  
  /*
   * @param freq -- frequency of the wave
   * Generates a single wave of the given frequency
   */
  def generateWave(freq: Int): Array[Short] = {
    val mod = this.findCycleLen(freq)
    val output = Array.ofDim[Short](this.findCycleLen(freq))
    
    for ( x <- output.indices ) {
      output(x) = (this.getSample(x + mod / 2, mod)*maxVolume).toShort
    }
    
    return output
  }
}