package ui

import javax.swing._
import javax.swing.event._
import java.awt._
import java.awt.event._
import osc.core.OscillatorGroup
import modulation.AmplitudeMod
import modulation.PhaseMod

class OffsetSliderListener(val oscNum: Int) extends ChangeListener {
  
  val validOsc = if ( oscNum < 1 || oscNum > 3 ) false else true
  
  def stateChanged(e: ChangeEvent) = {
    val source = e.getSource.asInstanceOf[JSlider]
    
    if (!source.getValueIsAdjusting && validOsc) {
      val offsetValue = source.getValue
      OscillatorGroup.setOffset(oscNum, offsetValue)
    } 
  } 
}

class VolumeSliderListener(val oscNum: Int) extends ChangeListener {

  val validOsc = if ( oscNum < 1 || oscNum > 3 ) false else true
  
  def stateChanged(e: ChangeEvent) = {
    val source = e.getSource.asInstanceOf[JSlider]
    
    if (!source.getValueIsAdjusting && validOsc) {
      val volumeValue = source.getValue
      println("volume = " + volumeValue)
      OscillatorGroup.setVolume(oscNum, volumeValue)
    } 
  }
}

class ButtonListener(oscNum: Int) extends ActionListener {
  import osc.core.OscillatorType
  
  def actionPerformed(e: ActionEvent) = {
    val button = e.getSource.asInstanceOf[JButton]
    button.getName() match {
      case "sine"     => { OscillatorGroup.setType(oscNum, OscillatorType.Sine)
                           OscillatorGroup.updateOscillator(oscNum) }
      case "saw"      => { OscillatorGroup.setType(oscNum, OscillatorType.Saw)
                           OscillatorGroup.updateOscillator(oscNum) }
      case "square"   => { OscillatorGroup.setType(oscNum, OscillatorType.Square)
                           OscillatorGroup.updateOscillator(oscNum) }
      case "triangle" => { OscillatorGroup.setType(oscNum, OscillatorType.Triangle)
                           OscillatorGroup.updateOscillator(oscNum) }
      case "power"    => { OscillatorGroup.togglePower(oscNum)
                           if ( button.getText() == "[on] / off" )
                             button.setText("on / [off]")
                           else
                             button.setText("[on] / off")
                                }
    }
  }
}

object AMButtonListener extends ActionListener {
  import osc.core.OscillatorType
  
  def actionPerformed(e: ActionEvent) = {
    val button = e.getSource.asInstanceOf[JButton]
    button.getName() match {
      case "sine"     => AmplitudeMod.setType(OscillatorType.Sine)
      case "saw"      => AmplitudeMod.setType(OscillatorType.Saw)
      case "square"   => AmplitudeMod.setType(OscillatorType.Square)
      case "triangle" => AmplitudeMod.setType(OscillatorType.Triangle)
      case "power"    =>{
                         AmplitudeMod.togglePower()
                         if ( button.getText() == "[on] / off" )
                             button.setText("on / [off]")
                           else
                             button.setText("[on] / off")
      }
      
    }
  }
}

object PMButtonListener extends ActionListener {
  import osc.core.OscillatorType
  
  def actionPerformed(e: ActionEvent) = {
    val button = e.getSource.asInstanceOf[JButton]
    button.getName() match {
      case "sine"     => PhaseMod.setType(OscillatorType.Sine)
      case "saw"      => PhaseMod.setType(OscillatorType.Saw)
      case "square"   => PhaseMod.setType(OscillatorType.Square)
      case "triangle" => PhaseMod.setType(OscillatorType.Triangle)
      case "power"    =>{
                         PhaseMod.togglePower()
                         PhaseMod.printP()
                         if ( button.getText() == "[on] / off" )
                             button.setText("on / [off]")
                           else
                             button.setText("[on] / off")
      }
      
    }
  }
}

object KeyPressHandler extends AbstractAction {
  
  // keeps track of which keys are being pressed
  // if a key is pressed, multiple keypress events are fired which we dont want.
  var pressedKeys = Set[String]()

  def actionPerformed(e: ActionEvent) = {
    if (!pressedKeys.contains(e.getActionCommand)) {
      pressedKeys += e.getActionCommand
      e.getActionCommand match {
        case "a" => OscillatorGroup.setNoteNo(40)
        case "w" => OscillatorGroup.setNoteNo(41)
        case "s" => OscillatorGroup.setNoteNo(42)
        case "e" => OscillatorGroup.setNoteNo(43)
        case "d" => OscillatorGroup.setNoteNo(44)
        case "f" => OscillatorGroup.setNoteNo(45)
        case "t" => OscillatorGroup.setNoteNo(46)
        case "g" => OscillatorGroup.setNoteNo(47)
        case "y" => OscillatorGroup.setNoteNo(48)
        case "h" => OscillatorGroup.setNoteNo(49)
        case "u" => OscillatorGroup.setNoteNo(50)
        case "j" => OscillatorGroup.setNoteNo(51)
        case "k" => OscillatorGroup.setNoteNo(52)
        case "o" => OscillatorGroup.setNoteNo(53)
        case "l" => OscillatorGroup.setNoteNo(54)
        case _   => // do nothing
      }
    }
  }
  
  def resetCurrentKey(keyCmd: String) = pressedKeys -= keyCmd
  
}

object KeyReleaseHandler extends AbstractAction {
  def actionPerformed(e: ActionEvent) = {
    KeyPressHandler.resetCurrentKey(e.getActionCommand)
    if (KeyPressHandler.pressedKeys.isEmpty)
      OscillatorGroup.setMute()
  }
}