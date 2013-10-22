package gui;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.JButton;
import org.jivesoftware.smack.XMPPException;
import Bot.Bot;
import Bot.Markov;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.InvalidPropertiesFormatException;

public class MainUI {
	private JFrame frame;
	private static JTextPane logsPane;
	private JButton btnClear;
	private static Bot conn;

	/**
	 * Launch the application.
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws InvalidPropertiesFormatException 
	 */
	public static void main(String[] args) throws InvalidPropertiesFormatException, MalformedURLException, IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainUI window = new MainUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
	}

	/**
	 * Create the application.
	 * @throws XMPPException 
	 */
	public MainUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws XMPPException 
	 */
	private void initialize(){
		// initialize the GUI
		
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setTitle("FoxBot Console");
		
		logsPane = new JTextPane();
		logsPane.setBounds(0, 0, 442, 241);
		logsPane.setEditable(false);
		frame.getContentPane().add(logsPane);
		
		btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logsPane.setText("");
			}
		});
		btnClear.setBounds(346, 246, 91, 23);
		frame.getContentPane().add(btnClear);	
		
		new Thread(new Runnable() {
			public void run(){
				// markov chain init
				new Markov(markovPath);
			}
		}).start();
		
		
		new Thread(new Runnable() {
			public void run(){
				// initialize the XMPP client side
				StoredPrefs storP = new StoredPrefs(xmlPath);
				conn = new Bot(storP, logsPane);
				conn.startEventLoop();
			}
		}).start();
	}
	private static final String xmlPath = "config/foxbot.xml";
	private static final String markovPath = "config/train.txt";
}
