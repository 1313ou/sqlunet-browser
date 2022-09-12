/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.propbank.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * PropBank example query
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class PbExampleQueryFromRoleSetId extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.PropBankExamplesQueryFromRoleSetId;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param roleSetId  role set id to build query from
	 */
	@SuppressWarnings("boxing")
	public PbExampleQueryFromRoleSetId(final SQLiteDatabase connection, final long roleSetId)
	{
		super(connection, PbExampleQueryFromRoleSetId.QUERY);
		setParams(roleSetId);
	}

	/**
	 * Get example id from cursor
	 *
	 * @return example id
	 */
	public long getExampleId()
	{
		assert this.cursor != null;
		return this.cursor.getLong(0);
	}

	/**
	 * Get text from cursor
	 *
	 * @return text
	 */
	public String getText()
	{
		assert this.cursor != null;
		return this.cursor.getString(1);
	}

	/**
	 * Get rel from cursor
	 *
	 * @return rel
	 */
	public String getRel()
	{
		assert this.cursor != null;
		return this.cursor.getString(2);
	}

	/**
	 * Get args from cursor
	 *
	 * @return args
	 */
	@Nullable
	public List<PbArg> getArgs()
	{
		assert this.cursor != null;
		final String concatArg = this.cursor.getString(3);
		if (concatArg == null)
		{
			return null;
		}
		final List<PbArg> args = new ArrayList<>();
		for (final String arg : concatArg.split("\\|")) //
		{
			final String[] argFields = arg.split("~");
			args.add(new PbArg(argFields));
		}
		return args;
	}

	/**
	 * Get aspect from cursor
	 *
	 * @return aspect
	 */
	public String getAspect()
	{
		assert this.cursor != null;
		return this.cursor.getString(4);
	}

	/**
	 * Get form from cursor
	 *
	 * @return form
	 */
	public String getForm()
	{
		assert this.cursor != null;
		return this.cursor.getString(5);
	}

	/**
	 * Get tense from cursor
	 *
	 * @return tense
	 */
	public String getTense()
	{
		assert this.cursor != null;
		return this.cursor.getString(6);
	}

	/**
	 * Get voice from cursor
	 *
	 * @return voice
	 */
	public String getVoice()
	{
		assert this.cursor != null;
		return this.cursor.getString(7);
	}

	/**
	 * Get person from cursor
	 *
	 * @return person
	 */
	public String getPerson()
	{
		assert this.cursor != null;
		return this.cursor.getString(8);
	}
}
