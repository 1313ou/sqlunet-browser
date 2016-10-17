/*
 * @author <a href="mailto:bbou@ac-toulouse.fr">Bernard Bou</a>
 * Created on 26 jan. 2005
 * Filename : Mapping.java
 * Class encapsulating id-name mappings
 */
package org.sqlunet.wordnet.sql;

import android.annotation.SuppressLint;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * LexDomainDef, utility class to encapsulate lexdomain data
 *
 * @author <a href="mailto:bbou@ac-toulouse.fr">Bernard Bou</a>
 */
class LexDomainDef
{
	/**
	 * <code>id</code> is the lexdomain id
	 */
	public final int id;

	/**
	 * <code>pos</code> is the pos id
	 */
	@SuppressWarnings("unused")
	public final int pos;

	/**
	 * <code>posName</code> is the part-of-speech
	 */
	public final String posName;

	/**
	 * <code>lexDomainName</code> is the lexdomain name
	 */
	public final String lexDomainName;

	/**
	 * Constructor
	 *
	 * @param id   is the lexdomain id
	 * @param pos  is the part-of-speech-id
	 * @param name is the lexdomain name
	 */
	public LexDomainDef(final int id, final int pos, final String name)
	{
		super();
		this.id = id;
		this.pos = pos;
		this.posName = getPosName(name);
		this.lexDomainName = getLexDomainName(name);
	}

	/**
	 * Get part-of-speech name
	 *
	 * @param string is the full lexdomain name
	 * @return the part-of-speech name
	 */
	private String getPosName(final String string)
	{
		final int index = string.indexOf('.');
		return index == -1 ? string : string.substring(0, index);
	}

	/**
	 * Get lexdomain name
	 *
	 * @param string is the full lexdomain name
	 * @return the lexdomain name
	 */
	private String getLexDomainName(final String string)
	{
		final int index = string.indexOf('.');
		return index == -1 ? string : string.substring(index + 1);
	}
}

/**
 * LinkDef, utility class to encapsulate link data
 *
 * @author <a href="mailto:bbou@ac-toulouse.fr">Bernard Bou</a>
 */

class LinkDef
{
	/**
	 * <code>id</code> is the link id
	 */
	public final int id;

	/**
	 * <code>pos</code> is the part-of-speech id
	 */
	@SuppressWarnings("unused")
	public int pos;

	/**
	 * <code>name</code> is the link name
	 */
	public final String name;

	/**
	 * <code>recurses</code> is whether the link recurses
	 */
	public final boolean recurses;

	/**
	 * Constructor
	 *
	 * @param id       is the link id
	 * @param name     is the link name
	 * @param recurses is whether the link recurses
	 */
	public LinkDef(final int id, final String name, final boolean recurses)
	{
		super();
		this.id = id;
		this.name = name;
		this.recurses = recurses;
	}
}

/**
 * Id-name mappings
 *
 * @author <a href="mailto:bbou@ac-toulouse.fr">Bernard Bou</a>
 */

class Mapping
{
	/**
	 * <code>lexDomains</code> is an array of lexdomains
	 */
	static private List<LexDomainDef> lexDomains = null;

	/**
	 * <code>lexDomains</code> is map of lexdomains by name
	 */
	static private Map<String, LexDomainDef> lexDomainsByName = null;

	/**
	 * <code>linksById</code> is links mapped by id
	 */
	static private SparseArray<LinkDef> linksById = null;

	/**
	 * <code>linksByName</code> is links mapped by name
	 */
	static private Map<String, LinkDef> linksByName = null;

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
	 * @param connection is the database connection
	 */
	static public void initLexDomains(final SQLiteDatabase connection)
	{
		// lexdomain
		LexDomainEnumQueryCommand query = null;
		Mapping.lexDomains = new ArrayList<>();
		Mapping.lexDomainsByName = new HashMap<>();
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
			e.printStackTrace();
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
	 * @param connection is the database connection
	 */
	@SuppressLint("DefaultLocale")
	static public void initLinks(final SQLiteDatabase connection)
	{
		LinkEnumQueryCommand query = null;
		Mapping.linksById = new SparseArray<>();
		Mapping.linksByName = new HashMap<>();
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
			e.printStackTrace();
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

	// get tables

	/**
	 * Get part-of-speech names as array of strings
	 *
	 * @return array of string part-of-speech names
	 */
	static public String[] getPosNames()
	{
		return new String[]{"noun", "verb", "adj", "adv"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
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

	// pos and lexdomain

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
			return "lexdomainid." + Integer.toString(lexDomainId); //$NON-NLS-1$
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
			return "lexdomainid." + Integer.toString(lexDomainId); //$NON-NLS-1$
		}
	}

	/**
	 * Find lexdomain id from part-of-speech name and lexdomain name
	 *
	 * @param posName       is the target part-of-speech name
	 * @param lexDomainName is the target lexdomain name
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
	 * @param posName is the target part-of-speech name
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
				//$NON-NLS-1$
				return 'n';
			case "verb":
				//$NON-NLS-1$
				return 'v';
			case "adj":
				//$NON-NLS-1$
				return 'a';
			case "adv":
				//$NON-NLS-1$
				return 'r';
		}
		return Mapping.ANYTYPE;
	}

	// links

	/**
	 * Find link name from link id
	 *
	 * @param linkType is the target link id
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
			return "linktype." + Integer.toString(linkType); //$NON-NLS-1$
		}
	}

	/**
	 * Find link id from link name
	 *
	 * @param linkName is the target link name
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
	 * @param linkType is the target link id
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