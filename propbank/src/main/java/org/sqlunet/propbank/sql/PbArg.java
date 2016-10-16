package org.sqlunet.propbank.sql;

import java.util.Locale;

class PbArg
{
	public final String nArg;

	public final String f;

	public final String vnTheta;

	public final String description;

	public final String subText;

	private PbArg(final String thisNArg, final String thisF, final String thisDescription, final String thisVnTheta, final String thisSubText)
	{
		this.nArg = thisNArg;
		this.f = thisF;
		this.vnTheta = thisVnTheta;
		this.description = thisDescription;
		this.subText = thisSubText;
	}

	public PbArg(final String... theseArgFields)
	{
		this(theseArgFields[0], theseArgFields[1].equals("*") ? null : theseArgFields[1], theseArgFields[2].toLowerCase(Locale.ENGLISH), theseArgFields[3].equals("*") ? null : theseArgFields[3], theseArgFields[4]); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
