package org.sqlunet.browser.xn;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import org.sqlunet.browser.R;

/**
 * Home fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class HomeFragment extends org.sqlunet.browser.HomeFragment
{
	static private final String TAG = "HomeFragment";

	/**
	 * Rings
	 */
	private static final float[] rings = {0.370F, 0.795F, 1F, Float.MAX_VALUE};

	/* Pies */
	private static final double[] pies = {Math.PI / 6F, Math.PI * 5F / 6F, Math.PI * 3F / 2F, Math.PI * 2F, Double.MAX_VALUE};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public HomeFragment()
	{
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		assert view != null;
		final ImageView image = view.findViewById(R.id.splash);
		image.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, @NonNull MotionEvent event)
			{
				float x = event.getX();
				float y = event.getY();
				Log.i(TAG, "TOUCH x=" + x + " y=" + y);

				Rect rect = new Rect();
				rect.left = image.getLeft();
				rect.top = image.getTop();
				rect.bottom = image.getBottom();
				rect.right = image.getRight();

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
				for (i = 0; i < rings.length; i++)
				{
					float f = rings[i];
					// Log.i("RING" + i, "dmax=" + (r * f));
					if (d < r * f)
					{
						break;
					}
				}
				int ring = i;
				Log.d(TAG, "ring=" + ring);

				for (i = 0; i < pies.length; i++)
				{
					double s = pies[i];
					// Log.i("SECTOR" + i, "smax=" + s);
					if (a < s)
					{
						break;
					}
				}
				int pie = i % 3;
				Log.d(TAG, "pie=" + pie);

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
					Log.i("PICK ", getString(messageId));
					Toast.makeText(getActivity(), messageId, Toast.LENGTH_LONG).show();
				}
				return false;
			}
		});
		return view;
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
}

