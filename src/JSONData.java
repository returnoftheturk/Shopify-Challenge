import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;


public class JSONData {
	static String JSONData;
	public JSONData() {
		
		
	}
	
	public static void main (String args[]) throws ParseException, IOException  {
		
		JSONData = getJSON();
		
		JSONParser jsonParser = new JSONParser();
		JSONObject json = (JSONObject)jsonParser.parse(JSONData);
		
		System.out.println(json.toString());

		
//		Gson g = new Gson();
//		Menu[] menu = g.fromJson(JSONData, Menu[].class);
//		System.out.println(g.toJson(menu));
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
