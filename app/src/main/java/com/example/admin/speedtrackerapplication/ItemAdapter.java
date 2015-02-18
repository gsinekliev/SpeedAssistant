package com.example.admin.speedtrackerapplication;

import com.example.admin.speedtrackerapplication.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemAdapter extends ArrayAdapter<String> {

	private final Context context;
	private final String[] iDs;
	private final int rowResourceID;

	public ItemAdapter(Context context, int textViewResourceId, String[] objects){

		super(context, textViewResourceId, objects);
		this.context = context;
		this.iDs = objects;
		this.rowResourceID = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(rowResourceID, parent, false);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
		TextView textView = (TextView) rowView.findViewById(R.id.textView);
		TextView textView1 = (TextView) rowView.findViewById(R.id.textView1);
		
		int id = Integer.parseInt(iDs[position]);
		String imageFile = Model.GetById(id).fIconFile;
		
		textView.setText(Model.GetById(id).fName);
		textView1.setText("Sample text");
		
		int imageID = 0;
		imageID = context.getResources().getIdentifier(imageFile , "drawable", context.getPackageName());
		
		imageView.setImageResource(imageID);
		rowView.invalidate();
		return rowView;
		}
}
