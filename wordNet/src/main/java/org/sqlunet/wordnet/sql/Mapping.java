/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.wordnet.sql;

import android.annotation.SuppressLint;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Id-name mappings
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */

class Mapping
{
	static private final String TAG = "Mapping";

	/**
	 * <code>domains</code> is an array of domains
	 */
	static private List<Domain> domains;

	/**
	 * <code>domains</code> is map of domains by name
	 */
	static private Map<String, Domain> domainsByName;

	/**
	 * <code>relationsById</code> is relations mapped by id
	 */
	static private SparseArray<Relation> relationsById;

	/**
	 * <code>relationsByName</code> is relations mapped by name
	 */
	static private Map<String, Relation> relationsByName;

	/**
	 * <code>topsId</code> is a constant for Tops domain id
	 */
	static public final int topsId = 3;

	/**
	 * <code>hyponymId</code> is id for hyponym relation
	 */
	static public final int hyponymId = 2;

	/**
	 * <code>instanceHyponymId</code> is id for instance hyponym relation
	 */
	static public final int instanceHyponymId = 4;

	/**
	 * is the constant for unspecified search types (pos, domains, relations)
	 */
	static public final int ANYTYPE = -1;

	/**
	 * is the constant for non recursive queries
	 */
	static public final int NONRECURSIVE = -1;

	/**
	 * Read domain mappings from database
	 *
	 * @param connection connection
	 */
	static public void initDomains(final SQLiteDatabase connection)
	{
		// domain
		Mapping.domains = new ArrayList<>();
		Mapping.domainsByName = new HashMap<>();
		DomainsQuery query = null;
		try
		{
			query = new DomainsQuery(connection);
			query.execute();

			while (query.next())
			{
				final int id = query.getId();
				final int pos = query.getPos();
				final String name = query.getPosDomainName().replace(' ', '.');
				final Domain domain = new Domain(id, pos, name);
				Mapping.domains.add(domain);
				Mapping.domainsByName.put(name, domain);
			}
		}
		catch (@NonNull final SQLException e)
		{
			Log.e(TAG, "While initializing domains", e);
			throw new RuntimeException(e);
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
	}

	/**
	 * Read relation mappings from database
	 *
	 * @param connection connection
	 */
	@SuppressLint("DefaultLocale")
	static public void initRelations(final SQLiteDatabase connection)
	{
		Mapping.relationsById = new SparseArray<>();
		Mapping.relationsByName = new HashMap<>();
		RelationsQuery query = null;
		try
		{
			query = new RelationsQuery(connection);
			query.execute();

			while (query.next())
			{
				final int id = query.getId();
				final String name = query.getName().replace(' ', '_').toLowerCase(Locale.US);
				final boolean recurses = query.getRecurse();
				final Relation relation = new Relation(id, name, recurses);
				Mapping.relationsById.put(id, relation);
				Mapping.relationsByName.put(name, relation);
			}
		}
		catch (@NonNull final SQLException e)
		{
			Log.e(TAG, "While initializing relations", e);
			throw new RuntimeException(e);
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
	}

	/**
	 * Get part-of-speech names as array of strings
	 *
	 * @return array of string part-of-speech names
	 */
	@NonNull
	static public String[] getPosNames()
	{
		return new String[]{"noun", "verb", "adj", "adv"};
	}

	/**
	 * Get domains as array of strings
	 *
	 * @return array of Strings
	 */
	@NonNull
	static public String[] getDomainNames()
	{
		final Set<String> nameSet = Mapping.domainsByName.keySet();
		return nameSet.toArray(new String[0]);
	}

	/**
	 * Get relations as array of strings
	 *
	 * @return array of Strings
	 */
	@NonNull
	static public String[] getRelationNames()
	{
		final Set<String> nameSet = Mapping.relationsByName.keySet();
		return nameSet.toArray(new String[0]);
	}

	/**
	 * Find part-of-speech name from domainid
	 *
	 * @param domainId is the domainid
	 * @return part-of-speech name
	 */
	@NonNull
	static public String getDomainPosName(final int domainId)
	{
		try
		{
			final Domain domain = Mapping.domains.get(domainId);
			return getPosName(domain.posId);
		}
		catch (@NonNull final IndexOutOfBoundsException | IllegalArgumentException e)
		{
			return "domainid." + domainId;
		}
	}

	/**
	 * Find domain name from domain id
	 *
	 * @param domainId is the domain id
	 * @return domain name or "domainid.xxx" if not found
	 */
	@NonNull
	static public String getDomainName(final int domainId)
	{
		try
		{
			final Domain domain = Mapping.domains.get(domainId);
			return domain.domainName;
		}
		catch (@NonNull final IndexOutOfBoundsException e)
		{
			return "domainid." + domainId;
		}
	}

	/**
	 * Find domain id from part-of-speech name and domain name
	 *
	 * @param posName    target part-of-speech name
	 * @param domainName target domain name
	 * @return domain id or -1 if not found
	 */
	static public int getDomainId(@Nullable final String posName, @Nullable final String domainName)
	{
		if (posName == null || domainName == null)
		{
			return Mapping.ANYTYPE;
		}
		final String fullName = posName + '.' + domainName;
		try
		{
			final Domain domain = Mapping.domainsByName.get(fullName);
			assert domain != null;
			return domain.id;
		}
		catch (@NonNull final NullPointerException e)
		{
			return Mapping.ANYTYPE;
		}
	}

	/**
	 * Find part-of-speech id (n,v,a,r) from part-of-speech name
	 *
	 * @param posName target part-of-speech name
	 * @return part-of-speech id or ANYTYPE if not found
	 */
	static public int getPosId(@Nullable final String posName)
	{
		if (posName == null)
		{
			return Mapping.ANYTYPE;
		}

		switch (posName)
		{
			case "noun":
				return 'n';
			case "verb":
				return 'v';
			case "adj":
				return 'a';
			case "adv":
				return 'r';
		}
		return Mapping.ANYTYPE;
	}

	/**
	 * Find part-of-speech id (n,v,a,r) from part-of-speech name
	 *
	 * @param posId target part-of-speech id
	 * @return part-of-speech name or null if not found
	 */
	@NonNull
	static public String getPosName(final int posId)
	{
		switch (posId)
		{
			case 'n':
				return "noun";
			case 'v':
				return "verb";
			case 'a':
				return "adj";
			case 'r':
				return "adv";
		}
		throw new IllegalArgumentException("" + posId);
	}

	/**
	 * Find relation name from relation id
	 *
	 * @param relationId target relation id
	 * @return relation name or "relation.xxx" if not found
	 */
	static public String getRelationName(final int relationId)
	{
		try
		{
			final Relation relation = Mapping.relationsById.get(relationId);
			return relation.name;
		}
		catch (@NonNull final NullPointerException e)
		{
			return "relation." + relationId;
		}
	}

	/**
	 * Find relation id from relation name
	 *
	 * @param relationName target relation name
	 * @return relation id or ANYTYPE if it is not found
	 */
	static public int getRelationId(@Nullable final String relationName)
	{
		if (relationName == null)
		{
			return Mapping.ANYTYPE;
		}

		try
		{
			final Relation relation = Mapping.relationsByName.get(relationName);
			assert relation != null;
			return relation.id;
		}
		catch (@NonNull final NullPointerException e)
		{
			return Mapping.ANYTYPE;
		}
	}

	/**
	 * Determine if this relation can recurse
	 *
	 * @param relationId target relation id
	 * @return whether this relation can recurse
	 */
	static public boolean canRecurse(final int relationId)
	{
		try
		{
			final Relation relation = Mapping.relationsById.get(relationId);
			return relation.recurses;
		}
		catch (@NonNull final IndexOutOfBoundsException e)
		{
			return false;
		}
		catch (@NonNull final NullPointerException e)
		{
			return false;
		}
	}
}