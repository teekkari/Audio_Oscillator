package osc.generator

import audio.GlobalAudioSettings

object SquareGenerator extends GlobalAudioSettings {
  
  // returns individual samples
  private def getSample(x: Double, mod: Int) =  if ( (x % mod) > mod / 2 ) 1 else -1
  
  // returns the length of the wave in samples
  private def findCycleLen(freq: Int) = (sampleRate.toDouble / freq).toInt
  
  /*
   * @param freq -- frequency of the wave
 	 * Generates a single wave of given frequency
   */
  def generateWave(freq: Int): Array[Short] = {
    val output = Array.ofDim[Short](this.findCycleLen(freq))
    val mod = sampleRate / freq
    
    for ( x <- output.indices ) {
      output(x) = (this.getSample(x, mod)*maxVolume).toShort
    }
    
    return output
  }
}