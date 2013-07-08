package com.aplus;

import tal.shaltis.FloatGroupView;
import tal.shaltis.FloatGroupView.FloatDirection;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Test extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testmyfloat);

		initialize();
	}

	FloatGroupView fgv;

	/**
	 * initializing
	 */
	private void initialize() {
		fgv = (FloatGroupView) findViewById(R.id.float1);
		for (int i = 0; i < 100; i++) {
			TextView tv = new TextView(this);
			tv.setText(randomize("This is just a test") + "!");
			fgv.addView(tv);
			tv.setBackgroundResource(R.drawable.border);
		}
		Button b = (Button) findViewById(R.id.bCenterf), b1 = (Button) findViewById(R.id.bRightf), b2 = (Button) findViewById(R.id.bLeftf);
		b.setOnClickListener(this);
		b1.setOnClickListener(this);
		b2.setOnClickListener(this);
	}

	private String randomize(String s) {
		return s.substring(0, (int) (Math.random() * s.length()));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bCenterf:
			fgv.setFloatDirection(FloatDirection.center);
			break;
		case R.id.bRightf:
			fgv.setFloatDirection(FloatDirection.right);
			break;
		case R.id.bLeftf:
			fgv.setFloatDirection(FloatDirection.left);

			break;
		}
	}
}
