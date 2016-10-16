/*
 * @author <a href="mailto:bbou@ac-toulouse.fr">Bernard Bou</a>
 * Created on 26 jan. 2005
 * Filename : Mapping.java
 * Class encapsulating id-name mappings
 */
package org.sqlunet.wordnet.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

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
	 * @param id
	 *            is the lexdomain id
	 * @param pos
	 *            is the part-of-speech-id
	 * @param name
	 *            is the lexdomain name
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
	 * @param string
	 *            is the full lexdomain name
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
	 * @param string
	 *            is the full lexdomain name
	 * @return the lexdomain name
	 */
	private String getLexDomainName(final String string)
	{
		final int thisIndex = string.indexOf('.');
		return thisIndex == -1 ? string : string.substring(thisIndex + 1);
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
	 * @param id
	 *            is the link id
	 * @param name
	 *            is the link name
	 * @param recurses
	 *            is whether the link recurses
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
	 * <code>theLexDomains</code> is an array of lexdomains
	 */
	static private List<LexDomainDef> theLexDomains = null;

	/**
	 * <code>theLexDomains</code> is map of lexdomains by name
	 */
	static private Map<String, LexDomainDef> theLexDomainsByName = null;

	/**
	 * <code>theLinks</code> is links mapped by id
	 */
	static private SparseArray<LinkDef> theLinksById = null;

	/**
	 * <code>theLinksByName</code> is links mapped by name
	 */
	static private Map<String, LinkDef> theLinksByName = null;

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
	 * @param thisConnection
	 *            is the database connection
	 */
	static public void initLexDomains(final SQLiteDatabase thisConnection)
	{
		// lexdomain
		LexDomainEnumQueryCommand thisLexDomainQuery = null;
		Mapping.theLexDomains = new ArrayList<>();
		Mapping.theLexDomainsByName = new HashMap<>();
		try
		{
			thisLexDomainQuery = new LexDomainEnumQueryCommand(thisConnection);
			thisLexDomainQuery.execute();

			while (thisLexDomainQuery.next())
			{
				final int thisId = thisLexDomainQuery.getId();
				final int thisPos = thisLexDomainQuery.getPos();
				final String thisName = thisLexDomainQuery.getPosLexDomainName().replace(' ', '.');
				final LexDomainDef thisLexDomain = new LexDomainDef(thisId, thisPos, thisName);
				Mapping.theLexDomains.add(thisLexDomain);
				Mapping.theLexDomainsByName.put(thisName, thisLexDomain);
			}
		}
		catch (final SQLException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		finally
		{
			if (thisLexDomainQuery != null)
			{
				thisLexDomainQuery.release();
			}
		}
	}

	/**
	 * Read link mappings from database
	 *
	 * @param thisConnection
	 *            is the database connection
	 */
	@SuppressWarnings("boxing")
	@SuppressLint("DefaultLocale")
	static public void initLinks(final SQLiteDatabase thisConnection)
	{
		LinkEnumQueryCommand thisLinksQuery = null;
		Mapping.theLinksById = new SparseArray<>();
		Mapping.theLinksByName = new HashMap<>();
		try
		{
			thisLinksQuery = new LinkEnumQueryCommand(thisConnection);
			thisLinksQuery.execute();

			while (thisLinksQuery.next())
			{
				final int thisId = thisLinksQuery.getId();
				final String thisName = thisLinksQuery.getName().replace(' ', '_').toLowerCase(Locale.US);
				final boolean recurses = thisLinksQuery.getRecurse();
				final LinkDef thisLinkDef = new LinkDef(thisId, thisName, recurses);
				Mapping.theLinksById.put(thisId, thisLinkDef);
				Mapping.theLinksByName.put(thisName, thisLinkDef);
			}
		}
		catch (final SQLException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		finally
		{
			if (thisLinksQuery != null)
			{
				thisLinksQuery.release();
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
		return new String[] { "noun", "verb", "adj", "adv" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	/**
	 * Get lexdomains as array of strings
	 *
	 * @return array of Strings
	 */
	static public String[] getLexDomainNames()
	{
		final Set<String> thisNameSet = Mapping.theLexDomainsByName.keySet();
		return thisNameSet.toArray(new String[thisNameSet.size()]);
	}

	/**
	 * Get linknames as array of strings
	 *
	 * @return array of Strings
	 */
	static public String[] getLinkNames()
	{
		final Set<String> thisNameSet = Mapping.theLinksByName.keySet();
		return thisNameSet.toArray(new String[thisNameSet.size()]);
	}

	// pos and lexdomain

	/**
	 * Find part-of-speech name from lexdomain id
	 *
	 * @param thisLexDomainId
	 *            is the lexdomain id
	 * @return part-of-speech name
	 */
	static public String getPosName(final int thisLexDomainId)
	{
		try
		{
			final LexDomainDef thisLexDomain = Mapping.theLexDomains.get(thisLexDomainId);
			return thisLexDomain.posName;
		}
		catch (final IndexOutOfBoundsException e)
		{
			return "lexdomainid." + Integer.toString(thisLexDomainId); //$NON-NLS-1$
		}
	}

	/**
	 * Find lexdomain name from lexdomain id
	 *
	 * @param thisLexDomainId
	 *            is the lexdomain id
	 * @return lexdomain name or "lexdomainid.xxx" if not found
	 */
	static public String getLexDomainName(final int thisLexDomainId)
	{
		try
		{
			final LexDomainDef thisLexDomain = Mapping.theLexDomains.get(thisLexDomainId);
			return thisLexDomain.lexDomainName;
		}
		catch (final IndexOutOfBoundsException e)
		{
			return "lexdomainid." + Integer.toString(thisLexDomainId); //$NON-NLS-1$
		}
	}

	/**
	 * Find lexdomain id from part-of-speech name and lexdomain name
	 *
	 * @param thisPosName
	 *            is the target part-of-speech name
	 * @param thisLexDomainName
	 *            is the target lexdomain name
	 * @return lexdomain id or -1 if not found
	 */
	static public int getLexDomainId(final String thisPosName, final String thisLexDomainName)
	{
		if (thisPosName == null || thisLexDomainName == null)
			return Mapping.ANYTYPE;
		final String thisString = thisPosName + '.' + thisLexDomainName;
		try
		{
			final LexDomainDef thisLexDomain = Mapping.theLexDomainsByName.get(thisString);
			return thisLexDomain.id;
		}
		catch (final NullPointerException e)
		{
			return Mapping.ANYTYPE;
		}
	}

	/**
	 * Find part-of-speech id (n,v,a,r) from part-of-speech name
	 *
	 * @param thisPosName
	 *            is the target part-of-speech name
	 * @return part-of-speech id or ANYTYPE if not found
	 */
	static public int getPosId(final String thisPosName)
	{
		if (thisPosName == null)
			return Mapping.ANYTYPE;

		switch (thisPosName)
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
	 * @param thisLinkType
	 *            is the target link id
	 * @return link name or "linktype.xxx" if not found
	 */
	@SuppressWarnings("boxing")
	static public String getLinkName(final int thisLinkType)
	{
		try
		{
			final LinkDef thisLinkDef = Mapping.theLinksById.get(thisLinkType);
			return thisLinkDef.name;
		}
		catch (final NullPointerException e)
		{
			return "linktype." + Integer.toString(thisLinkType); //$NON-NLS-1$
		}
	}

	/**
	 * Find link id from link name
	 *
	 * @param thisLinkName
	 *            is the target link name
	 * @return link id or ANYTYPE if it is not found
	 */
	static public int getLinkType(final String thisLinkName)
	{
		if (thisLinkName == null)
			return Mapping.ANYTYPE;

		try
		{
			final LinkDef thisLinkDef = Mapping.theLinksByName.get(thisLinkName);
			return thisLinkDef.id;
		}
		catch (final NullPointerException e)
		{
			return Mapping.ANYTYPE;
		}
	}

	/**
	 * Determine if this link can recurse
	 *
	 * @param thisLinkType
	 *            is the target link id
	 * @return whether this link type can recurse
	 */
	@SuppressWarnings("boxing")
	static public boolean canRecurse(final int thisLinkType)
	{
		try
		{
			final LinkDef thisLinkDef = Mapping.theLinksById.get(thisLinkType);
			return thisLinkDef.recurses;
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