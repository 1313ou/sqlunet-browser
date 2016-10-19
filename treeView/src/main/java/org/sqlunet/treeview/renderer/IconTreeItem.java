package org.sqlunet.treeview.renderer;

/**
 * Tree item with extra icon
 */
public class IconTreeItem
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
	public IconTreeItem(int icon, CharSequence text)
	{
		this.icon = icon;
		this.text = text;
	}
}