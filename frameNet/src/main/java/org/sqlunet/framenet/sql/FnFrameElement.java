package org.sqlunet.framenet.sql;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;

/**
 * FE
 *
 * @author Bernard Bou
 */
class FnFrameElement
{

	/**
	 * FEType
	 */
	public final String feType;

	/**
	 * FEType id
	 */
	@SuppressWarnings("unused")
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
	@SuppressWarnings("unused")
	public final String feAbbrev;

	/**
	 * FE core type
	 */
	public final String coreType;

	/**
	 * Whether FE is core
	 */
	@SuppressWarnings("unused")
	public final boolean isCore;

	/**
	 * FE sem types
	 */
	public final String[] semTypes;

	/**
	 * FE core set number
	 */
	final int coreSet;

	/**
	 * Constructor
	 *
	 * @param feId
	 *            FE id
	 * @param feTypeId
	 *            FE type id
	 * @param feType
	 *            FE type
	 * @param feDefinition
	 *            FE definition
	 * @param feAbbrev
	 *            FE abbrev
	 * @param coreType
	 *            FE core type
	 * @param semTypes
	 *            FE sem types
	 * @param isCore
	 *            whether FE is core
	 * @param coreSet
	 *            core set number
	 */
	private FnFrameElement(final long feId, final long feTypeId, final String feType, final String feDefinition, final String feAbbrev, final String coreType, final String semTypes, final boolean isCore, final int coreSet)
	{
		this.feId = feId;
		this.feTypeId = feTypeId;
		this.feType = feType;
		this.feAbbrev = feAbbrev;
		this.feDefinition = feDefinition;
		this.coreType = coreType;
		this.semTypes = semTypes != null ? semTypes.split("\\|") : null; //$NON-NLS-1$
		this.isCore = isCore;
		this.coreSet = coreSet;
	}

	/**
	 * Make sets of FEs from query built from annosetid
	 *
	 * @param thisConnection
	 *            is the database connection
	 * @param thisFrameId
	 *            is the word id to build query from
	 * @return list of FEs
	 */
	public static List<FnFrameElement> make(final SQLiteDatabase thisConnection, final long thisFrameId)
	{
		final List<FnFrameElement> thisResult = new ArrayList<>();
		FnFrameElementQueryCommand thisQuery = null;
		try
		{
			thisQuery = new FnFrameElementQueryCommand(thisConnection, thisFrameId);
			thisQuery.execute();

			while (thisQuery.next())
			{
				final long thisFETypeId = thisQuery.getFETypeId();
				final String thisFEType = thisQuery.getFEType();
				final long thisFEId = thisQuery.getFEId();
				final String thisFEDefinition = thisQuery.getFEDefinition();
				final String thisFEAbbrev = thisQuery.getFEAbbrev();
				final String thisFECoreType = thisQuery.getFECoreType();
				final String theseSemTypes = thisQuery.getSemTypes();
				final boolean isCore = thisQuery.getIsCore();
				final int thisCoreSet = thisQuery.getCoreSet();

				thisResult.add(new FnFrameElement(thisFEId, thisFETypeId, thisFEType, thisFEDefinition, thisFEAbbrev, thisFECoreType, theseSemTypes, isCore, thisCoreSet));
			}
		}
		finally
		{
			if (thisQuery != null)
			{
				thisQuery.release();
			}
		}
		return thisResult;
	}
}
