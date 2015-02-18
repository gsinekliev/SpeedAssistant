package com.example.admin.speedtrackerapplication;

public class CheckableItem {
	public boolean fChecked;
	public String fContents;
	
	public CheckableItem(boolean checked, String contents){
		this.fChecked = checked;
		this.fContents = contents;
	}
	
	public void toggleChecked(){
		fChecked = !fChecked;
	}
	
	public String toString(String planetName){
		return Boolean.toString(fChecked) + "__" + fContents;
	}
	
	public static CheckableItem fromString(String str, String currentPlanet){
		if (str.length() > 0){
			String[] items = str.split("__");
			return new CheckableItem(Boolean.parseBoolean(items[0]), items[1]);	
		}
		return null;
	}
}
