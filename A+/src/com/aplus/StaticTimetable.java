package com.aplus;

import java.util.ArrayList;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.aplus.TimeLesson.TimeLessonDisplay;
import com.aplus.Displays.SubjectDisplay;

public class StaticTimetable extends Activity implements OnClickListener,
		OnTouchListener {

	int LEFTWIDTH;
	static int SCREENWIDTH;
	View[][] idMat; // [day][hour]
	int[][] subjectIdMat;
	LinkedList<TimeLessonDisplay> timeLessonsDisplay = new LinkedList<TimeLesson.TimeLessonDisplay>();
	ArrayList<TimeLesson> timeLessons = new ArrayList<TimeLesson>();
	ArrayList<TimeLesson> timeLessonsSave = new ArrayList<TimeLesson>();
	int num_of_days;
	int num_of_hours;
	RelativeLayout table;
	ViewFlipper bottomBar;

	boolean isEditOn = false;
	ScrollView tableContainer;
	boolean[][] hideView;
	int dp100;
	int sideHeadHeight;
	SubjectDisplay chooseclass;
	int currentsubjectid = -1;
	String room;
	int sideHeadMargin;
	LayoutParams basicLayoutParams = new LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	private int touchPositionY;
	// ID פנוי
	static int id = 1;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// GlobalData.SetMyTL();
		setContentView(R.layout.statictt);
		dp100 = (int) (100 * this.getResources().getDisplayMetrics().density);
		sideHeadHeight = (int) this.getResources().getDisplayMetrics().scaledDensity * 40;
		sideHeadMargin = dp100 / 50;
		LEFTWIDTH = dp100 / 5;
		setupVars();
		createTable();
		tableContainer.addView(table);
		hideShowAllViewCells(true);
	}

	/*
	 * קורא לכל הפונקציות בניה האחרות הלוקחות חלק בבנית הטבלה
	 */
	private void createTable() {
		String[] days = { "ראשון", "שני", "שלישי", "רביעי", "חמישי", "שישי" };
		LayoutParams lpFill = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		LayoutParams lpMargin = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		lpMargin.setMargins(sideHeadMargin, sideHeadMargin, 0, 0);

		table.setLayoutParams(lpFill);

		// corner

		LayoutParams lpHH = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		lpHH.setMargins(0, sideHeadMargin, 0, 0);

		idMat[0][0] = createCorner();
		table.addView(idMat[0][0], lpHH);

		// top Heads
		for (int i = 1; i <= num_of_days; i++) {
			idMat[i][0] = createTopHeads(i, days, lpMargin);
			table.addView(idMat[i][0]);
		}

		// side Heads
		for (int i = 0; i < num_of_hours; i++) {
			idMat[0][i + 1] = createSideHeads(i, lpHH, 0);
			table.addView(idMat[0][i + 1]);
		}

		// all the rest
		lpMargin.setMargins(0, 0, 0, 0);
		createTimeLessons(GlobalData.MyTimeLessons, lpMargin);
		for (int i = 0; i < num_of_hours; i++)
			for (int j = 0; j < num_of_days; j++) {
				if (hideView[j][i])
					continue;
				idMat[j + 1][i + 1] = createCell(lpMargin, i, j);
				table.addView(idMat[j + 1][i + 1]);
			}
		table.setBackgroundColor(Color.BLACK);
		table.setPadding(0, 0, 0, sideHeadMargin);
	}

	// בונה ומכניס את שעות הלימוד
	private void createTimeLessons(ArrayList<TimeLesson> myTimeLessons,
			LayoutParams basicp) {
		for (TimeLesson tl : myTimeLessons) {
			createTimeLessonDisplay(tl, basicp);
		}
	}

	// בונה תצוגת שעות
	private void createTimeLessonDisplay(TimeLesson tl, LayoutParams basicp) {
		timeLessons.add(tl);
		TimeLessonDisplay tv = tl.createDisplay(this);
		timeLessonsDisplay.add(tv); // adding
		LayoutParams tvLayout = new LayoutParams(basicp);
		for (int i = 0; i < tl.mySpan; i++) {
			if (hideView[tl.myDay][tl.myHour + i])
				continue;
		}
		for (int i = 0; i < tl.mySpan; i++) {
			hideView[tl.myDay][tl.myHour + i] = true;
			if (idMat[tl.myDay + 1][tl.myHour + i + 1] != null)
				table.removeView(idMat[tl.myDay + 1][tl.myHour + i + 1]);
			idMat[tl.myDay + 1][tl.myHour + i + 1] = tv;
			subjectIdMat[tl.myDay][tl.myHour + i] = tl.mySub.myId;
		}
		tv.setLayoutParams(tvLayout);
		setLPtoTLD(tv);
		table.addView(tv);
		tv.setOnTouchListener(this);
		tv.setOnClickListener(this);
	}

	// מגדיר משתנים
	private void setupVars() {
		num_of_days = 6;
		num_of_hours = 20;
		idMat = new View[num_of_days + 1][num_of_hours + 1];
		subjectIdMat = new int[num_of_days][num_of_hours];
		hideView = new boolean[num_of_days][num_of_hours];
		SCREENWIDTH = getWidth();
		table = new RelativeLayout(this);

		Button edit = (Button) findViewById(R.id.bStartEdit);
		Button save = (Button) findViewById(R.id.bSaveEdit);
		Button cancel = (Button) findViewById(R.id.bCancelEdit);
		chooseclass = (SubjectDisplay) findViewById(R.id.tvChooseClass);
		chooseclass.setClickable(true);
		bottomBar = (ViewFlipper) findViewById(R.id.vfBottomBar);
		tableContainer = (ScrollView) findViewById(R.id.svTable);

		edit.setOnClickListener(this);
		save.setOnClickListener(this);
		cancel.setOnClickListener(this);
		chooseclass.setOnClickListener(this);
		tableContainer.setOnClickListener(this);

	}

	// בונה תא כותרת
	private TextView createTopHeads(int day, String[] days,
			LayoutParams lpMargin) {
		TextView tv = new TextView(this);
		tv.setId(findId());
		tv.setBackgroundResource(R.color.heads);
		tv.setText(days[day - 1]);
		tv.setWidth((SCREENWIDTH - LEFTWIDTH) / num_of_days
				- lpMargin.leftMargin);
		tv.setMaxWidth((SCREENWIDTH - LEFTWIDTH) / num_of_days
				- lpMargin.leftMargin);
		LayoutParams tvLayout = new LayoutParams(lpMargin);
		tvLayout.addRule(RelativeLayout.RIGHT_OF, idMat[day - 1][0].getId());
		tv.setLayoutParams(tvLayout);

		tv.setGravity(Gravity.CENTER);
		return tv;
	}

	// בונה תא כותרת צדדי
	private TextView createSideHeads(int hour, LayoutParams lpHH, int hourOffSet) {
		TextView tv = new TextView(this);
		tv.setId(findId());
		tv.setBackgroundResource(R.color.heads);
		tv.setText(Integer.toString(hour + hourOffSet));
		tv.setWidth(LEFTWIDTH);
		tv.setMaxWidth(LEFTWIDTH);
		tv.setHeight(sideHeadHeight);
		LayoutParams lpSH = new LayoutParams(lpHH);
		lpSH.addRule(RelativeLayout.BELOW, idMat[0][hour].getId());

		tv.setGravity(Gravity.CENTER);
		tv.setLayoutParams(lpSH);
		return tv;
	}

	// פינה
	private TextView createCorner() {
		TextView tv = new TextView(this);
		tv.setId(findId());
		tv.setBackgroundResource(R.color.corner);
		tv.setText(" ");
		tv.setWidth(LEFTWIDTH);
		tv.setMaxWidth(LEFTWIDTH);
		return tv;
	}

	// בונה תא ריק
	private View createCell(LayoutParams basicp, int hour, int day) {
		View tv = new View(this);
		subjectIdMat[day][hour] = 0;
		tv.setBackgroundResource(R.color.cell_color);

		LayoutParams tvLayout = new LayoutParams(basicp);
		tvLayout.width = (SCREENWIDTH - LEFTWIDTH) / num_of_days
				- basicp.leftMargin;

		tvLayout.addRule(RelativeLayout.ALIGN_LEFT, idMat[day + 1][0].getId());
		tvLayout.addRule(RelativeLayout.ALIGN_RIGHT, idMat[day + 1][0].getId());
		tvLayout.addRule(RelativeLayout.ALIGN_TOP, idMat[0][hour + 1].getId());
		tvLayout.addRule(RelativeLayout.ALIGN_BOTTOM,
				idMat[0][hour + 1].getId());
		tv.setLayoutParams(tvLayout);
		tv.setOnClickListener(this);
		return tv;
	}

	private int findId() {
		View v = findViewById(id);
		while (v != null)
			v = findViewById(++id);
		return id++;
	}

	// משיג רוחב מסך
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private int getWidth() {
		Point size = new Point();
		WindowManager w = getWindowManager();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			w.getDefaultDisplay().getSize(size);
			return size.x;
		} else {
			Display d = w.getDefaultDisplay();
			return d.getWidth();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				Bundle basket = data.getExtras();
				// id, subject, teacher,color
				chooseclass.setSubId(basket.getInt("id"));
				currentsubjectid = chooseclass.subject.myId;
				room = basket.getString("room", "");
				for (TimeLessonDisplay tld : timeLessonsDisplay) {
					if (tld.myTL.mySub.myId == currentsubjectid)
						tld.update();
				}
			} else {
				currentsubjectid = -1;
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bStartEdit:
			if (!isEditOn) {
				// Start editing
				isEditOn = true;
				hideShowAllViewCells(false);
				saveCurrentData();
				chooseclass.clear();
				bottomBar.showNext();
				currentsubjectid = -1;
			}
			break;
		case R.id.bSaveEdit:
			if (isEditOn) {
				// Implement changes
				isEditOn = false;
				hideShowAllViewCells(true);
				chooseclass.clear();
				bottomBar.showNext();
				currentsubjectid = -1;
				GlobalData.MyTimeLessons = timeLessons;
				GlobalData.saveTimeLessons(this);
			}
			break;

		case R.id.bCancelEdit:
			if (isEditOn) {
				// Recover From Saved state
				isEditOn = false;
				recoverFromSaved();
				hideShowAllViewCells(true);
				bottomBar.showNext();
			}
			break;
		case R.id.tvChooseClass:
			if (isEditOn) {
				Intent i = new Intent(StaticTimetable.this, ChooseSubject.class);
				Bundle basket = new Bundle();
				basket.putInt("subid", currentsubjectid);
				i.putExtras(basket);
				startActivityForResult(i, 0);
			}
			break;
		default:
			if (isEditOn && currentsubjectid != -1) {
				Point p = getPosition(v);
				if (p != null) {
					if (v.getClass().equals(TimeLessonDisplay.class)) {
						p.y += whereHeClicked();
						rearrangeDay(p.x, p.y, false);
					} else {
						subjectIdMat[p.x][p.y] = currentsubjectid;
						table.removeView(v);
						rearrangeDay(p.x, p.y, true);
					}
				}
			}
			break;
		}
	}

	private void recoverFromSaved() {
		for (int i = timeLessonsDisplay.size() - 1; i >= 0; i--) {
			TimeLessonDisplay tld = timeLessonsDisplay.get(i);
			removeTLDandAddCells(tld);
		}
		createTimeLessons(timeLessonsSave, basicLayoutParams);
	}

	private void saveCurrentData() {
		timeLessonsSave.clear();
		for (TimeLesson tl : timeLessons) {
			timeLessonsSave.add(tl);
		}
	}

	private void hideShowAllViewCells(boolean hide) {
		for (int i = 0; i < num_of_days; i++) {
			for (int j = 0; j < num_of_hours; j++) {
				if (subjectIdMat[i][j] == 0)
					idMat[i + 1][j + 1].setVisibility(hide ? 4 : 0);
			}
		}
		table.setBackgroundColor(hide ? Color.WHITE : Color.BLACK);
	}

	private void rearrangeDay(int day, int hour, boolean add) {
		if (add) {
			if (hour > 0
					&& subjectIdMat[day][hour - 1] == currentsubjectid
					&& ((TimeLessonDisplay) idMat[day + 1][hour]).myTL.myRoom == room) {

				if (hour + 1 < num_of_hours
						&& subjectIdMat[day][hour + 1] == currentsubjectid
						&& ((TimeLessonDisplay) idMat[day + 1][hour + 2]).myTL.myRoom == room) {
					TimeLessonDisplay tld = (TimeLessonDisplay) idMat[day + 1][hour];
					TimeLessonDisplay tld2 = (TimeLessonDisplay) idMat[day + 1][hour + 2];
					TimeLesson tl = tld2.myTL;
					idMat[day + 1][hour + 1] = tld;
					for (int i = 0; i < tl.mySpan; i++) {
						idMat[tl.myDay + 1][tl.myHour + i + 1] = tld;
						subjectIdMat[tl.myDay][tl.myHour + i] = tld.myTL.mySub.myId;
					}
					tld.myTL.mySpan += 1 + tld2.myTL.mySpan;
					setLPtoTLD(tld);
					removeTLD(tld2);
				} else {
					TimeLessonDisplay tld = (TimeLessonDisplay) idMat[day + 1][hour];
					idMat[day + 1][hour + 1] = tld;
					tld.myTL.mySpan++;
					setLPtoTLD(tld);
				}
			} else if (hour + 1 < num_of_hours
					&& subjectIdMat[day][hour + 1] == currentsubjectid
					&& ((TimeLessonDisplay) idMat[day + 1][hour + 2]).myTL.myRoom == room) {
				TimeLessonDisplay tld = (TimeLessonDisplay) idMat[day + 1][hour + 2];
				idMat[day + 1][hour + 1] = tld;
				tld.myTL.mySpan++;
				tld.myTL.myHour--;
				setLPtoTLD(tld);
			} else {
				TimeLesson tl = new TimeLesson(currentsubjectid, day, hour, 1,
						room);
				createTimeLessonDisplay(tl, basicLayoutParams);
			}
		} else {
			// delete
			if (subjectIdMat[day][hour] != currentsubjectid)
				return;
			TimeLessonDisplay tld = (TimeLessonDisplay) idMat[day + 1][hour + 1];
			idMat[day + 1][hour + 1] = createCell(basicLayoutParams, hour, day);
			table.addView(idMat[day + 1][hour + 1]);
			subjectIdMat[day][hour] = 0;
			if (tld.myTL.myHour != hour) {
				if (tld.myTL.myHour + tld.myTL.mySpan == hour + 1) {
					tld.myTL.mySpan--;
					setLPtoTLD(tld);
				} else {
					TimeLesson tl = new TimeLesson(currentsubjectid, day,
							hour + 1, tld.myTL.myHour + tld.myTL.mySpan - hour
									- 1, room);
					tld.myTL.mySpan = hour - tld.myTL.myHour;
					removeTLD(tld);
					createTimeLessonDisplay(tld.myTL, basicLayoutParams);
					createTimeLessonDisplay(tl, basicLayoutParams);
				}
			} else if (tld.myTL.mySpan == 1) {
				removeTLD(tld);
			} else {
				tld.myTL.mySpan--;
				tld.myTL.myHour++;
				setLPtoTLD(tld);
			}
		}
	}

	private void removeTLDandAddCells(TimeLessonDisplay tld) {
		TimeLesson tl = tld.myTL;
		for (int i = 0; i < tld.myTL.mySpan; i++) {
			idMat[tl.myDay + 1][tl.myHour + i + 1] = createCell(
					basicLayoutParams, tl.myHour + i, tl.myDay);
			table.addView(idMat[tl.myDay + 1][tl.myHour + i + 1]);
			subjectIdMat[tl.myDay][tl.myHour + i] = 0;
		}
		removeTLD(tld);
	}

	private void removeTLD(TimeLessonDisplay tld) {
		table.removeView(tld);
		timeLessonsDisplay.remove(tld);
		timeLessons.remove(tld.myTL);
	}

	private void setLPtoTLD(TimeLessonDisplay tld) {
		LayoutParams lp = (LayoutParams) tld.getLayoutParams();
		lp.addRule(RelativeLayout.ALIGN_LEFT,
				idMat[tld.myTL.myDay + 1][0].getId());
		lp.addRule(RelativeLayout.ALIGN_RIGHT,
				idMat[tld.myTL.myDay + 1][0].getId());
		lp.addRule(RelativeLayout.ALIGN_TOP,
				idMat[0][tld.myTL.myHour + 1].getId());
		lp.addRule(RelativeLayout.ALIGN_BOTTOM, idMat[0][tld.myTL.myHour
				+ tld.myTL.mySpan].getId());

		tld.setLayoutParams(lp);
	}

	private Point getPosition(View v) {
		for (int i = 1; i < num_of_days + 1; i++) {
			for (int j = 1; j < num_of_hours + 1; j++) {
				if (idMat[i][j].equals(v)) {
					return new Point(i - 1, j - 1);
				}
			}
		}
		return null;
	}

	private int whereHeClicked() {
		return touchPositionY / (sideHeadHeight + sideHeadMargin);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		touchPositionY = (int) event.getY();

		return false;
	}
}
