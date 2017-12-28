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
	static JSONArray jsonArrayFull;
	static List<Integer> parent_ids;
	static int menuDepth;
	public JSONData()  {		}
	
	public static void main (String args[]) throws ParseException, IOException  {
		JSONData = getFullJSON();
		System.out.println(JSONData);
//		System.out.println(((JSONArray)new JSONParser().parse(JSONData)).toString());
		jsonArrayFull = (JSONArray)new JSONParser().parse(getFullJSON());
		checkDependency(jsonArrayFull);
	}
	
	public static void initializeVariables() {
		parent_ids = new ArrayList<>();
		menuDepth = 0;
	}
	
	public static void addId(int id) {
		parent_ids.add(id);
		menuDepth++;
	}
	
	public static int checkDependency(JSONArray jsonArray) {
		for(int i =0; i< jsonArray.size(); i++) {
			
			JSONObject jsonObject = (JSONObject) jsonArray.get(i);
//			JSONArray jsonA = (JSONArray) jsonObject.get("child_ids");
//			int[] child_ids_int = JSONArraytoIntArray(jsonA);
			
			int id = Integer.valueOf(jsonObject.get("id").toString());
			
			initializeVariables();
			addId(id);
			
			
			if (jsonObject.get("parent_id")==null) {
				System.out.println(findLoop(i));
//				System.out.println("PARENT");
//				System.out.println(jsonObject.get("data").toString());
//				System.out.println(i);
			} else {
//				System.out.println("CHILD OF GOD");
//				System.out.println(jsonObject.get("data").toString());
//				System.out.println(jsonObject.get("parent_id").toString());
			}
			
		}
		
		return -1;
	}
	
	public static boolean findLoop(int index) {
		JSONObject jsonObject = (JSONObject) jsonArrayFull.get(index);
		JSONArray jsonA = (JSONArray) jsonObject.get("child_ids");
		if (menuDepth>4||parent_ids.contains(jsonArrayFull.get(index))) {
			return true;
		} else if (jsonA==null) {
			return false;
		} else {
			int [] child_ids_int = JSONArraytoIntArray(jsonA);
			for(int i=0; i<child_ids_int.length; i++) {
				System.out.println(child_ids_int[i]);
//				System.out.println(findLoop(findJSONObject(jsonArrayFull, child_ids_int[i])));
				addId(child_ids_int[i]);
				return findLoop(findJSONObject(jsonArrayFull, child_ids_int[i]));
				
			}
		}
		return false;	
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
