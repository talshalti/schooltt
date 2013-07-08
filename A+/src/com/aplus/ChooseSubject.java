package com.aplus;

import java.util.ArrayList;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aplus.Displays.SubjectSpinner;

public class ChooseSubject extends Activity implements OnClickListener, OnItemSelectedListener {

	View colorDisplay;
	TextView selectedRoom;
	ArrayList<String> roomsArray;
	EditText etSubject;
	EditText etTeacher;
	SubjectSpinner spinner;
	int ourColor = 0xff000000;
	int buttonId;
	LinearLayout roomsDisplay;
	LayoutParams lp;
	float dp;
	Button bdelete;
	int currentsubjectid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		currentsubjectid = getIntent().getExtras().getInt("subid");
		dp = getBaseContext().getResources().getDisplayMetrics().density;
		setContentView(R.layout.choose_subject);
		lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins((int) (5 * dp), (int) (5 * dp), (int) (5 * dp), (int) (5 * dp));
		buttonId = findId();
		initialize();
		fillRoomsList();

		if (currentsubjectid != -1) {
			Subject c = GlobalData.getSubjectById(currentsubjectid);
			spinner.setSelection(GlobalData.MySubjects.indexOf(c) + 1);
			setDisplayToSubject(c);
		}
	}

	private void fillRoomsList() {
		roomsDisplay = (LinearLayout) findViewById(R.id.hscRooms);
		if (roomsArray == null)
			roomsArray = new ArrayList<String>();
		for (String s : roomsArray)
			addNewRoom(s);
	}

	private void initialize() {
		Button bCancel = (Button) findViewById(R.id.bCcCancel);
		Button bSave = (Button) findViewById(R.id.bCcSave);
		Button bcol = (Button) findViewById(R.id.bColorChoose);

		Button bnew = (Button) findViewById(R.id.bNew);
		bdelete = (Button) findViewById(R.id.bDelete);
		etSubject = (EditText) findViewById(R.id.etSubject);
		etTeacher = (EditText) findViewById(R.id.etTeacher);
		bCancel.setOnClickListener(this);
		bSave.setOnClickListener(this);
		LinearLayout colorc = (LinearLayout) findViewById(R.id.llColorChoose);
		colorc.setClickable(true);
		colorc.setOnClickListener(this);
		bcol.setOnClickListener(this);

		bnew.setOnClickListener(this);
		bdelete.setOnClickListener(this);

		colorDisplay = findViewById(R.id.vColorDisplay);
		colorDisplay.setBackgroundColor(ourColor);
		spinner = (SubjectSpinner) findViewById(R.id.spinSubj);
		spinner.setOnItemSelectedListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bNew:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("הזן את מספר החדר");

			final EditText input = new EditText(this);
			input.setInputType(InputType.TYPE_CLASS_TEXT);
			builder.setView(input);

			builder.setPositiveButton("אחלה", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String ans = input.getText().toString();
					addNewRoom(ans);
				}
			});
			builder.setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});

			AlertDialog alertToShow = builder.create();
			alertToShow.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			alertToShow.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					InputMethodManager inputMethodManager = (InputMethodManager) ChooseSubject.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(ChooseSubject.this.getCurrentFocus().getWindowToken(), 0);
				}
			});
			alertToShow.show();

			break;

		case R.id.bDelete:
			v = selectedRoom;
			selectedRoom = null;
			bdelete.setEnabled(false);
			roomsArray.remove(((TextView) v).getText().toString());
			roomsDisplay.removeView(v);
			break;
		case R.id.bCcCancel:
			setResult(RESULT_CANCELED, null);
			finish();
			break;
		case R.id.bCcSave:
			if (!checkValidation())
				break;

			Intent classIntent = new Intent();
			Bundle backpack = new Bundle();
			if (currentsubjectid == -1)
				currentsubjectid = GlobalData.addNewSubject(etSubject.getText().toString(), etTeacher.getText().toString(), ourColor, roomsArray, this);
			else {
				GlobalData.updateSubject(currentsubjectid, etSubject.getText().toString(), etTeacher.getText().toString(), ourColor, roomsArray, this);

			}
			// openId, subject, teacher, color, rooms
			// id, subject, teacher,color
			backpack.putInt("id", currentsubjectid);
			backpack.putString("subject", etSubject.getText().toString());
			backpack.putString("teacher", etTeacher.getText().toString());
			backpack.putInt("color", ourColor);
			if (selectedRoom != null)
				backpack.putString("room", selectedRoom.getText().toString());

			classIntent.putExtras(backpack);
			setResult(RESULT_OK, classIntent);
			finish();

			break;
		case R.id.bColorChoose:
		case R.id.llColorChoose:
			AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, ourColor, new OnAmbilWarnaListener() {
				@Override
				public void onOk(AmbilWarnaDialog dialog, int color) {
					ourColor = color;
					colorDisplay.setBackgroundColor(ourColor);
				}

				@Override
				public void onCancel(AmbilWarnaDialog dialog) {
				}
			});
			dialog.show();
			break;
		default:
			clickedOnRoom(v);
			break;
		}
	}

	private boolean checkValidation() {
		if (selectedRoom == null && roomsArray.size() != 0) {
			Toast.makeText(this, "אנא בחר אחד מן החדרים", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (etSubject.getText().toString() != "")
			etSubject.setText(shortenIt(etSubject.getText().toString()));
		if (etSubject.getText().toString().isEmpty()) {
			Toast.makeText(this, "אנא הזן את שם המקצוע", Toast.LENGTH_SHORT).show();
			return false;
		}
		for (int i = 0; i < GlobalData.MySubjects.size(); i++) {
			if (GlobalData.MySubjects.get(i).myName.equals(etSubject.getText().toString()) && currentsubjectid != GlobalData.MySubjects.get(i).myId) {
				Toast.makeText(this, "שם המקצוע בשימוש, אנא בחר שם מקצוע יחודי", Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		return true;
	}

	private String shortenIt(String str) {
		int index = str.length() - 1;
		while (index >= 0 && str.charAt(index) == ' ')
			index--;

		index++;
		if (index <= 0)
			return "";
		return str.substring(0, index);
	}

	private void clickedOnRoom(View v) {
		if (selectedRoom != v) {
			selected(v);
			if (selectedRoom != null)
				deSelected(selectedRoom);
			selectedRoom = (TextView) v;
		}
	}

	private void addNewRoom(String room) {
		if (!roomsArray.contains(room)) {
			roomsArray.add(room);
			TextView tv = new TextView(this);
			tv.setText(room);
			tv.setId(findId());
			tv.setClickable(true);
			tv.setOnClickListener(this);
			int pad = (int) (10 * dp);
			tv.setPadding(pad, pad, pad, pad);

			tv.setBackgroundResource(R.drawable.border);
			tv.setGravity(Gravity.CENTER);

			roomsDisplay.addView(tv, lp);
			if (selectedRoom == null)
				clickedOnRoom(tv);
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("יש כבר חדר כזה!");

			builder.setNegativeButton("חזור", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});

			builder.show();

		}
	}

	private void selected(View v) {
		TextView textView = (TextView) v;
		bdelete.setEnabled(true);
		textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
	}

	private void deSelected(View v) {
		TextView textView = (TextView) v;
		textView.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
	}

	static int id = 1;

	private int findId() {
		View v = findViewById(id);
		while (v != null)
			v = findViewById(++id);
		return id++;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if (position != 0)
			setDisplayToSubject(GlobalData.MySubjects.get(position - 1));
		else {
			setFreshDisplay();
			currentsubjectid = -1;
		}
	}

	private void setDisplayToSubject(Subject i) {
		setFreshDisplay();
		etSubject.setText(i.myName);
		etTeacher.setText(i.myTeacher);
		ourColor = i.myColor;
		selectedRoom = null;
		bdelete.setEnabled(false);

		for (String room : i.m_rooms)
			addNewRoom(room);

		colorDisplay.setBackgroundColor(i.myColor);
		currentsubjectid = i.myId;
	}

	private void setFreshDisplay() {
		etSubject.setText("");
		etTeacher.setText("");
		ourColor = 0xFF000000;
		if (roomsArray == null)
			roomsArray = new ArrayList<String>();
		else
			roomsArray.clear();
		roomsDisplay.removeAllViews();
		selectedRoom = null;
		bdelete.setEnabled(false);
		colorDisplay.setBackgroundColor(ourColor);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		if (currentsubjectid == -1)
			spinner.setSelection(0);
	}
}
