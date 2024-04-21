package ticketmaster.display;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import ticketmaster.Customer;
import ticketmaster.Database;
import ticketmaster.Log;
import ticketmaster.User;

/**
 * This class make a pop up window asking for the username and password fields and two buttons options, "Login" and "Close".
 * If the correct username an password are given when Login button is press the userLogged method in User is call and the
 * user state machine menu/interaction is started.
 * 
 * @author Robert J Alvarez
 * @date November 8th, 2022
 */
public class LogUser implements ActionListener {
	private static JTextField username;
	private static JPasswordField password;
	private static JLabel message;
	private static JFrame frame = null;
	private static final int MAXLOGINTRIES = 3;
	private int nTriesLeft = MAXLOGINTRIES;

	private static void makeInvisible() {
		frame.setVisible(false);
	}

	public static void makeVisible() {
		frame.setVisible(true);
		frame.toFront();
	}

	protected static void logUser() {
		if (frame != null) {
			makeVisible();
			return;
		}

		//Create the frame
		frame = new JFrame("User login!");
		frame.setLocationRelativeTo(null);
		frame.setPreferredSize(new Dimension(300,150));
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

		//Create the panel
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setLayout(null);

		//Make username label (just the text)
		JLabel usernameLabel = new JLabel("Username");
		usernameLabel.setBounds(20, 20, 80, 25);
		panel.add(usernameLabel);

		//Make username text box to enter his/her username
		username = new JTextField(20);
		username.setBounds(100, 20, 165, 25);
		panel.add(username);

		//Make password label (just the text)
		JLabel passwordLabel = new JLabel("Password");
		passwordLabel.setBounds(20, 50, 80, 25);
		panel.add(passwordLabel);

		//Make password text box to enter his/her password and don't display what is input
		password = new JPasswordField();
		password.setBounds(100,50, 165,25);
		panel.add(password);

		//Make a Login button which when press it calls ActionPerformed with the ActionEvent having Login
		JButton button = new JButton("Login");
		button.setBounds(100, 80, 70, 20);
		button.addActionListener(new LogUser());
		panel.add(button);

		//Make an Exit button which when press it calls ActionPerformed with the ActionEvent having Exit
		JButton buttonExit = new JButton("Exit");
		buttonExit.setBounds(180, 80, 70, 20);
		buttonExit.addActionListener(new LogUser());
		panel.add(buttonExit);

		//Set a way to display a message once a button is press
		message = new JLabel(" ");
		message.setBounds(10, 110, 300, 25);
		panel.add(message);

		//Display the frame
		frame.pack();
		makeVisible();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();
		Customer customer;

		if ( (nTriesLeft <= 0) || (s.equals("Exit")) ) {
			makeInvisible();
			nTriesLeft = MAXLOGINTRIES;
			Viewer.makeVisible();
		} else if(s.equals("Login")) {
			String userUsername = username.getText();
			String userPassword = new String(password.getPassword());

			customer = Database.getCustomer(userUsername);
			if ( (customer != null) && (customer.checkPassword(userPassword)) ) {
				Log.logWrite(Level.FINE,"Log as user.");

				makeInvisible();
				nTriesLeft = MAXLOGINTRIES;

				User.setCustomer(customer);
        User.userLogged();
			} else {
				nTriesLeft--;	//One try to log in had been used
				Log.logWrite(Level.FINE,"User couldn't be log in.");
				message.setText("We couldn't log you in.");
			}
		}
	}
}
