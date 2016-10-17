package org.sqlunet.propbank.sql;

import java.util.Locale;

/**
 * Argument
 *
 * @author Bernard Bou
 */
class PbArg
{
	public final String nArg;

	public final String f;

	public final String vnTheta;

	public final String description;

	public final String subText;

	private PbArg(final String nArg, final String f, final String description, final String vnTheta, final String subText)
	{
		this.nArg = nArg;
		this.f = f;
		this.vnTheta = vnTheta;
		this.description = description;
		this.subText = subText;
	}

	public PbArg(final String... argFields)
	{
		this(argFields[0], "*".equals(argFields[1]) ? //$NON-NLS-1$
				null : argFields[1], argFields[2].toLowerCase(Locale.ENGLISH), "*".equals(argFields[3]) ? //$NON-NLS-1$
				null : argFields[3], argFields[4]);
	}
}
