package org.sqlunet.propbank.sql;

import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import org.sqlunet.dom.Factory;
import org.sqlunet.wordnet.sql.NodeFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.List;

/**
 * Encapsulates PropBank query implementation
 *
 * @author Bernard Bou
 */
public class PropBankImplementation implements PropBankInterface
{
	// S E L E C T I O N

	/**
	 * Business method the returns PropBank selector data as DOM document
	 *
	 * @param connection database connection
	 * @param word       the target word
	 * @return PropBank selector data as DOM document
	 */
	@Override
	public Document querySelectorDoc(final SQLiteDatabase connection, final String word)
	{
		final Document doc = Factory.makeDocument();
		final Node wordNode = org.sqlunet.sql.NodeFactory.makeNode(doc, doc, "propbank", word); //$NON-NLS-1$
		PropBankImplementation.walkSelector(connection, doc, wordNode, word);
		return doc;
	}

	/**
	 * Business method that returns PropBank selector data as XML
	 *
	 * @param connection database connection
	 * @param word       the target word
	 * @return PropBank selector data as XML
	 */
	@Override
	public String querySelectorXML(final SQLiteDatabase connection, final String word)
	{
		final Document doc = querySelectorDoc(connection, word);
		return Factory.docToString(doc, "PropBank_select.dtd");
	}

	// D E T A I L

	/**
	 * Business method the returns PropBank data as DOM document from word
	 *
	 * @param connection database connection
	 * @param word       the target word
	 * @return PropBank data as DOM document
	 */
	@Override
	public Document queryDoc(final SQLiteDatabase connection, final String word)
	{
		final Document doc = Factory.makeDocument();
		final Node wordNode = org.sqlunet.sql.NodeFactory.makeNode(doc, doc, "propbank", word); //$NON-NLS-1$
		PropBankImplementation.walk(connection, doc, wordNode, word);
		return doc;
	}

	/**
	 * Business method that returns PropBank data as XML from word
	 *
	 * @param connection database connection
	 * @param word       the target word
	 * @return PropBank data as XML
	 */
	@Override
	public String queryXML(final SQLiteDatabase connection, final String word)
	{
		final Document doc = queryDoc(connection, word);
		return Factory.docToString(doc, "PropBank.dtd");
	}

	/**
	 * Business method that returns PropBank data as DOM document from word id
	 *
	 * @param connection database connection
	 * @param wordId     the word id to build query from
	 * @param pos        the pos to build query from
	 * @return PropBank data as DOM document
	 */
	@Override
	public Document queryDoc(final SQLiteDatabase connection, final long wordId, final Character pos)
	{
		final Document doc = Factory.makeDocument();
		final Node wordNode = PbNodeFactory.makePbRootNode(doc, wordId);
		PropBankImplementation.walk(connection, doc, wordNode, wordId);
		return doc;
	}

	/**
	 * Business method that returns PropBank data as XML from word id
	 *
	 * @param connection database connection
	 * @param wordId     the target word id
	 * @param pos        the pos to build query from
	 * @return PropBank data as XML
	 */
	@Override
	public String queryXML(final SQLiteDatabase connection, final long wordId, final Character pos)
	{
		final Document doc = queryDoc(connection, wordId, pos);
		return Factory.docToString(doc, "PropBank.dtd");
	}

	// I T E M S

	/**
	 * Business method the returns role set data as DOM document from roleset id
	 *
	 * @param connection database connection
	 * @param roleSetId  the role set to build query from
	 * @param pos        the pos to build query from
	 * @return Propbank role set data as DOM document
	 */
	@Override
	public Document queryRoleSetDoc(final SQLiteDatabase connection, final long roleSetId, final Character pos)
	{
		final Document doc = Factory.makeDocument();
		final Node rootNode = PbNodeFactory.makePbRootRoleSetNode(doc, roleSetId);
		PropBankImplementation.walkRoleSet(connection, doc, rootNode, roleSetId);
		return doc;
	}

	/**
	 * Business method that returns role set data as XML from roleset id
	 *
	 * @param connection database connection
	 * @param roleSetId  the roleset id to build query from
	 * @param pos        the pos to build query from
	 * @return Propbank role set data as XML
	 */
	@Override
	public String queryRoleSetXML(final SQLiteDatabase connection, final long roleSetId, final Character pos)
	{
		final Document doc = queryRoleSetDoc(connection, roleSetId, pos);
		return Factory.docToString(doc, "PropBank.dtd");
	}

	// W A L K

	/**
	 * Perform queries for PropBank selector data from word
	 *
	 * @param connection connection
	 * @param doc        the org.w3c.dom.Document being built
	 * @param parent     the org.w3c.dom.Node the walk will attach results to
	 * @param targetWord the target word
	 */
	static private void walkSelector(final SQLiteDatabase connection, final Document doc, final Node parent, final String targetWord)
	{
		final Pair<Long, List<PbRoleSet>> result = PbRoleSet.makeFromWord(connection, targetWord);
		final Long wordId = result.first;
		final List<PbRoleSet> roleSets = result.second;
		if (roleSets == null)
		{
			return;
		}

		// word
		NodeFactory.makeWordNode(doc, parent, targetWord, wordId);

		// propbank nodes
		PropBankImplementation.makeSelector(doc, parent, roleSets);
	}

	/**
	 * Perform queries for PropBank data from word
	 *
	 * @param connection connection
	 * @param doc        the org.w3c.dom.Document being built
	 * @param parent     the org.w3c.dom.Node the walk will attach results to
	 * @param targetWord the target word
	 */
	static private void walk(final SQLiteDatabase connection, final Document doc, final Node parent, final String targetWord)
	{
		final Pair<Long, List<PbRoleSet>> result = PbRoleSet.makeFromWord(connection, targetWord);
		final Long wordId = result.first;
		final List<PbRoleSet> roleSets = result.second;
		if (roleSets == null)
		{
			return;
		}

		// word
		NodeFactory.makeWordNode(doc, parent, targetWord, wordId);

		// rolesets
		int i = 1;
		for (final PbRoleSet roleSet : roleSets)
		{
			// roleset
			PbNodeFactory.makePbRoleSetNode(doc, parent, roleSet, i++);
		}
	}

	/**
	 * Perform queries for PropBank data from word id
	 *
	 * @param connection   data source
	 * @param doc          the org.w3c.dom.Document being built
	 * @param parent       the org.w3c.dom.Node the walk will attach results to
	 * @param targetWordId the target word id
	 */
	static private void walk(final SQLiteDatabase connection, final Document doc, final Node parent, final long targetWordId)
	{
		// rolesets
		final List<PbRoleSet> roleSets = PbRoleSet.makeFromWordId(connection, targetWordId);
		walk(connection, doc, parent, roleSets);
	}

	/**
	 * Perform queries for PropBank data from roleset id
	 *
	 * @param connection data source
	 * @param doc        the org.w3c.dom.Document being built
	 * @param parent     the org.w3c.dom.Node the walk will attach results to
	 * @param roleSetId  rolesetid
	 */
	private static void walkRoleSet(final SQLiteDatabase connection, final Document doc, final Node parent, final long roleSetId)
	{
		// rolesets
		final List<PbRoleSet> roleSets = PbRoleSet.make(connection, roleSetId);
		walk(connection, doc, parent, roleSets);
	}

	/**
	 * Query PropBank data from rolesets
	 *
	 * @param connection data source
	 * @param doc        the org.w3c.dom.Document being built
	 * @param parent     the org.w3c.dom.Node the walk will attach results to
	 * @param roleSets   rolesets
	 */
	static private void walk(final SQLiteDatabase connection, final Document doc, final Node parent, final Iterable<PbRoleSet> roleSets)
	{
		// rolesets
		int i = 1;
		for (final PbRoleSet roleSet : roleSets)
		{
			// roleset
			final Node roleSetNode = PbNodeFactory.makePbRoleSetNode(doc, parent, roleSet, i++);

			// roles
			final List<PbRole> roles = PbRole.make(connection, roleSet.roleSetId);
			for (final PbRole role : roles)
			{
				PbNodeFactory.makePbRoleNode(doc, roleSetNode, role);
			}
			// examples
			final List<PbExample> examples = PbExample.make(connection, roleSet.roleSetId);
			for (final PbExample example : examples)
			{
				PbNodeFactory.makePbExampleNode(doc, roleSetNode, example);
			}
		}
	}

	// H E L P E R S

	/**
	 * Display query results for PropBank data from query result
	 *
	 * @param doc      the org.w3c.dom.Document being built
	 * @param parent   the org.w3c.dom.Node the walk will attach results to
	 * @param roleSets rolesets
	 */
	static private void makeSelector(final Document doc, final Node parent, final Iterable<PbRoleSet> roleSets)
	{
		// rolesets
		int i = 1;
		for (final PbRoleSet roleSet : roleSets)
		{
			// roleset
			PbNodeFactory.makePbRoleSetNode(doc, parent, roleSet, i++);
		}
	}
}
