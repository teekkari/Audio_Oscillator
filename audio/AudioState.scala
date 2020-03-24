package audio

/* 
 * Every time a note is played, the sound goes through these audiostates
 * AudioHandler utilizes these states when sending audiodata to AudioPlayer
 * Attack  => how long until the volume (amplitude) is at max
 * Sustain => at what amplitude is the wave played
 * Release => how long until the volume (amplitude) is at zero
 * 
 * Switch is used for notes changing without a pause inbetween
 * Mute just tells us not to play any sound.
 */

object AudioState extends Enumeration with GlobalAudioSettings {
  val Attack, Sustain, Release, Mute, Switch = Value
}