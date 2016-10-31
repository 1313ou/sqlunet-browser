package org.sqlunet.wordnet.loaders;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;

import org.sqlunet.browser.Module;
import org.sqlunet.style.Spanner;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.renderer.QueryRenderer;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.view.TreeFactory;
import org.sqlunet.wordnet.R;
import org.sqlunet.wordnet.provider.WordNetContract;
import org.sqlunet.wordnet.provider.WordNetContract.AdjPositions_AdjPositionTypes;
import org.sqlunet.wordnet.provider.WordNetContract.LexDomains;
import org.sqlunet.wordnet.provider.WordNetContract.LexLinks_Senses_Words_X;
import org.sqlunet.wordnet.provider.WordNetContract.LinkTypes;
import org.sqlunet.wordnet.provider.WordNetContract.MorphMaps_Morphs;
import org.sqlunet.wordnet.provider.WordNetContract.PosTypes;
import org.sqlunet.wordnet.provider.WordNetContract.Samples;
import org.sqlunet.wordnet.provider.WordNetContract.SemLinks_Synsets_Words_X;
import org.sqlunet.wordnet.provider.WordNetContract.Senses_Words;
import org.sqlunet.wordnet.provider.WordNetContract.Synsets;
import org.sqlunet.wordnet.provider.WordNetContract.Synsets_PosTypes_LexDomains;
import org.sqlunet.wordnet.provider.WordNetContract.VerbFrameMaps_VerbFrames;
import org.sqlunet.wordnet.provider.WordNetContract.VerbFrameSentenceMaps_VerbFrameSentences;
import org.sqlunet.wordnet.provider.WordNetContract.Words;
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

	private final Drawable memberDrawable;

	private final Drawable synsetDrawable;

	private final Drawable definitionDrawable;

	private final Drawable sampleDrawable;

	private final Drawable posDrawable;

	private final Drawable domainDrawable;

	private final Drawable flagDrawable;

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
		this.memberDrawable = Spanner.getDrawable(this.context, R.drawable.member);
		this.synsetDrawable = Spanner.getDrawable(this.context, R.drawable.synset);
		this.definitionDrawable = Spanner.getDrawable(this.context, R.drawable.definition);
		this.sampleDrawable = Spanner.getDrawable(this.context, R.drawable.sample);
		this.posDrawable = Spanner.getDrawable(this.context, R.drawable.pos);
		this.domainDrawable = Spanner.getDrawable(this.context, R.drawable.lexdomain);
		this.flagDrawable = Spanner.getDrawable(this.context, R.drawable.flag);
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

	// Sense

	/**
	 * Sense
	 *
	 * @param synsetId synset id
	 * @param wordId   word id
	 * @param parent   parent node
	 */
	@SuppressWarnings("unused")
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
				final String selection = Synsets_PosTypes_LexDomains.SYNSETID + " = ?"; //
				final String[] selectionArgs = {Long.toString(synsetId)};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.getCount() > 1)
				{
					throw new RuntimeException("Unexpected number of rows"); //
				}
				if (cursor.moveToFirst())
				{
					final SpannableStringBuilder sb = new SpannableStringBuilder();
					final SpannableStringBuilder sbdef = new SpannableStringBuilder();
					final int idDefinition = cursor.getColumnIndex(Synsets.DEFINITION);
					final int idPosName = cursor.getColumnIndex(PosTypes.POSNAME);
					final int idLexDomain = cursor.getColumnIndex(LexDomains.LEXDOMAIN);
					final String definition = cursor.getString(idDefinition);
					final String posName = cursor.getString(idPosName);
					final String lexDomain = cursor.getString(idLexDomain);

					sb.append('\n');
					Spanner.appendImage(sb, BasicModule.this.synsetDrawable);
					sb.append('\n');
					sb.append('\t');
					Spanner.appendImage(sb, BasicModule.this.posDrawable);
					sb.append(' ');
					sb.append(posName);
					sb.append('\n');
					sb.append('\t');
					Spanner.appendImage(sb, BasicModule.this.domainDrawable);
					sb.append(' ');
					sb.append(lexDomain);
					sb.append(' ');

					Spanner.appendImage(sbdef, BasicModule.this.definitionDrawable);
					sbdef.append(' ');
					sbdef.append(definition);

					// subnodes
					final TreeNode linksNode = TreeFactory.newQueryNode(new LinksQueryData(synsetId, wordId, R.drawable.ic_other, "Links"), BasicModule.this.context); //
					final TreeNode samplesNode = TreeFactory.newQueryNode(new SamplesQueryData(synsetId, R.drawable.sample, "Samples"), BasicModule.this.context); //

					// attach result
					TreeFactory.addTextNode(parent, sb, BasicModule.this.context);
					TreeFactory.addTextNode(parent, sbdef, BasicModule.this.context, linksNode, samplesNode);

					// expand
					linksNode.setExpanded(true);
					samplesNode.setExpanded(true);
					TreeView.expand(parent, false);
				}
				else
				{
					TreeView.disable(parent);
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
	 * @param addNewNode whether to add to (or set) node
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
				final String selection = Synsets_PosTypes_LexDomains.SYNSETID + " = ?"; //
				final String[] selectionArgs = {Long.toString(synsetId)};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.getCount() > 1)
				{
					throw new RuntimeException("Unexpected number of rows"); //
				}
				if (cursor.moveToFirst())
				{
					final SpannableStringBuilder sb = new SpannableStringBuilder();
					final int idDefinition = cursor.getColumnIndex(Synsets.DEFINITION);
					final int idPosName = cursor.getColumnIndex(PosTypes.POSNAME);
					final int idLexDomain = cursor.getColumnIndex(LexDomains.LEXDOMAIN);
					final String definition = cursor.getString(idDefinition);
					final String posName = cursor.getString(idPosName);
					final String lexDomain = cursor.getString(idLexDomain);

					Spanner.appendImage(sb, BasicModule.this.synsetDrawable);
					sb.append(' ');
					sb.append(Long.toString(synsetId));
					sb.append(' ');
					Spanner.appendImage(sb, BasicModule.this.posDrawable);
					sb.append(' ');
					sb.append(posName);
					sb.append(' ');
					Spanner.appendImage(sb, BasicModule.this.domainDrawable);
					sb.append(' ');
					sb.append(lexDomain);
					sb.append('\n');

					Spanner.appendImage(sb, BasicModule.this.definitionDrawable);
					sb.append(' ');
					Spanner.append(sb, definition, 0, WordNetFactories.definitionFactory);

					// attach result
					if (addNewNode)
					{
						TreeFactory.addTextNode(parent, sb, BasicModule.this.context);

						// expand
						TreeView.expand(parent, false);
					}
					else
					{
						TreeFactory.setNodeValue(parent, sb);
					}
				}
				else
				{
					if (addNewNode)
					{
						TreeView.disable(parent);
					}
					else
					{
						TreeView.remove(parent);
					}
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
	 * @param synsetId   synset
	 * @param parent     parent node
	 * @param addNewNode whether to add to (or set) node
	 */
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
				final String selection = Senses_Words.SYNSETID + " = ?"; //
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
						throw new RuntimeException("Unexpected number of rows"); //
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

					// attach result
					if (addNewNode)
					{
						TreeFactory.addTextNode(parent, sb, BasicModule.this.context);

						// expand
						TreeView.expand(parent, false);
					}
					else
					{
						TreeFactory.setNodeValue(parent, sb);
					}
				}
				else
				{
					if (addNewNode)
					{
						TreeView.disable(parent);
					}
					else
					{
						TreeView.remove(parent);
					}
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
	 * @param addNewNode whether to add to (or set) node
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
				final String selection = Samples.SYNSETID + " = ?"; //
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

					// attach result
					if (addNewNode)
					{
						TreeFactory.addTextNode(parent, sb, context);

						// expand
						TreeView.expand(parent, false);
					}
					else
					{
						TreeFactory.setNodeValue(parent, sb);
					}
				}
				else
				{
					if (addNewNode)
					{
						TreeView.disable(parent);
					}
					else
					{
						TreeView.remove(parent);
					}
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

	static private final String TARGET_SYNSETID = "d_synsetid"; //
	static private final String TARGET_DEFINITION = "d_definition"; //
	static private final String TARGET_LEMMA = "w_lemma"; //
	static private final String TARGET_WORDID = "w_wordid"; //

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

						final TreeNode linksNode = recurses == 0 ? //
								TreeFactory.newLeafNode(sb, getLinkRes(linkId), context) : //
								TreeFactory.newQueryNode(new SubLinksQueryData(targetSynsetId, linkId, getLinkRes(linkId), sb), context);
						parent.prependChild(linksNode);

						// expand
						// if(recurses == 0)
						//	TreeView.expand(linksNode, false);
					}
					while (cursor.moveToNext());

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					// parent.disable();
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
				final String selection = WordNetContract.LINK + '.' + SemLinks_Synsets_Words_X.SYNSET1ID + " = ? AND " + LinkTypes.LINKID + " = ?"; //
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

						final TreeNode linksNode = recurses == 0 ? //
								TreeFactory.newLeafNode(sb, getLinkRes(linkId), context) : //
								TreeFactory.newQueryNode(new SubLinksQueryData(targetSynsetId, linkId, getLinkRes(linkId), sb), context);
						parent.addChild(linksNode);

						// expand
						// if(recurses == 0)
						//	TreeView.expand(linksNode, false);
					}
					while (cursor.moveToNext());

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					TreeView.disable(parent);
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
						Spanner.appendImage(sb, getLinkDrawable(linkId));
						// sb.append(record);
						sb.append(' ');
						Spanner.append(sb, targetLemma, 0, WordNetFactories.lemmaFactory);
						sb.append(" in "); //
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

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					// parent.disable();
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
				final String selection = WordNetContract.LINK + ".synset1id = ? AND " + WordNetContract.LINK + ".word1id = ?"; //
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
					final Context context = BasicModule.this.context;

					final int idLinkId = cursor.getColumnIndex(LinkTypes.LINKID);
					// final int idLink = cursor.getColumnIndex(LinkTypes.LINK);

					// final int idTargetSynsetId = cursor.getColumnIndex(BasicModule.TARGET_SYNSETID);
					final int idTargetDefinition = cursor.getColumnIndex(BasicModule.TARGET_DEFINITION);
					// final int idTargetMembers = cursor.getColumnIndex(LexLinks_Senses_Words_X.MEMBERS2);

					// final int idTargetWordId = cursor.getColumnIndex(BasicModule.TARGET_WORDID);
					final int idTargetLemma = cursor.getColumnIndex(BasicModule.TARGET_LEMMA);

					final SpannableStringBuilder sb = new SpannableStringBuilder();
					do
					{
						final int linkId = cursor.getInt(idLinkId);
						// final String link = cursor.getString(idLink);

						// final String targetSynsetId = cursor.getString(idTargetSynsetId);
						final String targetDefinition = cursor.getString(idTargetDefinition);
						// final String targetMembers = cursor.getString(idTargetMembers);

						final String targetLemma = cursor.getString(idTargetLemma);
						// final String targetWordId = cursor.getString(idTargetWordId);

						// final String record = String.format(Locale.ENGLISH, "[%s] %s (%s)\n\t%s (synset %s) {%s}", link, targetLemma, targetWordId,targetDefinition, targetSynsetId, targetMembers);

						if (sb.length() != 0)
						{
							sb.append('\n');
						}
						Spanner.appendImage(sb, getLinkDrawable(linkId));
						// sb.append(record);
						sb.append(' ');
						Spanner.append(sb, targetLemma, 0, WordNetFactories.lemmaFactory);
						// sb.append(" in ");
						// sb.append(' ');
						// sb.append('{');
						// Spanner.append(sb, members, 0, WordNetFactories.membersFactory);
						// sb.append('}');
						sb.append(' ');
						Spanner.append(sb, targetDefinition, 0, WordNetFactories.definitionFactory);

						// attach result
						TreeFactory.newLeafNode(sb, getLinkRes(linkId), context);
					}
					while (cursor.moveToNext());

					// attach result
					// TreeFactory.addTextNode(parent, sb, context);

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					// parent.disable();
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
				final String selection = VerbFrameMaps_VerbFrames.SYNSETID + " = ?"; //
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
						final String record = String.format(Locale.ENGLISH, "%s", vframe); //
						if (sb.length() != 0)
						{
							sb.append('\n');
						}
						Spanner.appendImage(sb, BasicModule.this.flagDrawable);
						sb.append(' ');
						sb.append(record);
					}
					while (cursor.moveToNext());

					// attach result
					TreeFactory.addTextNode(parent, sb, context);

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					// parent.disable();
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
				final String selection = VerbFrameMaps_VerbFrames.SYNSETID + " = ? AND " + VerbFrameMaps_VerbFrames.WORDID + " = ?"; //
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
						final String record = String.format(Locale.ENGLISH, "%s", vframe); //
						if (sb.length() != 0)
						{
							sb.append('\n');
						}
						Spanner.appendImage(sb, BasicModule.this.flagDrawable);
						sb.append(' ');
						sb.append(record);
					}
					while (cursor.moveToNext());

					// attach result
					TreeFactory.addTextNode(parent, sb, context);

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					// parent.disable();
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
				final String selection = VerbFrameSentenceMaps_VerbFrameSentences.SYNSETID + " = ?"; //
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
						final String record = String.format(Locale.ENGLISH, vframesentence, "[-]"); //
						if (sb.length() != 0)
						{
							sb.append('\n');
						}
						Spanner.appendImage(sb, BasicModule.this.flagDrawable);
						sb.append(' ');
						sb.append(record);
					}
					while (cursor.moveToNext());

					// attach result
					TreeFactory.addTextNode(parent, sb, context);

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					// parent.disable();
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
		final String lemma = "---"; //
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{

			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(VerbFrameSentenceMaps_VerbFrameSentences.CONTENT_URI);
				final String[] projection = {VerbFrameSentenceMaps_VerbFrameSentences.SENTENCE};
				final String selection = VerbFrameSentenceMaps_VerbFrameSentences.SYNSETID + " = ? AND " + VerbFrameSentenceMaps_VerbFrameSentences.WORDID + " = ?"; //
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
						Spanner.appendImage(sb, BasicModule.this.flagDrawable);
						sb.append(' ');
						sb.append(record);
					}
					while (cursor.moveToNext());

					// attach result
					TreeFactory.addTextNode(parent, sb, context);

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					// parent.disable();
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
				final String selection = AdjPositions_AdjPositionTypes.SYNSETID + " = ?"; //
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
						final String record = String.format(Locale.ENGLISH, "%s", position); //
						if (sb.length() != 0)
						{
							sb.append('\n');
						}
						Spanner.appendImage(sb, BasicModule.this.flagDrawable);
						sb.append(' ');
						sb.append(record);
					}
					while (cursor.moveToNext());

					// attach result
					TreeFactory.addTextNode(parent, sb, context);

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					// parent.disable();
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
				final String selection = AdjPositions_AdjPositionTypes.SYNSETID + " = ? AND " + AdjPositions_AdjPositionTypes.WORDID + " = ?"; //
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
						final String record = String.format(Locale.ENGLISH, "%s", position); //
						if (sb.length() != 0)
						{
							sb.append('\n');
						}
						Spanner.appendImage(sb, BasicModule.this.flagDrawable);
						sb.append(' ');
						sb.append(record);
					}
					while (cursor.moveToNext());

					// attach result
					TreeFactory.addTextNode(parent, sb, context);

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					// parent.disable();
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
				final String selection = MorphMaps_Morphs.WORDID + " = ?"; //
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
						final String record = String.format(Locale.ENGLISH, "(%s) %s", pos1, morph1); //
						if (sb.length() != 0)
						{
							sb.append('\n');
						}
						Spanner.appendImage(sb, BasicModule.this.flagDrawable);
						sb.append(' ');
						sb.append(record);
					}
					while (cursor.moveToNext());

					// attach result
					TreeFactory.addTextNode(parent, sb, context);

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					// parent.disable();
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

	/**
	 * Match link id to drawable
	 *
	 * @param linkId link id
	 * @return drawable
	 */
	private Drawable getLinkDrawable(final int linkId)
	{
		final Context context = BasicModule.this.context;
		return getLinkDrawable(context, linkId);
	}

	/**
	 * Match link id to drawable
	 *
	 * @param context context
	 * @param linkId  link id
	 * @return drawable
	 */
	private Drawable getLinkDrawable(final Context context, final int linkId)
	{
		int resId = getLinkRes(linkId);
		return Spanner.getDrawable(context, resId);
	}

	// Q U E R I E S

	/**
	 * LinkData query
	 */
	public class LinksQueryData extends QueryRenderer.QueryData
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
		 * @param icon     icon
		 * @param text     label text
		 */
		public LinksQueryData(final long synsetId, final long wordId, final int icon, final CharSequence text)
		{
			super(synsetId, icon, text);
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

	public class SubLinksQueryData extends QueryRenderer.QueryData
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
		 * @param icon     icon
		 * @param text     label text
		 */
		public SubLinksQueryData(final long synsetId, final int linkId, final int icon, final CharSequence text)
		{
			super(synsetId, icon, text);
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
	public class SamplesQueryData extends QueryRenderer.QueryData
	{
		/**
		 * Constructor
		 *
		 * @param synsetId synset id
		 * @param icon     icon
		 * @param text     label text
		 */
		public SamplesQueryData(final long synsetId, final int icon, final CharSequence text)
		{
			super(synsetId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			// samples
			samples(this.id, node, true);
		}
	}
}
