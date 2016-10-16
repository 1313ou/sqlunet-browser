package org.sqlunet.propbank.sql;

import java.util.Locale;

class PbArg
{
	public final String theNArg;

	public final String theF;

	public final String theVnTheta;

	public final String theDescription;

	public final String theSubText;

	private PbArg(final String thisNArg, final String thisF, final String thisDescription, final String thisVnTheta, final String thisSubText)
	{
		this.theNArg = thisNArg;
		this.theF = thisF;
		this.theVnTheta = thisVnTheta;
		this.theDescription = thisDescription;
		this.theSubText = thisSubText;
	}

	public PbArg(final String... theseArgFields)
	{
		this(theseArgFields[0], theseArgFields[1].equals("*") ? null : theseArgFields[1], theseArgFields[2].toLowerCase(Locale.ENGLISH), theseArgFields[3].equals("*") ? null : theseArgFields[3], theseArgFields[4]); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
