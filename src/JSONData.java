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
		JSONData = getJSON();
		
		JSONParser jsonParser = new JSONParser();
		JSONObject json = (JSONObject)jsonParser.parse(JSONData);

		JSONArray menuArray = (JSONArray) json.get("menus");
		checkDependency(menuArray);
		System.out.println(menuArray.toString());
//		for (Object o: menuArray) {
//			checkDependency((JSONObject)o);
//		}
//		System.out.print(findJSONObject(menuArray, 6));
	}
	
	public static int checkDependency(JSONArray jsonArray) {
		for(int i =0; i< jsonArray.size(); i++) {
			JSONObject jsonObject = (JSONObject) jsonArray.get(i);
			JSONArray jsonA = (JSONArray) jsonObject.get("child_ids");
			System.out.println(Arrays.toString(JSONArraytoIntArray(jsonA)));
			
		}
		
		for (Object o: jsonArray) {
			int count = 0;
			int id = Integer.valueOf(((JSONObject) o).get("id").toString());
//			int[] child_ids = ((JSONObject)o).getJSONArray("child_ids");
//			System.out.println(((JSONObject)o).get("child_ids"));
//			System.out.println(o.toString());
			
		}
//		int id = Integer.valueOf(o.get("id").toString());
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
		return result.toString();
	}
	
}
