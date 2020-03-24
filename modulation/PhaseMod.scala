package modulation

import audio.GlobalAudioSettings
import audio.AudioSource
import osc.core.OscillatorType
import osc.core.OscillatorGroup
import osc.generator._

object PhaseMod extends GlobalAudioSettings with AudioSource {
  
  // is the mod on / off [true / false]
  private var power = false
  def togglePower() = power = !power
  def printP() = println(power)
  
  // the mod waveform is [-1, 1] so we scale it up by this factor (max 2000 == bufferSize)
  private var fxAmount = 1000
  def setAmount(x: Double) = fxAmount = (x * 1000).toInt
  
  private var freqRatio = 1.0
  def setRatio(x: Double) = if (x > 0) freqRatio = x

  // holds the wave used to modulate the current playing audio
  // the elements are all in the range [-1, 1]
  private var modWave: Array[Double] = Array[Double](0)
  private var waveType = OscillatorType.Sine
  private var samplePosition = 0
  
  def setType(x: OscillatorType.Value) = waveType = x
  
  // generates mod waves
  private def getWave(oscType: OscillatorType.Value, freq: Int): Array[Short] = {
    oscType match {
      case OscillatorType.Sine     => return SineGenerator.generateWave(freq)
      case OscillatorType.Saw      => return SawGenerator.generateWave(freq)
      case OscillatorType.Square   => return SquareGenerator.generateWave(freq)
      case OscillatorType.Triangle => return TriangleGenerator.generateWave(freq)
    }
  }
  
  def updateModWave() = {
    val newFrequency = (Math.pow(2, (OscillatorGroup.getNoteNo - 49).toDouble / 12) * 440 * freqRatio).toInt
    this.modWave = getWave(this.waveType, newFrequency).map(f => f.toDouble / this.maxVolume)
    this.samplePosition = 0
    this.bufferReset()
  }
  
  private var nextWave: Array[Short] = Array[Short](0)
  private var nextWaveFlag = false // false => nextWave needs to be populated, true => nextWave has a waveform
  def bufferReset() = nextWaveFlag = false
  
  
  // TODO: ONGELMA NUOTIN VAIHDOSSA (BUFFERI EI MUUTU HETI!!)!!!!!
  // ÄLÄ KÄYTÄ NULL, KÄYTÄ BOOL FLAG
  def getSamples(data: Array[Short]): Int = {
    
    AmplitudeMod.getSamples(data)
    return data.length
    
    // we set up "data" and "nextWave" variables, used in modulation    
    if (nextWaveFlag) {
      for (i <- data.indices)
        data(i) = nextWave(i)
      AmplitudeMod.getSamples(nextWave)
    } else {
      nextWave = Array.ofDim[Short](data.length)
      AmplitudeMod.getSamples(data)
      AmplitudeMod.getSamples(nextWave)
      nextWaveFlag = true
    }
    
    //this.applyModulation(data)
    
    return data.length
  }
  
  
  def applyModulation(data: Array[Short]): Int = {
    // if the mod is off return -1 and do nothing
    if (!power) return -1
    
    val original = data.clone
    // process the audiodata
    for (i <- data.indices) {
      val interval = Math.abs(i +(this.modWave(i % modWave.length) * this.fxAmount)).toInt
      if ( interval < original.length )
        data(i) = original(interval)
      else
        data(i) = nextWave(interval - original.length)
    }
    
    return data.length
  }
  
}
