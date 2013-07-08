/**
 * 
 */
package com.aplus.Displays;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.aplus.GlobalData;
import com.aplus.R;
import com.aplus.Subject;

/**
 * @author Administrator
 * 
 */
public class SubjectSpinner extends Spinner {

	private boolean mShowAddNewClass;

	public boolean getShowAddNewClass() {
		return mShowAddNewClass;
	}

	public void setShowAddNewClass(boolean ShowAddNewClass) {
		this.mShowAddNewClass = ShowAddNewClass;
	}

	/**
	 * @param context
	 * @param attrs
	 * 
	 */
	public SubjectSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SubjectSpinner, 0, 0);
		// mFloatDirection = FloatDirection.center;
		mShowAddNewClass = a.getBoolean(R.styleable.SubjectSpinner_ShowAddNewClass, true);
		if (!isInEditMode())
			setSpinner();
		else
			setSpinnerEditMode();
		a.recycle();
	}

	/**
	 * 
	 */
	private void setSpinnerEditMode() {
		// TODO Auto-generated method stub
		String[] subjects = new String[(mShowAddNewClass ? 1 : 0)];
		int i = 0;
		if (mShowAddNewClass)
			subjects[i++] = "מקצוע חדש...";
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, subjects);
		setAdapter(adapter);
		if (subjects.length == (mShowAddNewClass ? 1 : 0)) {
			setEnabled(false);
		}

	}

	/*
	 * NOTICE! if ShowAddNewClass than the result will be higher by 1 than
	 * expected
	 */
	@Override
	public void setOnItemClickListener(OnItemClickListener l) {
		// TODO Auto-generated method stub
		super.setOnItemClickListener(l);
	}

	/**
	 * 
	 */
	private void setSpinner() {
		String[] subjects = new String[GlobalData.MySubjects.size() + (mShowAddNewClass ? 1 : 0)];
		int i = 0;
		if (mShowAddNewClass)
			subjects[i++] = "מקצוע חדש...";
		for (Subject t : GlobalData.MySubjects) {
			subjects[i++] = t.myName;
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, subjects);
		setAdapter(adapter);
		if (subjects.length <= 1) {
			setEnabled(false);
		}
	}
}
