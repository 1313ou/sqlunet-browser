package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import org.sqlunet.dom.DomFactory;
import org.sqlunet.dom.DomTransformer;
import org.sqlunet.wordnet.sql.NodeFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.List;

/**
 * Encapsulates FrameNet query implementation
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FrameNetImplementation implements FrameNetInterface
{
	static public final String FN_NS = "http://org.sqlunet/fn";

	// S E L E C T O R

	/**
	 * Business method that returns FrameNet selector data as DOM document
	 *
	 * @param connection connection
	 * @param word       the target word
	 * @param pos        the pos to build query from
	 * @return FrameNet selector data as DOM document
	 */
	@Override
	public Document querySelectorDoc(final SQLiteDatabase connection, final String word, final Character pos)
	{
		final Document doc = DomFactory.makeDocument();
		final Node rootNode = FnNodeFactory.makeFnRootNode(doc, word, pos);
		FrameNetImplementation.walkSelector(connection, doc, rootNode, word);
		return doc;
	}

	/**
	 * Business method that returns FrameNet selector data as XML
	 *
	 * @param connection connection
	 * @param word       the target word
	 * @param pos        the pos to build query from
	 * @return FrameNet selector data as XML
	 */
	@Override
	public String querySelectorXML(final SQLiteDatabase connection, final String word, final Character pos)
	{
		final Document doc = querySelectorDoc(connection, word, pos);
		return DomTransformer.docToString(doc);
	}

	// D E T A I L

	/**
	 * Business method that returns FrameNet data as DOM document
	 *
	 * @param connection connection
	 * @param word       the target word
	 * @param pos        the pos to build query from
	 * @return FrameNet data as DOM document
	 */
	@Override
	public Document queryDoc(final SQLiteDatabase connection, final String word, final Character pos)
	{
		final Document doc = DomFactory.makeDocument();
		final Node rootNode = FnNodeFactory.makeFnRootNode(doc, word, pos);
		FrameNetImplementation.walk(connection, doc, rootNode, word);
		return doc;
	}

	/**
	 * Business method that returns FrameNet data as XML
	 *
	 * @param connection connection
	 * @param word       the target word
	 * @param pos        the pos to build query from
	 * @return FrameNet data as XML
	 */
	@Override
	public String queryXML(final SQLiteDatabase connection, final String word, final Character pos)
	{
		final Document doc = queryDoc(connection, word, pos);
		return DomTransformer.docToString(doc);
	}

	// I T E M S

	// word

	/**
	 * Business method that returns FrameNet data as DOM document
	 *
	 * @param connection connection
	 * @param wordId     the word id to build query from
	 * @param pos        the pos to build query from
	 * @return FrameNet data as DOM document
	 */
	@Override
	public Document queryDoc(final SQLiteDatabase connection, final long wordId, final Character pos)
	{
		final Document doc = DomFactory.makeDocument();
		final Node rootNode = FnNodeFactory.makeFnRootNode(doc, wordId, pos);
		FrameNetImplementation.walkWord(connection, doc, rootNode, wordId, pos);
		return doc;
	}

	/**
	 * Business method that returns FrameNet data as XML
	 *
	 * @param connection connection
	 * @param wordId     the target word id
	 * @param pos        the pos to build query from
	 * @return FrameNet data as XML
	 */
	@Override
	public String queryXML(final SQLiteDatabase connection, final long wordId, final Character pos)
	{
		final Document doc = queryDoc(connection, wordId, pos);
		return DomTransformer.docToString(doc);
	}

	// frame

	/**
	 * Business method that returns frame data as DOM document
	 *
	 * @param connection connection
	 * @param frameId    the frame to build query from
	 * @param pos        the pos to build query from
	 * @return FrameNet frame data as DOM document
	 */
	@Override
	public Document queryFrameDoc(final SQLiteDatabase connection, final long frameId, final Character pos)
	{
		final Document doc = DomFactory.makeDocument();
		final Node rootNode = FnNodeFactory.makeFnRootFrameNode(doc, frameId);
		FrameNetImplementation.walkFrame(connection, doc, rootNode, frameId);
		return doc;
	}

	/**
	 * Business method that returns frame data as XML
	 *
	 * @param connection connection
	 * @param frameId    the frame to build query from
	 * @param pos        the pos to build query from
	 * @return FrameNet frame data as XML
	 */
	@Override
	public String queryFrameXML(final SQLiteDatabase connection, final long frameId, final Character pos)
	{
		final Document doc = queryFrameDoc(connection, frameId, pos);
		return DomTransformer.docToString(doc);
	}

	// lexunit

	/**
	 * Business method that returns lexunit data as DOM document
	 *
	 * @param connection connection
	 * @param luId       the luId to build query from
	 * @return FrameNet lexunit data as DOM document
	 */
	@Override
	public Document queryLexUnitDoc(final SQLiteDatabase connection, final long luId)
	{
		final Document doc = DomFactory.makeDocument();
		final Node rootNode = FnNodeFactory.makeFnRootLexUnitNode(doc, luId);
		FrameNetImplementation.walkLexUnit(connection, doc, rootNode, luId);
		return doc;
	}

	/**
	 * Business method that returns lexunit data as XML
	 *
	 * @param connection connection
	 * @param luId       the luId to build query from
	 * @return FrameNet lexunit data as XML
	 */
	@Override
	public String queryLexUnitXML(final SQLiteDatabase connection, final long luId)
	{
		final Document doc = queryLexUnitDoc(connection, luId);
		return DomTransformer.docToString(doc);
	}

	// sentence

	/**
	 * Business method that returns sentence data as DOM document
	 *
	 * @param connection connection
	 * @param sentenceId the sentence id to build query from
	 * @return FrameNet sentence data as DOM document
	 */
	public Document querySentenceDoc(final SQLiteDatabase connection, final long sentenceId)
	{
		final Document doc = DomFactory.makeDocument();
		final Node rootNode = FnNodeFactory.makeFnRootSentenceNode(doc, sentenceId);
		FrameNetImplementation.walkSentence(connection, doc, rootNode, sentenceId);
		return doc;
	}

	/**
	 * Business method that returns sentence data as XML
	 *
	 * @param connection connection
	 * @param sentenceId the sentence id to build query from
	 * @return FrameNet sentence data as XML
	 */
	@SuppressWarnings("unused")
	public String querySentenceXML(final SQLiteDatabase connection, final long sentenceId)
	{
		final Document doc = querySentenceDoc(connection, sentenceId);
		return DomTransformer.docToString(doc);
	}

	// annoSet

	/**
	 * Business method that returns annoSet data as DOM document
	 *
	 * @param connection connection
	 * @param annoSetId  the annoSetId to build query from
	 * @return FrameNet annoSet data as DOM document
	 */
	public Document queryAnnoSetDoc(final SQLiteDatabase connection, final long annoSetId)
	{
		final Document doc = DomFactory.makeDocument();
		final Node rootNode = FnNodeFactory.makeFnRootAnnoSetNode(doc, annoSetId);
		FrameNetImplementation.walkAnnoSet(connection, doc, rootNode, annoSetId);
		return doc;
	}

	/**
	 * Business method that returns annoSet data as XML
	 *
	 * @param connection connection
	 * @param annoSetId  the annoSetId to build query from
	 * @return FrameNet annoSet data as XML
	 */
	@SuppressWarnings("unused")
	public String queryAnnoSetXML(final SQLiteDatabase connection, final long annoSetId)
	{
		final Document doc = queryAnnoSetDoc(connection, annoSetId);
		return DomTransformer.docToString(doc);
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
		final List<FnLexUnit> lexUnits = result.second;
		if (lexUnits == null)
		{
			return;
		}

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
		{
			return;
		}

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
				for (final FnFrameElement fe : fes)
				{
					FnNodeFactory.makeFnFENode(doc, frameNode, fe);
				}
			}

			// governors
			final List<FnGovernor> governors = FnGovernor.make(connection, lexUnit.luId);
			if (governors != null)
			{
				for (final FnGovernor governor : governors)
				{
					FnNodeFactory.makeFnGovernorNode(doc, lexUnitNode, governor);
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
		{
			return;
		}

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
			for (final FnFrameElement fe : fes)
			{
				FnNodeFactory.makeFnFENode(doc, frameNode, fe);
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
	static private void walkLexUnit(final SQLiteDatabase connection, final Document doc, final Node parent, final long luId)
	{
		// lexunit
		final FnLexUnit lexUnit = FnLexUnit.makeFromId(connection, luId);
		final Node lexUnitNode = FnNodeFactory.makeFnLexunitNode(doc, parent, lexUnit);

		// frame FEs
		/*
		final List<FnFrameElement> fes = FnFrameElement.make(connection, lexUnit.frame.frameId);
		if (fes != null)
		{
			for (final FnFrameElement fe : fes)
			{
				FnNodeFactory.makeFnFENode(doc, lexUnitNode, fe);
			}
		}
		*/

		// frame
		walkFrame(connection, doc, lexUnitNode, lexUnit.frame.frameId);

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

	/**
	 * Perform queries for FrameNet data from sentence id
	 *
	 * @param connection data source
	 * @param doc        the org.w3c.dom.Document being built
	 * @param parent     the org.w3c.dom.Node the walk will attach results to
	 * @param sentenceId the target sentence id
	 */
	static private void walkSentence(final SQLiteDatabase connection, final Document doc, final Node parent, final long sentenceId)
	{
		// sentence
		final FnSentence sentence = FnSentence.make(connection, sentenceId);
		final Node sentenceNode = FnNodeFactory.makeFnSentenceNode(doc, parent, sentence, 0);

		// layers
		walkLayersFromSentence(connection, doc, sentenceNode, sentenceId);
	}

	/**
	 * Perform queries for FrameNet data from annoSet id
	 *
	 * @param connection data source
	 * @param doc        the org.w3c.dom.Document being built
	 * @param parent     the org.w3c.dom.Node the walk will attach results to
	 * @param annoSetId  the target annoSet id
	 */
	static private void walkAnnoSet(final SQLiteDatabase connection, final Document doc, final Node parent, final long annoSetId)
	{
		// annoSet
		final FnAnnoSet annoSet = FnAnnoSet.make(connection, annoSetId);

		// sentence node
		final Node sentenceNode = FnNodeFactory.makeFnSentenceNode(doc, parent, annoSet.sentence, 0);

		// annoSet node
		final Node annoSetNode = FnNodeFactory.makeFnAnnoSetNode(doc, sentenceNode, annoSet);

		// layers
		walkLayersFromAnnoSet(connection, doc, annoSetNode, annoSetId);
	}

	/**
	 * Perform queries for FrameNet layers data from annoSet id
	 *
	 * @param connection data source
	 * @param doc        the org.w3c.dom.Document being built
	 * @param parent     the org.w3c.dom.Node the walk will attach results to
	 * @param annoSetId  the target annoSet id
	 */
	static private void walkLayersFromAnnoSet(final SQLiteDatabase connection, final Document doc, final Node parent, final long annoSetId)
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
	static private void walkLayersFromSentence(final SQLiteDatabase connection, final Document doc, final Node parent, final long sentenceId)
	{
		// layers
		final List<FnLayer> layers = FnLayer.makeFromSentence(connection, sentenceId);
		long annoSetId = -1;
		Node annoSetNode = null;
		for (final FnLayer layer : layers)
		{
			if (annoSetId != layer.annoSetId)
			{
				annoSetNode = FnNodeFactory.makeFnAnnoSetNode(doc, parent, layer.annoSetId);
				annoSetId = layer.annoSetId;
			}

			// layer
			FnNodeFactory.makeFnLayerNode(doc, annoSetNode, layer);
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
	static private void makeSelector(final Document doc, final Node parent, final Iterable<FnLexUnit> lexUnits, @SuppressWarnings("SameParameterValue") final boolean doFrame)
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
