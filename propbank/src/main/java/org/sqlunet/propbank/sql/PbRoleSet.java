/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.propbank.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * PropBank role set
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class PbRoleSet
{
	/**
	 * Name
	 */
	public final String roleSetName;

	/**
	 * Head
	 */
	public final String roleSetHead;

	/**
	 * Description
	 */
	public final String roleSetDescr;

	/**
	 * RoleSet id
	 */
	public final long roleSetId;

	/**
	 * Word Id
	 */
	public final long wordId;

	/**
	 * Constructor
	 *
	 * @param roleSetName  name
	 * @param roleSetHead  head
	 * @param roleSetDescr description
	 * @param roleSetId    role set id
	 */
	private PbRoleSet(final String roleSetName, final String roleSetHead, final String roleSetDescr, final long roleSetId, final long wordId)
	{
		super();
		this.roleSetName = roleSetName;
		this.roleSetHead = roleSetHead;
		this.roleSetDescr = roleSetDescr;
		this.roleSetId = roleSetId;
		this.wordId = wordId;
	}

	/**
	 * Make sets of PropBank roleSets from query built from word id
	 *
	 * @param connection connection
	 * @param word       is the word to build query from
	 * @return list of PropBank roleSets
	 */
	@Nullable
	static public List<PbRoleSet> makeFromWord(final SQLiteDatabase connection, final String word)
	{
		List<PbRoleSet> result = null;
		try (PbRoleSetQueryFromWord query = new PbRoleSetQueryFromWord(connection, word))
		{
			query.execute();

			while (query.next())
			{
				final String roleSetName = query.getRoleSetName();
				final String roleSetHead = query.getRoleSetHead();
				final String roleSetDescr = query.getRoleSetDescr();
				final long roleSetId = query.getRoleSetId();
				final long wordId = query.getWordId();
				if (result == null)
				{
					result = new ArrayList<>();
				}
				result.add(new PbRoleSet(roleSetName, roleSetHead, roleSetDescr, roleSetId, wordId));
			}
			return result;
		}
	}

	/**
	 * Make sets of PropBank roleSets from query built from word id
	 *
	 * @param connection connection
	 * @param wordId     is the word id to build query from
	 * @return list of PropBank roleSets
	 */
	@Nullable
	static public List<PbRoleSet> makeFromWordId(final SQLiteDatabase connection, final long wordId)
	{
		List<PbRoleSet> result = null;
		try (PbRoleSetQueryFromWordId query = new PbRoleSetQueryFromWordId(connection, wordId))
		{
			query.execute();

			while (query.next())
			{
				final String roleSetName = query.getRoleSetName();
				final String roleSetHead = query.getRoleSetHead();
				final String roleSetDescr = query.getRoleSetDescr();
				final long roleSetId = query.getRoleSetId();
				if (result == null)
				{
					result = new ArrayList<>();
				}
				result.add(new PbRoleSet(roleSetName, roleSetHead, roleSetDescr, roleSetId, wordId));
			}
		}
		return result;
	}

	/**
	 * Make sets of PropBank roleSets from query built from roleSet id
	 *
	 * @param connection connection
	 * @param roleSetId  is the role set id to build query from
	 * @return list of PropBank role sets
	 */
	@Nullable
	static public List<PbRoleSet> make(final SQLiteDatabase connection, final long roleSetId)
	{
		List<PbRoleSet> result = null;
		try (PbRoleSetQuery query = new PbRoleSetQuery(connection, roleSetId))
		{
			query.execute();

			while (query.next())
			{
				final String roleSetName = query.getRoleSetName();
				final String roleSetHead = query.getRoleSetHead();
				final String roleSetDescr = query.getRoleSetDescr();
				if (result == null)
				{
					result = new ArrayList<>();
				}
				result.add(new PbRoleSet(roleSetName, roleSetHead, roleSetDescr, roleSetId, 0));
			}
		}
		return result;
	}
}
