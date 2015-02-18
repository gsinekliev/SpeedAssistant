package com.example.admin.speedtrackerapplication;

import java.util.ArrayList;
import java.util.Arrays;

public class Model {
	public static ArrayList<Item> items;
	
	public static void LoadModel(){
		items = new ArrayList<Item>();
		String [] speedsArray = new String [] {"3 km/h", "20 km/h", "30 km/h", "40 km/h", "50 km/h", "80 km/h",
											   "90 km/h", "140 km/h"};
		ArrayList<String> namesAndIconsNames = new ArrayList<String>();
		// BodiesNames.addAll(Arrays.asList(BodiesNamesArray));
		
		// no zip in java, so improvising
		for(String name: speedsArray){
			String iconName = "s" + name.replaceAll("\\s+","").replace("/","");
			namesAndIconsNames.add(name.toLowerCase() + "_" + iconName );
		}
		// populating the items list
		int i = 1;
		for (String names: namesAndIconsNames){
			String [] tempNames = names.split("_");
			Item item = new Item(i++, tempNames[1], tempNames[0]);
			items.add(item);
		}
	}
	
	public static Item GetById(int id){
		for(Item item: items){
			if (item.fId == id)
				return item;
		}
		return null;
	}
}
