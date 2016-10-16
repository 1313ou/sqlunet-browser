/*
 * @author <a href="mailto:bbou@ac-toulouse.fr">Bernard Bou</a>
 * Created on 31 dec. 2004
 * Filename : Link.java
 * Class encapsulating WordNet linked synset
 */
package org.sqlunet.wordnet.sql;

import java.util.ArrayList;
import java.util.List;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Link, a linked synset
 *
 * @author <a href="mailto:bbou@ac-toulouse.fr">Bernard Bou</a>
 */
class Link extends Synset
{
	/**
	 * <code>linkType</code> is the relation type
	 */
	private final int linkType;

	/**
	 * <code>fromSynsetId</code> is the source synset id
	 */
	@SuppressWarnings("unused")
	private final long fromSynsetId;

	/**
	 * Constructor from query for synsets linked to a given synset
	 *
	 * @param query
	 *            is a query for synsets linked to a given synset
	 */
	public Link(final LinksQueryCommand query)
	{
		// construct synset
		super(query);

		// link data
		this.linkType = query.getLinkType();
		this.fromSynsetId = query.getFromSynset();
	}

	/**
	 * Constructor from query for synsets linked to a given synset through a given relation type
	 *
	 * @param query
	 *            is a query for synsets linked to a given synset through a given relation type
	 */
	Link(final TypedLinksQueryCommand query)
	{
		// construct synset
		super(query);

		// link data
		this.linkType = query.getLinkType();
		this.fromSynsetId = query.getFromSynset();
	}

	/**
	 * Get link name
	 *
	 * @return link name
	 */
	public String getLinkName()
	{
		return Mapping.getLinkName(this.linkType);
	}

	/**
	 * Get whether link can recurse
	 *
	 * @return true if the link can recurse
	 */
	public boolean canRecurse()
	{
		return Mapping.canRecurse(this.linkType);
	}

	/**
	 * Override : recurse only on links of the same link type
	 *
	 * @see org.sqlunet.wordnet.sql.Synset#getLinks(android.database.sqlite.SQLiteDatabase, long)
	 */
	@Override
	public List<Link> getLinks(final SQLiteDatabase connection, final long wordId)
	{
		TypedLinksQueryCommand query = null;
		List<Link> links = new ArrayList<>();
		try
		{
			query = new TypedLinksQueryCommand(connection);
			query.setFromSynset(this.synsetId);
			query.setFromWord(wordId);
			query.setLinkType(this.linkType);
			query.execute();

			while (query.next())
			{
				final Link link = new Link(query);
				links.add(link);
			}
		}
		catch (final SQLException e)
		{
			e.printStackTrace();
			links = null;
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
		return links;
	}
}