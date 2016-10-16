package org.sqlunet.framenet.sql;

import java.util.ArrayList;
import java.util.List;

/**
 * SemType
 *
 * @author Bernard Bou
 */
class FnSemType
{
	public final long semTypeId;

	public final String semTypeName;

	public final String semTypeDefinition;

	/**
	 * Constructor
	 *
	 * @param thisSemTypeId
	 *            is the semtype id
	 * @param thisSemTypeName
	 *            is the semtype name
	 * @param thisSemTypeDefinition
	 *            is the semtype definition
	 */
	private FnSemType(final long thisSemTypeId, final String thisSemTypeName, final String thisSemTypeDefinition)
	{
		this.semTypeId = thisSemTypeId;
		this.semTypeName = thisSemTypeName;
		this.semTypeDefinition = thisSemTypeDefinition;
	}

	/**
	 * Make semtypes from string
	 *
	 * @param theseSemTypesString
	 *            (id:def|id:def...)
	 * @return list of semtypes
	 */
	public static List<FnSemType> make(final String theseSemTypesString)
	{
		if (theseSemTypesString == null)
			return null;
		List<FnSemType> thisResult = null;
		final String[] theseSemTypes = theseSemTypesString.split("\\|"); //$NON-NLS-1$
		for (final String thisSemType : theseSemTypes)
		{
			if (thisResult == null)
			{
				thisResult = new ArrayList<>();
			}
			final String[] theseFields = thisSemType.split(":"); //$NON-NLS-1$
			final long thisSemTypeId = Long.parseLong(theseFields[0]);
			final String thisSemTypeName = theseFields[1];
			final String thisSemTypeDefinition = theseFields[2];
			thisResult.add(new FnSemType(thisSemTypeId, thisSemTypeName, thisSemTypeDefinition));
		}
		return thisResult;
	}
}
