import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;

public class JSONData {
	String JSONData;
	public JSONData() {
		
		
	}
	
	public void main (String args[])  {
		try {
			JSONData = getJSON();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public static String getJSON() throws IOException {
		StringBuilder result = new StringBuilder();
						
		URL url = new URL("https://backend-challenge-summer-2018.herokuapp.com/challenges.json?id=1&page=1");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		while((line=rd.readLine())!=null) {
			result.append(line);
		}
		rd.close();
		System.out.println(result.toString());
		return result.toString();
	}
	
	
}
