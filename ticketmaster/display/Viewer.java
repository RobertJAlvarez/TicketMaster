package ticketmaster.display;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

import ticketmaster.Database;
import ticketmaster.Log;

public class Viewer implements ActionListener {
  private static JFrame frame = null;
  private static JRadioButton radioAdmin;
  private static JRadioButton radioUser;

  protected static void makeVisible() {
    frame.setVisible(true);
  }

  public static void logUserOrAdmin() {
    if (frame != null) {
      makeVisible();
      return;
    }

    frame = new JFrame("Log in options");
    frame.setSize(300,300);
    frame.setLayout(null);
    frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

    JLabel label = new JLabel("System options:");
    label.setBounds(20,30,100,50);
    frame.add(label);

    radioAdmin = new JRadioButton("Admin");
    radioAdmin.setBounds(100,50,100,30);
    frame.add(radioAdmin);

    radioUser = new JRadioButton("User");
    radioUser.setBounds(100,50,100,30);
    frame.add(radioUser);

    ButtonGroup groupButton = new ButtonGroup();
    groupButton.add(radioAdmin);
    groupButton.add(radioUser);

    JButton loginButton = new JButton("click");
    loginButton.setBounds(100,150,80,30);
    loginButton.addActionListener(new Viewer());
    frame.add(loginButton);

    JButton buttonExit = new JButton("Exit");
		buttonExit.setBounds(180, 80, 70, 20);
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
      JOptionPane.showMessageDialog(frame, "I'm currently working on admin options.");
    } else if (radioUser.isSelected()) {
      LogUser.logUser();
    } else {
      JOptionPane.showMessageDialog(frame, "Select one of the two given options or exit the program.");
    }
  }
}
