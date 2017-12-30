/*AUTHOR: AHMET AKGUL
 * 12/30/2017
 * SHOPIFY-CHALLENGE
 * NOTE:  GITHUB REPOSITORY IS LOCATED AT https://github.com/returnoftheturk/Shopify-Challenge.git
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONData {
	static String JSONData;
	static JSONArray jsonArrayFull;
	static List<Integer> parent_ids;
	static List<Boolean> loopControl = new ArrayList<>();
	static int menuDepth;
	static JSONArray childrenArray;
	static int childDepth;
	
	public static void main (String args[]) throws ParseException, IOException  {
		System.out.println("Dataset 1 Input:");
		int dataId = 1;
		JSONData = getFullJSON(dataId);
		System.out.println(JSONData);
		jsonArrayFull = (JSONArray)new JSONParser().parse(getFullJSON(dataId));
		System.out.println("OUTPUT: ");
		checkDependency(jsonArrayFull);
		
		reset();
		
		System.out.println("\nDataset 2 Input:");
		dataId = 2;
		JSONData = getFullJSON(dataId);
		System.out.println(JSONData);
		jsonArrayFull = (JSONArray)new JSONParser().parse(getFullJSON(dataId));
		System.out.println("OUTPUT: ");
		checkDependency(jsonArrayFull);
		
	}
	
	//Iterate through API given ID number to get all the pages
	public static String getFullJSON(int pageId) throws IOException, ParseException {
		String fullJSON =  "";
		for (int i=1; i<6; i++) {
			fullJSON+=getJSON(pageId, i);
		}
		fullJSON = fullJSON.replace("][", ",");
		return fullJSON;
	}
	
	//Method to send Request to individual API to store data in a string
	public static String getJSON(int pageId, int pageNum) throws IOException, ParseException {
		StringBuilder result = new StringBuilder();
		
		URL url = new URL("https://backend-challenge-summer-2018.herokuapp.com/challenges.json?id=" + pageId + "&page="+pageNum);
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
	
	//Method that iterates through every single child node to find the loops
	public static void checkDependency(JSONArray jsonArray) {
		for(int i =0; i< jsonArray.size(); i++) {
			JSONObject jsonObject = (JSONObject) jsonArray.get(i);
			
			initializeVariables();
			
			if (jsonObject.get("parent_id")==null) {
				addBoolean(false);
			}else {
				addBoolean(findLoopButtomUp(i));
			}
		}
		changeRootBoolean();
		System.out.println(createReturnJson(loopControl, jsonArrayFull).toString());
	}
	
	//Method to initialize parentId's array list and menu depth count.  Runs each time new child is being checked
	public static void initializeVariables() {
			parent_ids = new ArrayList<>();
			menuDepth = 0;
		}
		
	//Method to add boolean to loopControl to indicate whether or not that index child is in a loop or not
		public static void addBoolean(boolean bool) {
			loopControl.add(bool);
		}
	
	public static void reset() {
		loopControl.clear();
		initializeChildrenArray();
		initializeVariables();
	}
	public static void initializeChildrenArray() {
		childDepth = 0;
		childrenArray = new JSONArray();
	}
	
	@SuppressWarnings("unchecked")
	public static void addChild(int id) {
		childrenArray.add(id);
	}
	
	/*This is the method that finds the incorrect loop in the system.  It is a recursive method.  It starts
	 *at each child node and builds its way up to the parent.  If it realizes that it has a child itself that is also a parent that 
	 *it can reach, it indicates that that child is in a circular reference loop, and returns false for that index.  This all gets
	 *stored in an arraylist.  Ex.  21 total nodes, index 5 false means child node 4 is not in a circular reference.  
	 *Index 12 true means child node 11 is not in a circular reference.   
	 */
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
	
	//Add array of ID's to arraylist
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

	//Add ID to parent Ids
	public static void addId(int id) {
		if(!parent_ids.contains(id)) {
			parent_ids.add(id);
			menuDepth++;	
		}
	}
		
	//Method to iterate through the loopControl array list, and find the parentId's of children nodes
	//that are loops, and make the parentLoops index value reflect the same (change from false to true)
	public static void changeRootBoolean() {
		for (int i = 0; i<loopControl.size(); i++) {
			JSONObject jsonObject = (JSONObject) jsonArrayFull.get(i);
			
			while(jsonObject.get("parent_id")!=null){
				jsonObject = (JSONObject) jsonArrayFull.get(findJSONObject(jsonArrayFull,Integer.valueOf(jsonObject.get("parent_id").toString())));
			}
			loopControl.set(findJSONObject(jsonArrayFull,Integer.valueOf(jsonObject.get("id").toString())), loopControl.get(i));
			
		}
	}	
	
	//METHOD TO OUTPUT DATA AFTER EVERYTHING IS DONE
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
	
	//Method to add all the children of a parent node into an array list called childrenArray
	public static void getChildrenArray(int id) {
		JSONObject jsonObject = (JSONObject)jsonArrayFull.get(findJSONObject(jsonArrayFull, id));
		JSONArray jsonArrayChild = (JSONArray)jsonObject.get("child_ids");
		
		int[] children_ids = JSONArraytoIntArray(jsonArrayChild);
		
		if (children_ids.length==0 || childDepth>4 ||(jsonObject.get("parent_id")==null && childrenArray.size()>0)) {	
			addChild(id);
		
		} else {
			for(int i=0; i<children_ids.length;i++) {
				childDepth++;
				getChildrenArray(children_ids[i]);
			}
			if(jsonObject.get("parent_id")!=null) {
				addChild(id);	
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
	
	//Returns index where JSONObject is located in the array if it exists, -1 if it doesn't exist.
	public static int findJSONObject(JSONArray jsonArray, int id) {
		for (int i = 0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = (JSONObject) jsonArray.get(i);
						
			if (Integer.valueOf(jsonObject.get("id").toString())==id) {
				return i;
			}
			
		}
		return -1;
	}
	

}
