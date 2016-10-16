package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import org.sqlunet.dom.Factory;
import org.sqlunet.wordnet.sql.NodeFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.List;

/**
 * Encapsulates FrameNet query implementation
 *
 * @author Bernard Bou
 */
public class FrameNetImplementation implements FrameNetInterface
{
	// S E L E C T I O N

	/**
	 * Business method that returns FrameNet selector data as DOM document
	 *
	 * @param connection database connection
	 * @param word       the target word
	 * @param pos        the pos to build query from
	 * @return FrameNet selector data as DOM document
	 */
	@Override
	public Document querySelectorDoc(final SQLiteDatabase connection, final String word, final Character pos)
	{
		final Document doc = Factory.makeDocument();
		final Node rootNode = FnNodeFactory.makeFnRootNode(doc, word, pos);
		FrameNetImplementation.walkSelector(connection, doc, rootNode, word);
		return doc;
	}

	/**
	 * Business method that returns FrameNet selector data as XML
	 *
	 * @param connection database connection
	 * @param word       the target word
	 * @param pos        the pos to build query from
	 * @return FrameNet selector data as XML
	 */
	@Override
	public String querySelectorXML(final SQLiteDatabase connection, final String word, final Character pos)
	{
		final Document doc = querySelectorDoc(connection, word, pos);
		return Factory.docToString(doc, "FrameNet_select.dtd");
	}

	// D E T A I L

	/**
	 * Business method the returns FrameNet data as DOM document
	 *
	 * @param connection database connection
	 * @param word       the target word
	 * @param pos        the pos to build query from
	 * @return FrameNet data as DOM document
	 */
	@Override
	public Document queryDoc(final SQLiteDatabase connection, final String word, final Character pos)
	{
		final Document doc = Factory.makeDocument();
		final Node rootNode = FnNodeFactory.makeFnRootNode(doc, word, pos);
		FrameNetImplementation.walk(connection, doc, rootNode, word);
		return doc;
	}

	/**
	 * Business method that returns FrameNet data as XML
	 *
	 * @param connection database connection
	 * @param word       the target word
	 * @param pos        the pos to build query from
	 * @return FrameNet data as XML
	 */
	@Override
	public String queryXML(final SQLiteDatabase connection, final String word, final Character pos)
	{
		final Document doc = queryDoc(connection, word, pos);
		return Factory.docToString(doc, "FrameNet.dtd");
	}

	// I T E M S

	// word

	/**
	 * Business method that returns FrameNet data as DOM document
	 *
	 * @param connection database connection
	 * @param wordId     the word id to build query from
	 * @param pos        the pos to build query from
	 * @return FrameNet data as DOM document
	 */
	@Override
	public Document queryDoc(final SQLiteDatabase connection, final long wordId, final Character pos)
	{
		final Document doc = Factory.makeDocument();
		final Node rootNode = FnNodeFactory.makeFnRootNode(doc, wordId, pos);
		FrameNetImplementation.walkWord(connection, doc, rootNode, wordId, pos);
		return doc;
	}

	/**
	 * Business method that returns FrameNet data as XML
	 *
	 * @param connection database connection
	 * @param wordId     the target word id
	 * @param pos        the pos to build query from
	 * @return FrameNet data as XML
	 */
	@Override
	public String queryXML(final SQLiteDatabase connection, final long wordId, final Character pos)
	{
		final Document doc = queryDoc(connection, wordId, pos);
		return Factory.docToString(doc, "FrameNet.dtd");
	}

	// frame

	/**
	 * Business method the returns frame data as DOM document
	 *
	 * @param connection database connection
	 * @param frameId    the frame to build query from
	 * @param pos        the pos to build query from
	 * @return FrameNet frame data as DOM document
	 */
	@Override
	public Document queryFrameDoc(final SQLiteDatabase connection, final long frameId, final Character pos)
	{
		final Document doc = Factory.makeDocument();
		final Node rootNode = FnNodeFactory.makeFnRootFrameNode(doc, frameId);
		FrameNetImplementation.walkFrame(connection, doc, rootNode, frameId);
		return doc;
	}

	/**
	 * Business method that returns frame data as XML
	 *
	 * @param connection database connection
	 * @param frameId    the frame to build query from
	 * @param pos        the pos to build query from
	 * @return FrameNet frame data as XML
	 */
	@Override
	public String queryFrameXML(final SQLiteDatabase connection, final long frameId, final Character pos)
	{
		final Document doc = queryFrameDoc(connection, frameId, pos);
		return Factory.docToString(doc, "FrameNet.dtd");
	}

	// lexunit

	/**
	 * Business method that returns lexunit data as DOM document
	 *
	 * @param connection database connection
	 * @param luId       the luid to build query from
	 * @return FrameNet lexunit data as DOM document
	 */
	public Document queryLexUnitDoc(final SQLiteDatabase connection, final long luId)
	{
		final Document doc = Factory.makeDocument();
		final Node rootNode = FnNodeFactory.makeFnRootLexUnitNode(doc, luId);
		FrameNetImplementation.walkLexUnit(connection, doc, rootNode, luId);
		return doc;
	}

	/**
	 * Business method that returns lexunit data as XML
	 *
	 * @param connection database connection
	 * @param luId       the luid to build query from
	 * @return FrameNet lexunit data as XML
	 */
	public String queryLexUnitXML(final SQLiteDatabase connection, final long luId)
	{
		final Document doc = queryLexUnitDoc(connection, luId);
		return Factory.docToString(doc, "FrameNet.dtd");
	}

	// sentence

	/**
	 * Business method that returns sentence data as DOM document
	 *
	 * @param connection database connection
	 * @param sentenceId the sentenceid to build query from
	 * @return FrameNet sentence data as DOM document
	 */
	public Document querySentenceDoc(final SQLiteDatabase connection, final long sentenceId)
	{
		final Document doc = Factory.makeDocument();
		final Node rootNode = FnNodeFactory.makeFnRootSentenceNode(doc, sentenceId);
		FrameNetImplementation.walkSentence(connection, doc, rootNode, sentenceId);
		return doc;
	}

	/**
	 * Business method that returns sentence data as XML
	 *
	 * @param connection database connection
	 * @param sentenceId the sentenceid to build query from
	 * @return FrameNet sentence data as XML
	 */
	@SuppressWarnings("unused")
	public String querySentenceXML(final SQLiteDatabase connection, final long sentenceId)
	{
		final Document doc = querySentenceDoc(connection, sentenceId);
		return Factory.docToString(doc, "FrameNet.dtd");
	}

	// annoset

	/**
	 * Business method that returns annoset data as DOM document
	 *
	 * @param connection database connection
	 * @param annoSetId  the annosetid to build query from
	 * @return FrameNet annoset data as DOM document
	 */
	public Document queryAnnoSetDoc(final SQLiteDatabase connection, final long annoSetId)
	{
		final Document doc = Factory.makeDocument();
		final Node rootNode = FnNodeFactory.makeFnRootAnnosetNode(doc, annoSetId);
		FrameNetImplementation.walkAnnoSet(connection, doc, rootNode, annoSetId);
		return doc;
	}

	/**
	 * Business method that returns annoset data as XML
	 *
	 * @param connection database connection
	 * @param annoSetId  the annosetid to build query from
	 * @return FrameNet annoset data as XML
	 */
	@SuppressWarnings("unused")
	public String queryAnnosetXML(final SQLiteDatabase connection, final long annoSetId)
	{
		final Document doc = queryAnnoSetDoc(connection, annoSetId);
		return Factory.docToString(doc, "FrameNet.dtd");
	}

	// W A L K

	/**
	 * Perform queries for FrameNet selection data
	 *
	 * @param connection connection
	 * @param doc        the org.w3c.dom.Document being built
	 * @param parent     the org.w3c.dom.Node the walk will attach results to
	 * @param targetWord the target word
	 */
	static private void walkSelector(final SQLiteDatabase connection, final Document doc, final Node parent, final String targetWord)
	{
		final Pair<Long, List<FnLexUnit>> result = FnLexUnit.makeFromWord(connection, targetWord);
		final Long wordId = result.first;
		final List<FnLexUnit> lexUnits = result.second;
		if (lexUnits == null)
			return;

		// word
		NodeFactory.makeWordNode(doc, parent, targetWord, wordId);

		// framenet nodes
		FrameNetImplementation.makeSelector(doc, parent, lexUnits, true);
	}

	/**
	 * Perform queries for FrameNet data from word
	 *
	 * @param connection connection
	 * @param doc        the org.w3c.dom.Document being built
	 * @param parent     the org.w3c.dom.Node the walk will attach results to
	 * @param targetWord the target word
	 */
	static private void walk(final SQLiteDatabase connection, final Document doc, final Node parent, final String targetWord)
	{
		final Pair<Long, List<FnLexUnit>> result = FnLexUnit.makeFromWord(connection, targetWord);
		final Long wordId = result.first;
		final List<FnLexUnit> lexUnits = result.second;
		if (lexUnits == null)
			return;

		// word
		NodeFactory.makeWordNode(doc, parent, targetWord, wordId);

		// lexunits
		for (final FnLexUnit lexUnit : lexUnits)
		{
			// lexunit
			final Node lexUnitNode = FnNodeFactory.makeFnLexunitNode(doc, parent, lexUnit);

			// frame
			final FnFrame frame = lexUnit.frame;
			FnNodeFactory.makeFnFrameNode(doc, lexUnitNode, frame, false);
		}
	}

	/**
	 * Perform queries for FrameNet data from word id
	 *
	 * @param connection   data source
	 * @param doc          the org.w3c.dom.Document being built
	 * @param parent       the org.w3c.dom.Node the walk will attach results to
	 * @param targetWordId the target word id
	 * @param pos          the target pos
	 */
	static private void walkWord(final SQLiteDatabase connection, final Document doc, final Node parent, final long targetWordId, final Character pos)
	{
		// lexunits
		final List<FnLexUnit> lexUnits = FnLexUnit.makeFromWordId(connection, targetWordId, pos);
		for (final FnLexUnit lexUnit : lexUnits)
		{
			// frame
			final Node lexUnitNode = FnNodeFactory.makeFnLexunitNode(doc, parent, lexUnit);

			// frame
			final Node frameNode = FnNodeFactory.makeFnFrameNode(doc, lexUnitNode, lexUnit.frame, false);

			// frame FEs
			final List<FnFrameElement> fes = FnFrameElement.make(connection, lexUnit.frame.frameId);
			if (fes != null)
			{
				int j = 1;
				for (final FnFrameElement fe : fes)
				{
					FnNodeFactory.makeFnFENode(doc, frameNode, fe);
					j++;
				}
			}

			// governors
			final List<FnGovernor> governors = FnGovernor.make(connection, lexUnit.luId);
			if (governors != null)
			{
				int j = 1;
				for (final FnGovernor governor : governors)
				{
					FnNodeFactory.makeFnGovernorNode(doc, lexUnitNode, governor);
					j++;
				}
			}

			// sentences
			final Node sentencesNode = FnNodeFactory.makeFnSentencesNode(doc, lexUnitNode);
			final List<FnSentence> sentences = FnSentence.makeFromLexicalUnit(connection, lexUnit.luId);
			if (sentences != null)
			{
				int j = 1;
				for (final FnSentence sentence : sentences)
				{
					FnNodeFactory.makeFnSentenceNode(doc, sentencesNode, sentence, j);
					j++;
				}
			}
		}
	}

	/**
	 * Perform queries for FrameNet data from frame id
	 *
	 * @param connection    data source
	 * @param doc           the org.w3c.dom.Document being built
	 * @param parent        the org.w3c.dom.Node the walk will attach results to
	 * @param targetFrameId the target frame id
	 */
	static private void walkFrame(final SQLiteDatabase connection, final Document doc, final Node parent, final long targetFrameId)
	{
		// frame
		final FnFrame frame = FnFrame.make(connection, targetFrameId);
		if (frame == null)
			return;

		final Node frameNode = FnNodeFactory.makeFnFrameNode(doc, parent, frame, false);

		// lexunits
		final List<FnLexUnit> lexUnits = FnLexUnit.makeFromFrame(connection, targetFrameId);
		for (final FnLexUnit lexUnit : lexUnits)
		{
			// includes frame info
			FnNodeFactory.makeFnLexunitNode(doc, frameNode, lexUnit);
		}

		// frame FEs
		final List<FnFrameElement> fes = FnFrameElement.make(connection, frame.frameId);
		if (fes != null)
		{
			int j = 1;
			for (final FnFrameElement fe : fes)
			{
				FnNodeFactory.makeFnFENode(doc, frameNode, fe);
				j++;
			}
		}
	}

	/**
	 * Perform queries for FrameNet data from lexunit id
	 *
	 * @param connection data source
	 * @param doc        the org.w3c.dom.Document being built
	 * @param parent     the org.w3c.dom.Node the walk will attach results to
	 * @param luId       the target lexunit id
	 */
	private static void walkLexUnit(final SQLiteDatabase connection, final Document doc, final Node parent, final long luId)
	{
		// lexunit
		final FnLexUnit lexUnit = FnLexUnit.makeFromId(connection, luId);
		final Node lexUnitNode = FnNodeFactory.makeFnLexunitNode(doc, parent, lexUnit);

		//			// frame FEs
		//			final List<FnFrameElement> fes = FnFrameElement.make(connection, lexUnit.frame.frameId);
		//			if (fes != null)
		//			{
		//				int j = 1;
		//				for (final FnFrameElement fe : fes)
		//				{
		//					FnNodeFactory.makeFnFENode(doc, lexUnitNode, fe, j);
		//					j++;
		//				}
		//			}

		// frame
		walkFrame(connection, doc, lexUnitNode, lexUnit.frame.frameId);
	}

	/**
	 * Perform queries for FrameNet data from sentence id
	 *
	 * @param connection data source
	 * @param doc        the org.w3c.dom.Document being built
	 * @param parent     the org.w3c.dom.Node the walk will attach results to
	 * @param sentenceId the target sentence id
	 */
	private static void walkSentence(final SQLiteDatabase connection, final Document doc, final Node parent, final long sentenceId)
	{
		// annoset
		final FnSentence sentence = FnSentence.make(connection, sentenceId);
		final Node sentenceNode = FnNodeFactory.makeFnSentenceNode(doc, parent, sentence, -1);

		// layers
		walkLayersFromSentence(connection, doc, sentenceNode, sentenceId);
	}

	/**
	 * Perform queries for FrameNet data from annoset id
	 *
	 * @param connection data source
	 * @param doc        the org.w3c.dom.Document being built
	 * @param parent     the org.w3c.dom.Node the walk will attach results to
	 * @param annoSetId  the target annoset id
	 */
	private static void walkAnnoSet(final SQLiteDatabase connection, final Document doc, final Node parent, final long annoSetId)
	{
		// annoset
		final FnAnnoSet annoSet = FnAnnoSet.make(connection, annoSetId);
		final Node annoSetNode = FnNodeFactory.makeFnAnnoSetNode(doc, parent, annoSet);

		// layers
		walkLayersFromAnnoSet(connection, doc, annoSetNode, annoSetId);
	}

	/**
	 * Perform queries for FrameNet layers data from annoset id
	 *
	 * @param connection data source
	 * @param doc        the org.w3c.dom.Document being built
	 * @param parent     the org.w3c.dom.Node the walk will attach results to
	 * @param annoSetId  the target annoset id
	 */
	private static void walkLayersFromAnnoSet(final SQLiteDatabase connection, final Document doc, final Node parent, final long annoSetId)
	{
		// layers
		final List<FnLayer> layers = FnLayer.makeFromAnnoSet(connection, annoSetId);
		for (final FnLayer layer : layers)
		{
			// layer
			FnNodeFactory.makeFnLayerNode(doc, parent, layer);
		}
	}

	/**
	 * Perform queries for FrameNet layers data from sentence id
	 *
	 * @param connection data source
	 * @param doc        the org.w3c.dom.Document being built
	 * @param parent     the org.w3c.dom.Node the walk will attach results to
	 * @param sentenceId the target sentence id
	 */
	private static void walkLayersFromSentence(final SQLiteDatabase connection, final Document doc, final Node parent, final long sentenceId)
	{
		// layers
		final List<FnLayer> layers = FnLayer.makeFromSentence(connection, sentenceId);
		for (final FnLayer layer : layers)
		{
			// layer
			FnNodeFactory.makeFnLayerNode(doc, parent, layer);
		}
	}

	// H E L P E R S

	/**
	 * Make selector
	 *
	 * @param doc      the org.w3c.dom.Document being built
	 * @param parent   the org.w3c.dom.Document being built
	 * @param lexUnits lexunits
	 * @param doFrame  whether to include frame data
	 */
	private static void makeSelector(final Document doc, final Node parent, final List<FnLexUnit> lexUnits, @SuppressWarnings("SameParameterValue") final boolean doFrame)
	{
		// lexunits
		for (final FnLexUnit lexUnit : lexUnits)
		{
			// lexunit
			final Node lexUnitNode = FnNodeFactory.makeFnLexunitNode(doc, parent, lexUnit);

			// frame
			if (doFrame)
			{
				final FnFrame frame = lexUnit.frame;
				FnNodeFactory.makeFnFrameNode(doc, lexUnitNode, frame, true);
			}
		}
	}
}
