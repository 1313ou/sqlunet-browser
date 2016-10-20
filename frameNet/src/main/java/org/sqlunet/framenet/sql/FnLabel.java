package org.sqlunet.framenet.sql;

/**
 * Label
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnLabel
{
	public final String from;

	public final String to;

	public final String label;

	public final String itype;

	public final String bgColor;

	public final String fgColor;

	public FnLabel(final String from, final String to, final String label, final String iType, final String bgColor, final String fgColor)
	{
		super();
		this.from = from;
		this.to = to;
		this.label = label;
		this.itype = iType;
		this.bgColor = bgColor != null && bgColor.isEmpty() ? null : bgColor;
		this.fgColor = fgColor != null && fgColor.isEmpty() ? null : fgColor;
	}
}
