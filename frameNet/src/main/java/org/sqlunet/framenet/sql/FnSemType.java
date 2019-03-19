package org.sqlunet.framenet.sql;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Semantic type
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FnSemType
{
	/**
	 * Semantic type id
	 */
	public final long semTypeId;

	/**
	 * Semantic type name
	 */
	public final String semTypeName;

	/**
	 * Semantic type definition
	 */
	public final String semTypeDefinition;

	/**
	 * Constructor
	 *
	 * @param semTypeId         semtype id
	 * @param semTypeName       semtype name
	 * @param semTypeDefinition semtype definition
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
	@Nullable
	static public List<FnSemType> make(@Nullable final String semTypesString)
	{
		if (semTypesString == null)
		{
			return null;
		}
		List<FnSemType> result = null;
		final String[] semTypes = semTypesString.split("\\|");
		for (final String semType : semTypes)
		{
			if (result == null)
			{
				result = new ArrayList<>();
			}
			final String[] fields = semType.split(":");
			final long semTypeId = Long.parseLong(fields[0]);
			final String semTypeName = fields[1];
			final String semTypeDefinition = fields[2];
			result.add(new FnSemType(semTypeId, semTypeName, semTypeDefinition));
		}
		return result;
	}
}
