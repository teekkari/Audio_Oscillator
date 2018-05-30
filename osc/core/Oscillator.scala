package osc.core

import audio.GlobalAudioSettings
import osc.generator._
import misc._

/*
 * Oscillator class which can generate different wavefunctions with a set frequency
 */
class Oscillator(_waveType: OscillatorType.Value = OscillatorType.Sine) extends GlobalAudioSettings {
  
  // Frequency of the wave function to be generated
  private var frequency = 440
    
  // how far the oscillator's note is from the oscillatorgroup's current note.
  private var offset = 0
  
  // Type of the wave function (sine, saw, square, triangle)
  private var waveType  = _waveType
  
  // Output volume of the oscillator
  private var volume = 1.0
  
  // Is the oscillator on / off [ true / false ]
  var powerToggle = true 
  
  private var currentWave = this.generateWave
  
  // Limit the frequency to a range of 40 to SR / 2 to eliminate nyquist distortion (samplerate 24k)
  def setFrequency(x: Int) = this.frequency = 40 max x min (sampleRate / 2)
  
  def setOffset(x: Int) = offset = x
  
  // volume is logarithmic, so we make it grow exponentially to make it seem "linear"
  def setVolume(x: Double) = volume = Math.pow(x / 10, 2)
  
  // the frequency is calculated from the note number with this algorithm
  def updateFrequency() = {
    frequency = (Math.pow(2, (OscillatorGroup.getNoteNo + offset - 49).toDouble / 12) * 440).toInt
  }
  
  // Set (change) the oscillator type (sin, square, wave)
  def setType(x: OscillatorType.Value) = waveType = x
  
  // when frequency or osc type changes, update the internal wave used to get samples
  def updateWave() = {
    currentWave = this.generateWave
  }
  
  
  /*
   * Generates a wave using an oscillator defined by this.waveType
   * 
   */
  def generateWave: Array[Short] = {
    waveType match {
      case OscillatorType.Sine     => return SineGenerator.generateWave(frequency)
      case OscillatorType.Saw      => return SawGenerator.generateWave(frequency)
      case OscillatorType.Square   => return SquareGenerator.generateWave(frequency)
      case OscillatorType.Triangle => return TriangleGenerator.generateWave(frequency)
      case _                       => throw new Exception("Invalid oscillator") // should never be thrown?
    }
  }
  
  def getSample(sample: Int): Short = {
    if ( this.powerToggle == false ) // is the osc off? return 0
      0
    else // else return correct sample
      (currentWave( sample % currentWave.length ) * this.volume).toShort
  }
  
  
}