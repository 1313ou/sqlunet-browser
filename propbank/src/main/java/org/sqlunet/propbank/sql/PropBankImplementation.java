package org.sqlunet.propbank.sql;

import java.util.List;

import org.sqlunet.dom.Factory;
import org.sqlunet.wordnet.sql.NodeFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

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
	 * @param thisConnection
	 *        database connection
	 * @param thisWord
	 *        the target word
	 * @return PropBank selector data as DOM document
	 */
	@Override
	public Document querySelectorDoc(final SQLiteDatabase thisConnection, final String thisWord)
	{
		final Document thisDoc = Factory.makeDocument();
		final Node thisWordNode = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisDoc, "propbank", thisWord); //$NON-NLS-1$
		PropBankImplementation.walkSelector(thisConnection, thisDoc, thisWordNode, thisWord);
		return thisDoc;
	}

	/**
	 * Business method that returns PropBank selector data as XML
	 *
	 * @param thisConnection
	 *        database connection
	 * @param thisWord
	 *        the target word
	 * @return PropBank selector data as XML
	 */
	@Override
	public String querySelectorXML(final SQLiteDatabase thisConnection, final String thisWord)
	{
		final Document thisDoc = querySelectorDoc(thisConnection, thisWord);
		return Factory.docToString(thisDoc, "PropBank_select.dtd");
	}

	// D E T A I L

	/**
	 * Business method the returns PropBank data as DOM document from word
	 *
	 * @param thisConnection
	 *        database connection
	 * @param thisWord
	 *        the target word
	 * @return PropBank data as DOM document
	 */
	@Override
	public Document queryDoc(final SQLiteDatabase thisConnection, final String thisWord)
	{
		final Document thisDoc = Factory.makeDocument();
		final Node thisWordNode = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisDoc, "propbank", thisWord); //$NON-NLS-1$
		PropBankImplementation.walk(thisConnection, thisDoc, thisWordNode, thisWord);
		return thisDoc;
	}

	/**
	 * Business method that returns PropBank data as XML from word
	 *
	 * @param thisConnection
	 *        database connection
	 * @param thisWord
	 *        the target word
	 * @return PropBank data as XML
	 */
	@Override
	public String queryXML(final SQLiteDatabase thisConnection, final String thisWord)
	{
		final Document thisDoc = queryDoc(thisConnection, thisWord);
		return Factory.docToString(thisDoc, "PropBank.dtd");
	}

	/**
	 * Business method that returns PropBank data as DOM document from word id
	 *
	 * @param thisConnection
	 *        database connection
	 * @param thisWordId
	 *        the word id to build query from
	 * @param thisPos
	 *        the pos to build query from
	 * @return PropBank data as DOM document
	 */
	@Override
	public Document queryDoc(final SQLiteDatabase thisConnection, final long thisWordId, final Character thisPos)
	{
		final Document thisDoc = Factory.makeDocument();
		final Node thisWordNode = PbNodeFactory.makePbRootNode(thisDoc, thisWordId);
		PropBankImplementation.walk(thisConnection, thisDoc, thisWordNode, thisWordId);
		return thisDoc;
	}

	/**
	 * Business method that returns PropBank data as XML from word id
	 *
	 * @param thisConnection
	 *        database connection
	 * @param thisWordId
	 *        the target word id
	 * @param thisPos
	 *        the pos to build query from
	 * @return PropBank data as XML
	 */
	@Override
	public String queryXML(final SQLiteDatabase thisConnection, final long thisWordId, final Character thisPos)
	{
		final Document thisDoc = queryDoc(thisConnection, thisWordId, thisPos);
		return Factory.docToString(thisDoc, "PropBank.dtd");
	}

	// I T E M S

	/**
	 * Business method the returns role set data as DOM document from roleset id
	 *
	 * @param thisConnection
	 *        database connection
	 * @param thisRoleSetId
	 *        the role set to build query from
	 * @param thisPos
	 *        the pos to build query from
	 * @return Propbank role set data as DOM document
	 */
	@Override
	public Document queryRoleSetDoc(final SQLiteDatabase thisConnection, final long thisRoleSetId, final Character thisPos)
	{
		final Document thisDoc = Factory.makeDocument();
		final Node thisRootNode = PbNodeFactory.makePbRootRoleSetNode(thisDoc, thisRoleSetId);
		PropBankImplementation.walkRoleSet(thisConnection, thisDoc, thisRootNode, thisRoleSetId);
		return thisDoc;
	}

	/**
	 * Business method that returns role set data as XML from roleset id
	 *
	 * @param thisConnection
	 *        database connection
	 * @param thisRoleSetId
	 *        the roleset id to build query from
	 * @param thisPos
	 *        the pos to build query from
	 * @return Propbank role set data as XML
	 */
	@Override
	public String queryRoleSetXML(final SQLiteDatabase thisConnection, final long thisRoleSetId, final Character thisPos)
	{
		final Document thisDoc = queryRoleSetDoc(thisConnection, thisRoleSetId, thisPos);
		return Factory.docToString(thisDoc, "PropBank.dtd");
	}

	// W A L K

	/**
	 * Perform queries for PropBank selector data from word
	 *
	 * @param thisConnection
	 *        connection
	 * @param thisDoc
	 *        the org.w3c.dom.Document being built
	 * @param thisParent
	 *        the org.w3c.dom.Node the walk will attach results to
	 * @param thisTargetWord
	 *        the target word
	 */
	static private void walkSelector(final SQLiteDatabase thisConnection, final Document thisDoc, final Node thisParent, final String thisTargetWord)
	{
		final Pair<Long, List<PbRoleSet>> thisResult = PbRoleSet.makeFromWord(thisConnection, thisTargetWord);
		final Long thisWordId = thisResult.first;
		final List<PbRoleSet> theseRoleSets = thisResult.second;
		if (theseRoleSets == null)
			return;

		// word
		NodeFactory.makeWordNode(thisDoc, thisParent, thisTargetWord, thisWordId);

		// propbank nodes
		PropBankImplementation.makeSelector(thisDoc, thisParent, theseRoleSets);
	}

	/**
	 * Perform queries for PropBank data from word
	 *
	 * @param thisConnection
	 *        connection
	 * @param thisDoc
	 *        the org.w3c.dom.Document being built
	 * @param thisParent
	 *        the org.w3c.dom.Node the walk will attach results to
	 * @param thisTargetWord
	 *        the target word
	 */
	static private void walk(final SQLiteDatabase thisConnection, final Document thisDoc, final Node thisParent, final String thisTargetWord)
	{
		final Pair<Long, List<PbRoleSet>> thisResult = PbRoleSet.makeFromWord(thisConnection, thisTargetWord);
		final Long thisWordId = thisResult.first;
		final List<PbRoleSet> theseRoleSets = thisResult.second;
		if (theseRoleSets == null)
			return;

		// word
		NodeFactory.makeWordNode(thisDoc, thisParent, thisTargetWord, thisWordId);

		// rolesets
		int i = 1;
		for (final PbRoleSet thisRoleSet : theseRoleSets)
		{
			// roleset
			PbNodeFactory.makePbRoleSetNode(thisDoc, thisParent, thisRoleSet, i++);
		}
	}

	/**
	 * Perform queries for PropBank data from word id
	 *
	 * @param thisConnection
	 *        data source
	 * @param thisDoc
	 *        the org.w3c.dom.Document being built
	 * @param thisParent
	 *        the org.w3c.dom.Node the walk will attach results to
	 * @param thisTargetWordId
	 *        the target word id
	 */
	static private void walk(final SQLiteDatabase thisConnection, final Document thisDoc, final Node thisParent, final long thisTargetWordId)
	{
		// rolesets
		final List<PbRoleSet> theseRoleSets = PbRoleSet.makeFromWordId(thisConnection, thisTargetWordId);
		walk(thisConnection, thisDoc, thisParent, theseRoleSets);
	}

	/**
	 * Perform queries for PropBank data from roleset id
	 * 
	 * @param thisConnection
	 *        data source
	 * @param thisDoc
	 *        the org.w3c.dom.Document being built
	 * @param thisParent
	 *        the org.w3c.dom.Node the walk will attach results to
	 * @param thisRoleSetId
	 *        rolesetid
	 */
	private static void walkRoleSet(final SQLiteDatabase thisConnection, final Document thisDoc, final Node thisParent, final long thisRoleSetId)
	{
		// rolesets
		final List<PbRoleSet> theseRoleSets = PbRoleSet.make(thisConnection, thisRoleSetId);
		walk(thisConnection, thisDoc, thisParent, theseRoleSets);
	}

	/**
	 * Query PropBank data from rolesets
	 * 
	 * @param thisConnection
	 *        data source
	 * @param thisDoc
	 *        the org.w3c.dom.Document being built
	 * @param thisParent
	 *        the org.w3c.dom.Node the walk will attach results to
	 * @param theseRoleSets
	 *        rolesets
	 */
	static private void walk(final SQLiteDatabase thisConnection, final Document thisDoc, final Node thisParent, final List<PbRoleSet> theseRoleSets)
	{
		// rolesets
		int i = 1;
		for (final PbRoleSet thisRoleSet : theseRoleSets)
		{
			// roleset
			final Node thisRoleSetNode = PbNodeFactory.makePbRoleSetNode(thisDoc, thisParent, thisRoleSet, i++);

			// roles
			final List<PbRole> theseRoles = PbRole.make(thisConnection, thisRoleSet.theRoleSetId);
			for (final PbRole thisRole : theseRoles)
			{
				PbNodeFactory.makePbRoleNode(thisDoc, thisRoleSetNode, thisRole);
			}
			// examples
			final List<PbExample> theseExamples = PbExample.make(thisConnection, thisRoleSet.theRoleSetId);
			for (final PbExample thisExample : theseExamples)
			{
				PbNodeFactory.makePbExampleNode(thisDoc, thisRoleSetNode, thisExample);
			}
		}
	}

	// H E L P E R S

	/**
	 * Display query results for PropBank data from query result
	 * 
	 * @param doc
	 *        the org.w3c.dom.Document being built
	 * @param parent
	 *        the org.w3c.dom.Node the walk will attach results to
	 * @param roleSets
	 *        rolesets
	 */
	static private void makeSelector(final Document doc, final Node parent, final List<PbRoleSet> roleSets)
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
