package org.sqlunet.wordnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.dom.Factory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.List;

/**
 * Encapsulates WordNet query implementation
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class WordNetImplementation implements WordNetInterface
{
	// S E T T I N  G S

	/**
	 * The maximum recursion level defined for down-tree queries
	 */
	static private final int MAX_RECURSE_LEVEL = 2;

	// S E L E C T O R

	/**
	 * Perform queries for WordNet selector
	 *
	 * @param connection connection
	 * @param doc        org.w3c.dom.Document being built
	 * @param parent     org.w3c.dom.Node walk will attach results to
	 * @param targetWord target word
	 */
	private static void walkSelector(final SQLiteDatabase connection, final Document doc, final Node parent, final String targetWord)
	{
		// word
		final Word word = Word.make(connection, targetWord);
		if (word == null)
		{
			return;
		}
		NodeFactory.makeWordNode(doc, parent, word.lemma, word.id);

		// iterate synsets

		final List<Synset> synsets = word.getSynsets(connection);
		if (synsets == null)
		{
			return;
		}
		String lexDomain = null;
		String pos = null;
		Node lexDomainNode = null;
		Node posNode = null;
		for (int i = 0; i < synsets.size(); i++)
		{
			final Synset synset = synsets.get(i);

			// pos node
			final String posName = synset.getPosName();
			if (!posName.equals(pos))
			{
				posNode = NodeFactory.makePosNode(doc, parent, posName);
				pos = posName;
				lexDomain = null;
			}

			// lexdomain node
			final String lexDomainName = synset.getLexDomainName();
			if (!lexDomainName.equals(lexDomain))
			{
				lexDomainNode = NodeFactory.makeLexDomainNode(doc, posNode, lexDomainName);
				lexDomain = lexDomainName;
			}

			// sense node
			final Node senseNode = NodeFactory.makeSenseNode(doc, lexDomainNode, i + 1);

			// synset nodes
			WordNetImplementation.walkSynset(connection, doc, senseNode, synset);
		}
	}

	/**
	 * Perform queries for WordNet data from word
	 *
	 * @param connection          connection
	 * @param targetWord          target word
	 * @param doc                 org.w3c.dom.Document being built
	 * @param parent              org.w3c.dom.Node walk will attach results to
	 * @param withLinks           determines if queries are to include links
	 * @param recurse             determines if queries are to follow links recursively
	 * @param targetPosType       target part-of-speech type (ANYTYPE for all types)
	 * @param targetLexDomainType target lexdomain type (ANYTYPE for all types)
	 * @param targetLinkType      target link type (ANYTYPE for all types)
	 */
	private static void walk(final SQLiteDatabase connection, final String targetWord, final Document doc, final Node parent, final boolean withLinks, final boolean recurse, final int targetPosType, final int targetLexDomainType, final int targetLinkType)
	{
		// word
		final Word word = Word.make(connection, targetWord);
		if (word == null)
		{
			return;
		}
		NodeFactory.makeWordNode(doc, parent, word.lemma, word.id);

		// iterate synsets
		final List<Synset> synsets = targetPosType == Mapping.ANYTYPE && targetLexDomainType == Mapping.ANYTYPE ?
				word.getSynsets(connection) :
				word.getTypedSynsets(connection, targetLexDomainType == Mapping.ANYTYPE ? targetPosType : targetLexDomainType, targetLexDomainType != Mapping.ANYTYPE);
		if (synsets == null)
		{
			return;
		}
		String lexDomain = null;
		String pos = null;
		Node lexDomainNode = null;
		Node posNode = null;
		for (int i = 0; i < synsets.size(); i++)
		{
			final Synset synset = synsets.get(i);

			// pos node
			final String posName = synset.getPosName();
			if (!posName.equals(pos))
			{
				posNode = NodeFactory.makePosNode(doc, parent, posName);
				pos = posName;
				lexDomain = null;
			}

			// lexdomain node
			final String lexDomainName = synset.getLexDomainName();
			if (!lexDomainName.equals(lexDomain))
			{
				lexDomainNode = NodeFactory.makeLexDomainNode(doc, posNode, lexDomainName);
				lexDomain = lexDomainName;
			}

			// sense node
			final Node senseNode = NodeFactory.makeSenseNode(doc, lexDomainNode, i + 1);

			// synset nodes
			WordNetImplementation.walkSynset(connection, doc, senseNode, synset);

			// links
			WordNetImplementation.walkSynsetLinks(connection, doc, senseNode, synset, word.id, withLinks, recurse, targetLinkType);
		}
	}

	// D E T A I L

	/**
	 * Perform queries for WordNet data from word id and synset id
	 *
	 * @param connection     connection
	 * @param wordId         target word id
	 * @param synsetId       target synset id
	 * @param doc            org.w3c.dom.Document being built
	 * @param parent         org.w3c.dom.Node walk will attach results to
	 * @param withLinks      determines if queries are to include links
	 * @param recurse        determines if queries are to follow links recursively
	 * @param targetLinkType target link type
	 */
	private static void walkSense(final SQLiteDatabase connection, //
			final long wordId,  //
			final Long synsetId,  //
			final Document doc, //
			final Node parent, //
			final boolean withLinks,//
			final boolean recurse, //
			@SuppressWarnings("SameParameterValue") final int targetLinkType)
	{
		if (synsetId == null)
		{
			final SynsetsQueryCommand query = new SynsetsQueryCommand(connection, wordId);
			query.execute();

			String posName = null;
			String lexDomainName = null;
			Node posNode = null;
			Node lexDomainNode = null;
			int i = 0;
			while (query.next())
			{
				final Synset synset = new Synset(query);

				// pos node
				final String synsetPosName = synset.getPosName();
				if (!synsetPosName.equals(posName))
				{
					posNode = NodeFactory.makePosNode(doc, parent, synsetPosName);
					posName = synsetPosName;
				}

				// lexdomain node
				final String synsetLexDomainName = synset.getLexDomainName();
				if (!synsetLexDomainName.equals(lexDomainName))
				{
					lexDomainNode = NodeFactory.makeLexDomainNode(doc, posNode, synsetLexDomainName);
					lexDomainName = synsetLexDomainName;
				}

				// sense node
				final Node senseNode = NodeFactory.makeSenseNode(doc, lexDomainNode, ++i);

				// synset
				WordNetImplementation.walkSynset(connection, doc, senseNode, synset);

				// links
				WordNetImplementation.walkSynsetLinks(connection, doc, senseNode, synset, wordId, withLinks, recurse, targetLinkType);
			}
			return;
		}

		final SynsetQueryCommand query = new SynsetQueryCommand(connection, synsetId);
		query.execute();
		if (query.next())
		{
			final Synset synset = new Synset(query);

			// pos node
			final String posName = synset.getPosName();
			final Node posNode = NodeFactory.makePosNode(doc, parent, posName);

			// lexdomain node
			final String lexDomainName = synset.getLexDomainName();
			final Node lexDomainNode = NodeFactory.makeLexDomainNode(doc, posNode, lexDomainName);

			// sense node
			final Node senseNode = NodeFactory.makeSenseNode(doc, lexDomainNode, 1);

			// synset
			WordNetImplementation.walkSynset(connection, doc, senseNode, synset);

			// links
			WordNetImplementation.walkSynsetLinks(connection, doc, senseNode, synset, wordId, withLinks, recurse, targetLinkType);
		}
	}

	/**
	 * Process synset data
	 *
	 * @param connection connection
	 * @param doc        org.w3c.dom.Document being built
	 * @param parent     org.w3c.dom.Node walk will attach results to
	 * @param synset     synset whose data are to be processed
	 */
	private static void walkSynset(final SQLiteDatabase connection, final Document doc, final Node parent, final Synset synset)
	{
		// synset words
		final List<Word> words = synset.getSynsetWords(connection);

		// anchor node
		final Node synsetNode = NodeFactory.makeSynsetNode(doc, parent, words != null ? words.size() : 0, synset.synsetId);

		// words
		if (words != null)
		{
			for (final Word word : words)
			{
				final String lemma = word.lemma.replace('_', ' ');
				NodeFactory.makeWordNode(doc, synsetNode, lemma, word.id);
			}
		}

		// gloss
		org.sqlunet.sql.NodeFactory.makeNode(doc, synsetNode, "definition", synset.definition); //

		// sample
		if (synset.sample != null)
		{
			org.sqlunet.sql.NodeFactory.makeNode(doc, synsetNode, "sample", synset.sample); //
		}
	}

	/**
	 * Perform synset link queries for WordNet data
	 *
	 * @param connection     connection
	 * @param doc            org.w3c.dom.Document being built
	 * @param parent         org.w3c.dom.Node walk will attach results to
	 * @param synset         target synset
	 * @param wordId         target word id
	 * @param withLinks      determines if queries are to include links
	 * @param recurse        determines if queries are to follow links recursively
	 * @param targetLinkType target link type
	 */
	private static void walkSynsetLinks(final SQLiteDatabase connection, final Document doc, final Node parent, final Synset synset, final long wordId, final boolean withLinks, final boolean recurse, final int targetLinkType)
	{
		if (withLinks)
		{
			// link node
			final Node linkNode = org.sqlunet.sql.NodeFactory.makeNode(doc, parent, "links", null); //

			// get links
			final List<Link> links = targetLinkType == Mapping.ANYTYPE ? synset.getLinks(connection, wordId) : synset.getTypedLinks(connection, wordId, targetLinkType);
			if (links == null)
			{
				return;
			}

			// iterate links
			Node linkTypeNode = null;
			String linkType = null;
			for (final Link link : links)
			{
				// anchor node
				final String type = link.getLinkName();
				if (!type.equals(linkType))
				{
					linkTypeNode = NodeFactory.makeLinkNode(doc, linkNode, type, 0);
					linkType = type;
				}

				// recurse check
				final int recurse2 = recurse ?
						synset.lexDomainId == Mapping.topsId && (targetLinkType == Mapping.hyponymId || targetLinkType == Mapping.instanceHyponymId || targetLinkType == Mapping.ANYTYPE) ? Mapping.NONRECURSIVE : 0 :
						Mapping.NONRECURSIVE;

				// process link
				WordNetImplementation.walkLink(connection, doc, linkTypeNode, link, wordId, recurse2, targetLinkType);
			}
		}
	}

	/**
	 * Process synset links (recurses)
	 *
	 * @param connection     connection
	 * @param doc            org.w3c.dom.Document being built
	 * @param parent         org.w3c.dom.Node walk will attach results to
	 * @param link           synset to start walk from
	 * @param wordId         word id to start walk from
	 * @param recurseLevel   recursion level
	 * @param targetLinkType target link type (cannot be ANYTYPE for all types)
	 */
	static private void walkLink(final SQLiteDatabase connection, final Document doc, final Node parent, final Link link, final long wordId, final int recurseLevel, final int targetLinkType)
	{
		// synset
		WordNetImplementation.walkSynset(connection, doc, parent, link);

		// recurse
		if (recurseLevel != Mapping.NONRECURSIVE && link.canRecurse())
		{
			// stop recursion in case maximum level is reached and
			// hyponym/all links and source synset lexdomain is tops
			if ((targetLinkType == Mapping.hyponymId || targetLinkType == Mapping.instanceHyponymId) && recurseLevel >= WordNetImplementation.MAX_RECURSE_LEVEL)
			{
				NodeFactory.makeMoreLinkNode(doc, parent, link.getLinkName(), recurseLevel);
			}
			else
			{
				// links
				final List<Link> subLinks = targetLinkType == Mapping.ANYTYPE ? link.getLinks(connection, wordId) : link.getTypedLinks(connection, wordId, targetLinkType);
				if (subLinks == null)
				{
					return;
				}

				// iterate sublinks
				Node subLinkTypeNode = null;
				String subLinkType = null;
				for (final Link subLink : subLinks)
				{
					// anchor node
					final String type = subLink.getLinkName();
					if (!type.equals(subLinkType))
					{
						subLinkTypeNode = NodeFactory.makeLinkNode(doc, parent, type, recurseLevel);
						subLinkType = type;
					}
					WordNetImplementation.walkLink(connection, doc, subLinkTypeNode, subLink, wordId, recurseLevel + 1, targetLinkType);
				}
			}
		}
	}

	/**
	 * Initial house-keeping - directory queries - connections - database static data
	 *
	 * @throws RuntimeException
	 */
	public static void init(final SQLiteDatabase connection)
	{
		// do queries for static maps
		Mapping.initLexDomains(connection);
		Mapping.initLinks(connection);
	}

	// I T E M S

	/**
	 * Business method that returns WordNet selector data as DOM document
	 *
	 * @param connection connection
	 * @param word       target word
	 * @return WordNet selector data as DOM document <!-- end-user-doc -->
	 */
	@Override
	public Document querySelectorDoc(final SQLiteDatabase connection, final String word)
	{
		final Document doc = Factory.makeDocument();
		final Node rootNode = org.sqlunet.sql.NodeFactory.makeNode(doc, doc, "wordnet", word); //
		org.sqlunet.sql.NodeFactory.makeTargetNode(doc, rootNode, "word", word); //
		WordNetImplementation.walkSelector(connection, doc, rootNode, word);
		return doc;
	}

	/**
	 * Business method that returns WordNet selector data as XML
	 *
	 * @param connection connection
	 * @param word       target word
	 * @return WordNet selector data as XML
	 */
	@Override
	public String querySelectorXML(final SQLiteDatabase connection, final String word)
	{
		final Document doc = querySelectorDoc(connection, word);
		return Factory.docToString(doc, "WordNet_select.dtd");
	}

	/**
	 * Business method that returns WordNet data as a Document
	 *
	 * @param connection connection
	 * @param word       target word
	 * @param withLinks  determines if queries are to include links
	 * @param recurse    determines if queries are to follow links recursively
	 * @return WordNet data as a DOM Document <!-- end-user-doc -->
	 */
	@Override
	public Document queryDoc(final SQLiteDatabase connection, final String word, final boolean withLinks, final boolean recurse)
	{
		final Document doc = Factory.makeDocument();
		final Node rootNode = org.sqlunet.sql.NodeFactory.makeNode(doc, doc, "wordnet", null); //
		org.sqlunet.sql.NodeFactory.makeTargetNode(doc, rootNode, "lemma", word); //
		WordNetImplementation.walk(connection, word, doc, rootNode, withLinks, recurse, Mapping.ANYTYPE, Mapping.ANYTYPE, Mapping.ANYTYPE);
		return doc;
	}

	/**
	 * Business method that returns WordNet data as a Document
	 *
	 * @param connection connection
	 * @param wordId     target word id
	 * @param synsetId   target synset id
	 * @param withLinks  determines if queries are to include links
	 * @param recurse    determines if queries are to follow links recursively
	 * @return WordNet data as a DOM Document <!-- end-user-doc -->
	 */
	@Override
	public Document queryDoc(final SQLiteDatabase connection, final long wordId, final Long synsetId, final boolean withLinks, final boolean recurse)
	{
		final Document doc = Factory.makeDocument();
		final Node rootNode = org.sqlunet.sql.NodeFactory.makeNode(doc, doc, "wordnet", null); //
		org.sqlunet.sql.NodeFactory.makeTargetNode(doc, rootNode, "word-id", Long.toString(wordId), "synset-id", synsetId == null ? null : Long.toString(synsetId)); //
		WordNetImplementation.walkSense(connection, wordId, synsetId, doc, rootNode, withLinks, recurse, Mapping.ANYTYPE);
		return doc;
	}

	/**
	 * Business method that returns complete data as XML
	 *
	 * @param connection connection
	 * @param word       target word
	 * @param withLinks  determines if queries are to include links
	 * @param recurse    determines if queries are to follow links recursively
	 * @return WordNet data as XML <!-- end-user-doc -->
	 */
	@Override
	public String queryXML(final SQLiteDatabase connection, final String word, final boolean withLinks, final boolean recurse)
	{
		final Document doc = queryDoc(connection, word, withLinks, recurse);
		return Factory.docToString(doc, "WordNet.dtd");
	}

	/**
	 * Business method that returns WordNet data as DOM document
	 *
	 * @param connection    connection
	 * @param word          target word
	 * @param posName       target part-of-speech
	 * @param lexDomainName target lexdomain
	 * @param linkName      target link type
	 * @param withLinks     determines if queries are to include links
	 * @param recurse       determines if queries are to follow links recursively
	 * @return WordNet data as DOM document <!-- end-user-doc -->
	 */
	@Override
	public Document queryDoc(final SQLiteDatabase connection, final String word, final String posName, final String lexDomainName, final String linkName, final boolean withLinks, final boolean recurse)
	{
		final Document doc = Factory.makeDocument();

		// parameters
		final int posType = Mapping.getPosId(posName);
		final int lexDomainType = Mapping.getLexDomainId(posName, lexDomainName);
		final int linkType = Mapping.getLinkType(linkName);

		// fill document
		final Node rootNode = org.sqlunet.sql.NodeFactory.makeNode(doc, doc, "wordnet", null); //
		org.sqlunet.sql.NodeFactory.makeTargetNode(doc, rootNode, //
				"word", word,  //
				"pos", posName,  //
				"lexdomain", lexDomainName, //
				"link", linkName, //
				"withlinks", Boolean.toString(withLinks), //
				"recurse", Boolean.toString(recurse)); //
		WordNetImplementation.walk(connection, word, doc, rootNode, withLinks, recurse, posType, lexDomainType, linkType);

		return doc;
	}

	/**
	 * Business method that returns WordNet data as XML
	 *
	 * @param connection    connection
	 * @param word          target word
	 * @param posName       target part-of-speech
	 * @param lexDomainName target lexdomain
	 * @param linkName      target link type
	 * @param withLinks     determines if queries are to include links
	 * @param recurse       determines if queries are to follow links recursively
	 * @return WordNet data as XML data <!-- end-user-doc -->
	 */
	@Override
	public String queryXML(final SQLiteDatabase connection, final String word, final String posName, final String lexDomainName, final String linkName, final boolean withLinks, final boolean recurse)
	{
		final Document doc = queryDoc(connection, word, posName, lexDomainName, linkName, withLinks, recurse);
		return Factory.docToString(doc, "WordNet.dtd");
	}

	// W A L K

	/**
	 * Business method that returns WordNet sense data as DOM document
	 *
	 * @param connection connection
	 * @param wordId     target word id
	 * @param synsetId   target synset id
	 * @return WordNet synset data as DOM document <!-- end-user-doc -->
	 */
	@Override
	public Document querySenseDoc(final SQLiteDatabase connection, final long wordId, final long synsetId)
	{
		final Document doc = Factory.makeDocument();
		final Node rootNode = org.sqlunet.sql.NodeFactory.makeNode(doc, doc, "wordnet", null); //
		org.sqlunet.sql.NodeFactory.makeTargetNode(doc, rootNode, "synset-id", Long.toString(synsetId)); //

		final Node senseNode = NodeFactory.makeSenseNode(doc, rootNode, wordId, synsetId);

		final SynsetQueryCommand query = new SynsetQueryCommand(connection, synsetId);
		query.execute();
		if (query.next())
		{
			// synset
			final Synset synset = new Synset(query);
			WordNetImplementation.walkSynset(connection, doc, senseNode, synset);

			// links
			WordNetImplementation.walkSynsetLinks(connection, doc, senseNode, synset, -1, true /* withLinks */, true /* recurse */, Mapping.ANYTYPE);
		}
		return doc;
	}

	/**
	 * Business method that returns WordNet sense data as XML
	 *
	 * @param connection connection
	 * @param wordId     target word id
	 * @param synsetId   target synset id
	 * @return WordNet synset data as XML
	 */
	@Override
	public String querySenseXML(final SQLiteDatabase connection, final long wordId, final long synsetId)
	{
		final Document doc = querySenseDoc(connection, wordId, synsetId);
		return Factory.docToString(doc, "WordNet.dtd");
	}

	/**
	 * Business method that returns WordNet synset data as DOM document
	 *
	 * @param connection connection
	 * @param synsetId   target synset id
	 * @return WordNet synset data as DOM document <!-- end-user-doc -->
	 */
	@Override
	public Document querySynsetDoc(final SQLiteDatabase connection, final long synsetId)
	{
		final Document doc = Factory.makeDocument();
		final Node rootNode = org.sqlunet.sql.NodeFactory.makeNode(doc, doc, "wordnet", null); //
		org.sqlunet.sql.NodeFactory.makeTargetNode(doc, rootNode, "synset-id", Long.toString(synsetId)); //
		final SynsetQueryCommand query = new SynsetQueryCommand(connection, synsetId);
		query.execute();
		if (query.next())
		{
			// synset
			final Synset synset = new Synset(query);
			WordNetImplementation.walkSynset(connection, doc, rootNode, synset);

			// links
			WordNetImplementation.walkSynsetLinks(connection, doc, rootNode, synset, -1, true /* withLinks */, true /* recurse */, Mapping.ANYTYPE);
		}
		return doc;
	}

	/**
	 * Business method that returns WordNet synset data as XML
	 *
	 * @param connection connection
	 * @param synsetId   target synset id
	 * @return WordNet synset data as XML
	 */
	@Override
	public String querySynsetXML(final SQLiteDatabase connection, final long synsetId)
	{
		final Document doc = querySynsetDoc(connection, synsetId);
		return Factory.docToString(doc, "WordNet.dtd");
	}

	/**
	 * Business method that returns WordNet parts-of-speech as array of strings
	 *
	 * @return array of Strings
	 */
	@Override
	public String[] getPosNames()
	{
		return Mapping.getPosNames();
	}

	/**
	 * Business method that returns WordNet lexdomains as array of strings
	 *
	 * @return array of Strings
	 */
	@Override
	public String[] getLexDomainNames()
	{
		return Mapping.getLexDomainNames();
	}

	// I N I T I A L I Z E

	/**
	 * Business method that returns WordNet link names as array of strings
	 *
	 * @return array of Strings
	 */
	@Override
	public String[] getLinkNames()
	{
		return Mapping.getLinkNames();
	}
}
