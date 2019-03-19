package org.sqlunet.treeview.control;

import androidx.annotation.Nullable;

/**
 * Value for tree item with extra icon
 *
 * @author Bogdan Melnychuk on 2/12/15.
 */
public class Value
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
	public Value(final CharSequence text, final int icon, @Nullable final Object... payload)
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
	public Value(final CharSequence text, final int icon)
	{
		this.text = text;
		this.icon = icon;
		this.payload = null;
	}
}