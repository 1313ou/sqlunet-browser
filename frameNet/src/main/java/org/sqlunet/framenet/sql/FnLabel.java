/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.framenet.sql;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Label
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnLabel
{
	/**
	 * From-indexOf
	 */
	public final String from;

	/**
	 * To-indexOf
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
	@Nullable
	public final String bgColor;

	/**
	 * Foreground color
	 */
	@Nullable
	public final String fgColor;

	/**
	 * Constructor
	 *
	 * @param from    from-indexOf
	 * @param to      to-indexOf
	 * @param label   label
	 * @param iType   i-type
	 * @param bgColor background color
	 * @param fgColor foreground color
	 */
	public FnLabel(final String from, final String to, final String label, final String iType, @Nullable final String bgColor, @Nullable final String fgColor)
	{
		super();
		this.from = from;
		this.to = to;
		this.label = label;
		this.iType = iType;
		this.bgColor = bgColor != null && bgColor.isEmpty() ? null : bgColor;
		this.fgColor = fgColor != null && fgColor.isEmpty() ? null : fgColor;
	}

	@NonNull
	@Override
	public String toString()
	{
		return this.label + '[' + this.from + ',' + this.to + "] type=" + this.iType;
	}
}
