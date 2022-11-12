package ticketmaster.display;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

import ticketmaster.Admin;
import ticketmaster.Database;
import ticketmaster.Log;

/**
 * 
 * @author Robert J Alvarez
 * @date September 18th, 2022
 */
public class Viewer implements ActionListener {
  private static JFrame frame = null;
  private static JRadioButton radioAdmin;
  private static JRadioButton radioUser;

  private static void makeInvisible() {
		frame.setVisible(false);
	}

  public static void makeVisible() {
    frame.setVisible(true);
  }

  public static void logUserOrAdmin() {
    if (frame != null) {
      makeVisible();
      return;
    }

    frame = new JFrame("Log in options");
    frame.setPreferredSize(new Dimension(300,140));
    frame.setLayout(null);
    frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

    JLabel label = new JLabel("System options:");
    label.setBounds(20,10,150,20);
    frame.add(label);

    radioAdmin = new JRadioButton("Admin");
    radioAdmin.setBounds(50,30,90,30);
    frame.add(radioAdmin);

    radioUser = new JRadioButton("User");
    radioUser.setBounds(150,30,100,30);
    frame.add(radioUser);

    ButtonGroup groupButton = new ButtonGroup();
    groupButton.add(radioAdmin);
    groupButton.add(radioUser);

    JButton loginButton = new JButton("Click");
    loginButton.setBounds(70,60,70,30);
    loginButton.addActionListener(new Viewer());
    frame.add(loginButton);

    JButton buttonExit = new JButton("Exit");
		buttonExit.setBounds(150, 60, 70, 30);
		buttonExit.addActionListener(new Viewer());
		frame.add(buttonExit);

    frame.pack();
    makeVisible();
  }

  @Override
	public void actionPerformed(ActionEvent e) {
    String s = e.getActionCommand();

    if (s.equals("Exit")) {
      Database.closeProgram();
      System.exit(0);
    } else if (radioAdmin.isSelected()) {
      Log.logWrite(Level.FINE,"Log as administrator.");
      Viewer.makeInvisible();
      Admin.logged();
      //JOptionPane.showMessageDialog(frame, "I'm currently working on admin options.");
    } else if (radioUser.isSelected()) {
      makeInvisible();
      LogUser.logUser();
    } else {
      JOptionPane.showMessageDialog(frame, "Select one of the two given options or exit the program.");
    }
  }
}
