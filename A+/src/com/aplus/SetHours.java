package com.aplus;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SetHours extends Activity implements OnClickListener {

	Button bnext;
	Button bprev;
	EditText[] etArr;
	TextView dayDisplay;
	String[][] classes;

	int currentDay;
	SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_hours);

		initialize();

		sp = PreferenceManager.getDefaultSharedPreferences(this);
		currentDay = 1;

		getClasses();
		setDayView(currentDay);
		bnext.setOnClickListener(this);
		bprev.setOnClickListener(this);

	}

	private void getClasses() {
		classes = new String[6][11];
		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 11; j++)
				classes[i][j] = sp.getString("DAY" + (i + 1) + "HOUR" + j, "");

	}

	private void initialize() {
		// TODO Auto-generated method stub
		bnext = (Button) this.findViewById(R.id.bNext);
		bprev = (Button) this.findViewById(R.id.bPrev);

		etArr = new EditText[11];
		int[] idarr = { R.id.etHour0, R.id.etHour1, R.id.etHour2, R.id.etHour3,
				R.id.etHour4, R.id.etHour5, R.id.etHour6, R.id.etHour7,
				R.id.etHour8, R.id.etHour9, R.id.etHour10 };
		for (int i = 0; i < 11; i++)
			etArr[i] = (EditText) findViewById(idarr[i]);
		dayDisplay = (TextView) findViewById(R.id.tvDay);
	}

	private void setDayView(int day) {
		if (day < 7) {
			String daystr = dayToString(day);
			dayDisplay.setText("יום " + daystr);
			for (int i = 0; i < etArr.length; i++) {
				etArr[i].setText(classes[day - 1][i]);
			}
		}
		bprev.setEnabled(day != 1);
		if (day == 6)
			bnext.setText("שמור");
		else
			bnext.setText("הבא");

	}

	private String dayToString(int day) {
		switch (day) {
		case 1:
			return "ראשון";
		case 2:
			return "שני";
		case 3:
			return "שלישי";
		case 4:
			return "רביעי";
		case 5:
			return "חמישי";
		case 6:
			return "שישי";
		case 7:
			return "שבת";
		default:
			return "";
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.bNext:
			if (currentDay != 6) {
				tempSave(currentDay);
				currentDay++;
				setDayView(currentDay);
			} else {
				tempSave(currentDay);
				longTermSave();
				finish();
			}
			break;

		case R.id.bPrev:
			tempSave(currentDay);
			currentDay--;
			setDayView(currentDay);
			break;
		}
	}

	private void tempSave(int currentDay2) {
		// TODO Auto-generated method stub
		for (int i = 0; i < etArr.length; i++) {
			classes[currentDay2 - 1][i] = etArr[i].getText().toString();
		}
	}

	private void longTermSave() {
		Editor e = sp.edit();
		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 11; j++)
				e.putString("DAY" + (i + 1) + "HOUR" + j, classes[i][j]);
		e.commit();
	}
}
