import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
//		System.out.println(((JSONArray)new JSONParser().parse(JSONData)).toString());
		checkDependency((JSONArray)new JSONParser().parse(JSONData));
	}
	
	public static int checkDependency(JSONArray jsonArray) {
		for(int i =0; i< jsonArray.size(); i++) {
			
			JSONObject jsonObject = (JSONObject) jsonArray.get(i);
			System.out.println(jsonObject.toString());
			JSONArray jsonA = (JSONArray) jsonObject.get("child_ids");
			System.out.println(jsonObject.get("parent_id")==null);
			if (jsonObject.get("parent_id")==null) {
				System.out.println(i);
			} else {
				System.out.println(jsonObject.get("parent_id").toString());
			}
			int id = Integer.valueOf(jsonObject.get("id").toString());
			List<Integer> parent_ids = new ArrayList<>();
			parent_ids.add(id);
			
			int[] child_ids_int = JSONArraytoIntArray(jsonA);
			
			System.out.println(Arrays.toString(JSONArraytoIntArray(jsonA)));
			for (int j=0; j<child_ids_int.length; j++) {
				int childIndex = findJSONObject(jsonArray, child_ids_int[j]);
				findLoop((JSONObject)jsonArray.get(childIndex), parent_ids);
				
				
			}
		}
		
		return -1;
	}
	
	public static int findLoop(JSONObject jsonChild, List<Integer> parent_ids) {
		int [] child_ids_int = JSONArraytoIntArray((JSONArray)jsonChild.get("child_ids"));
		
		
		
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
		for (int i = 0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = (JSONObject) jsonArray.get(i);
						
			if (Integer.valueOf(jsonObject.get("id").toString())==id) {
				return i;
			}
			
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
//		System.out.println(menuArray.toString());
		
		rd.close();	

		return menuArray.toString();
	}
	
	//Iterate through API to get all the pages
	public static String getFullJSON() throws IOException, ParseException {
		String fullJSON =  "";
		for (int i=1; i<4; i++) {
			fullJSON+=getJSON(i);
		}
		fullJSON = fullJSON.replace("][", ",");
		return fullJSON;
	}
}
