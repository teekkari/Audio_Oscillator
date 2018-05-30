package audio

trait GlobalAudioSettings {
  val sampleRate = 24000
  val msSampleRate = sampleRate / 1000
  val bitDepth = 16
  val audioChannels = 1
  val signed = true
  val bigEndian = false
  val maxVolume = Math.pow(2, 15) - 1
  
  val OffsetMin = -5
  val OffsetMax = 5
}