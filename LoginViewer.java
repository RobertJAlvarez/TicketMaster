import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginViewer implements ActionListener {
	private static JTextField username;
	private static JPasswordField password;
	private static JLabel message;
	private boolean popUpClose = false;

	public static void main(String[] args) {
		System.out.println("Before call");
		logUser();
		System.out.println("After call");
	}

	public static void logUser() {
		//Create the frame
		JFrame frame = new JFrame("Hello World!");
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
		button.addActionListener(new LoginViewer());
		panel.add(button);

		//Make an Exit button which when press it calls ActionPerformed with the ActionEvent having Exit
		JButton buttonExit = new JButton("Exit");
		buttonExit.setBounds(180, 80, 70, 20);
		buttonExit.addActionListener(new LoginViewer());
		panel.add(buttonExit);

		//Set a way to display a message once a button is press
		message = new JLabel(" ");
		message.setBounds(10, 110, 300, 25);
		panel.add(message);

		//Display the frame
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();
		Customer customer;

		if(s.equals("Login")) {
			String userUsername = username.getText();
			String userPassword = new String(password.getPassword());

			customer = Database.getCustomer(userUsername);
			if ( (customer != null) && (!customer.checkPassword(userPassword)) ) {
				popUpClose = true;
			} else {
				message.setText("Wrong password");
			}
		} else if (s.equals("Exit")) {
			popUpClose = true;
		}

		if (popUpClose) {
			System.exit(0);
		}
	}
}
