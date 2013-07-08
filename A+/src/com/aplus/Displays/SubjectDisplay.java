package com.aplus.Displays;

import com.aplus.GlobalData;
import com.aplus.R;
import com.aplus.Subject;
import com.aplus.TimeLesson;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

public class SubjectDisplay extends TextView {

	public Subject subject;

	public SubjectDisplay(Context context) {
		super(context);
		Drawable shape = getResources().getDrawable(R.drawable.border);
		setBackgroundResource(R.drawable.border);
		setBackground(shape);

		this.subject = null;
		setText("בחר שיעור");
		setGravity(Gravity.CENTER);
	}

	public SubjectDisplay(Context context, AttributeSet attr) {
		super(context, attr);
		Drawable shape = getResources().getDrawable(R.drawable.border);
		setBackgroundResource(R.drawable.border);
		setBackground(shape);

		this.subject = null;
		setText("בחר שיעור");
		setGravity(Gravity.CENTER);
	}

	public SubjectDisplay(Context context, int id) {
		super(context);
		this.subject = GlobalData.getSubjectById(id);
		Drawable shape = getResources().getDrawable(R.drawable.border);
		setBackgroundResource(R.drawable.border);
		shape.setColorFilter(subject.myColor, Mode.MULTIPLY);
		setBackground(shape);
		setText(subject.myName);
		setTextColor(TimeLesson.contrastColor(subject.myColor));
		setGravity(Gravity.CENTER);
	}

	public void setSubId(int id) {
		this.subject = GlobalData.getSubjectById(id);
		Drawable shape = getResources().getDrawable(R.drawable.border);
		setBackgroundResource(R.drawable.border);
		shape.setColorFilter(subject.myColor, Mode.MULTIPLY);
		setBackground(shape);
		setTextColor(TimeLesson.contrastColor(subject.myColor));
		setText(subject.myName);
	}

	public void clear() {
		Drawable shape = getResources().getDrawable(R.drawable.border);
		setBackgroundResource(R.drawable.border);
		setBackground(shape);

		this.subject = null;
		setText("בחר שיעור");
		setTextColor(Color.BLACK);
		setGravity(Gravity.CENTER);

	}
}