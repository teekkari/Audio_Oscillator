package audio

/*
 * This trait enables any class or object to work as a "AudioSource"
 * which means that AudioHandler object can request audio data from said class or object
 */
trait AudioSource {
  def getSamples(data: Array[Short]): Int
}