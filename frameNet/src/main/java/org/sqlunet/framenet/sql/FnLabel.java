package org.sqlunet.framenet.sql;

/**
 * Label
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnLabel
{
	/**
	 * From-index
	 */
	public final String from;

	/**
	 * To-index
	 */
	public final String to;

	/**
	 * Label
	 */
	public final String label;

	/**
	 * I-type
	 */
	public final String iType;

	/**
	 * Background color
	 */
	public final String bgColor;

	/**
	 * Foreground color
	 */
	public final String fgColor;

	/**
	 * Constructor
	 *
	 * @param from    from-index
	 * @param to      to-index
	 * @param label   label
	 * @param iType   i-type
	 * @param bgColor background color
	 * @param fgColor foreground color
	 */
	public FnLabel(final String from, final String to, final String label, final String iType, final String bgColor, final String fgColor)
	{
		super();
		this.from = from;
		this.to = to;
		this.label = label;
		this.iType = iType;
		this.bgColor = bgColor != null && bgColor.isEmpty() ? null : bgColor;
		this.fgColor = fgColor != null && fgColor.isEmpty() ? null : fgColor;
	}
}
