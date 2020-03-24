package ui

import javax.swing._
import javax.swing.event._
import java.awt._
import java.awt.event._
import javafx.scene.input.KeyCode

import audio.GlobalAudioSettings
import osc.core.OscillatorGroup
import modulation.AmplitudeMod
import modulation.PhaseMod
import audio.AudioPlayer
import audio.AudioHandler
import audio.AudioState

object JavaxSwingLauncher extends App {
  AudioPlayer.playSound()
  val mainFrame = new JFrame("Audiosyntetisaattori -- Ohjelmointistudio 2")
  mainFrame.setLayout(new BoxLayout(mainFrame.getContentPane, BoxLayout.Y_AXIS))
  
  val tooltip1 = new JLabel("PRESS A - L KEYS TO TRIGGER NOTES")
  tooltip1.setFont(new Font("Helvetica", Font.BOLD, 20))
  tooltip1.setAlignmentX(Component.CENTER_ALIGNMENT)
  mainFrame.getContentPane.add(tooltip1)
  
  mainFrame.getContentPane.add(UIFunctions.createOscillatorUI(1))
  mainFrame.getContentPane.add(UIFunctions.createOscillatorUI(2))
  mainFrame.getContentPane.add(UIFunctions.createOscillatorUI(3))
  
  val tooltip2 = new JLabel("MODULATION")
  tooltip2.setFont(new Font("Helvetica", Font.BOLD, 20))
  tooltip2.setAlignmentX(Component.CENTER_ALIGNMENT)
  mainFrame.getContentPane.add(tooltip2)

  // MODULATION UI -- AM
  mainFrame.getContentPane.add({
    val amMod = new JPanel(new FlowLayout(FlowLayout.CENTER))

    val modLabel = new JLabel("AM")
    modLabel.setFont(new Font("Helvetica", Font.BOLD, 15))
    amMod.add(modLabel)

    // waveform buttons
    val sineButton = new JButton("sine")
    val sawButton = new JButton("saw")
    val squareButton = new JButton("square")
    val triangleButton = new JButton("triangle")
    val powerButton = new JButton("on / [off]")

    sineButton.setName("sine")
    sawButton.setName("saw")
    squareButton.setName("square")
    triangleButton.setName("triangle")
    powerButton.setName("power")

    sineButton.addActionListener(AMButtonListener)
    sawButton.addActionListener(AMButtonListener)
    squareButton.addActionListener(AMButtonListener)
    triangleButton.addActionListener(AMButtonListener)
    powerButton.addActionListener(AMButtonListener)

    amMod.add(sineButton)
    amMod.add(sawButton)
    amMod.add(squareButton)
    amMod.add(triangleButton)

    // ratio slider for the modulator frequency
    val ratioAmount = new JSlider(1, 4, 1)
    ratioAmount.setMajorTickSpacing(1)
    ratioAmount.setPaintTicks(true)
    ratioAmount.setPaintLabels(true)
    ratioAmount.addChangeListener(new ChangeListener {
      def stateChanged(e: ChangeEvent) = {
        val source = e.getSource.asInstanceOf[JSlider]
        if (!source.getValueIsAdjusting) {
          AmplitudeMod.setRatio(source.getValue)
        }
      }
    })
    amMod.add(ratioAmount)

    // slider to select how much the modulation affects the sound
    val fxAmount = new JSlider(0, 10, 10)
    fxAmount.setMajorTickSpacing(5)
    fxAmount.setMinorTickSpacing(1)
    fxAmount.setPaintTicks(true)
    fxAmount.setPaintLabels(true)
    fxAmount.addChangeListener(new ChangeListener {
      def stateChanged(e: ChangeEvent) = {
        val slider = e.getSource.asInstanceOf[JSlider]
        AmplitudeMod.setAmount(slider.getValue.toDouble / 10)
      }
    })
    amMod.add(fxAmount)
    amMod.add(powerButton)
    amMod
  })
  //mainFrame.getContentPane.add({amMod})
  // MODULATION -- AM END
  
  // MODULATION -- PM START
  mainFrame.getContentPane.add({
    val pmMod = new JPanel(new FlowLayout(FlowLayout.CENTER))

    val modLabel = new JLabel("PM")
    modLabel.setFont(new Font("Helvetica", Font.BOLD, 15))
    pmMod.add(modLabel)

    // waveform buttons
    val sineButton = new JButton("sine")
    val sawButton = new JButton("saw")
    val squareButton = new JButton("square")
    val triangleButton = new JButton("triangle")
    val powerButton = new JButton("on / [off]")

    sineButton.setName("sine")
    sawButton.setName("saw")
    squareButton.setName("square")
    triangleButton.setName("triangle")
    powerButton.setName("power")

    sineButton.addActionListener(PMButtonListener)
    sawButton.addActionListener(PMButtonListener)
    squareButton.addActionListener(PMButtonListener)
    triangleButton.addActionListener(PMButtonListener)
    powerButton.addActionListener(PMButtonListener)

    pmMod.add(sineButton)
    pmMod.add(sawButton)
    pmMod.add(squareButton)
    pmMod.add(triangleButton)

    // ratio slider for the modulator frequency
    val ratioAmount = new JSlider(1, 4, 1)
    ratioAmount.setMajorTickSpacing(1)
    ratioAmount.setPaintTicks(true)
    ratioAmount.setPaintLabels(true)
    ratioAmount.addChangeListener(new ChangeListener {
      def stateChanged(e: ChangeEvent) = {
        val source = e.getSource.asInstanceOf[JSlider]
        if (!source.getValueIsAdjusting) {
          PhaseMod.setRatio(source.getValue)
        }
      }
    })
    pmMod.add(ratioAmount)

    // slider to select how much the modulation affects the sound
    val fxAmount = new JSlider(0, 10, 10)
    fxAmount.setMajorTickSpacing(5)
    fxAmount.setMinorTickSpacing(1)
    fxAmount.setPaintTicks(true)
    fxAmount.setPaintLabels(true)
    fxAmount.addChangeListener(new ChangeListener {
      def stateChanged(e: ChangeEvent) = {
        val slider = e.getSource.asInstanceOf[JSlider]
        PhaseMod.setAmount(slider.getValue.toDouble / 10)
      }
    })
    pmMod.add(fxAmount)
    pmMod.add(powerButton)
    pmMod
  })
  // MODULATION -- PM END
  
  
  // HANDLES KEYBINGINDS
  val inputmap = mainFrame.getRootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
  val actionmap = mainFrame.getRootPane.getActionMap()
    
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "a")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "w")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), "s")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, 0, false), "e")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "d")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, 0, false), "f")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, 0, false), "t")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_G, 0, false), "g")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, 0, false), "y")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, 0, false), "h")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_U, 0, false), "u")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_J, 0, false), "j")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_K, 0, false), "k")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, 0, false), "o")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_L, 0, false), "l")
  
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), "releaseA")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, true), "releaseW")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), "releaseS")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, 0, true), "releaseE")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), "releaseD")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, 0, true), "releaseF")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, 0, true), "releaseT")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_G, 0, true), "releaseG")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, 0, true), "releaseY")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, 0, true), "releaseH")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_U, 0, true), "releaseU")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_J, 0, true), "releaseJ")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_K, 0, true), "releaseK")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, 0, true), "releaseO")
  inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_L, 0, true), "releaseL")

  
  actionmap.put("a", KeyPressHandler)
  actionmap.put("w", KeyPressHandler)
  actionmap.put("s", KeyPressHandler)
  actionmap.put("e", KeyPressHandler)
  actionmap.put("d", KeyPressHandler)
  actionmap.put("f", KeyPressHandler)
  actionmap.put("t", KeyPressHandler)
  actionmap.put("g", KeyPressHandler)
  actionmap.put("y", KeyPressHandler)
  actionmap.put("h", KeyPressHandler)
  actionmap.put("u", KeyPressHandler)
  actionmap.put("j", KeyPressHandler)
  actionmap.put("k", KeyPressHandler)
  actionmap.put("o", KeyPressHandler)
  actionmap.put("l", KeyPressHandler)
  actionmap.put("releaseA", KeyReleaseHandler)
  actionmap.put("releaseW", KeyReleaseHandler)
  actionmap.put("releaseS", KeyReleaseHandler)
  actionmap.put("releaseE", KeyReleaseHandler)
  actionmap.put("releaseD", KeyReleaseHandler)
  actionmap.put("releaseF", KeyReleaseHandler)
  actionmap.put("releaseT", KeyReleaseHandler)
  actionmap.put("releaseG", KeyReleaseHandler)
  actionmap.put("releaseY", KeyReleaseHandler)
  actionmap.put("releaseH", KeyReleaseHandler)
  actionmap.put("releaseU", KeyReleaseHandler)
  actionmap.put("releaseJ", KeyReleaseHandler)
  actionmap.put("releaseK", KeyReleaseHandler)
  actionmap.put("releaseO", KeyReleaseHandler)
  actionmap.put("releaseL", KeyReleaseHandler)
  
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
