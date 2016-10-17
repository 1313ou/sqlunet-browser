package org.sqlunet.framenet.sql;

import java.util.ArrayList;
import java.util.List;

/**
 * Sem type
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
	 * @param semTypeId         is the semtype id
	 * @param semTypeName       is the semtype name
	 * @param semTypeDefinition is the semtype definition
	 */
	private FnSemType(final long semTypeId, final String semTypeName, final String semTypeDefinition)
	{
		this.semTypeId = semTypeId;
		this.semTypeName = semTypeName;
		this.semTypeDefinition = semTypeDefinition;
	}

	/**
	 * Make semtypes from string
	 *
	 * @param semTypesString (id:def|id:def...)
	 * @return list of semtypes
	 */
	public static List<FnSemType> make(final String semTypesString)
	{
		if (semTypesString == null)
		{
			return null;
		}
		List<FnSemType> result = null;
		final String[] semTypes = semTypesString.split("\\|"); //$NON-NLS-1$
		for (final String semType : semTypes)
		{
			if (result == null)
			{
				result = new ArrayList<>();
			}
			final String[] fields = semType.split(":"); //$NON-NLS-1$
			final long semTypeId = Long.parseLong(fields[0]);
			final String semTypeName = fields[1];
			final String semTypeDefinition = fields[2];
			result.add(new FnSemType(semTypeId, semTypeName, semTypeDefinition));
		}
		return result;
	}
}
