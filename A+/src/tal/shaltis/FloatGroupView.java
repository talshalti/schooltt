/**
 * 
 */
package tal.shaltis;

import com.aplus.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * @author Tal Shalti
 * 
 */
public class FloatGroupView extends RelativeLayout {

	private FloatDirection mFloatDirection;

	/**
	 * @param context
	 * @param attrs
	 */
	public FloatGroupView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FloatGroupView, 0, 0);
		mFloatDirection = FloatDirection.center;
		mFloatDirection.numVal = a.getInt(R.styleable.FloatGroupView_floatdir, 2);
		a.recycle();

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int maxHeightInLine = 0, height, width;
		// TODO set center float
		final int children = getChildCount();
		switch (mFloatDirection.getNumVal()) {
		case 0:
			final int startOfLine = l;
			for (int i = 0; i < children; i++) {
				View child = getChildAt(i);

				if (child.getVisibility() != View.VISIBLE)
					continue;

				height = child.getMeasuredHeight();
				width = child.getMeasuredWidth();
				if (l + width > r) {
					// new line
					l = startOfLine;
					t += maxHeightInLine;
					maxHeightInLine = 0;
					width = Math.min(width, r - l);
				}
				child.layout(l, t, l + width, t + height);
				maxHeightInLine = Math.max(maxHeightInLine, height);
				l += width;
			}
			break;
		case 1:

			final int startOfLine1 = r;
			for (int i = 0; i < children; i++) {
				View child = getChildAt(i);

				if (child.getVisibility() != View.VISIBLE)
					continue;

				height = child.getMeasuredHeight();
				width = child.getMeasuredWidth();
				if (r - width < l) {
					// new line
					r = startOfLine1;
					t += maxHeightInLine;
					maxHeightInLine = 0;
					width = Math.min(width, r - l);

				}
				child.layout(r - width, t, r, t + height);
				maxHeightInLine = Math.max(maxHeightInLine, height);
				r -= width;
			}
			break;

		case 2:
			final int startOfLine2 = l;
			int startingIndex = 0;
			int lwidth,
			lheight;
			for (int i = 0; i < children; i++) {
				View child = getChildAt(i);

				if (child.getVisibility() != View.VISIBLE)
					continue;

				height = child.getMeasuredHeight();
				width = child.getMeasuredWidth();
				if (l + width > r) {
					int leftOver = r - l;
					l = leftOver / 2;
					for (int j = startingIndex; j < i; j++) {
						View lchild = getChildAt(j);
						lheight = lchild.getMeasuredHeight();
						lwidth = lchild.getMeasuredWidth();
						lchild.layout(l, t, l + lwidth, t + lheight);
						l += lwidth;

					}
					// new line
					l = startOfLine2;
					t += maxHeightInLine;
					maxHeightInLine = 0;
					startingIndex = i;
				}
				maxHeightInLine = Math.max(maxHeightInLine, height);
				l += width;
			}
			int leftOver = r - l;
			l = leftOver / 2;
			for (int j = startingIndex; j < children; j++) {
				View lchild = getChildAt(j);
				lheight = lchild.getMeasuredHeight();
				lwidth = lchild.getMeasuredWidth();
				lchild.layout(l, t, l + lwidth, t + lheight);
				l += lwidth;
			}
			break;
		}
	}

	public FloatDirection getFloatDirection() {
		return mFloatDirection;
	}

	public void setFloatDirection(FloatDirection floatdir) {
		this.mFloatDirection = floatdir;
		requestLayout();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int desiredHeight = getDesiredHeight(widthSize);

		int width;
		int height;

		width = widthSize;

		// Measure Height
		if (heightMode == MeasureSpec.EXACTLY) {
			// Must be this size
			height = heightSize;
		} else if (heightMode == MeasureSpec.AT_MOST) {
			// Can't be bigger than...
			height = Math.min(desiredHeight, heightSize);
		} else {
			// Be whatever you want
			height = desiredHeight;
		}

		// MUST CALL THIS
		setMeasuredDimension(width, height);
	}

	/**
	 * @param widthSize
	 * @return the desired height
	 */
	private int getDesiredHeight(int widthSize) {
		int maxHeightInLine = 0, height, width;

		final int children = getChildCount();
		int l = 0, t = 0;
		for (int i = 0; i < children; i++) {
			View child = getChildAt(i);

			if (child.getVisibility() != View.VISIBLE)
				continue;

			height = child.getMeasuredHeight();
			width = child.getMeasuredWidth();
			if (l + width > widthSize) {
				// new line
				l = 0;
				t += maxHeightInLine;
				maxHeightInLine = 0;
				width = Math.min(width, widthSize);
			}
			maxHeightInLine = Math.max(maxHeightInLine, height);
			l += width;
		}
		return t + maxHeightInLine;
	}

	public enum FloatDirection {
		right(0), left(1), center(2);

		private int numVal;

		FloatDirection(int numVal) {
			this.numVal = numVal;
		}

		public int getNumVal() {
			return numVal;
		}
	}

}
