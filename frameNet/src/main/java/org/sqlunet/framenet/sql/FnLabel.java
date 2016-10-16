package org.sqlunet.framenet.sql;

public class FnLabel
{
	public final String from;

	public final String to;

	public final String label;

	public final String itype;

	public final String bgColor;

	public final String fgColor;

	public FnLabel(final String from0, final String to0, final String label0, final String itype0, final String bgColor0, final String fgColor0)
	{
		super();
		this.from = from0;
		this.to = to0;
		this.label = label0;
		this.itype = itype0;
		this.bgColor = bgColor0 != null && bgColor0.isEmpty() ? null : bgColor0;
		this.fgColor = fgColor0 != null && fgColor0.isEmpty() ? null : fgColor0;
	}
}
