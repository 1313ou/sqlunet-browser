/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.verbnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.dom.DomFactory;
import org.sqlunet.dom.DomTransformer;
import org.sqlunet.sql.NodeFactory;
import org.sqlunet.wordnet.sql.WnNodeFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * Encapsulates VerbNet query implementation
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class VerbNetImplementation implements VerbNetInterface
{
	static public final String VN_NS = "http://org.sqlunet/vn";

	// S E L E C T O R

	/**
	 * Business method that returns VerbNet selector data as DOM document
	 *
	 * @param connection connection
	 * @param word       target word
	 * @return VerbNet selector data as DOM document
	 */
	@NonNull
	@Override
	public Document querySelectorDoc(final SQLiteDatabase connection, final String word)
	{
		final Document doc = DomFactory.makeDocument();
		final Node rootNode = VnNodeFactory.makeVnRootNode(doc, word);
		VerbNetImplementation.walkSelector(connection, doc, rootNode, word);
		return doc;
	}

	/**
	 * Business method that returns VerbNet selector data as XML
	 *
	 * @param connection connection
	 * @param word       target word
	 * @return VerbNet selector data as XML
	 */
	@NonNull
	@Override
	public String querySelectorXML(final SQLiteDatabase connection, final String word)
	{
		final Document doc = querySelectorDoc(connection, word);
		return DomTransformer.docToString(doc);
	}

	// D E T A I L

	/**
	 * Business method that returns VerbNet data as DOM document from word
	 *
	 * @param connection connection
	 * @param word       target word
	 * @return VerbNet data as DOM document
	 */
	@NonNull
	@Override
	public Document queryDoc(final SQLiteDatabase connection, final String word)
	{
		final Document doc = DomFactory.makeDocument();
		final Node rootNode = VnNodeFactory.makeVnRootNode(doc, word);
		VerbNetImplementation.walk(connection, doc, rootNode, word);
		return doc;
	}

	/**
	 * Business method that returns VerbNet data as XML
	 *
	 * @param connection connection
	 * @param word       target word
	 * @return VerbNet data as XML
	 */
	@NonNull
	@Override
	public String queryXML(final SQLiteDatabase connection, final String word)
	{
		final Document doc = queryDoc(connection, word);
		return DomTransformer.docToString(doc);
	}

	/**
	 * Business method that returns VerbNet data as DOM document from sense
	 *
	 * @param connection connection
	 * @param wordId     word id to build query from
	 * @param synsetId   synset id to build query from (null if any)
	 * @param pos        pos to build query from
	 * @return VerbNet data as DOM document
	 */
	@NonNull
	@Override
	public Document queryDoc(final SQLiteDatabase connection, final long wordId, final Long synsetId, final Character pos)
	{
		final Document doc = DomFactory.makeDocument();
		final Node rootNode = VnNodeFactory.makeVnRootNode(doc, wordId, synsetId);
		VerbNetImplementation.walk(connection, doc, rootNode, wordId, synsetId, true, true);
		return doc;
	}

	/**
	 * Business method that returns VerbNet data as XML from sense
	 *
	 * @param connection connection
	 * @param wordId     target word id
	 * @param synsetId   target synset id (null if any)
	 * @param pos        pos to build query from
	 * @return VerbNet data as XML
	 */
	@NonNull
	@Override
	public String queryXML(final SQLiteDatabase connection, final long wordId, final Long synsetId, final Character pos)
	{
		final Document doc = queryDoc(connection, wordId, synsetId, pos);
		return DomTransformer.docToString(doc);
	}

	// class

	@NonNull
	@Override
	public Document queryClassDoc(final SQLiteDatabase connection, final long classId, final Character pos)
	{
		final Document doc = DomFactory.makeDocument();
		final Node rootNode = VnNodeFactory.makeVnRootClassNode(doc, classId);
		VerbNetImplementation.walkClass(connection, doc, rootNode, classId);
		return doc;
	}

	/**
	 * Business method that returns class data as XML from class id
	 *
	 * @param connection connection
	 * @param classId    class to build query from
	 * @param pos        pos to build query from
	 * @return VerbNet class data as XML
	 */
	@NonNull
	@Override
	public String queryClassXML(final SQLiteDatabase connection, final long classId, final Character pos)
	{
		final Document doc = queryClassDoc(connection, classId, pos);
		return DomTransformer.docToString(doc);
	}

	// W A L K

	/**
	 * Perform queries for VerbNet selector data
	 *
	 * @param connection connection
	 * @param doc        org.w3c.dom.Document being built
	 * @param parent     org.w3c.dom.Node walk will attach results to
	 * @param targetWord target word
	 */
	@SuppressWarnings("boxing")
	static private void walkSelector(final SQLiteDatabase connection, @NonNull final Document doc, final Node parent, final String targetWord)
	{
		// entry
		final VnEntry entry = VnEntry.make(connection, targetWord);
		if (entry == null)
		{
			return;
		}

		// word
		// NodeFactory.makeWordNode(doc, parent, entry.word.word, entry.word.id);

		// iterate synsets
		final List<VnSynset> synsets = entry.synsets;
		if (synsets == null)
		{
			return;
		}

		long currentId = -1L;
		boolean currentFlag = false;
		for (final VnSynset synset : synsets)
		{
			// select
			final boolean isFlagged = synset.flag;
			final boolean isSame = currentId == synset.synsetId;
			final boolean wasFlagged = currentFlag;
			currentId = synset.synsetId;
			currentFlag = isFlagged;
			if (isSame && !isFlagged && wasFlagged)
			{
				continue;
			}

			// verbnet nodes
			VerbNetImplementation.walk(connection, doc, parent, entry.word.id, synset.synsetId, false, false);
		}
	}

	/**
	 * Perform queries for VerbNet data from word
	 *
	 * @param connection connection
	 * @param doc        org.w3c.dom.Document being built
	 * @param parent     org.w3c.dom.Node walk will attach results to
	 * @param targetWord target word
	 */
	@SuppressWarnings("boxing")
	static private void walk(final SQLiteDatabase connection, @NonNull final Document doc, final Node parent, final String targetWord)
	{
		// entry
		final VnEntry entry = VnEntry.make(connection, targetWord);
		if (entry == null)
		{
			return;
		}

		// word
		WnNodeFactory.makeWordNode(doc, parent, entry.word.word, entry.word.id);

		// iterate synsets
		final List<VnSynset> synsets = entry.synsets;
		if (synsets == null)
		{
			return;
		}

		int i = 1;
		long currentId = -1L;
		boolean currentFlag = false;
		for (final VnSynset synset : synsets)
		{
			// select
			final boolean isFlagged = synset.flag;
			final boolean isSame = currentId == synset.synsetId;
			final boolean wasFlagged = currentFlag;
			currentId = synset.synsetId;
			currentFlag = isFlagged;
			if (isSame && !isFlagged && wasFlagged)
			{
				continue;
			}

			// sense node
			final Node senseNode = WnNodeFactory.makeSenseNode(doc, parent, entry.word.id, synset.synsetId, i++);

			// verbnet nodes
			VerbNetImplementation.walk(connection, doc, senseNode, entry.word.id, synset.synsetId, true, true);
		}
	}

	/**
	 * Perform queries for VerbNet data from sense
	 *
	 * @param connection     data source
	 * @param doc            org.w3c.dom.Document being built
	 * @param parent         org.w3c.dom.Node walk will attach results to
	 * @param targetWordId   target word id
	 * @param targetSynsetId target synset id (null for any)
	 * @param roles          whether to include roles
	 * @param frames         whether to include frames
	 */
	static private void walk(final SQLiteDatabase connection, @NonNull final Document doc, final Node parent, final long targetWordId, final Long targetSynsetId, final boolean roles, final boolean frames)
	{
		// classes
		final List<VnClassWithSense> vnClasses = VnClassWithSense.make(connection, targetWordId, targetSynsetId);
		if (vnClasses != null)
		{
			for (final VnClassWithSense vnClass : vnClasses)
			{
				// class
				final Node classNode = VnNodeFactory.makeVnClassWithSenseNode(doc, parent, vnClass);

				if (vnClass.synsetId != 0)
				{
					// sense node
					final Node senseNode = WnNodeFactory.makeSenseNode(doc, classNode, vnClass.wordId, vnClass.synsetId, vnClass.senseNum);

					// synset nodes
					final Node synsetNode = WnNodeFactory.makeSynsetNode(doc, senseNode, vnClass.synsetId, 0);

					// gloss
					NodeFactory.makeNode(doc, synsetNode, "definition", vnClass.definition);
				}

				// roles
				if (roles)
				{
					final Node rolesNode = VnNodeFactory.makeVnRolesNode(doc, classNode);
					final VnRoleSet roleSet = VnRoleSet.make(connection, vnClass.classId, targetWordId, targetSynsetId);
					if (roleSet != null)
					{
						int j = 1;
						for (final VnRole role : roleSet.roles)
						{
							VnNodeFactory.makeVnRoleNode(doc, rolesNode, role, j);
							j++;
						}
					}
				}

				// frames
				if (frames)
				{
					final Node framesNode = VnNodeFactory.makeVnFramesNode(doc, classNode);
					final VnFrameSet frameSet = VnFrameSet.make(connection, vnClass.classId, targetWordId, targetSynsetId);
					if (frameSet != null)
					{
						int j = 1;
						for (final VnFrame frame : frameSet.frames)
						{
							VnNodeFactory.makeVnFrameNode(doc, framesNode, frame, j);
							j++;
						}
					}
				}
			}
		}
	}

	// I T E M S

	/**
	 * Perform queries for VerbNet data from class id
	 *
	 * @param connection data source
	 * @param doc        org.w3c.dom.Document being built
	 * @param parent     org.w3c.dom.Node walk will attach results to
	 * @param classId    target class id
	 */
	static private void walkClass(final SQLiteDatabase connection, @NonNull final Document doc, final Node parent, final long classId)
	{
		// class
		final VnClass clazz = VnClass.make(connection, classId);
		assert clazz != null;
		final Node classNode = VnNodeFactory.makeVnClassNode(doc, parent, clazz);

		// roles
		final Node rolesNode = VnNodeFactory.makeVnRolesNode(doc, classNode);
		final VnRoleSet roleSet = VnRoleSet.make(connection, clazz.classId);
		if (roleSet != null)
		{
			int j = 1;
			for (final VnRole role : roleSet.roles)
			{
				VnNodeFactory.makeVnRoleNode(doc, rolesNode, role, j);
				j++;
			}
		}

		// frames
		final Node framesNode = VnNodeFactory.makeVnFramesNode(doc, classNode);
		final VnFrameSet frameSet = VnFrameSet.make(connection, clazz.classId);
		if (frameSet != null)
		{
			int j = 1;
			for (final VnFrame frame : frameSet.frames)
			{
				VnNodeFactory.makeVnFrameNode(doc, framesNode, frame, j);
				j++;
			}
		}
	}
}
