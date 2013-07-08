package com.aplus;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.TextView;

public class TimeLesson {
	public Subject mySub;
	public int myDay;
	public int myHour;
	public int mySpan;
	String myRoom;

	public TimeLesson(int id, int day, int hour, int span, String room) {
		myDay = day;
		myHour = hour;
		mySub = GlobalData.getSubjectById(id);
		mySpan = span;
		myRoom = room;
	}

	public TimeLesson(Subject s, int day, int hour, int span, String room) {
		myDay = day;
		myHour = hour;
		mySub = s;
		mySpan = span;
		myRoom = room;
	}

	public TimeLessonDisplay createDisplay(Context context) {
		if (this.mySub == null)
			return new TimeLessonDisplay(context, this, true);
		return new TimeLessonDisplay(context, this);
	}

	public static int contrastColor(int color) {

		// Counting the perceptive luminance - human eye favors green color...
		double a = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color
				.blue(color)) / 255;

		if (a < 0.5)
			return Color.BLACK; // bright colors - black font
		else
			return Color.WHITE; // dark colors - white font

	}

	public class TimeLessonDisplay extends TextView {

		public TimeLesson myTL;

		public TimeLessonDisplay(Context context, TimeLesson tl, boolean dummy) {
			super(context);
			this.myTL = tl;
			Drawable shape = getResources().getDrawable(R.drawable.border);
			setBackgroundResource(R.drawable.border);
			setBackground(shape);

			setText("subject not found");
			setGravity(Gravity.CENTER);
		}

		public TimeLessonDisplay(Context context, TimeLesson tl) {
			super(context);
			this.myTL = tl;
			Drawable shape = getResources().getDrawable(R.drawable.border);
			setBackgroundResource(R.drawable.border);
			shape.setColorFilter(tl.mySub.myColor, Mode.MULTIPLY);
			setBackground(shape);
			setTextColor(contrastColor(tl.mySub.myColor));
			setText(tl.mySub.myName);
			setGravity(Gravity.CENTER);
		}

		public void update() {
			Drawable shape = getResources().getDrawable(R.drawable.border);
			setBackgroundResource(R.drawable.border);
			shape.setColorFilter(myTL.mySub.myColor, Mode.MULTIPLY);
			setBackground(shape);
			setText(myTL.mySub.myName);
			setTextColor(contrastColor(myTL.mySub.myColor));
		}
	}
}
