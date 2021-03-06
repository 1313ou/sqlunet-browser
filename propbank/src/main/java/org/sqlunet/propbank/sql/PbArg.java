/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.propbank.sql;

import java.util.Locale;

/**
 * Argument
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class PbArg
{
	/**
	 * Argument n
	 */
	public final String nArg;

	/**
	 * Argument f
	 */
	public final String f;

	/**
	 * Argument VerbNet theta
	 */
	public final String vnTheta;

	/**
	 * Argument description
	 */
	public final String description;

	/**
	 * Argument subtext
	 */
	public final String subText;

	/**
	 * Constructor
	 *
	 * @param nArg        n
	 * @param f           f
	 * @param description description
	 * @param vnTheta     VerbNet theta
	 * @param subText     sub text
	 */
	private PbArg(final String nArg, final String f, final String description, final String vnTheta, final String subText)
	{
		this.nArg = nArg;
		this.f = f;
		this.vnTheta = vnTheta;
		this.description = description;
		this.subText = subText;
	}

	/**
	 * Constructor from argument fields
	 *
	 * @param argFields argument fields
	 */
	public PbArg(final String... argFields)
	{
		this(argFields[0], "*".equals(argFields[1]) ? //
				null : argFields[1], argFields[2].toLowerCase(Locale.ENGLISH), "*".equals(argFields[3]) ? //
				null : argFields[3], argFields[4]);
	}
}
