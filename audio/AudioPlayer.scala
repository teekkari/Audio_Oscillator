package audio

import javax.sound.sampled._
import osc.core.OscillatorGroup
import java.io.{ ByteArrayInputStream, InputStream }

/*
 * AudioPlayer handles the audio being played by the synth
 * I have taken some inspiration from the earlier Ohjelmointistudio 1 project Sound_2017 to play the sounds
 */
object AudioPlayer extends GlobalAudioSettings {
  //private var dataSource = osc.core.OscillatorGroup
  private var playing = false

  val buffersize = 2000
  private val audioBytes = Array.ofDim[Byte](buffersize)

  // Correct formatting for our audio
  val audioFormat: AudioFormat = new AudioFormat(sampleRate, bitDepth, audioChannels, signed, bigEndian)
  val dataLineInfo: DataLine.Info = new DataLine.Info(classOf[SourceDataLine], audioFormat)

  /*
   * Plays sound until stop is called
   * Utilizes threading to play in the background
   */
  def playSound() = {
    val soundplayer = new Thread {

      override def run {
        playing = true
        // get a data line to which audio data can be written using our needed format
        val sourceDataLine = AudioSystem.getLine(dataLineInfo).asInstanceOf[SourceDataLine]
        try {
          // open the line and start it
          sourceDataLine.open(audioFormat, buffersize)
          sourceDataLine.start()

          var bytesRead = 0

          while (bytesRead != -1 && playing) {
            bytesRead = OscillatorGroup.getSamples(audioBytes)
            if (bytesRead > 0) {
              sourceDataLine.write(audioBytes, 0, bytesRead)
            }
          }

        } catch {

          case e: Exception => e.printStackTrace()

        } finally {
          sourceDataLine.drain()
          sourceDataLine.close()
        }
      }
    }
    if (!soundplayer.isAlive)
      soundplayer.start()
  }

  def stopSound() = playing = false
}