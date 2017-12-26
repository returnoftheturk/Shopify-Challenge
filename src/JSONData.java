import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.Arrays;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONData {
	static String JSONData;
	public JSONData() {		}
	
	public static void main (String args[]) throws ParseException, IOException  {
		JSONData = getFullJSON();
		System.out.println(JSONData);
	}
	
	public static int checkDependency(JSONArray jsonArray) {
		for(int i =0; i< jsonArray.size(); i++) {
			JSONObject jsonObject = (JSONObject) jsonArray.get(i);
			JSONArray jsonA = (JSONArray) jsonObject.get("child_ids");
			System.out.println(Arrays.toString(JSONArraytoIntArray(jsonA)));

		}
		
		return -1;
	}
	
	//Use to convert the child_ids value into a int[] array.
	public static int[] JSONArraytoIntArray(JSONArray jsonArray) {
		int[] toReturn = new int[jsonArray.size()];
		for(int i = 0; i<jsonArray.size(); i++) {
			toReturn[i]= Integer.valueOf(jsonArray.get(i).toString());
		}
		return toReturn;
		
	}
	
	//Returns index where JSONObject is located in the array if it exists, -1 if it doesnt exist.
	public static int findJSONObject(JSONArray jsonArray, int id) {
		int i = 0;
		for (Object o: jsonArray) {
			if (Integer.valueOf(((JSONObject)o).get("id").toString())==id) {
				return i;
			}
			i++;
		}
		return -1;
	}
	
	public static String getJSON(int pageNum) throws IOException, ParseException {
		StringBuilder result = new StringBuilder();
		
		URL url = new URL("https://backend-challenge-summer-2018.herokuapp.com/challenges.json?id=1&page="+pageNum);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		while((line=rd.readLine())!=null) {
			result.append(line);
		}
		JSONParser jsonParser = new JSONParser();
		JSONObject json = (JSONObject)jsonParser.parse(result.toString());
		
		JSONArray menuArray = (JSONArray) json.get("menus");
		System.out.println(menuArray.toString());
		
		rd.close();	

		return menuArray.toString();
	}
	
	//Iterate through API to get all the pages
	public static String getFullJSON() throws IOException, ParseException {
		String fullJSON =  "";
		for (int i=1; i<4; i++) {
			fullJSON+=getJSON(i);
		}
		fullJSON.replace("][", ",");
		return fullJSON;
	}
}
