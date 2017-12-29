import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JSONData {
	static String JSONData;
	static JSONArray jsonArrayFull;
	static List<Integer> parent_ids;
	static List<Boolean> loopControl = new ArrayList<>();
	static int menuDepth;
	static JSONArray childrenArray;
	
	public JSONData()  {		}
	
	public static void initializeChildrenArray() {
		childrenArray = new JSONArray();
	}
	
	@SuppressWarnings("unchecked")
	public static void addChild(int id) {
		childrenArray.add(id);
	}
	public static void main (String args[]) throws ParseException, IOException  {
		JSONData = getFullJSON();
		System.out.println(JSONData);
		jsonArrayFull = (JSONArray)new JSONParser().parse(getFullJSON());
		checkDependency(jsonArrayFull);
	}
	
	public static void addBoolean(boolean bool) {
		loopControl.add(bool);
	}
	
	public static void initializeVariables() {
		parent_ids = new ArrayList<>();
		menuDepth = 0;
	}
	
	public static void addId(int id) {
		if(!parent_ids.contains(id)) {
			parent_ids.add(id);
			menuDepth++;	
		}
		
	}
	
	public static void addIdArray(int [] id) {
		int added = 0;
		for (int i=0; i<id.length; i++) {
			if(!parent_ids.contains(id[i])) {
				parent_ids.add(id[i]);
				added++;
			}
				
		}
		if(added>0) {
			menuDepth++;	
		}
		
	}
	
	public static void changeRootBoolean() {
//		System.out.println(loopControl.toString());
		for (int i = 0; i<loopControl.size(); i++) {
			JSONObject jsonObject = (JSONObject) jsonArrayFull.get(i);
			
			while(jsonObject.get("parent_id")!=null){
				jsonObject = (JSONObject) jsonArrayFull.get(findJSONObject(jsonArrayFull,Integer.valueOf(jsonObject.get("parent_id").toString())));
			}
			loopControl.set(findJSONObject(jsonArrayFull,Integer.valueOf(jsonObject.get("id").toString())), loopControl.get(i));
			
		}
//		System.out.println(loopControl.toString());
		
	}

	public static void checkDependency(JSONArray jsonArray) {
		for(int i =0; i< jsonArray.size(); i++) {
			JSONObject jsonObject = (JSONObject) jsonArray.get(i);
			
			initializeVariables();
			
			if (jsonObject.get("parent_id")==null) {
				addBoolean(false);
//				System.out.println("Index: " + i + " " + jsonObject.get("data").toString() + " " + findLoopButtomUp(i));
			}else {
				addBoolean(findLoopButtomUp(i));
			}
		}
		changeRootBoolean();
		System.out.println(createReturnJson(loopControl, jsonArrayFull).toString());
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject createReturnJson(List<Boolean> bools, JSONArray jsonArray) {
		JSONArray toReturnArrayValidJSON = new JSONArray();
		JSONArray toReturnArrayInValidJSON = new JSONArray();
				
		for(int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = (JSONObject) jsonArray.get(i);
			
			if (jsonObject.get("parent_id")==null) {
				JSONObject newMenuItem = new JSONObject();
				
				newMenuItem.put("root_id", jsonObject.get("id"));
				initializeChildrenArray();
				getChildrenArray(Integer.valueOf(jsonObject.get("id").toString()));
				System.out.println(childrenArray.toString());
				newMenuItem.put("children", childrenArray);

				if(bools.get(i)==false) {
					toReturnArrayValidJSON.add(newMenuItem);
				} else {	
					toReturnArrayInValidJSON.add(newMenuItem);
				}
			}
		}
		Map<String, JSONArray> JSONFull = new HashMap<String, JSONArray>();
		JSONFull.put("valid_menus", toReturnArrayValidJSON);
		JSONFull.put("invalid_menus", toReturnArrayInValidJSON);
		JSONObject JSONFullCasted = new JSONObject(JSONFull);

		return JSONFullCasted;
		
	}
	
	public static void getChildrenArray(int id) {
		JSONObject jsonObject = (JSONObject)jsonArrayFull.get(findJSONObject(jsonArrayFull, id));
		JSONArray jsonArrayChild = (JSONArray)jsonObject.get("child_ids");
//		JSONArray jsonArrayParent = (JSONArray) jsonObject.get("parent_id");
//		int[] parent_id = JSONArraytoIntArray(jsonArrayParent);
		
		int[] children_ids = JSONArraytoIntArray(jsonArrayChild);
		System.out.println("NEW ITERATION");
		System.out.println(Arrays.toString(children_ids));
		System.out.println(id);
		System.out.println(jsonObject.get("parent_id")==null);
		if (children_ids.length==0 || (jsonObject.get("parent_id")==null && childrenArray.size()>0)) {	
			addChild(id);
		
		} else {
//			System.out.println(id);
			for(int i=0; i<children_ids.length;i++) {
//				System.out.println(children_ids[i]);
				getChildrenArray(children_ids[i]);
			}
			if(jsonObject.get("parent_id")!=null) {
				addChild(id);	
			}
			
		}
	}
	
	public static boolean findLoopButtomUp(int index) {
		JSONObject jsonObject = (JSONObject) jsonArrayFull.get(index);
		int[] child_ids_int = JSONArraytoIntArray((JSONArray)jsonObject.get("child_ids"));
		if (child_ids_int.length>0) {
			addIdArray(child_ids_int);
		}
				
		if(parent_ids.contains(Integer.valueOf(jsonObject.get("id").toString()))) {
			return true;
		} else {
			addId(Integer.valueOf(jsonObject.get("id").toString()));
			if(jsonObject.get("parent_id")!=null) {
				int parentJId = Integer.valueOf(jsonObject.get("parent_id").toString());
				int parentIndex = findJSONObject(jsonArrayFull, parentJId);

				return findLoopButtomUp(parentIndex);
			} else {
				return false;				
			}
		}
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
		
		URL url = new URL("https://backend-challenge-summer-2018.herokuapp.com/challenges.json?id=2&page="+pageNum);
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
		
		rd.close();	

		return menuArray.toString();
	}
	
	//Iterate through API to get all the pages
	public static String getFullJSON() throws IOException, ParseException {
		String fullJSON =  "";
		for (int i=1; i<6; i++) {
			fullJSON+=getJSON(i);
		}
		fullJSON = fullJSON.replace("][", ",");
		return fullJSON;
	}
}
