package com.example.admin.speedtrackerapplication;

import java.util.ArrayList;

import com.example.admin.speedtrackerapplication.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class CheckboxAdapter extends BaseAdapter{
	
	private final Context context;
	private final int rowResourceID;
	public ArrayList<CheckableItem> container;


	public CheckboxAdapter(Context context, int rowResourceID, ArrayList<CheckableItem> container, boolean fakeVar){
		this.context = context;
		this.rowResourceID = rowResourceID;
		this.container = (ArrayList<CheckableItem>)container;
	}
	
	public CheckboxAdapter(Context context, int rowResourceID, ArrayList<String> container){
		this.context = context;
		this.rowResourceID = rowResourceID;
		ArrayList<CheckableItem> cont = new ArrayList<CheckableItem>();
		for(String item: container){
			CheckableItem chItem = new CheckableItem(false, item);
			cont.add(chItem);
		}
		this.container = cont;
	}
	
	public void remove(int position){
		container.remove(position);
	}
	
	public void remove(CheckableItem chItem){
		container.remove(chItem);
	}
	
	public void insert(CheckableItem chItem){
		container.add(chItem);
	}
	
	@Override
	public int getCount() {
		return container.size();
	}

	@Override
	public Object getItem(int position) {
		return container.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void add(String text){
		CheckableItem ch_item = new CheckableItem(false, text);
		container.add(ch_item);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(rowResourceID, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.rowTextView);
		textView.setText(container.get(position).fContents);
		CheckBox cb = (CheckBox) rowView.findViewById(R.id.checkBox);
		cb.setChecked(container.get(position).fChecked);
		return rowView;
	}

}
