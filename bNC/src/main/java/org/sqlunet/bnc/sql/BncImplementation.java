package org.sqlunet.bnc.sql;

import java.util.List;

import org.sqlunet.dom.Factory;
import org.sqlunet.sql.NodeFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import android.database.sqlite.SQLiteDatabase;

/**
 * Encapsulates BNC query implementation
 *
 * @author Bernard Bou
 */
public class BncImplementation implements BncInterface
{
	/**
	 * Business method the returns BNC data as DOM document from word
	 *
	 * @param thisConnection
	 *        database connection
	 * @param thisWord
	 *        the target word
	 * @return BNC data as DOM document
	 */
	@Override
	public Document queryDoc(final SQLiteDatabase thisConnection, final String thisWord)
	{
		final Document thisDoc = Factory.makeDocument();
		final Node thisWordNode = NodeFactory.makeNode(thisDoc, thisDoc, "bnc", thisWord); //$NON-NLS-1$
		BncImplementation.walk(thisConnection, thisDoc, thisWordNode, thisWord);
		return thisDoc;
	}

	/**
	 * Business method that returns BNC data as XML from word
	 *
	 * @param thisConnection
	 *        database connection
	 * @param thisWord
	 *        the target word
	 * @return BNC data as XML
	 */
	@Override
	public String queryXML(final SQLiteDatabase thisConnection, final String thisWord)
	{
		final Document thisDoc = queryDoc(thisConnection, thisWord);
		return Factory.docToString(thisDoc, "Bnc.dtd");
	}

	/**
	 * Business method that returns BNC data as DOM document from wordid and pos
	 *
	 * @param thisConnection
	 *        database connection
	 * @param thisWordId
	 *        the word id to build query from
	 * @param thisPos
	 *        the pos to build query from (null if any)
	 * @return BNC data as DOM document
	 */
	@Override
	public Document queryDoc(final SQLiteDatabase thisConnection, final long thisWordId, final Character thisPos)
	{
		final Document thisDoc = Factory.makeDocument();
		final Node thisWordNode = BncNodeFactory.makeBncRootNode(thisDoc, thisWordId, thisPos);
		BncImplementation.walk(thisConnection, thisDoc, thisWordNode, thisWordId, thisPos);
		return thisDoc;
	}

	/**
	 * Business method that returns BNC data as XML from wordid and pos
	 *
	 * @param thisConnection
	 *        database connection
	 * @param thisWordId
	 *        the target word id
	 * @param thisPos
	 *        the target pos (null if any)
	 * @return BNC data as XML
	 */
	@Override
	public String queryXML(final SQLiteDatabase thisConnection, final long thisWordId, final Character thisPos)
	{
		final Document thisDoc = queryDoc(thisConnection, thisWordId, thisPos);
		return Factory.docToString(thisDoc, "BNC.dtd");
	}

	// B N C

	/**
	 * Perform queries for BNC data
	 *
	 * @param thisConnection
	 *        data source
	 * @param thisDoc
	 *        the org.w3c.dom.Document being built
	 * @param thisParent
	 *        the org.w3c.dom.Node the walk will attach results to
	 * @param thisTargetWord
	 *        the target word
	 */
	private static void walk(final SQLiteDatabase thisConnection, final Document thisDoc, final Node thisParent, final String thisTargetWord)
	{
		final List<BncData> theseData = BncData.makeData(thisConnection, thisTargetWord);
		int i = 1;
		for (final BncData thisData : theseData)
		{
			BncNodeFactory.makeBncNode(thisDoc, thisParent, thisData, i++);
		}
	}

	/**
	 * Perform queries for BNC data
	 *
	 * @param thisConnection
	 *        data source
	 * @param thisDoc
	 *        the org.w3c.dom.Document being built
	 * @param thisParent
	 *        the org.w3c.dom.Node the walk will attach results to
	 * @param thisTargetWordId
	 *        the target word id
	 * @param thisTargetPos
	 *        the target pos (null for any)
	 */
	private static void walk(final SQLiteDatabase thisConnection, final Document thisDoc, final Node thisParent, final long thisTargetWordId, final Character thisTargetPos)
	{
		final List<BncData> theseData = BncData.makeData(thisConnection, thisTargetWordId, thisTargetPos);
		int i = 1;
		for (final BncData thisData : theseData)
		{
			BncNodeFactory.makeBncNode(thisDoc, thisParent, thisData, i++);
		}
	}
}
