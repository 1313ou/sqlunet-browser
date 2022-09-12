/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.browser.xn;

import android.content.Context;
import android.graphics.Rect;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.material.snackbar.Snackbar;

import org.sqlunet.browser.R;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

class HomeImageView extends AppCompatImageView
{
	static private final String TAG = "HomeImageView";

	public HomeImageView(@NonNull final Context context)
	{
		super(context);
	}

	public HomeImageView(@NonNull final Context context, final AttributeSet attrs)
	{
		super(context, attrs);
	}

	public HomeImageView(@NonNull final Context context, final AttributeSet attrs, final int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

	@SuppressWarnings({"SameReturnValue"})
	void init()
	{
		setOnTouchListener((v, event) -> {
			// accessibility
			switch (event.getAction())
			{
				case MotionEvent.ACTION_UP:
					v.performClick();
					break;
				case MotionEvent.ACTION_DOWN:
				default:
					break;
			}

			float x = event.getX();
			float y = event.getY();
			Log.i(HomeImageView.TAG, "TOUCH x=" + x + " y=" + y);

			Rect rect = new Rect();
			rect.left = getLeft();
			rect.top = getTop();
			rect.bottom = getBottom();
			rect.right = getRight();

			x += rect.left;
			y += rect.top;

			float w = rect.width();
			float h = rect.height();
			float r = Math.min(w, h) / 2;
			float cx = rect.centerX();
			float cy = rect.centerY();
			float d = distance(cx, cy, x, y);
			double a = arg(cx, cy, x, y);

			// Log.i(TAG, "image rect=" + rect);
			// Log.i(TAG, "center " + cx + "," + cy);
			// Log.i(TAG, "dist=" + d);
			// Log.i(TAG, "arg=" + a);

			int i;
			for (i = 0; i < HomeImageView.rings.length; i++)
			{
				float f = HomeImageView.rings[i];
				// Log.i("RING" + i, "dmax=" + (r * f));
				if (d < r * f)
				{
					break;
				}
			}
			int ring = i;
			Log.d(HomeImageView.TAG, "ring=" + ring);

			for (i = 0; i < HomeImageView.pies.length; i++)
			{
				double s = HomeImageView.pies[i];
				// Log.i("SECTOR" + i, "smax=" + s);
				if (a < s)
				{
					break;
				}
			}
			int pie = i % 3;
			Log.d(HomeImageView.TAG, "pie=" + pie);

			int messageId = 0;
			switch (ring)
			{
				case 0:
					messageId = R.string.wordnet_blurb;
					break;
				case 1:
					switch (pie)
					{
						case 0:
							messageId = R.string.propbank_blurb;
							break;
						case 1:
							messageId = R.string.framenet_blurb;
							break;
						case 2:
							messageId = R.string.verbnet_blurb;
							break;
					}
					break;
				case 2:
					messageId = R.string.predicatematrix_blurb;
					break;
			}
			if (messageId != 0)
			{
				final Context context = getContext();
				final String message = context.getString(messageId);
				final Spanned html = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N ? Html.fromHtml(message, 0) : Html.fromHtml(message);
				//Log.i("Pick ", message);

//				final Toast toast = Toast.makeText(getContext(), html, Toast.LENGTH_LONG);
//				final View view = toast.getView();
//				if (view != null)
//				{
//					view.setBackgroundResource(android.R.drawable.toast_frame);
//					final TextView textView = view.findViewById(android.R.id.message);
//					textView.setBackgroundColor(Color.TRANSPARENT);
//				}
//				toast.show();

				Snackbar snackbar = Snackbar.make(v, html, Snackbar.LENGTH_LONG);
				// View snackBarView = snackbar.getView();
				// snackBarView.setBackgroundColor(context.getResources().getColor(R.color.secondaryColor));
				snackbar.show();
			}
			return false;
		});
	}

	@SuppressWarnings("SameReturnValue")
	@Override
	public boolean performClick()
	{
		// Calls the super implementation, which generates an AccessibilityEvent and calls the onClick() listener on the view, if any
		super.performClick();
		// Handle the action for the custom click here
		return true;
	}

	private static float distance(final float x1, final float y1, final float x2, final float y2)
	{
		final float dx = x2 - x1;
		final float dy = y2 - y1;
		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	private static double arg(final float x1, final float y1, final float x2, final float y2)
	{
		final float dx = x2 - x1;
		final float dy = y2 - y1;
		double a = Math.atan2(dy, dx);
		// return a;
		return a < 0 ? (2 * Math.PI + a) : a;
	}

	/**
	 * Rings
	 */
	private static final float[] rings = {0.370F, 0.795F, 1F, Float.MAX_VALUE};

	/* Pies */
	private static final double[] pies = {Math.PI / 6F, Math.PI * 5F / 6F, Math.PI * 3F / 2F, Math.PI * 2F, Double.MAX_VALUE};
}
