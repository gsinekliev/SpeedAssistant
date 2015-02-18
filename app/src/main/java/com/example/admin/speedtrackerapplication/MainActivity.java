package com.example.admin.speedtrackerapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends Activity {

    ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Model.LoadModel();
        listView =  (ListView) findViewById(R.id.mainListView);
        String[] ids = new String[ Model.items.size() ];
        // populate the list of ids
        for (int i = 0; i < ids.length; i++){
            ids[i] = Integer.toString(i+1);
        }

        ItemAdapter adapter = new ItemAdapter(this, R.layout.row, ids);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, final View view, int position, long arg3){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final String currentSpeed = ((TextView) view.findViewById(R.id.textView)).getText().toString();
                String message = "Speed will be tracked for speed limit of " + currentSpeed;
                builder.setMessage(message).setTitle("Set tracking speed");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id){
                        //Intent i = new Intent(MainActivity.this, SingleListItem.class);
                        Intent i = new Intent(MainActivity.this, SpeedShowingActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("speed", currentSpeed);
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id){}
                });
                builder.show();
            }
        });
    }
}
