package ui

import javax.swing._
import javax.swing.event._
import java.awt._
import java.awt.event._
import javafx.scene.input.KeyCode

import audio.GlobalAudioSettings
import osc.core.OscillatorGroup
import audio.AudioPlayer

object JavaxSwingLauncher extends App {
  OscillatorGroup.mute = false
  AudioPlayer.playSound()
  val mainFrame = new JFrame("Audiosyntetisaattori -- Ohjelmointistudio 2")
  mainFrame.setLayout(new BoxLayout(mainFrame.getContentPane, BoxLayout.Y_AXIS))
  val tooltip = new JLabel("PRESS A - L KEYS TO TRIGGER NOTES")
  tooltip.setFont(new Font("Helvetica", Font.BOLD, 20))
  tooltip.setAlignmentX(Component.CENTER_ALIGNMENT)
  mainFrame.getContentPane.add(tooltip)
  mainFrame.getContentPane.add(UIFunctions.createOscillatorUI(1))
  mainFrame.getContentPane.add(UIFunctions.createOscillatorUI(2))
  mainFrame.getContentPane.add(UIFunctions.createOscillatorUI(3))
  
  
  // HANDLES KEYBINGINDS
  val inputmap = mainFrame.getRootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
  val actionmap = mainFrame.getRootPane.getActionMap()
  
  inputmap.put(KeyStroke.getKeyStroke('a'), "a")
  inputmap.put(KeyStroke.getKeyStroke('s'), "s")
  inputmap.put(KeyStroke.getKeyStroke('d'), "d")
  inputmap.put(KeyStroke.getKeyStroke('f'), "f")
  inputmap.put(KeyStroke.getKeyStroke('g'), "g")
  inputmap.put(KeyStroke.getKeyStroke('h'), "h")
  inputmap.put(KeyStroke.getKeyStroke('j'), "j")
  inputmap.put(KeyStroke.getKeyStroke('k'), "k")
  inputmap.put(KeyStroke.getKeyStroke('l'), "l")
 
  
  val keys = new KeyHandler
  actionmap.put("a", keys)
  actionmap.put("s", keys)
  actionmap.put("d", keys)
  actionmap.put("f", keys)
  actionmap.put("g", keys)
  actionmap.put("h", keys)
  actionmap.put("j", keys)
  actionmap.put("k", keys)
  actionmap.put("l", keys)
  
  // Prepare and show GUI
  mainFrame.pack()
  mainFrame.setResizable(false)
  mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  mainFrame.setVisible(true)
  
}

object UIFunctions extends GlobalAudioSettings {
  def createOscillatorUI(num: Int): JPanel = {
    val oscUI = new JPanel(new FlowLayout)
    
    // waveform buttons
    val sineButton = new JButton("sine")
    val sawButton = new JButton("saw")
    val squareButton = new JButton("square")
    val triangleButton = new JButton("triangle")
    
    sineButton.setName("sine")
    sawButton.setName("saw")
    squareButton.setName("square")
    triangleButton.setName("triangle")
    
    val buttonListener = new ButtonListener(num)
    sineButton.addActionListener(buttonListener)
    sawButton.addActionListener(buttonListener)
    squareButton.addActionListener(buttonListener)
    triangleButton.addActionListener(buttonListener)
    
    oscUI.add(sineButton)
    oscUI.add(sawButton)
    oscUI.add(squareButton)
    oscUI.add(triangleButton)
    
    
    // add slider for the oscillator offsets
    val offsetSlider = new JSlider(OffsetMin, OffsetMax, 0)
    offsetSlider.setMajorTickSpacing(1)
    offsetSlider.setPaintTicks(true)
    offsetSlider.setPaintLabels(true)
    offsetSlider.addChangeListener(new OffsetSliderListener(num))
    oscUI.add(offsetSlider)
    
    // volume slider
    val volumeSlider = new JSlider(0, 10, 5)
    volumeSlider.setMajorTickSpacing(5)
    volumeSlider.setMinorTickSpacing(1)
    volumeSlider.setPaintTicks(true)
    volumeSlider.setPaintLabels(true)
    volumeSlider.addChangeListener(new VolumeSliderListener(num))
    oscUI.add(volumeSlider)
    
    val volLabel = new JLabel("<== volume")
    oscUI.add(volLabel)
    
    // osc on/off button
    val powerButton = new JButton("[on] / off")
    powerButton.setName("power")
    powerButton.addActionListener(buttonListener)
    oscUI.add(powerButton)
    
    return oscUI
  }
}

class OffsetSliderListener(val oscNum: Int) extends ChangeListener {
  
  val validOsc = if ( oscNum < 1 || oscNum > 3 ) false else true
  
  def stateChanged(e: ChangeEvent) = {
    val source = e.getSource.asInstanceOf[JSlider]
    
    if (!source.getValueIsAdjusting && validOsc) {
      val offsetValue = source.getValue
      println("offset = " + offsetValue)
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

class KeyHandler extends AbstractAction {
  
  // keeps track of which key was pressed last
  // if a key is pressed, multiple keypress events are fired which we dont want.
  var currentKey: Char = '0' // 0 as a placeholder

  def actionPerformed(e: ActionEvent) = {
    e.getActionCommand match {
      case "a" => {
        if (currentKey != 'a') {
          OscillatorGroup.setNoteNo(40)
          currentKey = 'a'
        }
      }
      case "s" => {
        if (currentKey != 's') {
          OscillatorGroup.setNoteNo(42)
          currentKey = 's'
        }
      }
      case "d" => {
        if (currentKey != 'd') {
          OscillatorGroup.setNoteNo(44)
          currentKey = 'd'
        }
      }
      case "f" => {
        if (currentKey != 'f') {
          OscillatorGroup.setNoteNo(45)
          currentKey = 'f'
        }
      }
      case "g" => {
        if (currentKey != 'g') {
          OscillatorGroup.setNoteNo(47)
          currentKey = 'g'
        }
      }
      case "h" => {
        if (currentKey != 'h') {
          OscillatorGroup.setNoteNo(49)
          currentKey = 'h'
        }
      }
      case "j" => {
        if (currentKey != 'j') {
          OscillatorGroup.setNoteNo(51)
          currentKey = 'j'
        }
      }
      case "k" => {
        if (currentKey != 'k') {
          OscillatorGroup.setNoteNo(52)
          currentKey = 'k'
        }
      }
      case "l" => {
        if (currentKey != 'l') {
          OscillatorGroup.setNoteNo(54)
          currentKey = 'l'
        }
      }
      case _ => { println("Something went wrong with the keyboard.") } // should never happen
    }
  }
}