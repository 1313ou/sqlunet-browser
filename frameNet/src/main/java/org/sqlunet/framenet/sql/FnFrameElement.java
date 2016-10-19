package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * FE
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
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
	 * @param connection is the database connection
	 * @param frameId    is the word id to build query from
	 * @return list of FEs
	 */
	public static List<FnFrameElement> make(final SQLiteDatabase connection, final long frameId)
	{
		final List<FnFrameElement> result = new ArrayList<>();
		FnFrameElementQueryCommand query = null;
		try
		{
			query = new FnFrameElementQueryCommand(connection, frameId);
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

				result.add(new FnFrameElement(feId, feTypeId, feType, feDefinition, feAbbrev, feCoreType, semTypes, isCore, coreSet));
			}
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
		return result;
	}
}
