package org.sqlunet.wordnet.loaders;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;

import org.sqlunet.browser.Module;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.style.Spanner;
import org.sqlunet.treeview.control.Link;
import org.sqlunet.treeview.control.Query;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.view.FireEvent;
import org.sqlunet.view.TreeFactory;
import org.sqlunet.wordnet.R;
import org.sqlunet.wordnet.SensePointer;
import org.sqlunet.wordnet.SynsetPointer;
import org.sqlunet.wordnet.WordPointer;
import org.sqlunet.wordnet.browser.SynsetActivity;
import org.sqlunet.wordnet.browser.WordActivity;
import org.sqlunet.wordnet.provider.WordNetContract;
import org.sqlunet.wordnet.provider.WordNetContract.AdjPositions_AdjPositionTypes;
import org.sqlunet.wordnet.provider.WordNetContract.LexDomains;
import org.sqlunet.wordnet.provider.WordNetContract.LexLinks_Senses_Words_X;
import org.sqlunet.wordnet.provider.WordNetContract.LinkTypes;
import org.sqlunet.wordnet.provider.WordNetContract.MorphMaps_Morphs;
import org.sqlunet.wordnet.provider.WordNetContract.PosTypes;
import org.sqlunet.wordnet.provider.WordNetContract.Samples;
import org.sqlunet.wordnet.provider.WordNetContract.SemLinks_Synsets_Words_X;
import org.sqlunet.wordnet.provider.WordNetContract.Senses;
import org.sqlunet.wordnet.provider.WordNetContract.Senses_Words;
import org.sqlunet.wordnet.provider.WordNetContract.Synsets;
import org.sqlunet.wordnet.provider.WordNetContract.Synsets_PosTypes_LexDomains;
import org.sqlunet.wordnet.provider.WordNetContract.VerbFrameMaps_VerbFrames;
import org.sqlunet.wordnet.provider.WordNetContract.VerbFrameSentenceMaps_VerbFrameSentences;
import org.sqlunet.wordnet.provider.WordNetContract.Words;
import org.sqlunet.wordnet.provider.WordNetContract.Words_MorphMaps_Morphs;
import org.sqlunet.wordnet.provider.WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains;
import org.sqlunet.wordnet.style.WordNetFactories;

import java.util.Locale;

/**
 * Base module for WordNet
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract public class BasicModule extends Module
{
	// Resources

	private final Drawable synsetDrawable;

	private final Drawable memberDrawable;

	private final Drawable definitionDrawable;

	private final Drawable sampleDrawable;

	private final Drawable posDrawable;

	private final Drawable lexdomainDrawable;

	private final Drawable verbframeDrawable;

	private final Drawable morphDrawable;

	/**
	 * Whether members are grouped
	 */
	private boolean membersGrouped = false;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	BasicModule(final Fragment fragment)
	{
		super(fragment);

		// drawables
		this.synsetDrawable = Spanner.getDrawable(this.context, R.drawable.synset);
		this.memberDrawable = Spanner.getDrawable(this.context, R.drawable.synsetmember);
		this.definitionDrawable = Spanner.getDrawable(this.context, R.drawable.definition);
		this.sampleDrawable = Spanner.getDrawable(this.context, R.drawable.sample);
		this.posDrawable = Spanner.getDrawable(this.context, R.drawable.pos);
		this.lexdomainDrawable = Spanner.getDrawable(this.context, R.drawable.domain);
		this.verbframeDrawable = Spanner.getDrawable(this.context, R.drawable.verbframe);
		this.morphDrawable = this.verbframeDrawable;
	}

	/**
	 * Set member grouping
	 *
	 * @param membersGrouped member grouping flag
	 */
	@SuppressWarnings("unused")
	public void setMembersGrouped(final boolean membersGrouped)
	{
		this.membersGrouped = membersGrouped;
	}

	// L O A D E R S

	// Word

	private static final String ALLMORPHS = "allmorphs";

	/**
	 * Word
	 *
	 * @param wordId     word id
	 * @param parent     tree parent node
	 * @param addNewNode whether to addItem to (or set) node
	 */
	@SuppressWarnings("WeakerAccess")
	protected void word(final long wordId, final TreeNode parent, final boolean addNewNode)
	{
		// load the contents
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(Words_MorphMaps_Morphs.CONTENT_URI_BY_WORD);
				final String[] projection = { //
						Words_MorphMaps_Morphs.LEMMA, //
						Words_MorphMaps_Morphs.WORDID, //
						"GROUP_CONCAT(" + Words_MorphMaps_Morphs.MORPH + "||'-'||" + Words_MorphMaps_Morphs.POS + ") AS " + BasicModule.ALLMORPHS};
				final String selection = Words.WORDID + " = ?";
				final String[] selectionArgs = {Long.toString(wordId)};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.getCount() > 1)
				{
					throw new RuntimeException("Unexpected number of rows");
				}

				// store source result
				if (cursor.moveToFirst())
				{
					final SpannableStringBuilder sb = new SpannableStringBuilder();

					// final int idWordId = cursor.getColumnIndex(Words.WORDID);
					final int idLemma = cursor.getColumnIndex(Words_MorphMaps_Morphs.LEMMA);
					final int idMorphs = cursor.getColumnIndex(BasicModule.ALLMORPHS);
					final String lemma = cursor.getString(idLemma);
					final String morphs = cursor.getString(idMorphs);

					Spanner.appendImage(sb, BasicModule.this.memberDrawable);
					sb.append(' ');
					Spanner.append(sb, lemma, 0, WordNetFactories.wordFactory);

					if (morphs != null && !morphs.isEmpty())
					{
						sb.append(' ');
						Spanner.append(sb, morphs, 0, WordNetFactories.dataFactory);
					}

					// result
					if (addNewNode)
					{
						TreeFactory.addTextNode(parent, sb, BasicModule.this.context);
						FireEvent.onResults(parent);
					}
					else
					{
						FireEvent.onResults(parent, sb);
					}
				}
				else
				{
					FireEvent.onNoResult(parent, true);
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}

	// Sense

	/**
	 * Senses from word
	 *
	 * @param word   word
	 * @param parent tree parent node
	 */
	@SuppressWarnings("unused")
	protected void senses(final String word, final TreeNode parent)
	{
		// load the contents
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.CONTENT_URI);
				final String[] projection = { //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID + " AS _id", //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.WORDID, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SENSEID, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SENSENUM, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SENSEKEY, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEXID, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.TAGCOUNT, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.DEFINITION, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.POSNAME, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEXDOMAIN, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.CASED};
				final String selection = Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEMMA + " = ?";
				final String[] selectionArgs = {word};
				final String sortOrder = Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.POS + ',' + Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SENSENUM;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				// store source result
				if (cursor.moveToFirst())
				{
					final int idWordId = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.WORDID);
					final int idSynsetId = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID);
					final int idPosName = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.POSNAME);
					final int idLexDomain = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEXDOMAIN);
					final int idDefinition = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.DEFINITION);
					final int idTagCount = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.TAGCOUNT);
					final int idCased = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.CASED);

					senses(cursor, idWordId, idSynsetId, idPosName, idLexDomain, idDefinition, idTagCount, idCased, parent);

					FireEvent.onResults(parent);
				}
				else
				{
					FireEvent.onNoResult(parent, true);
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}

	/**
	 * Senses from word id
	 *
	 * @param wordId word id
	 * @param parent tree parent node
	 */
	@SuppressWarnings("WeakerAccess")
	protected void senses(final long wordId, final TreeNode parent)
	{
		// load the contents
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.CONTENT_URI);
				final String[] projection = { //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID + " AS _id", //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.WORDID, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SENSEID, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SENSENUM, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SENSEKEY, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEXID, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.TAGCOUNT, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.DEFINITION, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.POSNAME, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEXDOMAIN, //
						Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.CASED};
				final String selection = Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.WORDID + " = ?";
				final String[] selectionArgs = {Long.toString(wordId)};
				final String sortOrder = WordNetContract.POS + '.' + Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.POS + ',' + Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SENSENUM;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				// store source result
				if (cursor.moveToFirst())
				{
					final int idWordId = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.WORDID);
					final int idSynsetId = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID);
					final int idPosName = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.POSNAME);
					final int idLexDomain = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEXDOMAIN);
					final int idDefinition = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.DEFINITION);
					final int idTagCount = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.TAGCOUNT);
					final int idCased = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.CASED);

					senses(cursor, idWordId, idSynsetId, idPosName, idLexDomain, idDefinition, idTagCount, idCased, parent);

					FireEvent.onResults(parent);
				}
				else
				{
					FireEvent.onNoResult(parent, true);
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}

	/**
	 * Senses
	 *
	 * @param cursor       cursor
	 * @param idWordId     column id of wordid
	 * @param idSynsetId   column id of synset id
	 * @param idPosName    column id of  pos name
	 * @param idLexDomain  column id of lex domain
	 * @param idDefinition column id of  definition
	 * @param idTagCount   column id of  tag count
	 * @param idCased      column id of cased word
	 * @param parent       tree parent node
	 */
	private void senses(final Cursor cursor, final int idWordId, final int idSynsetId, final int idPosName, final int idLexDomain, final int idDefinition, final int idTagCount, final int idCased, final TreeNode parent)
	{
		do
		{
			final long wordId = cursor.getLong(idWordId);
			final long synsetId = cursor.getLong(idSynsetId);
			final String posName = cursor.getString(idPosName);
			final String lexDomain = cursor.getString(idLexDomain);
			final String definition = cursor.getString(idDefinition);
			final String cased = cursor.getString(idCased);
			final int tagCount = cursor.getInt(idTagCount);

			final SpannableStringBuilder sb = new SpannableStringBuilder();
			sense(sb, synsetId, posName, lexDomain, definition, tagCount, cased);

			// result
			final TreeNode synsetNode = TreeFactory.newLinkNode(sb, R.drawable.synset, new SenseLink(synsetId, wordId), BasicModule.this.context);
			parent.addChild(synsetNode);
		}
		while (cursor.moveToNext());
	}

	/**
	 * Sense
	 *
	 * @param senseId sense id
	 * @param parent  parent node
	 */
	@SuppressWarnings("unused")
	public void sense(final long senseId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(Senses.CONTENT_URI);
				final String[] projection = { //
						Senses.WORDID, //
						Senses.SYNSETID, //
				};
				final String selection = Senses.SENSEID + " = ?";
				final String[] selectionArgs = {Long.toString(senseId)};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.getCount() > 1)
				{
					throw new RuntimeException("Unexpected number of rows");
				}
				if (cursor.moveToFirst())
				{
					final int idWordId = cursor.getColumnIndex(PosTypes.POSNAME);
					final int idSynsetId = cursor.getColumnIndex(Synsets.DEFINITION);
					final long wordId = cursor.getLong(idWordId);
					final long synsetId = cursor.getLong(idSynsetId);

					sense(synsetId, wordId, parent);
				}
				else
				{
					FireEvent.onNoResult(parent, true);
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}

	/**
	 * Sense
	 *
	 * @param senseKey sense key
	 * @param parent   parent node
	 */
	@SuppressWarnings("WeakerAccess")
	public void sense(final String senseKey, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(Senses.CONTENT_URI);
				final String[] projection = { //
						Senses.WORDID, //
						Senses.SYNSETID, //
				};
				final String selection = Senses.SENSEKEY + " = ?";
				final String[] selectionArgs = {senseKey};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.getCount() > 1)
				{
					throw new RuntimeException("Unexpected number of rows");
				}
				if (cursor.moveToFirst())
				{
					final int idWordId = cursor.getColumnIndex(Senses.WORDID);
					final int idSynsetId = cursor.getColumnIndex(Senses.SYNSETID);
					final long wordId = cursor.getLong(idWordId);
					final long synsetId = cursor.getLong(idSynsetId);

					// sub nodes
					final TreeNode wordNode = TreeFactory.newTextNode("Word", BasicModule.this.context);
					parent.addChild(wordNode);
					FireEvent.onResults(parent);

					// word
					word(wordId, wordNode, false);
					sense(synsetId, wordId, wordNode);
				}
				else
				{
					FireEvent.onNoResult(parent, true);
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}

	/**
	 * Sense
	 *
	 * @param synsetId synset id
	 * @param wordId   word id
	 * @param parent   parent node
	 */
	@SuppressWarnings("WeakerAccess")
	public void sense(final long synsetId, final long wordId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(Synsets_PosTypes_LexDomains.CONTENT_URI);
				final String[] projection = { //
						Synsets.DEFINITION, //
						PosTypes.POSNAME, //
						LexDomains.LEXDOMAIN, //
				};
				final String selection = Synsets_PosTypes_LexDomains.SYNSETID + " = ?";
				final String[] selectionArgs = {Long.toString(synsetId)};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.getCount() > 1)
				{
					throw new RuntimeException("Unexpected number of rows");
				}
				if (cursor.moveToFirst())
				{
					final SpannableStringBuilder sb = new SpannableStringBuilder();
					final int idPosName = cursor.getColumnIndex(PosTypes.POSNAME);
					final int idLexDomain = cursor.getColumnIndex(LexDomains.LEXDOMAIN);
					final int idDefinition = cursor.getColumnIndex(Synsets.DEFINITION);
					final String posName = cursor.getString(idPosName);
					final String lexDomain = cursor.getString(idLexDomain);
					final String definition = cursor.getString(idDefinition);

					Spanner.appendImage(sb, BasicModule.this.synsetDrawable);
					sb.append(' ');
					synset(sb, synsetId, posName, lexDomain, definition);

					// attach result
					TreeFactory.addTextNode(parent, sb, BasicModule.this.context);

					// subnodes
					final TreeNode linksNode = TreeFactory.newQueryNode("Links", R.drawable.ic_links, new LinksQuery(synsetId, wordId), true, BasicModule.this.context).addTo(parent);
					final TreeNode samplesNode = TreeFactory.newQueryNode("Samples", R.drawable.sample, new SamplesQuery(synsetId), true, BasicModule.this.context).addTo(parent);

					// fire event
					FireEvent.onQueryReady(linksNode);
					FireEvent.onQueryReady(samplesNode);
					FireEvent.onResults(parent);
				}
				else
				{
					FireEvent.onNoResult(parent, true);
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}

	// Synset

	/**
	 * Synset
	 *
	 * @param synsetId   synset id
	 * @param parent     parent node
	 * @param addNewNode whether to addItem to (or set) node
	 */
	void synset(final long synsetId, final TreeNode parent, final boolean addNewNode)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(Synsets_PosTypes_LexDomains.CONTENT_URI);
				final String[] projection = { //
						Synsets.DEFINITION, //
						PosTypes.POSNAME, //
						LexDomains.LEXDOMAIN, //
				};
				final String selection = Synsets_PosTypes_LexDomains.SYNSETID + " = ?";
				final String[] selectionArgs = {Long.toString(synsetId)};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.getCount() > 1)
				{
					throw new RuntimeException("Unexpected number of rows");
				}
				if (cursor.moveToFirst())
				{
					final SpannableStringBuilder sb = new SpannableStringBuilder();
					final int idPosName = cursor.getColumnIndex(PosTypes.POSNAME);
					final int idLexDomain = cursor.getColumnIndex(LexDomains.LEXDOMAIN);
					final int idDefinition = cursor.getColumnIndex(Synsets.DEFINITION);
					final String posName = cursor.getString(idPosName);
					final String lexDomain = cursor.getString(idLexDomain);
					final String definition = cursor.getString(idDefinition);

					Spanner.appendImage(sb, BasicModule.this.synsetDrawable);
					sb.append(' ');
					synset(sb, synsetId, posName, lexDomain, definition);

					// result
					if (addNewNode)
					{
						TreeFactory.addTextNode(parent, sb, BasicModule.this.context);
						FireEvent.onResults(parent);
					}
					else
					{
						FireEvent.onResults(parent, sb);
					}
				}
				else
				{
					FireEvent.onNoResult(parent, addNewNode);
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}

	/**
	 * Sense to string builder
	 *
	 * @param sb         string builder
	 * @param synsetId   synset id
	 * @param posName    pos
	 * @param lexDomain  lex domain
	 * @param definition definition
	 * @param tagCount   tag count
	 * @param cased      cased
	 * @return string builder
	 */
	private SpannableStringBuilder sense(final SpannableStringBuilder sb, final long synsetId, final CharSequence posName, final CharSequence lexDomain, final CharSequence definition, final int tagCount, final CharSequence cased)
	{
		synset_head(sb, synsetId, posName, lexDomain);

		if (cased != null && cased.length() > 0)
		{
			Spanner.appendImage(sb, BasicModule.this.memberDrawable);
			sb.append(' ');
			Spanner.append(sb, cased, 0, WordNetFactories.wordFactory);
			sb.append(' ');
		}
		if (tagCount > 0)
		{
			sb.append(' ');
			Spanner.append(sb, "tagcount:" + Integer.toString(tagCount), 0, WordNetFactories.dataFactory);
		}

		sb.append('\n');
		synset_definition(sb, definition);

		return sb;
	}

	/**
	 * Synset to string builder
	 *
	 * @param sb         string builder
	 * @param synsetId   synset id
	 * @param posName    pos
	 * @param lexDomain  lex domain
	 * @param definition definition
	 * @return string builder
	 */
	private SpannableStringBuilder synset(final SpannableStringBuilder sb, final long synsetId, final CharSequence posName, final CharSequence lexDomain, final CharSequence definition)
	{
		synset_head(sb, synsetId, posName, lexDomain);
		sb.append('\n');
		synset_definition(sb, definition);
		return sb;
	}

	/**
	 * Synset head to string builder
	 *
	 * @param sb        string builder
	 * @param synsetId  synset id
	 * @param posName   pos
	 * @param lexDomain lex domain
	 * @return string builder
	 */
	private SpannableStringBuilder synset_head(final SpannableStringBuilder sb, final long synsetId, final CharSequence posName, final CharSequence lexDomain)
	{
		Spanner.appendImage(sb, BasicModule.this.posDrawable);
		sb.append(' ');
		sb.append(posName);
		sb.append(' ');
		Spanner.appendImage(sb, BasicModule.this.lexdomainDrawable);
		sb.append(' ');
		sb.append(lexDomain);
		sb.append(' ');
		Spanner.append(sb, Long.toString(synsetId), 0, WordNetFactories.dataFactory);
		return sb;
	}

	/**
	 * Synset definition to string builder
	 *
	 * @param sb         string builder
	 * @param definition definition
	 * @return string builder
	 */
	private SpannableStringBuilder synset_definition(final SpannableStringBuilder sb, final CharSequence definition)
	{
		Spanner.appendImage(sb, BasicModule.this.definitionDrawable);
		sb.append(' ');
		Spanner.append(sb, definition, 0, WordNetFactories.definitionFactory);
		return sb;
	}

	/**
	 * Members
	 *
	 * @param synsetId   synset
	 * @param parent     parent node
	 * @param addNewNode whether to addItem to (or set) node
	 */
	@SuppressWarnings("unused")
	void members(final long synsetId, final TreeNode parent, final boolean addNewNode)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(BasicModule.this.membersGrouped ? Senses_Words.CONTENT_URI_BY_SYNSET : Senses_Words.CONTENT_URI);
				final String[] projection = BasicModule.this.membersGrouped ? //
						new String[]{Senses_Words.MEMBERS} : new String[]{Words.LEMMA};
				final String selection = Senses_Words.SYNSETID + " = ?";
				final String[] selectionArgs = {Long.toString(synsetId)};
				final String sortOrder = Words.LEMMA;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (BasicModule.this.membersGrouped)
				{
					if (cursor.getCount() > 1)
					{
						throw new RuntimeException("Unexpected number of rows");
					}
				}

				if (cursor.moveToFirst())
				{
					final SpannableStringBuilder sb = new SpannableStringBuilder();
					if (BasicModule.this.membersGrouped)
					{
						final int idMembers = cursor.getColumnIndex(Senses_Words.MEMBERS);
						sb.append(cursor.getString(idMembers));
					}
					else
					{
						final int lemmaId = cursor.getColumnIndex(Words.LEMMA);
						// int i = 1;
						do
						{
							final String lemma = cursor.getString(lemmaId);
							if (sb.length() != 0)
							{
								sb.append('\n');
							}
							// final String record = String.format(Locale.ENGLISH, "[%d] %s", i++, lemma);
							// sb.append(record);
							Spanner.appendImage(sb, BasicModule.this.memberDrawable);
							sb.append(' ');
							// sb.append(Integer.toString(i++));
							// sb.append('-');
							// sb.append(lemma);
							Spanner.append(sb, lemma, 0, WordNetFactories.membersFactory);
						}
						while (cursor.moveToNext());
					}

					// result
					if (addNewNode)
					{
						TreeFactory.addTextNode(parent, sb, BasicModule.this.context);
						FireEvent.onResults(parent);
					}
					else
					{
						FireEvent.onResults(parent, sb);
					}
				}
				else
				{
					FireEvent.onNoResult(parent, addNewNode);
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}

	/**
	 * Members
	 *
	 * @param synsetId synset
	 * @param parent   parent node
	 */
	void members(final long synsetId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(Senses_Words.CONTENT_URI);
				final String[] projection = {Senses_Words.WORDID, Senses_Words.MEMBER};
				final String selection = Senses_Words.SYNSETID + " = ?";
				final String[] selectionArgs = {Long.toString(synsetId)};
				final String sortOrder = Senses_Words.MEMBER;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (BasicModule.this.membersGrouped)
				{
					if (cursor.getCount() > 1)
					{
						throw new RuntimeException("Unexpected number of rows");
					}
				}

				if (cursor.moveToFirst())
				{
					final int idWordId = cursor.getColumnIndex(Senses_Words.WORDID);
					final int idMember = cursor.getColumnIndex(Senses_Words.MEMBER);
					// int i = 1;
					do
					{
						final long wordId = cursor.getLong(idWordId);
						final String member = cursor.getString(idMember);

						final SpannableStringBuilder sb = new SpannableStringBuilder();
						// final String record = String.format(Locale.ENGLISH, "[%d] %s", i++, lemma);
						// sb.append(record);
						// sb.append(Integer.toString(i++));
						// sb.append('-');
						// sb.append(lemma);
						Spanner.append(sb, member, 0, WordNetFactories.membersFactory);

						// result
						final TreeNode memberNode = TreeFactory.newLinkNode(sb, R.drawable.member, new WordLink(wordId), BasicModule.this.context);
						parent.addChild(memberNode);
					}
					while (cursor.moveToNext());

					FireEvent.onResults(parent);
				}
				else
				{
					FireEvent.onNoResult(parent, true);
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}

	/**
	 * Samples
	 *
	 * @param synsetId   synset id
	 * @param parent     parent node
	 * @param addNewNode whether to addItem to (or set) node
	 */
	@SuppressWarnings("WeakerAccess")
	void samples(final long synsetId, final TreeNode parent, final boolean addNewNode)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(Samples.CONTENT_URI);
				final String[] projection = { //
						Samples.SAMPLEID, //
						Samples.SAMPLE, //
				};
				final String selection = Samples.SYNSETID + " = ?";
				final String[] selectionArgs = {Long.toString(synsetId)};
				final String sortOrder = Samples.SAMPLEID;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.moveToFirst())
				{
					final Context context = BasicModule.this.context;

					final int idSample = cursor.getColumnIndex(Samples.SAMPLE);

					final SpannableStringBuilder sb = new SpannableStringBuilder();
					do
					{
						final String sample = cursor.getString(idSample);
						// final int sampleId = cursor.getInt(idSampleId);
						if (sb.length() != 0)
						{
							sb.append('\n');
						}
						Spanner.appendImage(sb, BasicModule.this.sampleDrawable);
						sb.append(' ');
						// sb.append(Integer.toString(sampleId));
						// sb.append(' ');
						Spanner.append(sb, sample, 0, WordNetFactories.sampleFactory);
						// final String record = String.format(Locale.ENGLISH, "[%d] %s", sampleId, sample);
						// sb.append(record);
					}
					while (cursor.moveToNext());

					// result
					if (addNewNode)
					{
						TreeFactory.addTextNode(parent, sb, context);
						FireEvent.onResults(parent);
					}
					else
					{
						FireEvent.onResults(parent, sb);
					}
				}
				else
				{
					FireEvent.onNoResult(parent, addNewNode);
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}

	static private final String TARGET_SYNSETID = "d_synsetid";
	static private final String TARGET_DEFINITION = "d_definition";
	static private final String TARGET_LEMMA = "w_lemma";
	static private final String TARGET_WORDID = "w_wordid";

	/**
	 * Semantic links
	 *
	 * @param synsetId synset id
	 * @param parent   parent node
	 */
	private void semLinks(final long synsetId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(SemLinks_Synsets_Words_X.CONTENT_URI);
				final String[] projection = { //
						WordNetContract.DEST + '.' + Synsets.SYNSETID + " AS " + BasicModule.TARGET_SYNSETID, //
						WordNetContract.DEST + '.' + Synsets.DEFINITION + " AS " + BasicModule.TARGET_DEFINITION, //
						LinkTypes.LINK, //
						LinkTypes.LINKID, //
						LinkTypes.RECURSES, //
				};
				final String selection = WordNetContract.LINK + '.' + SemLinks_Synsets_Words_X.SYNSET1ID + " = ?";  ////
				final String[] selectionArgs = {Long.toString(synsetId)};
				final String sortOrder = LinkTypes.LINKID;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				// noinspection StatementWithEmptyBody
				if (cursor.moveToFirst())
				{
					final Context context = BasicModule.this.context;

					final int idLinkId = cursor.getColumnIndex(LinkTypes.LINKID);
					// final int idLink = cursor.getColumnIndex(LinkTypes.LINK);
					final int idTargetSynsetId = cursor.getColumnIndex(BasicModule.TARGET_SYNSETID);
					final int idTargetDefinition = cursor.getColumnIndex(BasicModule.TARGET_DEFINITION);
					final int idTargetMembers = cursor.getColumnIndex(SemLinks_Synsets_Words_X.MEMBERS2);
					final int idRecurses = cursor.getColumnIndex(SemLinks_Synsets_Words_X.RECURSES);

					do
					{
						final SpannableStringBuilder sb = new SpannableStringBuilder();

						final int linkId = cursor.getInt(idLinkId);
						// final String link = cursor.getString(idLink);

						final long targetSynsetId = cursor.getLong(idTargetSynsetId);
						final String targetDefinition = cursor.getString(idTargetDefinition);
						final String targetMembers = cursor.getString(idTargetMembers);
						final int recurses = cursor.getInt(idRecurses);

						Spanner.append(sb, targetMembers, 0, WordNetFactories.membersFactory);
						sb.append(' ');
						Spanner.append(sb, targetDefinition, 0, WordNetFactories.definitionFactory);

						if (recurses == 0)
						{
							TreeFactory.newLeafNode(sb, getLinkRes(linkId), context).prependTo(parent);
						}
						else
						{
							final TreeNode linksNode = TreeFactory.newLinkQueryNode(sb, getLinkRes(linkId), new SubLinksQuery(targetSynsetId, linkId), new SynsetLink(targetSynsetId), false, context).prependTo(parent);

							// fire event
							FireEvent.onQueryReady(linksNode);
						}
					}
					while (cursor.moveToNext());

					// fire event
					FireEvent.onResults(parent);
				}
				else
				{
					// FireEvent.onNoResult(parent, true)
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}

	/**
	 * Semantic links
	 *
	 * @param synsetId synset id
	 * @param linkId   link id
	 * @param parent   parent node
	 */
	private void semLinks(final long synsetId, final int linkId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(SemLinks_Synsets_Words_X.CONTENT_URI);
				final String[] projection = { //
						WordNetContract.DEST + '.' + Synsets.SYNSETID + " AS " + BasicModule.TARGET_SYNSETID, //
						WordNetContract.DEST + '.' + Synsets.DEFINITION + " AS " + BasicModule.TARGET_DEFINITION, //
						LinkTypes.LINK, //
						LinkTypes.LINKID, //
						LinkTypes.RECURSES, //
				};
				final String selection = WordNetContract.LINK + '.' + SemLinks_Synsets_Words_X.SYNSET1ID + " = ? AND " + LinkTypes.LINKID + " = ?";
				final String[] selectionArgs = {Long.toString(synsetId), Integer.toString(linkId)};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.moveToFirst())
				{
					final Context context = BasicModule.this.context;

					// final int idLinkId = cursor.getColumnIndex(LinkTypes.LINKID);
					// final int idLink = cursor.getColumnIndex(LinkTypes.LINK);
					final int idTargetSynsetId = cursor.getColumnIndex(BasicModule.TARGET_SYNSETID);
					final int idTargetDefinition = cursor.getColumnIndex(BasicModule.TARGET_DEFINITION);
					final int idTargetMembers = cursor.getColumnIndex(SemLinks_Synsets_Words_X.MEMBERS2);
					final int idRecurses = cursor.getColumnIndex(SemLinks_Synsets_Words_X.RECURSES);

					do
					{
						final SpannableStringBuilder sb = new SpannableStringBuilder();

						// final int linkId = cursor.getInt(idLinkId);
						// final String link = cursor.getString(idLink);

						final long targetSynsetId = cursor.getLong(idTargetSynsetId);
						final String targetDefinition = cursor.getString(idTargetDefinition);
						final String targetMembers = cursor.getString(idTargetMembers);
						final int recurses = cursor.getInt(idRecurses);

						Spanner.append(sb, targetMembers, 0, WordNetFactories.membersFactory);
						sb.append(' ');
						Spanner.append(sb, targetDefinition, 0, WordNetFactories.definitionFactory);

						if (recurses == 0)
						{
							TreeFactory.newLeafNode(sb, getLinkRes(linkId), context).addTo(parent);
						}
						else
						{
							final TreeNode linksNode = TreeFactory.newLinkQueryNode(sb, getLinkRes(linkId), new SubLinksQuery(targetSynsetId, linkId), new SynsetLink(targetSynsetId), false, context).addTo(parent);

							// fire event
							FireEvent.onQueryReady(linksNode);
						}
					}
					while (cursor.moveToNext());

					// fire event
					FireEvent.onResults(parent);
				}
				else
				{
					FireEvent.onNoResult(parent, true);
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}

	/**
	 * Lexical links
	 *
	 * @param synsetId synset id
	 * @param parent   parent
	 */
	@SuppressWarnings("unused")
	void lexLinks(final long synsetId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(LexLinks_Senses_Words_X.CONTENT_URI);
				final String[] projection = { //
						WordNetContract.DEST + '.' + Synsets.SYNSETID + " AS " + BasicModule.TARGET_SYNSETID, //
						WordNetContract.DEST + '.' + Synsets.DEFINITION + " AS " + BasicModule.TARGET_DEFINITION, //
						WordNetContract.WORD + '.' + Words.LEMMA + " AS " + BasicModule.TARGET_LEMMA, //
						WordNetContract.WORD + '.' + Words.WORDID + " AS " + BasicModule.TARGET_WORDID, //
						LinkTypes.LINKID, //
						LinkTypes.LINK};
				final String selection = WordNetContract.LINK + '.' + LexLinks_Senses_Words_X.SYNSET1ID + " = ?";  ////
				final String[] selectionArgs = {Long.toString(synsetId)};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				// noinspection StatementWithEmptyBody
				if (cursor.moveToFirst())
				{
					final Context context = BasicModule.this.context;

					final int idLinkId = cursor.getColumnIndex(LinkTypes.LINKID);
					// final int idLink = cursor.getColumnIndex(LinkTypes.LINK);

					// final int idTargetSynsetId = cursor.getColumnIndex(BasicModule.TARGET_SYNSETID);
					final int idTargetDefinition = cursor.getColumnIndex(BasicModule.TARGET_DEFINITION);
					final int idTargetMembers = cursor.getColumnIndex(LexLinks_Senses_Words_X.MEMBERS2);

					// final int idTargetWordId = cursor.getColumnIndex(BasicModule.TARGET_WORDID);
					final int idTargetLemma = cursor.getColumnIndex(BasicModule.TARGET_LEMMA);

					final SpannableStringBuilder sb = new SpannableStringBuilder();
					do
					{
						final int linkId = cursor.getInt(idLinkId);
						// final String link = cursor.getString(idLink);

						// final String targetSynsetId = cursor.getString(idTargetSynsetId);
						final String targetDefinition = cursor.getString(idTargetDefinition);
						final String targetMembers = cursor.getString(idTargetMembers);

						// final String targetWordId = cursor.getString(idTargetWordId);
						final String targetLemma = cursor.getString(idTargetLemma);

						// final String record = String.format(Locale.ENGLISH, "[%s] %s (%s)\n\t%s (synset %s) {%s}", link, targetLemma, targetWordId, targetDefinition, targetSynsetId, targetMembers);

						if (sb.length() != 0)
						{
							sb.append('\n');
						}
						// Spanner.appendImage(sb, getLinkDrawable(linkId));
						// sb.append(record);
						// sb.append(' ');
						Spanner.append(sb, targetLemma, 0, WordNetFactories.lemmaFactory);
						sb.append(" in ");
						sb.append(' ');
						sb.append('{');
						Spanner.append(sb, targetMembers, 0, WordNetFactories.membersFactory);
						sb.append('}');
						sb.append(' ');
						Spanner.append(sb, targetDefinition, 0, WordNetFactories.definitionFactory);

						// attach result
						TreeFactory.newLeafNode(sb, getLinkRes(linkId), context);
					}
					while (cursor.moveToNext());

					// attach result
					TreeFactory.addTextNode(parent, sb, context);

					// fire event
					FireEvent.onResults(parent);
				}
				else
				{
					// FireEvent.onNoResult(parent, true)
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}

	/**
	 * Lexical links
	 *
	 * @param synsetId synset id
	 * @param wordId   word id
	 * @param parent   parent node
	 */
	private void lexLinks(final long synsetId, final long wordId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(LexLinks_Senses_Words_X.CONTENT_URI);
				final String[] projection = { //
						WordNetContract.DEST + '.' + Synsets.SYNSETID + " AS " + BasicModule.TARGET_SYNSETID, //
						WordNetContract.DEST + '.' + Synsets.DEFINITION + " AS " + BasicModule.TARGET_DEFINITION, //
						WordNetContract.WORD + '.' + Words.LEMMA + " AS " + BasicModule.TARGET_LEMMA, //
						WordNetContract.WORD + '.' + Words.WORDID + " AS " + BasicModule.TARGET_WORDID, //
						LinkTypes.LINK, LinkTypes.LINKID,};
				final String selection = WordNetContract.LINK + ".synset1id = ? AND " + WordNetContract.LINK + ".word1id = ?";
				final String[] selectionArgs = {Long.toString(synsetId), Long.toString(wordId)};
				final String sortOrder = LinkTypes.LINKID;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				//noinspection StatementWithEmptyBody
				if (cursor.moveToFirst())
				{
					final int idLinkId = cursor.getColumnIndex(LinkTypes.LINKID);
					// final int idLink = cursor.getColumnIndex(LinkTypes.LINK);

					final int idTargetSynsetId = cursor.getColumnIndex(BasicModule.TARGET_SYNSETID);
					final int idTargetDefinition = cursor.getColumnIndex(BasicModule.TARGET_DEFINITION);
					// final int idTargetMembers = cursor.getColumnIndex(LexLinks_Senses_Words_X.MEMBERS2);

					final int idTargetWordId = cursor.getColumnIndex(BasicModule.TARGET_WORDID);
					final int idTargetLemma = cursor.getColumnIndex(BasicModule.TARGET_LEMMA);

					do
					{
						final SpannableStringBuilder sb = new SpannableStringBuilder();

						final int linkId = cursor.getInt(idLinkId);
						// final String link = cursor.getString(idLink);
						final long targetSynsetId = cursor.getLong(idTargetSynsetId);
						final String targetDefinition = cursor.getString(idTargetDefinition);
						// final String targetMembers = cursor.getString(idTargetMembers);
						final String targetLemma = cursor.getString(idTargetLemma);
						// final String targetWordId = cursor.getString(idTargetWordId);

						// final String record = String.format(Locale.ENGLISH, "[%s] %s (%s)\n\t%s (synset %s) {%s}", link, targetLemma, targetWordId,targetDefinition, targetSynsetId, targetMembers);

						if (sb.length() != 0)
						{
							sb.append('\n');
						}
						// Spanner.appendImage(sb, getLinkDrawable(linkId));
						// sb.append(record);
						// sb.append(' ');
						Spanner.append(sb, targetLemma, 0, WordNetFactories.lemmaFactory);
						// sb.append(" in ");
						// sb.append(' ');
						// sb.append('{');
						// Spanner.append(sb, members, 0, WordNetFactories.membersFactory);
						// sb.append('}');
						sb.append(' ');
						Spanner.append(sb, targetDefinition, 0, WordNetFactories.definitionFactory);

						// attach result
						final TreeNode linkNode = TreeFactory.newLinkLeafNode(sb, getLinkRes(linkId), new SenseLink(targetSynsetId, idTargetWordId), BasicModule.this.context);
						parent.addChild(linkNode);
					}
					while (cursor.moveToNext());

					// fire event
					FireEvent.onResults(parent);
				}
				else
				{
					// FireEvent.onNoResult(parent, true)
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}

	/**
	 * Verb frames
	 *
	 * @param synsetId synset id
	 * @param parent   parent node
	 */
	void vFrames(final long synsetId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{

			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(VerbFrameMaps_VerbFrames.CONTENT_URI);
				final String[] projection = {VerbFrameMaps_VerbFrames.FRAME};
				final String selection = VerbFrameMaps_VerbFrames.SYNSETID + " = ?";
				final String[] selectionArgs = {Long.toString(synsetId)};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				//noinspection StatementWithEmptyBody
				if (cursor.moveToFirst())
				{
					final Context context = BasicModule.this.context;

					final int vframeId = cursor.getColumnIndex(VerbFrameMaps_VerbFrames.FRAME);

					final SpannableStringBuilder sb = new SpannableStringBuilder();
					do
					{
						final String vframe = cursor.getString(vframeId);
						final String record = String.format(Locale.ENGLISH, "%s", vframe);
						if (sb.length() != 0)
						{
							sb.append('\n');
						}
						Spanner.appendImage(sb, BasicModule.this.verbframeDrawable);
						sb.append(' ');
						sb.append(record);
					}
					while (cursor.moveToNext());

					// attach result
					TreeFactory.addTextNode(parent, sb, context);

					// fire event
					FireEvent.onResults(parent);
				}
				else
				{
					// FireEvent.onNoResult(parent, true)
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}

	/**
	 * Verb frames
	 *
	 * @param synsetId synset id
	 * @param wordId   word id
	 * @param parent   parent node
	 */
	void vFrames(final long synsetId, final long wordId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{

			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(VerbFrameMaps_VerbFrames.CONTENT_URI);
				final String[] projection = {VerbFrameMaps_VerbFrames.FRAME};
				final String selection = VerbFrameMaps_VerbFrames.SYNSETID + " = ? AND " + VerbFrameMaps_VerbFrames.WORDID + " = ?";
				final String[] selectionArgs = {Long.toString(synsetId), Long.toString(wordId)};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				//noinspection StatementWithEmptyBody
				if (cursor.moveToFirst())
				{
					final Context context = BasicModule.this.context;

					final int vframeId = cursor.getColumnIndex(VerbFrameMaps_VerbFrames.FRAME);

					final SpannableStringBuilder sb = new SpannableStringBuilder();
					do
					{
						final String vframe = cursor.getString(vframeId);
						final String record = String.format(Locale.ENGLISH, "%s", vframe);
						if (sb.length() != 0)
						{
							sb.append('\n');
						}
						Spanner.appendImage(sb, BasicModule.this.verbframeDrawable);
						sb.append(' ');
						sb.append(record);
					}
					while (cursor.moveToNext());

					// attach result
					TreeFactory.addTextNode(parent, sb, context);

					// fire event
					FireEvent.onResults(parent);
				}
				else
				{
					// FireEvent.onNoResult(parent, true)
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}

	/**
	 * Verb frame sentences
	 *
	 * @param synsetId synset id
	 * @param parent   parent node
	 */
	void vFrameSentences(final long synsetId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{

			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(VerbFrameSentenceMaps_VerbFrameSentences.CONTENT_URI);
				final String[] projection = {VerbFrameSentenceMaps_VerbFrameSentences.SENTENCE};
				final String selection = VerbFrameSentenceMaps_VerbFrameSentences.SYNSETID + " = ?";
				final String[] selectionArgs = {Long.toString(synsetId)};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				//noinspection StatementWithEmptyBody
				if (cursor.moveToFirst())
				{
					final Context context = BasicModule.this.context;

					final int vframeId = cursor.getColumnIndex(VerbFrameSentenceMaps_VerbFrameSentences.SENTENCE);

					final SpannableStringBuilder sb = new SpannableStringBuilder();
					do
					{
						final String vframesentence = cursor.getString(vframeId);
						final String record = String.format(Locale.ENGLISH, vframesentence, "[-]");
						if (sb.length() != 0)
						{
							sb.append('\n');
						}
						Spanner.appendImage(sb, BasicModule.this.verbframeDrawable);
						sb.append(' ');
						sb.append(record);
					}
					while (cursor.moveToNext());

					// attach result
					TreeFactory.addTextNode(parent, sb, context);

					// fire event
					FireEvent.onResults(parent);
				}
				else
				{
					// FireEvent.onNoResult(parent, true)
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}

	/**
	 * Verb frame sentences
	 *
	 * @param synsetId synset id
	 * @param wordId   word id
	 * @param parent   parent node
	 */
	void vFrameSentences(final long synsetId, final long wordId, final TreeNode parent)
	{
		final String lemma = "---";
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{

			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(VerbFrameSentenceMaps_VerbFrameSentences.CONTENT_URI);
				final String[] projection = {VerbFrameSentenceMaps_VerbFrameSentences.SENTENCE};
				final String selection = VerbFrameSentenceMaps_VerbFrameSentences.SYNSETID + " = ? AND " + VerbFrameSentenceMaps_VerbFrameSentences.WORDID + " = ?";
				final String[] selectionArgs = {Long.toString(synsetId), Long.toString(wordId)};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				// noinspection StatementWithEmptyBody
				if (cursor.moveToFirst())
				{
					final Context context = BasicModule.this.context;

					final int vframeId = cursor.getColumnIndex(VerbFrameSentenceMaps_VerbFrameSentences.SENTENCE);

					final SpannableStringBuilder sb = new SpannableStringBuilder();
					do
					{
						final String vframesentence = cursor.getString(vframeId);
						final String record = String.format(Locale.ENGLISH, vframesentence, '[' + lemma + ']');
						if (sb.length() != 0)
						{
							sb.append('\n');
						}
						Spanner.appendImage(sb, BasicModule.this.verbframeDrawable);
						sb.append(' ');
						sb.append(record);
					}
					while (cursor.moveToNext());

					// attach result
					TreeFactory.addTextNode(parent, sb, context);

					// fire event
					FireEvent.onResults(parent);
				}
				else
				{
					// FireEvent.onNoResult(parent, true)
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}

	/**
	 * Adjective positions
	 *
	 * @param synsetId synset id
	 * @param parent   parent node
	 */
	void adjPosition(final long synsetId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{

			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(AdjPositions_AdjPositionTypes.CONTENT_URI);
				final String[] projection = {AdjPositions_AdjPositionTypes.POSITIONNAME};
				final String selection = AdjPositions_AdjPositionTypes.SYNSETID + " = ?";
				final String[] selectionArgs = {Long.toString(synsetId)};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				//noinspection StatementWithEmptyBody
				if (cursor.moveToFirst())
				{
					final Context context = BasicModule.this.context;

					final int positionId = cursor.getColumnIndex(AdjPositions_AdjPositionTypes.POSITIONNAME);

					final SpannableStringBuilder sb = new SpannableStringBuilder();
					do
					{
						final String position = cursor.getString(positionId);
						final String record = String.format(Locale.ENGLISH, "%s", position);
						if (sb.length() != 0)
						{
							sb.append('\n');
						}
						Spanner.appendImage(sb, BasicModule.this.verbframeDrawable);
						sb.append(' ');
						sb.append(record);
					}
					while (cursor.moveToNext());

					// attach result
					TreeFactory.addTextNode(parent, sb, context);

					// fire event
					FireEvent.onResults(parent);
				}
				else
				{
					// FireEvent.onNoResult(parent, true)
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}

	/**
	 * Adjective positions
	 *
	 * @param synsetId synset id
	 * @param wordId   word id
	 * @param parent   parent node
	 */
	void adjPosition(final long synsetId, final long wordId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{

			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(AdjPositions_AdjPositionTypes.CONTENT_URI);
				final String[] projection = {AdjPositions_AdjPositionTypes.POSITIONNAME};
				final String selection = AdjPositions_AdjPositionTypes.SYNSETID + " = ? AND " + AdjPositions_AdjPositionTypes.WORDID + " = ?";
				final String[] selectionArgs = {Long.toString(synsetId), Long.toString(wordId)};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				//noinspection StatementWithEmptyBody
				if (cursor.moveToFirst())
				{
					final Context context = BasicModule.this.context;

					final int positionId = cursor.getColumnIndex(AdjPositions_AdjPositionTypes.POSITIONNAME);

					final SpannableStringBuilder sb = new SpannableStringBuilder();
					do
					{
						final String position = cursor.getString(positionId);
						final String record = String.format(Locale.ENGLISH, "%s", position);
						if (sb.length() != 0)
						{
							sb.append('\n');
						}
						Spanner.appendImage(sb, BasicModule.this.verbframeDrawable);
						sb.append(' ');
						sb.append(record);
					}
					while (cursor.moveToNext());

					// attach result
					TreeFactory.addTextNode(parent, sb, context);

					// fire event
					FireEvent.onResults(parent);
				}
				else
				{
					// FireEvent.onNoResult(parent, true)
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}

	/**
	 * Morphology
	 *
	 * @param wordId word id
	 * @param parent parent node
	 */
	void morphs(final long wordId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(MorphMaps_Morphs.CONTENT_URI);
				final String[] projection = {MorphMaps_Morphs.POS, MorphMaps_Morphs.MORPH};
				final String selection = MorphMaps_Morphs.WORDID + " = ?";
				final String[] selectionArgs = {Long.toString(wordId)};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				//noinspection StatementWithEmptyBody
				if (cursor.moveToFirst())
				{
					final Context context = BasicModule.this.context;

					final int morphId = cursor.getColumnIndex(MorphMaps_Morphs.MORPH);
					final int posId = cursor.getColumnIndex(MorphMaps_Morphs.POS);

					final SpannableStringBuilder sb = new SpannableStringBuilder();
					do
					{
						final String morph1 = cursor.getString(morphId);
						final String pos1 = cursor.getString(posId);
						final String record = String.format(Locale.ENGLISH, "(%s) %s", pos1, morph1);
						if (sb.length() != 0)
						{
							sb.append('\n');
						}
						Spanner.appendImage(sb, BasicModule.this.morphDrawable);
						sb.append(' ');
						sb.append(record);
					}
					while (cursor.moveToNext());

					// attach result
					TreeFactory.addTextNode(parent, sb, context);

					// fire event
					FireEvent.onResults(parent);
				}
				else
				{
					// FireEvent.onNoResult(parent, true)
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}

	// H E L P E R S

	/**
	 * Match link id to drawable resource id
	 *
	 * @param linkId link id
	 * @return kink res id
	 */
	private int getLinkRes(final int linkId)
	{
		switch (linkId)
		{
			case 1:
				return R.drawable.ic_hypernym;
			case 2:
				return R.drawable.ic_hyponym;
			case 3:
				return R.drawable.ic_instance_hypernym;
			case 4:
				return R.drawable.ic_instance_hyponym;
			case 11:
				return R.drawable.ic_part_holonym;
			case 12:
				return R.drawable.ic_part_meronym;
			case 13:
				return R.drawable.ic_member_holonym;
			case 14:
				return R.drawable.ic_member_meronym;
			case 15:
				return R.drawable.ic_substance_holonym;
			case 16:
				return R.drawable.ic_substance_meronym;
			case 21:
				return R.drawable.ic_entail;
			case 23:
				return R.drawable.ic_cause;
			case 30:
				return R.drawable.ic_antonym;
			case 40:
				return R.drawable.ic_similar;
			case 50:
				return R.drawable.ic_also;
			case 60:
				return R.drawable.ic_attribute;
			case 70:
				return R.drawable.ic_verb_group;
			case 71:
				return R.drawable.ic_participle;
			case 80:
				return R.drawable.ic_pertainym;
			case 81:
				return R.drawable.ic_derivation;
			case 91:
				return R.drawable.ic_domain_category;
			case 92:
				return R.drawable.ic_domain_member_category;
			case 93:
				return R.drawable.ic_domain_region;
			case 94:
				return R.drawable.ic_domain_member_region;
			case 95:
				return R.drawable.ic_domain_usage;
			case 96:
				return R.drawable.ic_domain_member_usage;
			case 97:
				return R.drawable.ic_domain;
			case 98:
				return R.drawable.ic_domain_member;
			default:
				return R.drawable.error;
		}
	}

	// Q U E R I E S

	/**
	 * Link query
	 */
	class LinksQuery extends Query
	{
		/**
		 * Word id
		 */
		final long wordId;

		/**
		 * Constructor
		 *
		 * @param synsetId synset id
		 * @param wordId   word id
		 */
		LinksQuery(final long synsetId, final long wordId)
		{
			super(synsetId);
			this.wordId = wordId;
		}

		@Override
		public void process(final TreeNode node)
		{
			// sem links
			semLinks(this.id, node);

			// lex links
			lexLinks(this.id, this.wordId, node);
		}
	}

	/**
	 * Semantic Link query
	 */
	@SuppressWarnings("unused")
	class SemLinksQuery extends Query
	{
		/**
		 * Constructor
		 *
		 * @param synsetId synset id
		 */
		public SemLinksQuery(final long synsetId)
		{
			super(synsetId);
		}

		@Override
		public void process(final TreeNode node)
		{
			semLinks(this.id, node);
		}
	}

	/**
	 * Lexical Link query
	 */
	@SuppressWarnings("unused")
	class LexLinksQuery extends Query
	{
		/**
		 * Word id
		 */
		final long wordId;

		/**
		 * Constructor
		 *
		 * @param synsetId synset id
		 * @param wordId   word id
		 */
		public LexLinksQuery(final long synsetId, final long wordId)
		{
			super(synsetId);
			this.wordId = wordId;
		}

		@Override
		public void process(final TreeNode node)
		{
			lexLinks(this.id, this.wordId, node);
		}
	}

	/**
	 * Sub links of give type query
	 */
	class SubLinksQuery extends Query
	{
		/**
		 * Link id
		 */
		final int linkId;

		/**
		 * Constructor
		 *
		 * @param synsetId synset id
		 * @param linkId   link id
		 */
		public SubLinksQuery(final long synsetId, final int linkId)
		{
			super(synsetId);
			this.linkId = linkId;
		}

		@Override
		public void process(final TreeNode node)
		{
			// semLinks
			semLinks(this.id, this.linkId, node);
		}
	}

	/**
	 * Samples query
	 */
	class SamplesQuery extends Query
	{
		/**
		 * Constructor
		 *
		 * @param synsetId synset id
		 */
		public SamplesQuery(final long synsetId)
		{
			super(synsetId);
		}

		@Override
		public void process(final TreeNode node)
		{
			// samples
			samples(this.id, node, true);
		}
	}

	/**
	 * Word link data
	 */
	class WordLink extends Link
	{
		/**
		 * Constructor
		 *
		 * @param wordId word id
		 */
		public WordLink(final long wordId)
		{
			super(wordId);
		}

		@Override
		public void process()
		{
			final Parcelable pointer = new WordPointer(this.id);
			final Intent intent = new Intent(BasicModule.this.context, WordActivity.class);
			intent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_WORD);
			intent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer);
			intent.setAction(ProviderArgs.ACTION_QUERY);

			BasicModule.this.context.startActivity(intent);
		}
	}

	/**
	 * Synset link data
	 */
	class SynsetLink extends Link
	{
		/**
		 * Constructor
		 *
		 * @param synsetId synset id
		 */
		public SynsetLink(final long synsetId)
		{
			super(synsetId);
		}

		@SuppressWarnings("boxing")
		@Override
		public void process()
		{
			final Parcelable pointer = new SynsetPointer(this.id, null);
			final Intent intent = new Intent(BasicModule.this.context, SynsetActivity.class);
			intent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_SYNSET);
			intent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer);
			intent.setAction(ProviderArgs.ACTION_QUERY);

			BasicModule.this.context.startActivity(intent);
		}
	}

	/**
	 * Sense link data
	 */
	class SenseLink extends SynsetLink
	{
		final private long wordId;

		/**
		 * Constructor
		 *
		 * @param synsetId synset id
		 * @param wordId   word id
		 */
		public SenseLink(final long synsetId, final long wordId)
		{
			super(synsetId);
			this.wordId = wordId;
		}

		@Override
		public void process()
		{
			final Parcelable pointer = new SensePointer(this.id, null, this.wordId, null, null);
			final Intent intent = new Intent(BasicModule.this.context, SynsetActivity.class);
			intent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_SYNSET);
			intent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer);
			intent.setAction(ProviderArgs.ACTION_QUERY);

			BasicModule.this.context.startActivity(intent);
		}
	}
}
