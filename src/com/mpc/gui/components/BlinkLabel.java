package com.mpc.gui.components;

import java.awt.Color;
import java.awt.event.*;

import javax.swing.*;

import com.mpc.gui.Bootstrap;
 
public class BlinkLabel extends JLabel {
  private static final long serialVersionUID = 1L;
  
  private static final int BLINKING_RATE = 500; // in ms

  private boolean blinkingOn = true;
  
  public BlinkLabel(String text) {
    super(text);
    Timer timer = new Timer( BLINKING_RATE , new TimerListener(this));
    timer.setInitialDelay(0);
    timer.start();
  }
  
  public void setBlinking(boolean flag) {
    this.blinkingOn = flag;
  }
  public boolean getBlinking() {
    return this.blinkingOn;
  }

  
  private class TimerListener implements ActionListener {
    private BlinkLabel bl;
    private Color bg;
    private Color fg;
    private boolean isForeground = true;
    
    public TimerListener(BlinkLabel bl) {
      this.bl = bl;
      fg = bl.getForeground();
      bg = bl.getBackground();
      bg = Bootstrap.lcdOff;
      fg = Bootstrap.lcdOn;
    }
 
    public void actionPerformed(ActionEvent e) {
      if (bl.blinkingOn) {
        if (isForeground) {
          bl.setForeground(fg);
        }
        else {
          bl.setForeground(bg);
        }
        isForeground = !isForeground;
      }
      else {
        // here we want to make sure that the label is visible
        // if the blinking is off.
        if (isForeground) {
          bl.setForeground(fg);
          isForeground = false;
        }
      }
    }
    
  }
 
  // --- for testing 
  private static void createAndShowUI() {
    JFrame frame = new JFrame("BlinkLabel");
    final BlinkLabel bl = new BlinkLabel("I'm blinking!");
    
    frame.getContentPane().setLayout(new java.awt.FlowLayout());
    frame.getContentPane().add(bl);
    
    JButton b = new JButton("toogle blink");
    b.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent ae) {
            bl.blinkingOn = !bl.blinkingOn;
          }
        });
    frame.getContentPane().add(b);
    
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

  }
 
  public static void main(String[] args)  {
    java.awt.EventQueue.invokeLater(new Runnable(){
      public void run(){
        createAndShowUI();
      }
    });
  }
  // ---
}