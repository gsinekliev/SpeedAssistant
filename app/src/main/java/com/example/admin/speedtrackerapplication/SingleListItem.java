package com.example.admin.speedtrackerapplication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.example.admin.speedtrackerapplication.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class SingleListItem extends Activity {
	
	public static final String PREFS_NAME = "ListState";
	
	private String fSpeedName = "No Product";
	private TextView fProductLabelTextView;
	private TextView fListPlaceholder;
	private CheckboxAdapter listAdapter;
	private ListView fEditableList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.single_list_item_view);
		
		Bundle intentBundle = getIntent().getExtras();
		
		if(intentBundle != null && intentBundle.containsKey("speed"))
			fSpeedName = intentBundle.getString("speed");
		
		String text = fSpeedName.toUpperCase() + ", MOTHERFUCKER!";
		fProductLabelTextView = (TextView) findViewById(R.id.item_text);
		if(fProductLabelTextView != null)
			fProductLabelTextView.setText(text);
		
		fListPlaceholder = (TextView) findViewById(R.id.list_placeholder);
		fListPlaceholder.setText("Here be a list");
		fEditableList = (ListView) findViewById(R.id.editable_list);
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		String serializedItems = settings.getString(fSpeedName, "");
		ArrayList<CheckableItem> container = new ArrayList<CheckableItem>();
		if (serializedItems.length() > 0){
			container = deserizlizeCheckableItems(serializedItems);
			//Collections.sort(container, new CheckableItemComparator());
		}
		
//		Collection<String> stateHolder = ((Map<String, String>) settings.getAll()).values();
//		
////		for(String stateString: stateHolder){
////			CheckableItem chItem = CheckableItem.fromString(stateString, fPlanetName);
////			if (chItem != null) //really, !=...
////				container.add(chItem);
////		}
////		
		
		if (container.isEmpty())
			listAdapter = new CheckboxAdapter(this, R.layout.simplerow, new ArrayList<String>());
		else
			listAdapter = new CheckboxAdapter(this, R.layout.simplerow, container, false);
		fEditableList.setAdapter(listAdapter);
		
		if(container.size() > 0)
		{
			fEditableList.setVisibility(View.VISIBLE);
			fListPlaceholder.setVisibility(View.GONE);
		}
		
		final Button button = (Button) findViewById(R.id.button_id);
		
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				addCrap();
			}
		});
		
		fEditableList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, final View view, int position, long arg3){
				CheckBox cb = (CheckBox) view.findViewById(R.id.checkBox);
				cb.setChecked(!cb.isChecked());
				CheckboxAdapter adapter = (CheckboxAdapter) fEditableList.getAdapter();
				adapter.container.get(position).toggleChecked();
			}
		});
		
		fEditableList.setOnItemLongClickListener( new OnItemLongClickListener(){
			
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				AlertDialog.Builder builder = new AlertDialog.Builder(SingleListItem.this);
				builder.setTitle("Choose wisely human");
				String[] options = new String[] {"Edit", "Delete"};
				builder.setItems(options, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ListView list = SingleListItem.this.fEditableList;
						//ArrayAdapter<String> adapter = (ArrayAdapter<String>) list.getAdapter();
						CheckboxAdapter adapter = (CheckboxAdapter) list.getAdapter();
						if (which == 1) {
							adapter.remove((CheckableItem) adapter.getItem(position));
							adapter.notifyDataSetChanged();
						} else {
							CheckableItem chItem = (CheckableItem) adapter.getItem(position);
							String text = chItem.fContents;
							editCrap(text, position, adapter);
						}
					}
				});
				builder.show();
				return true;
			}
		} );
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		//"serialize" all the items into one string and put it in preferences
		//for (CheckableItem chItem: listAdapter.container)
		//	editor.putString(Integer.toString(chItem.fId) + fPlanetName, chItem.toString(fPlanetName));
		String serializedItems = serializeCheckableItems(listAdapter.container);
		editor.putString(fSpeedName, serializedItems);
		editor.commit();
	}
	
	public String serializeCheckableItems(ArrayList<CheckableItem> checkablesList){
		String result = "";
		for (CheckableItem chItem: checkablesList)
			result = result.concat(";" + chItem.toString(fSpeedName));
		return (result.length() > 0) ? result.substring(1): result;
	}
	
	public ArrayList<CheckableItem> deserizlizeCheckableItems(String itemsString){
		String[] items = itemsString.split(";");
		ArrayList<CheckableItem> result = new ArrayList<CheckableItem>();
		for(String item: items){
			if (CheckableItem.fromString(item, fSpeedName) != null);
				result.add(CheckableItem.fromString(item, fSpeedName));
			}
		return result;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_add_list, menu);
	    return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		ArrayList<Integer> checkedPositions = getCheckedList();
		MenuItem item = menu.getItem(1);
		if (!checkedPositions.isEmpty())
			item.setVisible(true);
		else 
			item.setVisible(false);
		return true;
	}
	
	public ArrayList<Integer> getCheckedList(){
		ArrayList<Integer> checkedPositions = new ArrayList<Integer>();
		for(CheckableItem chItem: listAdapter.container)
		{
			if (chItem.fChecked)
				checkedPositions.add(listAdapter.container.indexOf(chItem));
		}
		return checkedPositions;
	}
	
	public void deleteSelected(){
		//deprecated
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		boolean flag = true;
		if (((Map<String, String>) settings.getAll()).isEmpty())
			flag  = false;
		if (!listAdapter.container.isEmpty()){
			for(CheckableItem chItem: listAdapter.container){
				if (chItem.fChecked){
					listAdapter.remove(chItem);
					if (flag)
						editor.remove(fSpeedName).commit();
				}	
			}

			listAdapter.notifyDataSetChanged();
		}
	}
	
	public void removeChecked(ArrayList<Integer> checkedPositions){
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		
		if (!(listAdapter.container.isEmpty() && checkedPositions.isEmpty())){
			Collections.reverse(checkedPositions);
			for(Integer pos: checkedPositions){
				listAdapter.remove(pos);
				editor.remove(Integer.toString(pos) + fSpeedName).commit();
			}
		}
		listAdapter.notifyDataSetChanged();
		
		// no trqbva i ot preferences da se mahne, ako sa bili save-nati na predishen onBackPressed
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    // commented, because it cries after importing google play lib
//	        case R.id.add_crap:
//	        	addCrap();
//	            return true;
//	        case R.id.delete_selected:
//	        	//deleteSelected();
//	        	removeChecked(getCheckedList());
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	public void addCrap(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Here you can add items. Are you surprised?").setTitle("Add item");
		
		final EditText input = new EditText(this);
        builder.setView(input);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int id){
				String text = input.getEditableText().toString();
				int placeholder_visibility = fListPlaceholder.getVisibility();
				if (placeholder_visibility == View.VISIBLE){
					fListPlaceholder.setVisibility(View.GONE);
					fEditableList.setVisibility(View.VISIBLE);
					ArrayList<String> supportList = new ArrayList<String>();
					listAdapter = new CheckboxAdapter(SingleListItem.this, R.layout.simplerow, supportList);
					fEditableList.setAdapter(listAdapter);
				}
				if (! text.equals(""))	
					listAdapter.add(text);
				
				listAdapter.notifyDataSetChanged();
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id){}
		});
        builder.show();
	}

	public void editCrap(final String text, final int position, final CheckboxAdapter adapter){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Here you can edit items. Surprised?").setTitle("Edit item");
		final EditText input = new EditText(this);
		input.append(text);
        builder.setView(input);
        //final String showText = input.getEditableText().toString();
        
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id){}
			});
		
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id){
				adapter.remove((CheckableItem)adapter.getItem(position));;
				CheckableItem chItem = new CheckableItem(false, input.getEditableText().toString());
				adapter.insert(chItem);
				adapter.notifyDataSetChanged();
			}
		});
        builder.show();
	}

}
