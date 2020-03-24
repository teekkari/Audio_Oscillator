package modulation

import audio.GlobalAudioSettings
import audio.AudioSource
import osc.core.OscillatorGroup
import osc.core.OscillatorType
import osc.generator._

object AmplitudeMod extends GlobalAudioSettings with AudioSource {
  // is the module on / off [true / false]
  var powerToggle = false
  
  var frequency = 440
  
  // percentage on how much modulation is applied
  private var fxAmount = 1.0
  def setAmount(x: Double) = if (x >= 0 && x <= 1.0) fxAmount = x
  
  private var fxRatio = 1.0
  def setRatio(x: Double) = if (x > 0) fxRatio = x
  
  // holds the wave used to modulate the current playing audio
  private var modWave: Array[Short] = Array[Short](1)
  private var waveType = OscillatorType.Sine
  private var samplePosition = 0
  
  def setType(x: OscillatorType.Value) = {
    this.waveType = x
    this.updateModWave()
  }
  
  private def getWave(oscType: OscillatorType.Value, freq: Int): Array[Short] = {
    oscType match {
      case OscillatorType.Sine     => return SineGenerator.generateWave(freq)
      case OscillatorType.Saw      => return SawGenerator.generateWave(freq)
      case OscillatorType.Square   => return SquareGenerator.generateWave(freq)
      case OscillatorType.Triangle => return TriangleGenerator.generateWave(freq)
    }
  }
  
  def updateModWave() = {
    val newFrequency = (this.fxRatio * Math.pow(2, (OscillatorGroup.getNoteNo - 49).toDouble / 12) * 440).toInt
    this.frequency = newFrequency
    this.modWave = getWave(this.waveType, newFrequency)
    this.samplePosition = 0
  }
  
  def getSamples(data: Array[Short]): Int = {
    val sampleLength = OscillatorGroup.getSamples(data)
    this.applyModulation(data)
    return sampleLength
  }
  
  def applyModulation(data: Array[Short]): Int = {
    // if the modulator isnt on we do nothing and return negative
    if (!powerToggle) return -1
    
    for (i <- data.indices) {
      val modulator = modWave(this.samplePosition % modWave.length).toDouble / maxVolume
      data(i) = (data(i) - data(i)*(1 - modulator)*this.fxAmount).toShort
      samplePosition += 1
    }
    return data.length
  }
  
  
  def togglePower() = powerToggle = !powerToggle
}