package org.sqlunet.browser.config;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

import org.sqlunet.browser.AbstractTableFragment;

/**
 * A list fragment representing a table.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class TableFragment extends AbstractTableFragment
{
	// static private final String TAG = "TableFragment";
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public TableFragment()
	{
		super();
	}

	/**
	 * Make view binder
	 *
	 * @return ViewBinder
	 */
	@Nullable
	@Override
	protected ViewBinder makeViewBinder()
	{
		return new ViewBinder()
		{
			@Override
			public boolean setViewValue(final View view, @NonNull final Cursor cursor, final int columnIndex)
			{
				String value = cursor.getString(columnIndex);
				if (value == null)
				{
					value = "";
				}

				if (view instanceof TextView)
				{
					((TextView) view).setText(value);
				}
				else if (view instanceof ImageView)
				{
					try
					{
						((ImageView) view).setImageResource(Integer.parseInt(value));
					}
					catch (@NonNull final NumberFormatException nfe)
					{
						((ImageView) view).setImageURI(Uri.parse(value));
					}
				}
				else
				{
					throw new IllegalStateException(view.getClass().getName() + " is not a view that can be bound by this SimpleCursorAdapter");
				}
				return true;
			}
		};
	}
}