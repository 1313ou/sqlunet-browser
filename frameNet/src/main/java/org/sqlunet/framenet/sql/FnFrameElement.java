/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Frame element
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FnFrameElement
{
	/**
	 * FE type
	 */
	public final String feType;

	/**
	 * FE type id
	 */
	public final long feTypeId;

	/**
	 * FE id
	 */
	public final long feId;

	/**
	 * FE definition
	 */
	public final String feDefinition;

	/**
	 * FE abbrev
	 */
	public final String feAbbrev;

	/**
	 * FE core type
	 */
	public final String coreType;

	/**
	 * Whether FE is core
	 */
	public final boolean isCore;

	/**
	 * FE sem types
	 */
	@Nullable
	public final String[] semTypes;

	/**
	 * FE core set number
	 */
	final int coreSet;

	/**
	 * Constructor
	 *
	 * @param feId         FE id
	 * @param feTypeId     FE type id
	 * @param feType       FE type
	 * @param feDefinition FE definition
	 * @param feAbbrev     FE abbrev
	 * @param coreType     FE core type
	 * @param semTypes     FE sem types
	 * @param isCore       whether FE is core
	 * @param coreSet      core set number
	 */
	private FnFrameElement(final long feId, final long feTypeId, final String feType, final String feDefinition, final String feAbbrev, final String coreType, @Nullable final String semTypes, final boolean isCore, final int coreSet)
	{
		this.feId = feId;
		this.feTypeId = feTypeId;
		this.feType = feType;
		this.feAbbrev = feAbbrev;
		this.feDefinition = feDefinition;
		this.coreType = coreType;
		this.semTypes = semTypes == null ? null : semTypes.split("\\|");
		this.isCore = isCore;
		this.coreSet = coreSet;
	}

	/**
	 * Make sets of FEs from frame id
	 *
	 * @param connection connection
	 * @param frameId    target frame id
	 * @return list of FEs
	 */
	@Nullable
	static public List<FnFrameElement> make(final SQLiteDatabase connection, final long frameId)
	{
		List<FnFrameElement> result = null;
		try (FnFrameElementQueryFromFrameId query = new FnFrameElementQueryFromFrameId(connection, frameId))
		{
			query.execute();

			while (query.next())
			{
				final long feTypeId = query.getFETypeId();
				final String feType = query.getFEType();
				final long feId = query.getFEId();
				final String feDefinition = query.getFEDefinition();
				final String feAbbrev = query.getFEAbbrev();
				final String feCoreType = query.getFECoreType();
				final String semTypes = query.getSemTypes();
				final boolean isCore = query.getIsCore();
				final int coreSet = query.getCoreSet();
				if (result == null)
				{
					result = new ArrayList<>();
				}
				result.add(new FnFrameElement(feId, feTypeId, feType, feDefinition, feAbbrev, feCoreType, semTypes, isCore, coreSet));
			}
		}
		return result;
	}
}
