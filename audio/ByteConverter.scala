package audio

import scala.collection.mutable.ArrayBuffer

object ByteConverter extends GlobalAudioSettings {
  
  // Converts a given Short array (16-bit numbers) to an array of bytes (little-Endian)
  def getByteArray(data: Array[Short]): Array[Byte] = {
    val output = ArrayBuffer[Byte]()
    for ( x <- data ) {
      val bytes = this.getByte(x)
      output += bytes._1
      output += bytes._2
    }
    return output.toArray
  }
  
  def getByte(x: Short): (Byte, Byte) = {
    val first = ( x & 0xff ).asInstanceOf[Byte]
    val second = ( x >> 8 ).asInstanceOf[Byte]
    return (first, second)
  }
}