/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.xn;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.sqlunet.browser.ColorUtils;
import org.sqlunet.browser.Info;
import org.sqlunet.browser.R;
import org.sqlunet.browser.config.ExecAsyncTask;
import org.sqlunet.browser.config.TaskObserver;
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.settings.StorageUtils;

import java.io.File;

import androidx.annotation.NonNull;

/**
 * Set up with SQL fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SetupSqlFragment extends org.sqlunet.browser.config.SetupSqlFragment
{
	static private final String TAG = "SetupSqlF";

	/**
	 * Constructor
	 */
	public SetupSqlFragment()
	{
		// Required empty public constructor
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// view
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		assert view != null;
		return view;
	}

	/**
	 * Update status
	 */
	@SuppressWarnings("WeakerAccess")
	@Override
	public void update()
	{
		super.update();

		final Context context = getContext();
		if (context != null)
		{
			// sql zip file
			final String sqlZip = StorageSettings.getSqlSource(context);
			boolean sqlZipExists = new File(sqlZip).exists();

			// images
			final Drawable okDrawable = ColorUtils.getDrawable(context, R.drawable.ic_ok);
			ColorUtils.tint(ColorUtils.getColor(context, R.color.tertiaryForeColor), okDrawable);
			final Drawable failDrawable = ColorUtils.getDrawable(context, R.drawable.ic_fail);

			// status
			final int status = Status.status(context);
			final boolean existsDatabase = (status & Status.EXISTS) != 0;
			final boolean existsTables = (status & Status.EXISTS_TABLES) != 0;
			// final boolean existsIndexes = (status & org.sqlunet.browser.config.Status.EXISTS_INDEXES) != 0;
		}
	}
}
