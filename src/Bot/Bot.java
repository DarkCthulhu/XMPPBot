package Bot;

import java.awt.Color;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import gui.StoredPrefs;

public class Bot {
	private XMPPClient client;
	private JTextPane logsPane;
	public enum Severity {
		DEBUG, WARNING, ERROR, INCOMING_CHAT, OUTGOING_CHAT
	}
	public Bot(StoredPrefs sp, JTextPane logsPane){
		this.logsPane = logsPane;
		try {
			String hostname = sp.lookupSymbol("host");
			Integer port = Integer.valueOf(sp.lookupSymbol("port"));
			String username = sp.lookupSymbol("username");
			String password = sp.lookupSymbol("password");
			String resource = sp.lookupSymbol("resource");
			
			writeLogLine(Severity.DEBUG, hostname, String.valueOf(port), username);
			client = new XMPPClient(hostname, port, username, password, resource);
			writeLogLine(Severity.DEBUG, "Connection Succeeded");
			
		} catch (NumberFormatException | XMPPException e) {
			writeLogLine(Severity.ERROR, e.getMessage());
		}
	}
	
	public void writeLogLine(Severity code, String... logStrings){
		StyledDocument doc = logsPane.getStyledDocument();
		SimpleAttributeSet keyWord = new SimpleAttributeSet();		
		switch(code){
			case DEBUG:
				StyleConstants.setForeground(keyWord, Color.BLACK);
				break;
			case WARNING:
				StyleConstants.setForeground(keyWord, Color.ORANGE);
				break;
			case ERROR:
				StyleConstants.setForeground(keyWord, Color.RED);
				StyleConstants.setBold(keyWord, true);
				break;				
			
			case INCOMING_CHAT:
				StyleConstants.setForeground(keyWord, Color.BLUE);
				break;
			case OUTGOING_CHAT:
				StyleConstants.setForeground(keyWord, Color.MAGENTA);
				break;
		}
		
		StringBuilder builder = new StringBuilder();

		for (String string : logStrings) {
		    if (builder.length() > 0) {
		        builder.append(" | ");
		    }
		    builder.append(string);
		}
		builder.append("\n");
		
		try{
		    doc.insertString(doc.getLength(), builder.toString(), keyWord );
		}catch(Exception e) { System.out.println(e); }
	}

	public void startEventLoop() {
		if (client == null){
			return;
		}
		//start the listener for messages
		try {
			client.startListener(this);
		} catch (InterruptedException e) {
			writeLogLine(Severity.ERROR, e.getMessage());
		}
	}

	public void processAndReply(Message message){
		if (message == null || message.getBody().equals("null")) {
			return;
		}
		
		writeLogLine(Severity.INCOMING_CHAT, message.getFrom().toString(), message.getBody());
		String uuid = getAnswerString(message.getFrom().toString(), message.getBody());
		
		try {
			client.sendMessage(uuid, message.getFrom().toString());
			writeLogLine(Severity.OUTGOING_CHAT, "Bot: ", uuid);
			
		} catch (XMPPException e) {
			writeLogLine(Severity.ERROR, e.getMessage());
		}
	}

	private String getAnswerString(String person, String incomingMsg) {
		Random generator = new Random(); 
		int len_sentence = generator.nextInt(10) + 10; //decide length of chain
		String seed_word = "0";
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<len_sentence; i++){
			String word = getNextWord(seed_word);
			sb.append(word + " ");
			seed_word = word;
		}
		return sb.toString();
	}

	private String getNextWord(String seed_word) {
		HashMap<String, Integer> innerHash;
		Random generator = new Random();	
		if (!Markov.markov.containsKey(seed_word)){
			Object[] keys = Markov.markov.keySet().toArray();
			seed_word = (String) keys[generator.nextInt(keys.length)];
		}
		
		innerHash = Markov.markov.get(seed_word);
		Object[] keys = innerHash.keySet().toArray();
		return (String) keys[generator.nextInt(keys.length)];
	}
}
