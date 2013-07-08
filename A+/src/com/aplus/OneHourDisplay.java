package com.aplus;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

public class OneHourDisplay extends RelativeLayout implements OnClickListener {

	public OneHourDisplay(Context context,AttributeSet attr) {
		super(context,attr);
		// TODO Auto-generated constructor stub
	}


	String subject;
	int room;
	Time start;
	Time end;
	Color color;
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}
}


