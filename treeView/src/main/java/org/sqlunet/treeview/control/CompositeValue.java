/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.treeview.control;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Value for tree item with extra icon
 *
 * @author Bogdan Melnychuk on 2/12/15.
 */
public class CompositeValue
{
	/**
	 * Label text
	 */
	public final CharSequence text;

	/**
	 * Extra icon
	 */
	public final int icon;

	/**
	 * Payload
	 */
	@Nullable
	public final Object[] payload;

	/**
	 * Constructor
	 *
	 * @param text    label text
	 * @param icon    extra icon
	 * @param payload payload
	 */
	public CompositeValue(final CharSequence text, final int icon, @Nullable final Object... payload)
	{
		this.text = text;
		this.icon = icon;
		this.payload = payload;
	}

	/**
	 * Constructor
	 *
	 * @param text label text
	 * @param icon extra icon
	 */
	public CompositeValue(final CharSequence text, final int icon)
	{
		this.text = text;
		this.icon = icon;
		this.payload = null;
	}

	// S T R I N G I F Y

	@NonNull
	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append('"');
		sb.append(text);
		sb.append('"');
		if (payload != null)
		{
			sb.append(' ');
			sb.append("payload=");
			boolean first = true;
			sb.append('{');
			for (Object obj : payload)
			{
				if (first)
				{
					first = false;
				}
				else
				{
					sb.append(',');
				}
				sb.append(obj);
			}
			sb.append('}');
		}
		return sb.toString();
	}
}