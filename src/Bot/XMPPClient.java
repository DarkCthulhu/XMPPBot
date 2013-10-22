package Bot;
import java.util.*;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
 
public class XMPPClient implements MessageListener{
    private XMPPConnection connection;
    //construct and login
    public XMPPClient(String host, Integer port, String userName, String password, String resource) throws XMPPException{
    	ConnectionConfiguration config = new ConnectionConfiguration(host, port, resource);
	    connection = new XMPPConnection(config);
	    connection.connect();
	    connection.login(userName, password);
    }
    
    //send
    public void sendMessage(String message, String to) throws XMPPException{
	    Chat chat = connection.getChatManager().createChat(to, this);
	    chat.sendMessage(message);
    }
 
    // get all buddies
    public void displayBuddyList(){
	    Roster roster = connection.getRoster();
	    Collection<RosterEntry> entries = roster.getEntries();
	    
	    System.out.println("\n\n" + entries.size() + " buddy(ies):");
		for(RosterEntry r:entries){
		  	System.out.println(r.getUser());
	    }
    }
 
    public void disconnect(){
    	connection.disconnect();
    }
 
    public void startListener(final Bot conn) throws InterruptedException{
    	  connection.getChatManager().addChatListener(new ChatManagerListener(){
    		  public void chatCreated(final Chat chat, final boolean createdLocally){
    			  chat.addMessageListener(new MessageListener(){
				  	public void processMessage(Chat chat, Message message){
				  		if(message!=null && message.getBody()!="null"){
				  			conn.processAndReply(message);
				  		}	
				  	}
    			  });
    		  }
    	  });
    	  final long start = System.nanoTime();
    	  while ((System.nanoTime() - start) / 1000000 < 20000){
    	     Thread.sleep(500);
    	  }
    }

	@Override
	public void processMessage(Chat arg0, Message arg1) {
		
		
	}
}