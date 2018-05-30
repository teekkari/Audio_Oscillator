package osc.core

import audio.GlobalAudioSettings
import audio.ByteConverter

object OscillatorGroup extends GlobalAudioSettings {
  
  var mute = false
  
  // 3 oscillators, sine by default
  private val oscillators = Array[Oscillator](new Oscillator(), new Oscillator(), new Oscillator())
  
  // Volume of the final output of the combined oscillator waves
  private var maxVol = 0.3
  
  // counter for the samples, resets when a note changes
  // is sent to the oscillators to generate the correct samples
  private var samplePosition = 0
  
  // Number of the note being played (on a standard piano) 37 == A3 == 220Hz
  // range is 1 - 88
  private var noteNo = 49
  
  private var noteChange = false
  
  // when a note changes, we store here the rest of the waveform, so it doesn't jump to straight 0
  // but rather smoothly gets back to 0 before switching waveforms
  private var noteChangeWaveform: Array[Byte] = Array[Byte](0, 0)
  
  def getNoteNo = noteNo
  
  // sets the note and updates the oscillators with corresponding frequencies
  def setNoteNo(x: Int) = {
    this.noteChange = true
    noteChangeWaveform = generateEndWave
    this.noteNo = 1 max x min 88
    this.updateFrequency()
    this.mute = false
  }
  
  // updates frequencies and waveforms of all oscillators
  def updateFrequency() = {
    //this.samplePosition = 0
    this.oscillators.foreach(_.updateFrequency())
    this.oscillators.foreach(_.updateWave())
  }
  
  // updates the frequency and waveform of a given oscillator.
  def updateOscillator(oscNum: Int) = {
    this.oscillators(oscNum - 1).updateFrequency()
    this.oscillators(oscNum - 1).updateWave()
  }
  
  // oscNum is from 1 to 3
  // sets the offset of given oscillator to "offset" arg
  def setOffset(oscNum: Int, offset: Int) = {
    this.oscillators(oscNum - 1).setOffset(offset)
    this.updateOscillator(oscNum)
  }
  
  // oscNum is from 1 to 3
  // sets the volume of given oscillator to "volume" arg
  def setVolume(oscNum: Int, vol: Double) = {
    this.oscillators(oscNum - 1).setVolume(vol)
  }
  
  // Changes the waveform of the given oscillator
  def setType(oscNum: Int, oscType: OscillatorType.Value) = {
    this.oscillators(oscNum - 1).setType(oscType)
  }
  
  // on / off switch
  def togglePower(oscNum: Int) = {
    this.oscillators(oscNum - 1).powerToggle = !this.oscillators(oscNum - 1).powerToggle
  }
 
  
  // fills the given byte array with audiodata from the oscillators.
  // returns the amount of bytes that were written to the array in the parameter
  def getSamples(bytes: Array[Byte]): Int = {
    
    // if a note has been changed, play the rest of the waveform before switching over
    if (noteChange) {
       for (i <- noteChangeWaveform.indices) {
         bytes(i) = noteChangeWaveform(i)
       }
       noteChange = false
       return noteChangeWaveform.length
    }
    
    // if note has been changed already, fill the buffer with audio normally.
    val length = bytes.length / 2
    
    for ( i <- 0 until bytes.length by 2 ) {
        val currentSample = ByteConverter.getByte(this.getSample(i/2))
        bytes(i)   = currentSample._1
        bytes(i+1) = currentSample._2
    }
    this.samplePosition += length
    return bytes.length
  }
  
  // Calculates the sample from all 3 oscillators and returns the summed sample with the correct amplitude (this.maxVol)
  private def getSample(x: Int): Short = {
    if (mute) return 0
    
    val currentSample = this.samplePosition + x
    var finalSample: Int = 0
    for ( osc <- this.oscillators ) {
      finalSample += osc.getSample(currentSample)
    }
    return (finalSample * this.maxVol).toShort
  }
  
  // generates the rest of the waveform, by cutting it.
  // it will look for a spot where the samples go from negative to positive and cut it there.
  // the volume (amplitude) of the waveform goes to 0 linearly by multiplying by (1 - i*step)
  private def generateEndWave(): Array[Byte] = {
    val size = 200
    val step = 1.0 / size
    val data = Array.ofDim[Short](size)
    for ( i <- data.indices ) {
      data(i) = (this.getSample(i)*(1 - i*step)).toShort
    }
    
    // here we look for a spot where the waveform goes to 0 from below and cut it there-- D(f(x)) > 0 and then 0
    var index = data.length
    for ( i <- 0 until data.length - 1 ) {
      if ( data(i) <= 0 && data(i + 1) >= 0) {
        index = i
      }
    }
    this.samplePosition = 0
    return ByteConverter.getByteArray(data.take(index)) // return a byte-array.
  }
  
}