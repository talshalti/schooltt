package com.aplus;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ListActivity { // לוקח גם מאקטיביטי וגם
													// פונקציות נוספות המיוחדות
													// בליסט אקטיביטי

	String classes[] = { "Wheel3D", "SetHours", "StaticTimetable", "Test",
			"example1" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(MainActivity.this,
				android.R.layout.simple_list_item_1, classes)); // כמה מאפיינים
																// של ליסט
		GlobalData.initialize(this);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub

		super.onListItemClick(l, v, position, id);
		String mys = classes[position];
		try {
			Class<?> ourClass = Class.forName("com.aplus." + mys);
			Intent ourIntent = new Intent(MainActivity.this, ourClass);
			startActivity(ourIntent);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
