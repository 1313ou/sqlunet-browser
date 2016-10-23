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

/**
 * Id-name mappings
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */

class Mapping
{
	private static final String TAG = "LexDomain"; //

	/**
	 * <code>lexDomains</code> is an array of lexdomains
	 */
	static private List<LexDomainDef> lexDomains;

	/**
	 * <code>lexDomains</code> is map of lexdomains by name
	 */
	static private Map<String, LexDomainDef> lexDomainsByName;

	/**
	 * <code>linksById</code> is links mapped by id
	 */
	static private SparseArray<LinkDef> linksById;

	/**
	 * <code>linksByName</code> is links mapped by name
	 */
	static private Map<String, LinkDef> linksByName;

	/**
	 * <code>topsId</code> is a constant for Tops lexdomain id
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
	 * is the constant for unspecified search types (pos, lexdomains, links)
	 */
	public static final int ANYTYPE = -1;

	/**
	 * is the constant for non recursive queries
	 */
	public static final int NONRECURSIVE = -1;

	/**
	 * Read lexdomain mappings from database
	 *
	 * @param connection connection
	 */
	static public void initLexDomains(final SQLiteDatabase connection)
	{
		// lexdomain
		Mapping.lexDomains = new ArrayList<>();
		Mapping.lexDomainsByName = new HashMap<>();
		LexDomainEnumQueryCommand query = null;
		try
		{
			query = new LexDomainEnumQueryCommand(connection);
			query.execute();

			while (query.next())
			{
				final int id = query.getId();
				final int pos = query.getPos();
				final String name = query.getPosLexDomainName().replace(' ', '.');
				final LexDomainDef lexDomain = new LexDomainDef(id, pos, name);
				Mapping.lexDomains.add(lexDomain);
				Mapping.lexDomainsByName.put(name, lexDomain);
			}
		}
		catch (final SQLException e)
		{
			Log.e(TAG, "While initializing lexdomains", e); //
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
	 * Read link mappings from database
	 *
	 * @param connection connection
	 */
	@SuppressLint("DefaultLocale")
	static public void initLinks(final SQLiteDatabase connection)
	{
		Mapping.linksById = new SparseArray<>();
		Mapping.linksByName = new HashMap<>();
		LinkEnumQueryCommand query = null;
		try
		{
			query = new LinkEnumQueryCommand(connection);
			query.execute();

			while (query.next())
			{
				final int id = query.getId();
				final String name = query.getName().replace(' ', '_').toLowerCase(Locale.US);
				final boolean recurses = query.getRecurse();
				final LinkDef linkDef = new LinkDef(id, name, recurses);
				Mapping.linksById.put(id, linkDef);
				Mapping.linksByName.put(name, linkDef);
			}
		}
		catch (final SQLException e)
		{
			Log.e(TAG, "While initializing links", e);
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
	static public String[] getPosNames()
	{
		return new String[]{"noun", "verb", "adj", "adv"}; //
	}

	/**
	 * Get lexdomains as array of strings
	 *
	 * @return array of Strings
	 */
	static public String[] getLexDomainNames()
	{
		final Set<String> nameSet = Mapping.lexDomainsByName.keySet();
		return nameSet.toArray(new String[nameSet.size()]);
	}

	/**
	 * Get linknames as array of strings
	 *
	 * @return array of Strings
	 */
	static public String[] getLinkNames()
	{
		final Set<String> nameSet = Mapping.linksByName.keySet();
		return nameSet.toArray(new String[nameSet.size()]);
	}

	/**
	 * Find part-of-speech name from lexdomain id
	 *
	 * @param lexDomainId is the lexdomain id
	 * @return part-of-speech name
	 */
	static public String getPosName(final int lexDomainId)
	{
		try
		{
			final LexDomainDef lexDomain = Mapping.lexDomains.get(lexDomainId);
			return lexDomain.posName;
		}
		catch (final IndexOutOfBoundsException e)
		{
			return "lexdomainid." + Integer.toString(lexDomainId); //
		}
	}

	/**
	 * Find lexdomain name from lexdomain id
	 *
	 * @param lexDomainId is the lexdomain id
	 * @return lexdomain name or "lexdomainid.xxx" if not found
	 */
	static public String getLexDomainName(final int lexDomainId)
	{
		try
		{
			final LexDomainDef lexDomain = Mapping.lexDomains.get(lexDomainId);
			return lexDomain.lexDomainName;
		}
		catch (final IndexOutOfBoundsException e)
		{
			return "lexdomainid." + Integer.toString(lexDomainId); //
		}
	}

	/**
	 * Find lexdomain id from part-of-speech name and lexdomain name
	 *
	 * @param posName       target part-of-speech name
	 * @param lexDomainName target lexdomain name
	 * @return lexdomain id or -1 if not found
	 */
	static public int getLexDomainId(final String posName, final String lexDomainName)
	{
		if (posName == null || lexDomainName == null)
		{
			return Mapping.ANYTYPE;
		}
		final String fullName = posName + '.' + lexDomainName;
		try
		{
			final LexDomainDef lexDomain = Mapping.lexDomainsByName.get(fullName);
			return lexDomain.id;
		}
		catch (final NullPointerException e)
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
	static public int getPosId(final String posName)
	{
		if (posName == null)
		{
			return Mapping.ANYTYPE;
		}

		switch (posName)
		{
			case "noun":
				//
				return 'n';
			case "verb":
				//
				return 'v';
			case "adj":
				//
				return 'a';
			case "adv":
				//
				return 'r';
		}
		return Mapping.ANYTYPE;
	}

	/**
	 * Find link name from link id
	 *
	 * @param linkType target link id
	 * @return link name or "linktype.xxx" if not found
	 */
	static public String getLinkName(final int linkType)
	{
		try
		{
			final LinkDef linkDef = Mapping.linksById.get(linkType);
			return linkDef.name;
		}
		catch (final NullPointerException e)
		{
			return "linktype." + Integer.toString(linkType); //
		}
	}

	/**
	 * Find link id from link name
	 *
	 * @param linkName target link name
	 * @return link id or ANYTYPE if it is not found
	 */
	static public int getLinkType(final String linkName)
	{
		if (linkName == null)
		{
			return Mapping.ANYTYPE;
		}

		try
		{
			final LinkDef linkDef = Mapping.linksByName.get(linkName);
			return linkDef.id;
		}
		catch (final NullPointerException e)
		{
			return Mapping.ANYTYPE;
		}
	}

	/**
	 * Determine if this link can recurse
	 *
	 * @param linkType target link id
	 * @return whether this link type can recurse
	 */
	static public boolean canRecurse(final int linkType)
	{
		try
		{
			final LinkDef linkDef = Mapping.linksById.get(linkType);
			return linkDef.recurses;
		}
		catch (final IndexOutOfBoundsException e)
		{
			return false;
		}
		catch (final NullPointerException e)
		{
			return false;
		}
	}
}