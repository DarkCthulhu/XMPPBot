package gui;

import java.io.File;
import java.io.IOException;
import java.util.Properties;


public class StoredPrefs {
    private Properties symbolmap;
    public StoredPrefs(String str){
    	File file = new File(str);
    	symbolmap = new Properties();
        try {
			symbolmap.loadFromXML(file.toURI().toURL().openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    //variable length arguments are packed into an array
    //which can be accessed and passed just like any array
    public String lookupSymbol(String symbol) {
        //Retrieve the value of the associated key
        String message = symbolmap.getProperty(symbol);
        if(message == null)
            return "";
        return message;
    }
}