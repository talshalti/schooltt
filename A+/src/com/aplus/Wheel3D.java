package com.aplus;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class Wheel3D extends Activity implements OnClickListener {

	LinearLayout sv;
	SurfaceHolder sh;
	EditText[] et;
	TextView log;
	Shalti ourNewSurface;
	boolean getCrazy = false;
	boolean isReadyGo = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wheel3d);
		sv = (LinearLayout) findViewById(R.id.vCanvas);

		Button b = (Button) findViewById(R.id.bPaint);
		b.setOnClickListener(this);
		et = new EditText[5];
		et[0] = (EditText) findViewById(R.id.etDistance);
		et[1] = (EditText) findViewById(R.id.etRadios);
		et[2] = (EditText) findViewById(R.id.etWidth);
		et[4] = (EditText) findViewById(R.id.etDAngle);
		log = (TextView) findViewById(R.id.tvP);
		ourNewSurface = new Shalti(this, 500, 350, 200, 5);
		Set();
		sv.addView(ourNewSurface);
	}

	private void Set() {
		et[0].setText(Double.toString(ourNewSurface.getDistance()));
		et[1].setText(Double.toString(ourNewSurface.getRadios()));
		et[2].setText(Double.toString(ourNewSurface.getWidth()));
		et[4].setText(Double.toString(ourNewSurface.getDensity()));
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		ourNewSurface.pause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		ourNewSurface.resume();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		ourNewSurface.setWheel(Float.valueOf(et[1].getText().toString()),
				Float.valueOf(et[0].getText().toString()),
				Float.valueOf(et[2].getText().toString()));
		ourNewSurface.setDensity(Float.valueOf(et[4].getText().toString()));
		getCrazy ^= true;
		isReadyGo = true;
	}

	public class ShaltiItem {
		public float Start;
		public float End;
		public Paint Paint;

		public ShaltiItem(float start, float end, Paint p) {
			Paint = p;
			Start = start;
			End = end;
		}
	}

	// =============================================
	public class Shalti extends SurfaceView implements Runnable,
			OnTouchListener {
		long lastMili;
		long lastTouch;
		SurfaceHolder ourHolder;
		Thread ourThread = null;
		boolean isRunning = false, velocityPlus = false;
		Path p2;
		Paint pen;
		float x, y, sX, sY, velocity = 0;
		float centerX = getWidth() / 2;
		float centerY = getHeight() / 2;
		static final float force = (float) 0.0002;
		float xmek = 0;
		float ymek = 0;
		float m_height = 0;
		float d;
		float r;
		float w;
		float angle;
		float density, minAngle, maxAngle;
		float twoPi = (float) (Math.PI * 2);
		public ShaltiItem[] Items;
		public List<ShaltiItem> CurrentDisplay = new LinkedList<ShaltiItem>();

		public Shalti(Context context, float distance, float radios,
				float width, float densit) {
			super(context);

			angle = 0;
			setWheel(radios, distance, width);
			setDensity(densit);

			ourHolder = getHolder();
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			lp.weight = 1;
			setLayoutParams(lp);
			p2 = new Path();

			Items = new ShaltiItem[5];
			for (int i = 0; i < Items.length; i++) {
				Items[i] = new ShaltiItem(i * 50, 50 + i * 50, new Paint());

				Items[i].Paint.setStyle(Paint.Style.FILL_AND_STROKE);
				Items[i].Paint.setAntiAlias(true);
			}
			Items[0].Paint.setColor(android.graphics.Color.RED);
			Items[1].Paint.setColor(android.graphics.Color.BLUE);
			Items[2].Paint.setColor(android.graphics.Color.GREEN);
			Items[3].Paint.setColor(android.graphics.Color.YELLOW);
			Items[4].Paint.setColor(android.graphics.Color.BLACK);
			pen = new Paint();
			pen.setTextSize(20 * getContext().getResources()
					.getDisplayMetrics().density);
			pen.setColor(android.graphics.Color.CYAN);
			this.setOnTouchListener(this);
		}

		public void paintUpDown(Canvas canvas) {
			canvas.drawLine(0, m_height + centerY, centerX, m_height + centerY,
					pen);
			canvas.drawLine(0, -m_height + centerY, centerX, -m_height
					+ centerY, pen);
		}

		public void pause() {
			isRunning = false;
			while (true) {
				try {
					ourThread.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			ourThread = null;
		}

		public void resume() {
			isRunning = true;
			ourThread = new Thread(this);
			ourThread.start();
			lastMili = System.currentTimeMillis();
		}

		public void run() {
			while (isRunning) {
				if (!ourHolder.getSurface().isValid() || !isReadyGo)
					continue;

				centerX = getWidth() / 2;
				centerY = getHeight() / 2;
				Canvas canvas = ourHolder.lockCanvas();
				canvas.drawColor(Color.WHITE);

				// log.setText(angle + " " + m_height);

				paintUpDown(canvas);
				ourHolder.unlockCanvasAndPost(canvas);
				if (velocity != 0) {
					if (velocityPlus) {
						velocity -= (System.currentTimeMillis() - lastMili)
								* force;
						if (velocity < 0)
							velocity = 0;
					} else {
						velocity += (System.currentTimeMillis() - lastMili)
								* force;
						if (velocity > 0)
							velocity = 0;
					}
					angle += (System.currentTimeMillis() - lastMili) * velocity;
				}
				lastMili = System.currentTimeMillis();
			}
		}

		private float getX(float ang) {
			return (float) (xmek / (r * Math.cos(ang) - d));
		}

		private float getY(float ang) {
			return (float) (ymek / (r * Math.cos(ang) - d) * Math.sin(ang));
		}

		@SuppressWarnings("unused")
		private void writeOnSet(String text, float sangle, float eangle,
				Paint pa, Canvas canvas) {
			float middle = (float) ((sangle + eangle) / 360 * Math.PI);

			if (Math.cos(middle) >= r / d && Math.cos(middle) <= 1)
				if (getY(middle) - m_height > pa.getTextSize())
					canvas.drawText(text, centerX - pa.measureText(text) / 2,
							centerY + getY(middle), pa);
				else
					canvas.drawText(text, centerX - pa.measureText(text) / 2,
							centerY + m_height + pa.getTextSize() - 2, pa);

		}

		private float makeItPositive(float ang) {
			// in rad
			return ang + (-(int) (ang / twoPi) + 1) * twoPi;

		}

		@SuppressWarnings("unused")
		private void setPoints(float sangle, float eangle, float density,
				Canvas canvas, Paint pa) {

			sangle = ((float) (sangle / 180 * Math.PI));
			eangle = (float) (eangle / 180 * Math.PI);
			density = ((float) (density / 180 * Math.PI));
			if (!(Math.cos(sangle) >= r / d && Math.cos(sangle) <= 1)) {
				if (sangle > 0)
					sangle += (-Math.acos(r / d) + twoPi - (sangle % (twoPi)));
				else
					sangle += (-Math.acos(r / d) + twoPi - (makeItPositive(sangle) % (twoPi)));
			}
			if (!(Math.cos(eangle) >= r / d && Math.cos(eangle) <= 1)) {
				eangle -= (-Math.acos(r / d) + makeItPositive(eangle) % (twoPi));
			}
			if (sangle >= eangle)
				return;

			p2.reset();
			p2.moveTo(getX(sangle) + centerX, getY(sangle) + centerY);

			// eangle = (float) (eangle - ((int) ((eangle - sangle) / (twoPi)))
			// * twoPi); // removing loops
			float temp = sangle;
			while (sangle + density < eangle) {
				sangle += density;
				lineItRight(sangle);
			}
			lineItRight(eangle);
			lineItLeft(eangle);
			while (sangle > temp) {
				lineItLeft(sangle);
				sangle -= density;
			}
			lineItLeft(temp);
			lineItRight(temp);
			p2.close();
			canvas.drawPath(p2, pa);
		}

		private void lineItRight(float a) {
			p2.lineTo(centerX + getX(a), centerY + getY(a));
		}

		private void lineItLeft(float a) {
			p2.lineTo(centerX - getX(a), centerY + getY(a));
		}

		public void setVars() {
			minAngle = (float) -Math.acos(r / d);
			m_height = getY(minAngle);
			xmek = (r * r - d * d) * w / 2 / d;
			ymek = (r * r - d * d) * r / d;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				x = sX = event.getX();
				y = sY = event.getY();
				lastTouch = System.currentTimeMillis();
				velocity = 0;
				break;
			case MotionEvent.ACTION_UP:
				velocity = (float) ((-Math.atan2(sY - centerY, d) + Math.atan2(
						event.getY() - centerY, d)) / Math.PI * 180 / (System
						.currentTimeMillis() - lastTouch));
				velocityPlus = velocity > 0;
				break;

			default:
				angle += (-Math.atan2(y - centerY, d) + Math.atan2(event.getY()
						- centerY, d))
						/ Math.PI * 180;
				lastTouch = System.currentTimeMillis();
				sY = y;
				x = event.getX();
				y = event.getY();
				break;
			}
			return true;
		}

		public float getRadios() {
			return r;
		}

		public float getDistance() {
			return d;
		}

		public float getWheelWidth() {
			return w;
		}

		public float getAngle() {
			return angle;
		}

		public float getDensity() {
			return density;
		}

		public float getVelocity() {
			return velocity;
		}

		public void setWheel(float radios, float distance, float width) {
			r = radios;
			d = distance;
			w = width;
			setVars();
		}

		public void setDensity(float densit) {
			density = densit;
		}

		public void setVelocity(float vel) {
			velocity = vel;
		}
	}
}
