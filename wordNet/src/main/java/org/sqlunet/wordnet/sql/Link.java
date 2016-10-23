package org.sqlunet.wordnet.sql;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Link, a linked synset
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class Link extends Synset
{
	private static final String TAG = "Link"; //

	/**
	 * <code>linkType</code> relation type
	 */
	private final int linkType;

	/**
	 * <code>fromSynsetId</code> source synset id
	 */
	@SuppressWarnings("unused")
	private final long fromSynsetId;

	/**
	 * Constructor from query for synsets linked to a given synset
	 *
	 * @param query query for synsets linked to a given synset
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
	 * @param query is a query for synsets linked to a given synset through a given relation type
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
			Log.e(TAG, "While querying", e);
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