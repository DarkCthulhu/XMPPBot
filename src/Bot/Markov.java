package Bot;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Markov {
	public static HashMap<String, HashMap<String, Integer>> markov = new HashMap<String, HashMap<String, Integer>>();
	public Markov(String fileName){
		try {
			populateChains(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void populateChains(String fileName) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		String line = null;
		String prevToken = "0";
		while((line = br.readLine())!= null ){
		    String[] tokens = line.split("\\s+");
		    for(String token: tokens){
		    	token = token.replaceAll("[^a-zA-Z.]", "").toLowerCase(); //find chains
		    	if (token.endsWith(".")) {
		    		prevToken = "0";
		    	}
		    	token = token.replaceAll("[.]", ""); //get rid of fullstops
		    	populateMap(prevToken, token);
		    	prevToken = token;
		    }
		}
		br.close();
	}
	private void populateMap(String first, String second){
		//check if valid input
		if (first.length() == 0 || second.length() == 0)
			return;
		
		
		HashMap<String, Integer> innerHash;
		if (markov.containsKey(first)){
			innerHash = markov.get(first);
			int count = innerHash.containsKey(second) ? innerHash.get(second) : 0;
			innerHash.put(second, ++count);
		}else{
			innerHash = new HashMap<String, Integer>();
			innerHash.put(second, 1);
			markov.put(first, innerHash);
		}
	}
	public void printMap(){
		for (String key: markov.keySet()) {
		    System.out.println("Key: " + key + ", Value: " + markov.get(key));
		}
	}
}
