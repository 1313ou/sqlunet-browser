package org.sqlunet.treeview.renderer;

/**
 * Value for tree item with extra icon
 *
 * @author Bogdan Melnychuk on 2/12/15.
 */
public class Value
{
	/**
	 * Extra icon
	 */
	public final int icon;

	/**
	 * Label text
	 */
	public final CharSequence text;

	/**
	 * Constructor
	 *
	 * @param icon extra icon
	 * @param text label text
	 */
	public Value(final int icon, final CharSequence text)
	{
		this.icon = icon;
		this.text = text;
	}
}