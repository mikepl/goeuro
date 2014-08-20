package com.goeuro;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

/**
 * Java command line tool that takes as an input parameter a string.
 * 
 * @author michal
 */
public class App {

	/** The parameter. */
	public String parameter;
	
	/** The Constant url. */
	public static final String url = "http://api.goeuro.com/api/v2/position/suggest/en/";

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		if (args.length < 1) {

			System.out.println("No parameter");
		} else {
			String ret = transformToCSV(url+args[0]);
			System.out.println(ret);
		}
	}
	
	/**
	 * Gets the value depending on type. 
	 *
	 * @param jsonObject the json object
	 * @param name the name
	 * @return the val
	 */
	private static String getVal(JsonObject jsonObject, String name){
		
		String o=null;
		JsonValue value = jsonObject.get(name);
		if(value == null){
			return "";
		}
		if(value.getValueType().equals(ValueType.STRING)){
			o = jsonObject.getString(name).toString();
		}
		if(value.getValueType().equals(ValueType.NUMBER)){
			o = jsonObject.getJsonNumber(name).toString();
		}
		return o;
	}
	 

	/**
	 * Transform to csv.
	 *
	 * @param apiUrl the api url
	 * @return the string  CSV
	 */
	public static String transformToCSV(String apiUrl) {
		
		String csv="_type, _id, name, type, latitude, longitude\n";

		try {
			URL url = new URL(apiUrl);

			Scanner scan = new Scanner(url.openStream());
			String str = new String();
			while (scan.hasNext()){
				str += scan.nextLine();
			}
			scan.close();
		
			 JsonReader jsonReader = Json.createReader(new StringReader(str));
			 JsonArray jsonArray = jsonReader.readArray();
			 jsonReader.close();
			
			 for(JsonValue jsonValue : jsonArray){
				 String _type="", _id="", name="", type="", latitude="", longitude="";
				 JsonObject jsonObject = (JsonObject) jsonValue;
				 
				 _type= getVal(jsonObject,"_type");
				 _id = getVal(jsonObject,"_id");
				 name = getVal(jsonObject, "name");
				 type = getVal(jsonObject,"type");
				 
				 JsonValue v4 = jsonObject.get("geo_position");
				 if(v4 != null){
					 JsonObject jo = (JsonObject) v4;
					 latitude = getVal(jo,"latitude");
					 longitude = getVal(jo,"longitude");
				 }
					 csv += _type+","+_id+","+name+","+type+","+latitude+","+longitude+"\n";
				 
			 }
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return csv;
	}

}
