package osc.core

import audio.GlobalAudioSettings
import audio.ByteConverter
import audio.AudioSource
import audio.AudioHandler
import audio.AudioState
import modulation.AmplitudeMod
import modulation.PhaseMod

object OscillatorGroup extends GlobalAudioSettings with AudioSource {
  
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
  
  private var notePlaying = false
  private var noteChange = false
  
  private var soundBuffer = Array.ofDim[Short](bufferSize / 2)
  
  def getNoteNo = noteNo
  
  // sets the note and updates the oscillators with corresponding frequencies
  def setNoteNo(x: Int) = {
    if (notePlaying) {
      this.getSamples(soundBuffer)
      noteChange = true
    } else {
      this.noteNo = 1 max x min 88
      AmplitudeMod.updateModWave()
      PhaseMod.updateModWave()
    }
    
    this.noteNo = 1 max x min 88
    this.updateFrequency()
    this.notePlaying = true
    this.samplePosition = 0
    AudioHandler.setState(AudioState.Attack)
  }
  
  def setMute() = {
    this.notePlaying = false
    AudioHandler.setState(AudioState.Release)
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
  // returns the amount of samples that were written to the array in the parameter
  def getSamples(data: Array[Short]): Int = {
    
    // if we change notes, it cant just abruptly change the waveform. Got to send one buffer to release the note first.
    if (this.noteChange) {
      AudioHandler.setState(AudioState.Switch)
      for ( i <- data.indices )
        data(i) = this.soundBuffer(i)
        
      this.noteChange = false
      AmplitudeMod.updateModWave()
      PhaseMod.updateModWave()
      return data.length
    }
    
    // if there is no note-switch, just fill the buffer normally
    val length = data.length

    for (i <- data.indices) {
      val currentSample = this.getSample(i)
      data(i) = currentSample
    }
    
    this.samplePosition += length
    return data.length
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
  
}