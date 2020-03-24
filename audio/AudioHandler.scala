package audio

import modulation.PhaseMod
import modulation.AmplitudeMod
import osc.core.OscillatorGroup

object AudioHandler {
  
  // keeps track of which state is currently active
  // starts at Attack when no notes are playing
  private var currentState: AudioState.Value = AudioState.Mute
  
  def setState(state: AudioState.Value) = currentState = state
  
  // where the audio data to be played is requested
  private var audioSource: AudioSource = AmplitudeMod
  
  def getSamples(bytes: Array[Byte]): Int = {
    val audioData = Array.ofDim[Short](bytes.length / 2)
    val dataLength = audioSource.getSamples(audioData)
    
    val step = 1.0 / dataLength
    
    for (i <- 0 until dataLength) {
      val sample = currentState match {
        case AudioState.Attack  => ByteConverter.getByte((audioData(i) * step * i).toShort)
        case AudioState.Sustain => ByteConverter.getByte(audioData(i))
        case AudioState.Release => ByteConverter.getByte((audioData(i) * (1 - step * i)).toShort)
        case AudioState.Switch  => ByteConverter.getByte((audioData(i) * (1 - step * i)).toShort)
        case AudioState.Mute    => ByteConverter.getByte(0)
      }
      bytes(2 * i)     = sample._1
      bytes(2 * i + 1) = sample._2
    }
    
    currentState match {
      case AudioState.Attack  => this.setState(AudioState.Sustain)
      case AudioState.Release => this.setState(AudioState.Mute)
      case AudioState.Switch  => this.setState(AudioState.Attack)
      case _                  => // if mute or sustain, do nothing
    }
    
    return dataLength*2
  }
  
}