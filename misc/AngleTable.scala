package misc

object AngleTable {
  val precision = 50
  val angles    = 360
  val mod       = precision * angles
  
  val sinTable = Array.ofDim[Double](precision * angles)
  val cosTable = Array.ofDim[Double](precision * angles)
  
  for ( x <- sinTable.indices ) {
    sinTable(x) = Math.sin(Math.toRadians(x.toDouble/precision))
    cosTable(x) = Math.cos(Math.toRadians(x.toDouble/precision))
  }
  
  // Argument x is in degrees
  def getSinDeg(x: Double) = sinTable( (x * precision).toInt % mod)
  // Argument x is in radians
  def getSinRad(x: Double) = sinTable( (Math.toDegrees(x) * precision).toInt % mod)
  
  def getCosDeg(x: Double) = cosTable( (x * precision).toInt % mod)
  def getCosRad(x: Double) = cosTable( (Math.toDegrees(x) * precision).toInt % mod)
}