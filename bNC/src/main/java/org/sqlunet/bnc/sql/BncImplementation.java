package org.sqlunet.bnc.sql;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import org.sqlunet.sql.NodeFactory;
import org.sqlunet.dom.DomFactory;
import org.sqlunet.dom.DomTransformer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.List;

/**
 * Encapsulates BNC query implementation
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class BncImplementation implements BncInterface
{
	static public final String BNC_NS = "http://org.sqlunet/bc";

	/**
	 * Business method that returns BNC data as DOM document from word
	 *
	 * @param connection connection
	 * @param word       the target word
	 * @return BNC data as DOM document
	 */
	@Override
	public Document queryDoc(final SQLiteDatabase connection, final String word)
	{
		final Document doc = DomFactory.makeDocument();
		final Node rootNode = NodeFactory.makeNode(doc, doc, "bnc", word, BncImplementation.BNC_NS);
		BncImplementation.walk(connection, doc, rootNode, word);
		return doc;
	}

	/**
	 * Business method that returns BNC data as XML from word
	 *
	 * @param connection connection
	 * @param word       the target word
	 * @return BNC data as XML
	 */
	@Override
	public String queryXML(final SQLiteDatabase connection, final String word)
	{
		final Document doc = queryDoc(connection, word);
		return DomTransformer.docToString(doc);
	}

	/**
	 * Business method that returns BNC data as DOM document from word id and pos
	 *
	 * @param connection connection
	 * @param wordId     the word id to build query from
	 * @param pos        the pos to build query from (null if any)
	 * @return BNC data as DOM document
	 */
	@Override
	public Document queryDoc(final SQLiteDatabase connection, final long wordId, final Character pos)
	{
		final Document doc = DomFactory.makeDocument();
		final Node rootNode = BncNodeFactory.makeBncRootNode(doc, wordId, pos);
		BncImplementation.walk(connection, doc, rootNode, wordId, pos);
		return doc;
	}

	/**
	 * Business method that returns BNC data as XML from word id and pos
	 *
	 * @param connection connection
	 * @param wordId     the target word id
	 * @param pos        the target pos (null if any)
	 * @return BNC data as XML
	 */
	@Override
	public String queryXML(final SQLiteDatabase connection, final long wordId, final Character pos)
	{
		final Document doc = queryDoc(connection, wordId, pos);
		return DomTransformer.docToString(doc);
	}

	// B N C

	/**
	 * Perform queries for BNC data
	 *
	 * @param connection data source
	 * @param doc        the org.w3c.dom.Document being built
	 * @param parent     the org.w3c.dom.Node the walk will attach results to
	 * @param targetWord the target word
	 */
	static private void walk(final SQLiteDatabase connection, @NonNull final Document doc, final Node parent, final String targetWord)
	{
		final List<BncData> datas = BncData.makeData(connection, targetWord);
		int i = 1;
		for (final BncData data : datas)
		{
			BncNodeFactory.makeBncNode(doc, parent, data, i++);
		}
	}

	/**
	 * Perform queries for BNC data
	 *
	 * @param connection   data source
	 * @param doc          the org.w3c.dom.Document being built
	 * @param parent       the org.w3c.dom.Node the walk will attach results to
	 * @param targetWordId the target word id
	 * @param targetPos    the target pos (null for any)
	 */
	static private void walk(final SQLiteDatabase connection, @NonNull final Document doc, final Node parent, final long targetWordId, final Character targetPos)
	{
		final List<BncData> datas = BncData.makeData(connection, targetWordId, targetPos);
		int i = 1;
		for (final BncData data : datas)
		{
			BncNodeFactory.makeBncNode(doc, parent, data, i++);
		}
	}
}
