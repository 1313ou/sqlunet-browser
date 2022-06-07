package org.sqlunet.speak;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ListPreference extends androidx.preference.ListPreference
{
	public ListPreference(@NonNull final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public ListPreference(@NonNull final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

	public ListPreference(@NonNull final Context context, @Nullable final AttributeSet attrs)
	{
		super(context, attrs);
	}

	public ListPreference(@NonNull final Context context)
	{
		super(context);
	}

	public void notifyEntriesChanged()
	{
		notifyChanged();
	}
}
