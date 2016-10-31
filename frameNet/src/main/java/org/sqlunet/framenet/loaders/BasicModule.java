package org.sqlunet.framenet.loaders;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import org.sqlunet.browser.Module;
import org.sqlunet.framenet.R;
import org.sqlunet.framenet.Utils;
import org.sqlunet.framenet.provider.FrameNetContract;
import org.sqlunet.framenet.provider.FrameNetContract.AnnoSets_Layers_X;
import org.sqlunet.framenet.provider.FrameNetContract.Frames_FEs;
import org.sqlunet.framenet.provider.FrameNetContract.Frames_Related;
import org.sqlunet.framenet.provider.FrameNetContract.Frames_X;
import org.sqlunet.framenet.provider.FrameNetContract.Governors_AnnoSets_Sentences;
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits;
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_FERealizations_ValenceUnits;
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_Governors;
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels;
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_X;
import org.sqlunet.framenet.provider.FrameNetContract.Patterns_Layers_X;
import org.sqlunet.framenet.provider.FrameNetContract.Patterns_Sentences;
import org.sqlunet.framenet.provider.FrameNetContract.Sentences_Layers_X;
import org.sqlunet.framenet.provider.FrameNetContract.ValenceUnits_Layers_X;
import org.sqlunet.framenet.provider.FrameNetContract.ValenceUnits_Sentences;
import org.sqlunet.framenet.provider.FrameNetContract.Words_LexUnits_Frames;
import org.sqlunet.framenet.sql.FnLabel;
import org.sqlunet.framenet.style.FrameNetFactories;
import org.sqlunet.framenet.style.FrameNetFrameProcessor;
import org.sqlunet.framenet.style.FrameNetMarkupFactory;
import org.sqlunet.framenet.style.FrameNetProcessor;
import org.sqlunet.framenet.style.FrameNetSpanner;
import org.sqlunet.style.Spanner;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.renderer.QueryRenderer;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.view.TreeFactory;

import java.util.List;
import java.util.Locale;

/**
 * Basic frame module
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract public class BasicModule extends Module
{
	// private static final String TAG = "BasicModule";

	/**
	 * Verbose flag
	 */
	private static boolean VERBOSE = false;

	/**
	 * Focused layer name
	 */
	private static final String FOCUSLAYER = "FE"; // "Target"; //

	// agents

	/**
	 * Processor
	 */
	private final FrameNetProcessor processor;

	/**
	 * Frame Processor
	 */
	private final FrameNetFrameProcessor frameProcessor;

	/**
	 * Spanner
	 */
	private final FrameNetSpanner spanner;

	// resources

	/**
	 * Drawable for frame
	 */
	private final Drawable frameDrawable;

	/**
	 * Drawable for FE
	 */
	private final Drawable feDrawable;

	/**
	 * Drawable for lexUnit
	 */
	private final Drawable lexunitDrawable;

	/**
	 * Drawable for definition
	 */
	private final Drawable definitionDrawable;

	/**
	 * Drawable for meta definition
	 */
	private final Drawable metadefinitionDrawable;

	/**
	 * Drawable for realization
	 */
	private final Drawable realizationDrawable;

	/**
	 * Drawable for sentence
	 */
	private final Drawable sentenceDrawable;

	/**
	 * Drawable for semtype
	 */
	private final Drawable semtypeDrawable;

	/**
	 * Drawable for coreset
	 */
	private final Drawable coresetDrawable;

	/**
	 * Drawable for layer
	 */
	private final Drawable layerDrawable;

	/**
	 * Constructor
	 */
	BasicModule(final Fragment fragment)
	{
		super(fragment);

		// drawables
		this.frameDrawable = Spanner.getDrawable(this.context, R.drawable.fnframe);
		this.feDrawable = Spanner.getDrawable(this.context, R.drawable.rolealt);
		this.lexunitDrawable = Spanner.getDrawable(this.context, R.drawable.lexunit);
		this.definitionDrawable = Spanner.getDrawable(this.context, R.drawable.definition);
		this.metadefinitionDrawable = Spanner.getDrawable(this.context, R.drawable.metadefinition);
		this.sentenceDrawable = Spanner.getDrawable(this.context, R.drawable.sentence);
		this.realizationDrawable = Spanner.getDrawable(this.context, R.drawable.realization);
		this.semtypeDrawable = Spanner.getDrawable(this.context, R.drawable.semtype);
		this.coresetDrawable = Spanner.getDrawable(this.context, R.drawable.coreset);
		this.layerDrawable = Spanner.getDrawable(this.context, R.drawable.layer);

		// agents
		this.processor = new FrameNetProcessor();
		this.frameProcessor = new FrameNetFrameProcessor();
		this.spanner = new FrameNetSpanner(this.context);
	}

	// C R E A T I O N

	/**
	 * Set always-simple-preferences flag
	 *
	 * @param verbose flag
	 */
	@SuppressWarnings("unused")
	public static void setVerbose(boolean verbose)
	{
		VERBOSE = verbose;
	}

	// L O A D E R S

	// frame

	/**
	 * FrameNet frame
	 *
	 * @param frameId frame id
	 * @param parent  parent node
	 */
	void frame(final long frameId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(Frames_X.CONTENT_URI);
				final String[] projection = { //
						Frames_X.FRAMEID, //
						Frames_X.FRAME, //
						Frames_X.FRAMEDEFINITION, //
						Frames_X.SEMTYPE, //
						Frames_X.SEMTYPEABBREV, //
						Frames_X.SEMTYPEDEFINITION, //
				};
				final String selection = Frames_X.FRAMEID + " = ?"; //
				final String[] selectionArgs = {Long.toString(frameId)};
				final String sortOrder = Frames_X.FRAME;
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

					// column indices
					// final int idFrameId = cursor.getColumnIndex(Frames_X.FRAMEID);
					final int idFrame = cursor.getColumnIndex(Frames_X.FRAME);
					final int idFrameDefinition = cursor.getColumnIndex(Frames_X.FRAMEDEFINITION);

					// data
					// final int frameId = cursor.getInt(idFrameId);

					// frame
					Spanner.appendImage(sb, BasicModule.this.frameDrawable);
					sb.append(' ');
					Spanner.append(sb, cursor.getString(idFrame), 0, FrameNetFactories.frameFactory);
					if (VERBOSE)
					{
						sb.append(' ');
						sb.append(Long.toString(frameId));
					}
					sb.append('\n');

					// definition
					Spanner.appendImage(sb, BasicModule.this.metadefinitionDrawable);
					sb.append(' ');
					final String frameDefinition = cursor.getString(idFrameDefinition);
					final CharSequence[] frameDefinitionFields = processDefinition(frameDefinition, 0);
					Spanner.append(sb, frameDefinitionFields[0], 0, FrameNetFactories.metadefinitionFactory);

					// examples in definition
					for (int i = 1; i < frameDefinitionFields.length; i++)
					{
						sb.append('\n');
						sb.append(frameDefinitionFields[i]);
					}

					// sub nodes
					final TreeNode fesNode = TreeFactory.newQueryNode(new FEsQueryData(frameId, R.drawable.roles, "Frame Elements"), BasicModule.this.context); //
					final TreeNode lexUnitsNode = TreeFactory.newQueryNode(new LexUnitsQueryData(frameId, R.drawable.lexunit, "Lex Units"), BasicModule.this.context); //
					final TreeNode relatedNode = TreeFactory.newQueryNode(new RelatedQueryData(frameId, R.drawable.fnframe, "Related"), BasicModule.this.context); //

					// attach result
					TreeFactory.addTextNode(parent, sb, BasicModule.this.context, fesNode, lexUnitsNode, relatedNode);

					// expand
					TreeView.expand(parent, false);
					TreeView.expand(fesNode, false);
					TreeView.expand(lexUnitsNode, false);
					// TreeView.expand(relatedNode, false);
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
	 * Related frames
	 *
	 * @param frameId frame id
	 * @param parent  parent node
	 */
	private void relatedFrames(final long frameId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(Frames_Related.CONTENT_URI);
				final String[] projection = { //
						FrameNetContract.SRC + '.' + Frames_Related.FRAMEID + " AS " + "i1", //
						FrameNetContract.SRC + '.' + Frames_Related.FRAME + " AS " + "f1", //
						FrameNetContract.DEST + '.' + Frames_Related.FRAME2ID + " AS " + "i2", //
						FrameNetContract.DEST + '.' + Frames_Related.FRAME2 + " AS " + "f2", //
						Frames_Related.RELATIONID, //
						Frames_Related.RELATION, //
				};
				final String selection = FrameNetContract.SRC + '.' + Frames_Related.FRAMEID + " = ?" + " OR " + FrameNetContract.DEST + '.' + Frames_Related.FRAME2ID + " = ?"; //
				final String[] selectionArgs = {Long.toString(frameId), Long.toString(frameId)};
				final String sortOrder = FrameNetContract.SRC + '.' + Frames_Related.FRAME;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.moveToFirst())
				{
					final SpannableStringBuilder sb = new SpannableStringBuilder();

					// column indices
					// final int idFrameId = cursor.getColumnIndex(Frames_Related.FRAMEID);
					// final int idFrame = cursor.getColumnIndex(Frames_Related.FRAME);
					// final int idFrame2Id = cursor.getColumnIndex(Frames_Related.FRAME2ID);
					// final int idFrame2 = cursor.getColumnIndex(Frames_Related.FRAME2);
					final int idFrameId = cursor.getColumnIndex("i1"); //
					final int idFrame = cursor.getColumnIndex("f1"); //
					final int idFrame2Id = cursor.getColumnIndex("i2"); //
					final int idFrame2 = cursor.getColumnIndex("f2"); //
					final int idRelation = cursor.getColumnIndex(Frames_Related.RELATION);
					final int idRelationId = cursor.getColumnIndex(Frames_Related.RELATIONID);

					boolean first = true;
					do
					{
						if (!first)
						{
							sb.append('\n');
						}
						else
						{
							first = false;
						}

						// data
						final int frame1Id = cursor.getInt(idFrameId);
						final int frame2Id = cursor.getInt(idFrame2Id);
						final int relationId = cursor.getInt(idRelationId);
						final String frame1 = cursor.getString(idFrame);
						final String frame2 = cursor.getString(idFrame2);
						String relation = cursor.getString(idRelation).toLowerCase(Locale.ENGLISH);

						// related
						Spanner.appendImage(sb, BasicModule.this.frameDrawable);
						sb.append(' ');

						// 1
						if (frame1Id == frameId)
						{
							sb.append("it"); //
						}
						else
						{
							Spanner.append(sb, frame1, 0, FrameNetFactories.frameFactory);
							if (VERBOSE)
							{
								sb.append(' ');
								sb.append(Integer.toString(frame1Id));
							}
						}

						// relation
						sb.append(' ');
						if ("see also".equals(relation)) //
						{
							relation = "has see-also relation to"; //
						}
						if ("perspective on".equals(relation)) //
						{
							relation = "perpectivizes"; //
						}
						if ("subframe of".equals(relation)) //
						{
							relation = "is subframe of"; //
						}
						sb.append(relation);
						if (VERBOSE)
						{
							sb.append(' ');
							sb.append(Integer.toString(relationId));
						}
						sb.append(' ');

						// 2
						if (frame2Id == frameId)
						{
							sb.append("it"); //
						}
						else
						{
							Spanner.append(sb, frame2, 0, FrameNetFactories.frameFactory);
							if (VERBOSE)
							{
								sb.append(' ');
								sb.append(Integer.toString(frame2Id));
							}
						}
					}
					while (cursor.moveToNext());

					// attach result
					TreeFactory.addTextNode(parent, sb, BasicModule.this.context);

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

	// fes

	/**
	 * Frame elements for frame
	 *
	 * @param frameId frame id
	 * @param parent  parent node
	 */
	private void fesForFrame(final int frameId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(Frames_FEs.CONTENT_URI_BY_FE);
				final String[] projection = { //
						Frames_FEs.FETYPEID, //
						Frames_FEs.FETYPE, //
						Frames_FEs.FEABBREV, //
						Frames_FEs.FEDEFINITION, //
						"GROUP_CONCAT(" + Frames_FEs.SEMTYPE + ",'|') AS " + Frames_FEs.SEMTYPES, //
						Frames_FEs.CORETYPEID, //
						Frames_FEs.CORETYPE, //
						Frames_FEs.CORESET, //
				};
				final String selection = Frames_FEs.FRAMEID + " = ? "; //
				final String[] selectionArgs = {Integer.toString(frameId)};
				final String sortOrder = Frames_FEs.CORETYPEID + ',' + Frames_FEs.FETYPE;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.moveToFirst())
				{
					// column indices
					final int idFeType = cursor.getColumnIndex(Frames_FEs.FETYPE);
					final int idFeAbbrev = cursor.getColumnIndex(Frames_FEs.FEABBREV);
					final int idDefinition = cursor.getColumnIndex(Frames_FEs.FEDEFINITION);
					final int idSemTypes = cursor.getColumnIndex(Frames_FEs.SEMTYPES);
					final int idCoreset = cursor.getColumnIndex(Frames_FEs.CORESET);
					final int idCoreTypeId = cursor.getColumnIndex(Frames_FEs.CORETYPEID);
					final int idCoreType = cursor.getColumnIndex(Frames_FEs.CORETYPE);

					// read cursor
					do
					{
						final SpannableStringBuilder sb = new SpannableStringBuilder();

						final String feType = cursor.getString(idFeType);
						final String feAbbrev = cursor.getString(idFeAbbrev);
						final String feDefinition = cursor.getString(idDefinition).trim().replaceAll("\n+", "\n").replaceAll("\n$", ""); //
						final String feSemTypes = cursor.getString(idSemTypes);
						final boolean isInCoreSet = !cursor.isNull(idCoreset);
						final int coreTypeId = cursor.getInt(idCoreTypeId);
						final String coreType = cursor.getString(idCoreType);

						// fe
						Spanner.append(sb, feType, 0, FrameNetFactories.feFactory);
						sb.append(' ');
						Spanner.append(sb, feAbbrev, 0, FrameNetFactories.feAbbrevFactory);

						// attach fe
						final TreeNode feNode = TreeFactory.addTreeItemNode(parent, sb, coreTypeId == 1 ? R.drawable.corerole : R.drawable.role, BasicModule.this.context);

						// more info
						final SpannableStringBuilder sb2 = new SpannableStringBuilder();

						// fe definition
						final CharSequence[] frameDefinitionFields = processDefinition(feDefinition, FrameNetMarkupFactory.FEDEF);
						sb2.append('\t');
						Spanner.appendImage(sb2, BasicModule.this.metadefinitionDrawable);
						sb2.append(' ');
						Spanner.append(sb2, frameDefinitionFields[0], 0, FrameNetFactories.metadefinitionFactory);
						if (frameDefinitionFields.length > 1)
						{
							sb2.append('\n');
							sb2.append('\t');
							sb2.append(frameDefinitionFields[1]);
						}

						// core type
						sb2.append('\n');
						sb2.append('\t');
						Spanner.appendImage(sb2, BasicModule.this.coresetDrawable);
						sb2.append(' ');
						sb2.append(coreType);

						// coreset
						if (isInCoreSet)
						{
							final int coreset = cursor.getInt(idCoreset);
							sb2.append('\n');
							sb2.append('\t');
							Spanner.appendImage(sb2, BasicModule.this.coresetDrawable);
							sb2.append("[coreset] "); //
							sb2.append(Integer.toString(coreset));
						}

						// sem types
						if (feSemTypes != null)
						{
							sb2.append('\n');
							sb2.append('\t');
							Spanner.appendImage(sb2, BasicModule.this.semtypeDrawable);
							sb2.append(' ');
							sb2.append(feSemTypes);
						}

						// attach more info to fe node
						TreeFactory.addTextNode(feNode, sb2, BasicModule.this.context);
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

	// lexunits

	/**
	 * Lex unit
	 *
	 * @param luId      lu id
	 * @param parent    parent node
	 * @param withFrame whether to include frames
	 * @param withFes   whether to include frame elements
	 */
	void lexUnit(final long luId, final TreeNode parent, final boolean withFrame, final boolean withFes)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(LexUnits_X.CONTENT_URI);
				final String[] projection = { //
						LexUnits_X.LUID, //
						LexUnits_X.LEXUNIT, //
						FrameNetContract.LU + '.' + LexUnits_X.FRAMEID, //
						FrameNetContract.LU + '.' + LexUnits_X.POSID, //
						LexUnits_X.LUDEFINITION, //
						LexUnits_X.LUDICT, //
						LexUnits_X.INCORPORATEDFETYPE, //
						LexUnits_X.INCORPORATEDFEDEFINITION, //
				};
				final String selection = LexUnits_X.LUID + " = ?"; //
				final String[] selectionArgs = {Long.toString(luId)};
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

					// column indices
					// final int idLuId = cursor.getColumnIndex(LexUnits_X.LUID);
					final int idLexUnit = cursor.getColumnIndex(LexUnits_X.LEXUNIT);
					final int idDefinition = cursor.getColumnIndex(LexUnits_X.LUDEFINITION);
					final int idDictionary = cursor.getColumnIndex(LexUnits_X.LUDICT);
					final int idFrameId = cursor.getColumnIndex(LexUnits_X.FRAMEID);
					final int idIncorporatedFEType = cursor.getColumnIndex(LexUnits_X.INCORPORATEDFETYPE);
					final int idIncorporatedFEDefinition = cursor.getColumnIndex(LexUnits_X.INCORPORATEDFEDEFINITION);

					// data
					// final int luId = cursor.getInt(idLuId);
					final int frameId = cursor.getInt(idFrameId);
					final String definition = cursor.getString(idDefinition);
					final String dictionary = cursor.getString(idDictionary);
					final String incorporatedFEType = cursor.getString(idIncorporatedFEType);
					final String incorporatedFEDefinition = cursor.getString(idIncorporatedFEDefinition);

					// lexUnit
					Spanner.appendImage(sb, BasicModule.this.lexunitDrawable);
					sb.append(' ');
					Spanner.append(sb, cursor.getString(idLexUnit), 0, FrameNetFactories.lexunitFactory);
					if (VERBOSE)
					{
						sb.append(' ');
						sb.append(Long.toString(luId));
					}

					// definition
					sb.append('\n');
					Spanner.appendImage(sb, BasicModule.this.definitionDrawable);
					sb.append(' ');
					Spanner.append(sb, definition.trim(), 0, FrameNetFactories.definitionFactory);
					sb.append(' ');
					sb.append('[');
					sb.append(dictionary);
					sb.append(']');

					// incorporated fe
					if (incorporatedFEType != null)
					{
						sb.append('\n');
						Spanner.appendImage(sb, BasicModule.this.feDrawable);
						sb.append(' ');
						sb.append("Incorporated"); //
						sb.append(' ');
						Spanner.append(sb, incorporatedFEType, 0, FrameNetFactories.fe2Factory);
						if (incorporatedFEDefinition != null)
						{
							sb.append(' ');
							sb.append('-');
							sb.append(' ');
							final CharSequence[] definitionFields = processDefinition(incorporatedFEDefinition, FrameNetMarkupFactory.FEDEF);
							Spanner.append(sb, definitionFields[0], 0, FrameNetFactories.metadefinitionFactory);
							// if (definitionFields.length > 1)
							// {
							// sb.append('\n');
							// sb.append('\t');
							// sb.append(definitionFields[1]);
							// }
						}
					}

					// sub nodes
					final TreeNode governorsNode = TreeFactory.newQueryNode(new GovernorsQueryData(luId, R.drawable.governor, "Governors"), BasicModule.this.context); //
					final TreeNode realizationsNode = TreeFactory.newQueryNode(new RealizationsQueryData(luId, R.drawable.realization, "Realizations"), BasicModule.this.context); //
					final TreeNode groupRealizationsNode = TreeFactory.newQueryNode(new GroupRealizationsQueryData(luId, R.drawable.grouprealization, "Group realizations"), BasicModule.this.context); //
					final TreeNode sentencesNode = TreeFactory.newQueryNode(new SentencesForLexUnitQueryData(luId, R.drawable.sentence, "Sentences"), BasicModule.this.context); //

					// attach result
					if (withFrame)
					{
						final TreeNode frameNode = TreeFactory.newQueryNode(new FrameQueryData(frameId, R.drawable.fnframe, "Frame"), BasicModule.this.context); //
						if (withFes)
						{
							final TreeNode fesNode = TreeFactory.newQueryNode(new FEsQueryData(frameId, R.drawable.roles, "Frame Elements"), BasicModule.this.context); //
							TreeFactory.addTextNode(parent, sb, BasicModule.this.context, frameNode, fesNode, governorsNode, realizationsNode, groupRealizationsNode, sentencesNode);
						}
						else
						{
							TreeFactory.addTextNode(parent, sb, BasicModule.this.context, frameNode, governorsNode, realizationsNode, groupRealizationsNode, sentencesNode);
						}

					}
					else
					{
						TreeFactory.addTextNode(parent, sb, BasicModule.this.context, governorsNode, realizationsNode, groupRealizationsNode, sentencesNode);
					}

					// expand
					TreeView.expand(parent, false);
					// TreeView.expand(governorNode, false);
					// TreeView.expand(realizationsNode, false);
					// TreeView.expand(groupRealizationsNode, false);
					// TreeView.expand(sentencesNode, false);
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
	 * Lex units for frame
	 *
	 * @param frameId   frame id
	 * @param parent    parent node
	 * @param withFrame whether to include frame
	 */
	private void lexUnitsForFrame(final long frameId, final TreeNode parent, final boolean withFrame)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(LexUnits_X.CONTENT_URI);
				final String[] projection = { //
						LexUnits_X.LUID, //
						LexUnits_X.LEXUNIT, //
						FrameNetContract.LU + '.' + LexUnits_X.FRAMEID, //
						FrameNetContract.LU + '.' + LexUnits_X.POSID, //
						LexUnits_X.LUDEFINITION, //
						LexUnits_X.LUDICT, //
						LexUnits_X.INCORPORATEDFETYPE, //
						LexUnits_X.INCORPORATEDFEDEFINITION, //
				};
				final String selection = FrameNetContract.FRAME + '.' + LexUnits_X.FRAMEID + " = ?"; //
				final String[] selectionArgs = {Long.toString(frameId)};
				final String sortOrder = LexUnits_X.LEXUNIT;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.moveToFirst())
				{
					// column indices
					// final int idFrameId = cursor.getColumnIndex(LexUnits_X.FRAMEID);
					final int idLuId = cursor.getColumnIndex(LexUnits_X.LUID);
					final int idLexUnit = cursor.getColumnIndex(LexUnits_X.LEXUNIT);
					final int idDefinition = cursor.getColumnIndex(LexUnits_X.LUDEFINITION);
					final int idIncorporatedFEType = cursor.getColumnIndex(LexUnits_X.INCORPORATEDFETYPE);
					final int idIncorporatedFEDefinition = cursor.getColumnIndex(LexUnits_X.INCORPORATEDFEDEFINITION);

					// data
					do
					{
						final SpannableStringBuilder sb = new SpannableStringBuilder();

						// final int frameId = cursor.getInt(idFrameId);
						final long luId = cursor.getLong(idLuId);
						final String lexUnit = cursor.getString(idLexUnit);
						final String incorporatedFEType = cursor.getString(idIncorporatedFEType);
						final String incorporatedFEDefinition = cursor.getString(idIncorporatedFEDefinition);

						// lex unit
						Spanner.append(sb, lexUnit, 0, FrameNetFactories.lexunitFactory);
						if (VERBOSE)
						{
							sb.append(' ');
							sb.append(Long.toString(luId));
						}

						// attach fe
						final TreeNode luNode = TreeFactory.addTreeItemNode(parent, sb, R.drawable.lexunit, BasicModule.this.context);

						// more info
						final SpannableStringBuilder sb2 = new SpannableStringBuilder();

						// definition
						Spanner.appendImage(sb2, BasicModule.this.definitionDrawable);
						sb2.append(' ');
						Spanner.append(sb2, cursor.getString(idDefinition).trim(), 0, FrameNetFactories.definitionFactory);

						// incorporated fe
						if (incorporatedFEType != null)
						{
							sb2.append('\n');
							Spanner.appendImage(sb2, BasicModule.this.feDrawable);
							sb2.append(' ');
							sb2.append("Incorporated"); //
							sb2.append(' ');
							Spanner.append(sb2, incorporatedFEType, 0, FrameNetFactories.fe2Factory);
							if (incorporatedFEDefinition != null)
							{
								sb2.append(' ');
								sb2.append('-');
								sb2.append(' ');
								final CharSequence[] definitionFields = processDefinition(incorporatedFEDefinition, FrameNetMarkupFactory.FEDEF);
								Spanner.append(sb2, definitionFields[0], 0, FrameNetFactories.definitionFactory);
								// if (definitionFields.length > 1)
								// {
								// sb.append('\n');
								// sb.append('\t');
								// sb.append(definitionFields[1]);
								// }
							}
						}

						// sub nodes
						final TreeNode governorsNode = TreeFactory.newQueryNode(new GovernorsQueryData(luId, R.drawable.governor, "Governors"), BasicModule.this.context); //
						final TreeNode realizationsNode = TreeFactory.newQueryNode(new RealizationsQueryData(luId, R.drawable.realization, "Realizations"), BasicModule.this.context); //
						final TreeNode groupRealizationsNode = TreeFactory.newQueryNode(new GroupRealizationsQueryData(luId, R.drawable.grouprealization, "Group realizations"), BasicModule.this.context); //
						final TreeNode sentencesNode = TreeFactory.newQueryNode(new SentencesForLexUnitQueryData(luId, R.drawable.sentence, "Sentences"), BasicModule.this.context); //

						// attach result
						if (withFrame)
						{
							final TreeNode frameNode = TreeFactory.newQueryNode(new FrameQueryData(frameId, R.drawable.fnframe, "Frame"), BasicModule.this.context); //
							final TreeNode fesNode = TreeFactory.newQueryNode(new FEsQueryData(frameId, R.drawable.roles, "Frame Elements"), BasicModule.this.context); //
							TreeFactory.addTextNode(luNode, sb2, BasicModule.this.context, frameNode, fesNode, governorsNode, realizationsNode, groupRealizationsNode, sentencesNode);

							// expand
							// TreeView.expand(frameNode, false);
							// TreeView.expand(fesNode, false);
						}
						else
						{
							TreeFactory.addTextNode(luNode, sb2, BasicModule.this.context, governorsNode, realizationsNode, groupRealizationsNode, sentencesNode);
						}

						// expand
						// TreeView.expand(governorNode, false);
						// TreeView.expand(realizationsNode, false);
						// TreeView.expand(groupRealizationsNode, false);
						// TreeView.expand(sentencesNode, false);
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
	 * Lex units for word and pos
	 *
	 * @param wordId word id
	 * @param pos    pos
	 * @param parent parent node
	 */
	void lexUnitsForWordAndPos(final long wordId, final Character pos, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(Words_LexUnits_Frames.CONTENT_URI);
				final String[] projection = { //
						Words_LexUnits_Frames.LUID, //
						Words_LexUnits_Frames.LEXUNIT, //
						FrameNetContract.LU + '.' + Words_LexUnits_Frames.FRAMEID, //
						FrameNetContract.LU + '.' + Words_LexUnits_Frames.POSID, //
						Words_LexUnits_Frames.LUDEFINITION, //
						Words_LexUnits_Frames.LUDICT, //
						Words_LexUnits_Frames.INCORPORATEDFETYPE, //
						Words_LexUnits_Frames.INCORPORATEDFEDEFINITION, //
				};
				final String selection = pos == null ?  //
						Words_LexUnits_Frames.WORDID + " = ?" :  //
						Words_LexUnits_Frames.WORDID + " = ? AND " + FrameNetContract.LU + '.' + Words_LexUnits_Frames.POSID + " = ?"; //
				final String[] selectionArgs = pos == null ? new String[]{Long.toString(wordId)} : new String[]{Long.toString(wordId), Integer.toString(Utils.posToPosId(pos))};
				final String sortOrder = Words_LexUnits_Frames.FRAME + ',' + Words_LexUnits_Frames.LUID;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.moveToFirst())
				{
					// column indices
					final int idLuId = cursor.getColumnIndex(Words_LexUnits_Frames.LUID);
					final int idLexUnit = cursor.getColumnIndex(Words_LexUnits_Frames.LEXUNIT);
					final int idDefinition = cursor.getColumnIndex(Words_LexUnits_Frames.LUDEFINITION);
					final int idFrameId = cursor.getColumnIndex(Words_LexUnits_Frames.FRAMEID);
					final int idIncorporatedFEType = cursor.getColumnIndex(Words_LexUnits_Frames.INCORPORATEDFETYPE);
					final int idIncorporatedFEDefinition = cursor.getColumnIndex(Words_LexUnits_Frames.INCORPORATEDFEDEFINITION);

					// data
					do
					{
						final SpannableStringBuilder sb = new SpannableStringBuilder();

						final int luId = cursor.getInt(idLuId);
						final int frameId = cursor.getInt(idFrameId);
						final String definition = cursor.getString(idDefinition);
						final String incorporatedFEType = cursor.getString(idIncorporatedFEType);
						final String incorporatedFEDefinition = cursor.getString(idIncorporatedFEDefinition);

						// lexUnit
						Spanner.appendImage(sb, BasicModule.this.lexunitDrawable);
						sb.append(' ');
						Spanner.append(sb, cursor.getString(idLexUnit), 0, FrameNetFactories.lexunitFactory);
						if (VERBOSE)
						{
							sb.append(' ');
							sb.append(Integer.toString(luId));
						}

						// definition
						sb.append('\n');
						Spanner.appendImage(sb, BasicModule.this.definitionDrawable);
						sb.append(' ');
						Spanner.append(sb, definition.trim(), 0, FrameNetFactories.definitionFactory);

						// incorporated FE
						if (incorporatedFEType != null)
						{
							sb.append('\n');
							Spanner.appendImage(sb, BasicModule.this.feDrawable);
							sb.append(' ');
							sb.append("Incorporated"); //
							sb.append(' ');
							Spanner.append(sb, incorporatedFEType, 0, FrameNetFactories.fe2Factory);
							if (incorporatedFEDefinition != null)
							{
								sb.append(' ');
								sb.append('-');
								sb.append(' ');
								final CharSequence[] definitionFields = processDefinition(incorporatedFEDefinition, FrameNetMarkupFactory.FEDEF);
								Spanner.append(sb, definitionFields[0], 0, FrameNetFactories.metadefinitionFactory);
								// if (definitionFields.length > 1)
								// {
								// sb.append('\n');
								// sb.append('\t');
								// sb.append(definitionFields[1]);
								// }
							}
						}

						// sub nodes
						final TreeNode frameNode = TreeFactory.newQueryNode(new FrameQueryData(frameId, R.drawable.fnframe, "Frame"), BasicModule.this.context); //
						final TreeNode fesNode = TreeFactory.newQueryNode(new FEsQueryData(frameId, R.drawable.roles, "Frame Elements"), BasicModule.this.context); //
						final TreeNode governorsNode = TreeFactory.newQueryNode(new GovernorsQueryData(luId, R.drawable.governor, "Governors"), BasicModule.this.context); //
						final TreeNode realizationsNode = TreeFactory.newQueryNode(new RealizationsQueryData(luId, R.drawable.realization, "Realizations"), BasicModule.this.context); //
						final TreeNode groupRealizationsNode = TreeFactory.newQueryNode(new GroupRealizationsQueryData(luId, R.drawable.grouprealization, "Group realizations"), BasicModule.this.context); //
						final TreeNode sentencesNode = TreeFactory.newQueryNode(new SentencesForLexUnitQueryData(luId, R.drawable.sentence, "Sentences"), BasicModule.this.context); //

						// attach result
						TreeFactory.addTextNode(parent, sb, BasicModule.this.context, frameNode, fesNode, governorsNode, realizationsNode, groupRealizationsNode, sentencesNode);

						// expand
						// TreeView.expand(frameNode, false);
						// TreeView.expand(fesNode, false);
						// TreeView.expand(governorNode, false);
						// TreeView.expand(realizationsNode, false);
						// TreeView.expand(groupRealizationsNode, false);
						// TreeView.expand(sentencesNode, false);
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

	// governors

	/**
	 * Governors for lex unit
	 *
	 * @param luId   lex unit id
	 * @param parent parent node
	 */
	private void governorsForLexUnit(final long luId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(LexUnits_Governors.CONTENT_URI);
				final String[] projection = { //
						LexUnits_Governors.LUID, //
						LexUnits_Governors.GOVERNORID, //
						LexUnits_Governors.GOVERNORTYPE, //
						LexUnits_Governors.FNWORDID, //
						LexUnits_Governors.FNWORD, //
				};
				final String selection = LexUnits_Governors.LUID + " = ?"; //
				final String[] selectionArgs = {Long.toString(luId)};
				final String sortOrder = LexUnits_Governors.GOVERNORID;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.moveToFirst())
				{
					// column indices
					final int idGovernorId = cursor.getColumnIndex(LexUnits_Governors.GOVERNORID);
					final int idGovernorType = cursor.getColumnIndex(LexUnits_Governors.GOVERNORTYPE);
					final int idWord = cursor.getColumnIndex(LexUnits_Governors.FNWORD);
					final int idWordId = cursor.getColumnIndex(LexUnits_Governors.FNWORDID);

					// data
					do
					{
						final SpannableStringBuilder sb = new SpannableStringBuilder();

						// data
						final long governorId = cursor.getLong(idGovernorId);
						final String governorType = cursor.getString(idGovernorType);
						final String word = cursor.getString(idWord);

						// type
						Spanner.append(sb, governorType, 0, FrameNetFactories.governorTypeFactory);
						sb.append(' ');
						if (VERBOSE)
						{
							sb.append(' ');
							sb.append(Long.toString(governorId));
							sb.append(' ');
							sb.append(Integer.toString(cursor.getInt(idWordId)));
						}
						sb.append(' ');
						Spanner.append(sb, word, 0, FrameNetFactories.governorFactory);

						// attach annoSets node
						final TreeNode annoSetsNode = TreeFactory.newQueryNode(new AnnoSetsForGovernorQueryData(governorId, R.drawable.governor, sb), BasicModule.this.context);
						parent.addChild(annoSetsNode);

						// expand
						// TreeView.expand(annoSetsNode, false);
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
	 * AnnoSets for governor
	 *
	 * @param governorId governor id
	 * @param parent     parent id
	 */
	private void annoSetsForGovernor(final long governorId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(Governors_AnnoSets_Sentences.CONTENT_URI);
				final String[] projection = { //
						Governors_AnnoSets_Sentences.GOVERNORID, //
						Governors_AnnoSets_Sentences.ANNOSETID, //
						Governors_AnnoSets_Sentences.SENTENCEID, //
						Governors_AnnoSets_Sentences.TEXT, //
				};
				final String selection = Governors_AnnoSets_Sentences.GOVERNORID + " = ?"; //
				final String[] selectionArgs = {Long.toString(governorId)};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.moveToFirst())
				{
					// column indices
					final int idSentenceId = cursor.getColumnIndex(Governors_AnnoSets_Sentences.SENTENCEID);
					final int idText = cursor.getColumnIndex(Governors_AnnoSets_Sentences.TEXT);
					final int idAnnoSetId = cursor.getColumnIndex(Governors_AnnoSets_Sentences.ANNOSETID);

					// data
					do
					{
						final SpannableStringBuilder sb = new SpannableStringBuilder();

						// data
						final String text = cursor.getString(idText);
						final long annoSetId = cursor.getLong(idAnnoSetId);

						// sentence
						Spanner.append(sb, text, 0, FrameNetFactories.sentenceFactory);
						sb.append(' ');
						if (VERBOSE)
						{
							sb.append(' ');
							sb.append("sentenceid="); //
							sb.append(cursor.getString(idSentenceId));
							sb.append(' ');
							sb.append("annosetid="); //
							sb.append(Long.toString(annoSetId));
						}

						// attach annoSet node
						final TreeNode annoSetNode = TreeFactory.newQueryNode(new AnnoSetQuery(annoSetId, R.drawable.annoset, sb, false), BasicModule.this.context);
						parent.addChild(annoSetNode);

						// expand
						// TreeView.expand(annoSetNode, false);
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

	// realizations

	/**
	 * Realizations for lex unit
	 *
	 * @param luId   lex unit id
	 * @param parent parent node
	 */
	private void realizationsForLexicalunit(final long luId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(LexUnits_FERealizations_ValenceUnits.CONTENT_URI_BY_REALIZATION);
				final String[] projection = { //
						LexUnits_FERealizations_ValenceUnits.LUID, //
						LexUnits_FERealizations_ValenceUnits.FERID, //
						LexUnits_FERealizations_ValenceUnits.FETYPE, //
						"GROUP_CONCAT(IFNULL(" +  //
								LexUnits_FERealizations_ValenceUnits.PT +
								",'') || ':' || IFNULL(" + //
								LexUnits_FERealizations_ValenceUnits.GF +
								",'') || ':' || " +  //
								LexUnits_FERealizations_ValenceUnits.VUID +
								") AS " +  //
								LexUnits_FERealizations_ValenceUnits.FERS, //
						LexUnits_FERealizations_ValenceUnits.TOTAL, //
				};
				final String selection = LexUnits_FERealizations_ValenceUnits.LUID + " = ?"; //
				final String[] selectionArgs = {Long.toString(luId)};
				final String sortOrder = LexUnits_FERealizations_ValenceUnits.FERID;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.moveToFirst())
				{
					// column indices
					final int idFerId = cursor.getColumnIndex(LexUnits_FERealizations_ValenceUnits.FERID);
					final int idFeType = cursor.getColumnIndex(LexUnits_FERealizations_ValenceUnits.FETYPE);
					final int idFers = cursor.getColumnIndex(LexUnits_FERealizations_ValenceUnits.FERS);
					final int idTotal = cursor.getColumnIndex(LexUnits_FERealizations_ValenceUnits.TOTAL);

					// data
					do
					{
						final SpannableStringBuilder sb = new SpannableStringBuilder();

						// realization
						Spanner.append(sb, cursor.getString(idFeType), 0, FrameNetFactories.feFactory);
						sb.append(' ');
						sb.append("[annotated] "); //
						sb.append(Integer.toString(cursor.getInt(idTotal)));
						if (VERBOSE)
						{
							sb.append(' ');
							sb.append(cursor.getString(idFerId));
						}

						// fe
						final TreeNode feNode = TreeFactory.addTreeItemNode(parent, sb, R.drawable.toprole, BasicModule.this.context);

						// fe realizations
						final String fers = cursor.getString(idFers);
						for (String fer : fers.split(",")) //
						{
							// pt:gf:valenceUnit id

							// valenceUnit id
							long vuId = -1;
							String[] fields = fer.split(":"); //
							if (fields.length > 2)
							{
								vuId = Long.parseLong(fields[2]);
							}

							final SpannableStringBuilder sb1 = new SpannableStringBuilder();

							// pt
							if (fields.length > 0)
							{
								Spanner.append(sb1, processPT(fields[0]), 0, FrameNetFactories.ptFactory);
								sb1.append(' ');
							}
							// gf
							if (fields.length > 1)
							{
								sb1.append(' ');
								Spanner.append(sb1, fields[1], 0, FrameNetFactories.gfFactory);
							}

							if (VERBOSE)
							{
								sb.append(' ');
								sb.append(Long.toString(vuId));
							}

							// attach fer node
							final TreeNode ferNode = TreeFactory.newQueryNode(new SentencesForValenceUnitQueryData(vuId, R.drawable.realization, sb1), BasicModule.this.context);
							feNode.addChild(ferNode);

							// expand
							// TreeView.expand(ferNode, false);
						}
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
	 * Group realizations for lex unit
	 *
	 * @param luId   lex unit id
	 * @param parent parent node
	 */
	private void groupRealizationsForLexUnit(final long luId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(LexUnits_FEGroupRealizations_Patterns_ValenceUnits.CONTENT_URI_BY_PATTERN);
				final String[] projection = { //
						LexUnits_FEGroupRealizations_Patterns_ValenceUnits.LUID, //
						LexUnits_FEGroupRealizations_Patterns_ValenceUnits.FEGRID, //
						LexUnits_FEGroupRealizations_Patterns_ValenceUnits.PATTERNID, //
						"GROUP_CONCAT(" + //
								LexUnits_FEGroupRealizations_Patterns_ValenceUnits.FETYPE +
								" || '.' || " + //
								LexUnits_FEGroupRealizations_Patterns_ValenceUnits.PT +
								" || '.'|| IFNULL(" + //
								LexUnits_FEGroupRealizations_Patterns_ValenceUnits.GF + ", '--')) AS " + //
								LexUnits_FEGroupRealizations_Patterns_ValenceUnits.GROUPREALIZATIONS,}; //
				final String selection = LexUnits_FEGroupRealizations_Patterns_ValenceUnits.LUID + " = ?"; //
				final String[] selectionArgs = {Long.toString(luId)};
				final String sortOrder = null; // LexUnits_FEGroupRealizations_Patterns_ValenceUnits.FEGRID;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.moveToFirst())
				{
					// column indices
					final int idFEGRId = cursor.getColumnIndex(LexUnits_FEGroupRealizations_Patterns_ValenceUnits.FEGRID);
					final int idGroupRealizations = cursor.getColumnIndex(LexUnits_FEGroupRealizations_Patterns_ValenceUnits.GROUPREALIZATIONS);
					final int idPatternId = cursor.getColumnIndex(LexUnits_FEGroupRealizations_Patterns_ValenceUnits.PATTERNID);

					// data
					int groupNumber = 0;
					long groupId = -1;
					TreeNode groupNode = null;
					do
					{
						final SpannableStringBuilder sb = new SpannableStringBuilder();

						// data
						long feGroupId = cursor.getLong(idFEGRId);
						final String groupRealizations = cursor.getString(idGroupRealizations);
						final int patternId = cursor.getInt(idPatternId);
						// Log.d(TAG, "GROUP REALIZATIONS " + feGroupId + ' ' + groupRealizations + ' ' + patternId); //
						if (groupRealizations == null)
						{
							continue;
						}

						// group
						if (groupId != feGroupId)
						{
							final Editable sb1 = new SpannableStringBuilder();
							sb1.append("group"); //
							sb1.append(' ');
							sb1.append(Integer.toString(++groupNumber));

							if (VERBOSE)
							{
								sb1.append(' ');
								sb1.append(Long.toString(feGroupId));
							}

							groupId = feGroupId;
							groupNode = TreeFactory.addTreeItemNode(parent, sb1, R.drawable.grouprealization, BasicModule.this.context);
						}

						// group realization
						parseGroupRealizations(groupRealizations, sb);

						// attach sentences node
						final TreeNode sentencesNode = TreeFactory.newQueryNode(new SentencesForPatternQueryData(patternId, R.drawable.grouprealization, sb), BasicModule.this.context);
						assert groupNode != null;
						groupNode.addChild(sentencesNode);

						// expand
						// TreeView.expand(sentencesNode, false);
					}
					while (cursor.moveToNext());

					// expand
					TreeView.expand(parent, 2);
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
	 * Parse group realizations
	 *
	 * @param aggregate aggregate to parse
	 * @param sb        builder to host result
	 * @return builder
	 */
	private CharSequence parseGroupRealizations(final String aggregate, final SpannableStringBuilder sb)
	{
		// fe.pt.gf,fe.pt.gf,...
		final String[] groupRealizations = aggregate.split(","); //
		boolean first = true;
		for (final String groupRealization : groupRealizations)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				sb.append('\n');
			}

			Spanner.appendImage(sb, BasicModule.this.realizationDrawable);

			// fe.pt.gf
			final String[] components = groupRealization.split("\\."); //
			if (components.length > 0)
			{
				Spanner.append(sb, components[0], 0, FrameNetFactories.feFactory);
			}
			sb.append(' ');
			if (components.length > 1)
			{
				// pt
				Spanner.append(sb, processPT(components[1]), 0, FrameNetFactories.ptFactory);
			}
			sb.append(' ');
			if (components.length > 2)
			{
				// gf
				Spanner.append(sb, components[2], 0, FrameNetFactories.gfFactory);
			}
		}
		return sb;
	}

	// sentences

	/**
	 * Sentences for lex unit
	 *
	 * @param luId   lex unit id
	 * @param parent parent node
	 */
	private void sentencesForLexUnit(final long luId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(LexUnits_Sentences_AnnoSets_Layers_Labels.CONTENT_URI_BY_SENTENCE);
				final String[] projection = { //
						LexUnits_Sentences_AnnoSets_Layers_Labels.SENTENCEID, //
						LexUnits_Sentences_AnnoSets_Layers_Labels.TEXT, //
						LexUnits_Sentences_AnnoSets_Layers_Labels.LAYERTYPE, //
						LexUnits_Sentences_AnnoSets_Layers_Labels.RANK, //
						"GROUP_CONCAT(" + //
								LexUnits_Sentences_AnnoSets_Layers_Labels.START + "||':'||" + //
								LexUnits_Sentences_AnnoSets_Layers_Labels.END + "||':'||" + //
								LexUnits_Sentences_AnnoSets_Layers_Labels.LABELTYPE + "||':'||" + //
								"CASE WHEN " + LexUnits_Sentences_AnnoSets_Layers_Labels.LABELITYPE + " IS NULL THEN '' ELSE " + LexUnits_Sentences_AnnoSets_Layers_Labels.LABELITYPE + " END||':'||" + //
								"CASE WHEN " + LexUnits_Sentences_AnnoSets_Layers_Labels.BGCOLOR + " IS NULL THEN '' ELSE " + LexUnits_Sentences_AnnoSets_Layers_Labels.BGCOLOR + " END||':'||" + //
								"CASE WHEN " + LexUnits_Sentences_AnnoSets_Layers_Labels.FGCOLOR + " IS NULL THEN '' ELSE " + LexUnits_Sentences_AnnoSets_Layers_Labels.FGCOLOR + " END" + //
								",'|')" + //
								" AS " + LexUnits_Sentences_AnnoSets_Layers_Labels.LAYERANNOTATION, //
				};
				final String selection = FrameNetContract.LU + '.' + LexUnits_Sentences_AnnoSets_Layers_Labels.LUID + " = ? AND " + LexUnits_Sentences_AnnoSets_Layers_Labels.LAYERTYPE + " = ?"; //
				final String[] selectionArgs = {Long.toString(luId), BasicModule.FOCUSLAYER};
				final String sortOrder = LexUnits_Sentences_AnnoSets_Layers_Labels.CORPUSID + ',' + //
						LexUnits_Sentences_AnnoSets_Layers_Labels.DOCUMENTID + ',' + //
						LexUnits_Sentences_AnnoSets_Layers_Labels.PARAGNO + ',' + //
						LexUnits_Sentences_AnnoSets_Layers_Labels.SENTNO; //
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.moveToFirst())
				{
					final SpannableStringBuilder sb = new SpannableStringBuilder();

					// column indices
					final int idText = cursor.getColumnIndex(LexUnits_Sentences_AnnoSets_Layers_Labels.TEXT);
					final int idLayerType = cursor.getColumnIndex(LexUnits_Sentences_AnnoSets_Layers_Labels.LAYERTYPE);
					final int idAnnotations = cursor.getColumnIndex(LexUnits_Sentences_AnnoSets_Layers_Labels.LAYERANNOTATION);
					final int idSentenceId = cursor.getColumnIndex(LexUnits_Sentences_AnnoSets_Layers_Labels.SENTENCEID);

					// read cursor
					do
					{
						final String text = cursor.getString(idText);
						final String layerType = cursor.getString(idLayerType);
						final String annotations = cursor.getString(idAnnotations);
						final long sentenceId = cursor.getLong(idSentenceId);

						// sentence
						Spanner.appendImage(sb, BasicModule.this.sentenceDrawable);
						sb.append(' ');
						final int sentenceStart = sb.length();
						Spanner.append(sb, text, 0, FrameNetFactories.sentenceFactory);
						if (VERBOSE)
						{
							sb.append(Long.toString(sentenceId));
							sb.append(' ');
						}
						sb.append('\n');

						// labels
						final List<FnLabel> labels = Utils.parseLabels(annotations);
						if (labels != null)
						{
							for (final FnLabel label : labels)
							{
								// segment
								final int from = Integer.parseInt(label.from);
								final int to = Integer.parseInt(label.to) + 1;

								// span text
								Spanner.setSpan(sb, sentenceStart + from, sentenceStart + to, 0, "Target".equals(layerType) ? FrameNetFactories.targetFactory : FrameNetFactories.highlightTextFactory); //

								// label
								sb.append('\t');
								Spanner.append(sb, label.label, 0, "FE".equals(layerType) ? FrameNetFactories.feFactory : FrameNetFactories.labelFactory); //
								sb.append(' ');

								// value (subtext)
								final String subtext = text.substring(from, to);
								final int p = sb.length();
								Spanner.append(sb, subtext, 0, FrameNetFactories.subtextFactory);
								if (label.bgColor != null)
								{
									final int color = Integer.parseInt(label.bgColor, 16);
									sb.setSpan(new BackgroundColorSpan(color), p, p + subtext.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
								}
								if (label.fgColor != null)
								{
									final int color = Integer.parseInt(label.fgColor, 16);
									sb.setSpan(new ForegroundColorSpan(color), p, p + subtext.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
								}

								sb.append('\n');
							}
						}
					}
					while (cursor.moveToNext());

					// attach result
					TreeFactory.addTextNode(parent, sb, BasicModule.this.context);

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
	 * Sentences for pattern
	 *
	 * @param patternId pattern id
	 * @param parent    parent node
	 */
	private void sentencesForPattern(final long patternId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(Patterns_Sentences.CONTENT_URI);
				final String[] projection = { //
						Patterns_Sentences.ANNOSETID, //
						Patterns_Sentences.SENTENCEID, //
						Patterns_Sentences.TEXT, //
				};
				final String selection = Patterns_Sentences.PATTERNID + " = ?"; //
				final String[] selectionArgs = {Long.toString(patternId)};
				final String sortOrder = Patterns_Sentences.SENTENCEID;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.moveToFirst())
				{
					// column indices
					final int idAnnotationId = cursor.getColumnIndex(Patterns_Sentences.ANNOSETID);
					final int idText = cursor.getColumnIndex(Patterns_Sentences.TEXT);
					// final int idSentenceId = cursor.getColumnIndex(Patterns_Sentences.SENTENCEID);

					// read cursor
					do
					{
						final SpannableStringBuilder sb = new SpannableStringBuilder();

						final long annotationId = cursor.getLong(idAnnotationId);
						final String text = cursor.getString(idText);
						// final long sentenceId = cursor.getLong(idSentenceId);

						// sentence
						Spanner.append(sb, text, 0, FrameNetFactories.sentenceFactory);

						// annotation id
						if (VERBOSE)
						{
							sb.append(' ');
							sb.append(Long.toString(annotationId));
						}

						// attach annoSet node
						final TreeNode annoSetNode = TreeFactory.newQueryNode(new AnnoSetQuery(annotationId, R.drawable.sentence, sb, false), BasicModule.this.context);
						parent.addChild(annoSetNode);

						// expand
						// TreeView.expand(annoSetNode, false);
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
	 * Sentences for valence unit
	 *
	 * @param vuId   valence unit id
	 * @param parent parent node
	 */
	private void sentencesForValenceUnit(final long vuId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(ValenceUnits_Sentences.CONTENT_URI);
				final String[] projection = { //
						Patterns_Sentences.ANNOSETID, //
						Patterns_Sentences.SENTENCEID, //
						Patterns_Sentences.TEXT, //
				};
				final String selection = ValenceUnits_Sentences.VUID + " = ?"; //
				final String[] selectionArgs = {Long.toString(vuId)};
				final String sortOrder = ValenceUnits_Sentences.SENTENCEID;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.moveToFirst())
				{
					// column indices
					final int idAnnotationId = cursor.getColumnIndex(ValenceUnits_Sentences.ANNOSETID);
					final int idText = cursor.getColumnIndex(ValenceUnits_Sentences.TEXT);
					// final int idSentenceId = cursor.getColumnIndex(Patterns_Sentences.SENTENCEID);

					// read cursor
					do
					{
						final SpannableStringBuilder sb = new SpannableStringBuilder();

						final long annotationId = cursor.getLong(idAnnotationId);
						final String text = cursor.getString(idText);
						// final long sentenceId = cursor.getLong(idSentenceId);

						// sentence
						Spanner.append(sb, text, 0, FrameNetFactories.sentenceFactory);

						// annotation id
						if (VERBOSE)
						{
							sb.append(' ');
							sb.append(Long.toString(annotationId));
						}

						// pattern
						final TreeNode annoSetNode = TreeFactory.newQueryNode(new AnnoSetQuery(annotationId, R.drawable.sentence, sb, false), BasicModule.this.context);
						parent.addChild(annoSetNode);

						// expand
						// TreeView.expand(annoSetNode, false);
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

	// annoSets

	/**
	 * AnnoSet
	 *
	 * @param annoSetId    annoSetId
	 * @param parent       parent node
	 * @param withSentence whether to include sentence
	 */
	void annoSet(final long annoSetId, final TreeNode parent, final boolean withSentence)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(AnnoSets_Layers_X.CONTENT_URI);
				final String[] projection = { //
						AnnoSets_Layers_X.SENTENCEID, //
						AnnoSets_Layers_X.SENTENCETEXT, //
						AnnoSets_Layers_X.LAYERID, //
						AnnoSets_Layers_X.LAYERTYPE, //
						AnnoSets_Layers_X.RANK, //
						AnnoSets_Layers_X.LAYERANNOTATIONS, //
				};
				final String selection = null; // embedded selection
				final String[] selectionArgs = {Long.toString(annoSetId)};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				final SpannableStringBuilder sb = new SpannableStringBuilder();

				if (cursor.moveToFirst())
				{
					// column indices
					final int idLayerType = cursor.getColumnIndex(AnnoSets_Layers_X.LAYERTYPE);
					final int idAnnotations = cursor.getColumnIndex(AnnoSets_Layers_X.LAYERANNOTATIONS);
					final int idSentenceText = cursor.getColumnIndex(AnnoSets_Layers_X.SENTENCETEXT);
					final int idSentenceId = cursor.getColumnIndex(AnnoSets_Layers_X.SENTENCEID);
					final int idRank = cursor.getColumnIndex(AnnoSets_Layers_X.RANK);

					// read cursor
					boolean first = true;
					while (true)
					{
						final String layerType = cursor.getString(idLayerType);
						final String annotations = cursor.getString(idAnnotations);
						final String sentenceText = cursor.getString(idSentenceText);

						// sentence
						if (withSentence && first)
						{
							Spanner.appendImage(sb, BasicModule.this.sentenceDrawable);
							sb.append(' ');
							Spanner.append(sb, sentenceText, 0, FrameNetFactories.sentenceFactory);
							if (VERBOSE)
							{
								final long sentenceId = cursor.getLong(idSentenceId);
								sb.append(' ');
								sb.append(Long.toString(sentenceId));
							}
							sb.append('\n');
							first = false;
						}

						// layer
						Spanner.appendImage(sb, BasicModule.this.layerDrawable);
						sb.append(' ');
						Spanner.append(sb, processLayer(layerType), 0, FrameNetFactories.layerTypeFactory);
						if (VERBOSE)
						{
							final String rank = cursor.getString(idRank);
							sb.append(' ');
							sb.append('[');
							sb.append(rank);
							sb.append(']');
						}

						// annotations
						final List<FnLabel> labels = Utils.parseLabels(annotations);
						if (labels != null)
						{
							for (final FnLabel label : labels)
							{
								sb.append('\n');
								sb.append('\t');
								sb.append('\t');

								// label
								Spanner.append(sb, label.label, 0, "FE".equals(layerType) ? FrameNetFactories.feFactory : FrameNetFactories.labelFactory); //
								sb.append(' ');

								// subtext value
								final int from = Integer.parseInt(label.from);
								final int to = Integer.parseInt(label.to) + 1;
								final String subtext = sentenceText.substring(from, to);
								final int p = sb.length();
								Spanner.append(sb, subtext, 0, FrameNetFactories.subtextFactory);
								if (label.bgColor != null)
								{
									final int color = Integer.parseInt(label.bgColor, 16);
									sb.setSpan(new BackgroundColorSpan(color), p, p + subtext.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
								}
								if (label.fgColor != null)
								{
									final int color = Integer.parseInt(label.fgColor, 16);
									sb.setSpan(new ForegroundColorSpan(color), p, p + subtext.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
								}
							}
						}
						if (!cursor.moveToNext())
						{
							//noinspection BreakStatement
							break;
						}
						sb.append('\n');
					}

					// attach result
					TreeFactory.addTextNode(parent, sb, BasicModule.this.context);

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
	 * AnnoSets for pattern
	 *
	 * @param patternId pattern id
	 * @param parent    parent node
	 */
	void annoSetsForPattern(final long patternId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(Patterns_Layers_X.CONTENT_URI);
				final String[] projection = { //
						Patterns_Layers_X.SENTENCEID, //
						Patterns_Layers_X.SENTENCETEXT, //
						Patterns_Layers_X.LAYERID, //
						Patterns_Layers_X.LAYERTYPE, //
						Patterns_Layers_X.RANK, //
						Patterns_Layers_X.LAYERANNOTATIONS, //
				};
				final String selection = null; // embedded selection
				final String[] selectionArgs = {Long.toString(patternId)};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				final SpannableStringBuilder sb = new SpannableStringBuilder();

				if (cursor.moveToFirst())
				{
					// column indices
					final int idLayerType = cursor.getColumnIndex(Patterns_Layers_X.LAYERTYPE);
					final int idRank = cursor.getColumnIndex(Patterns_Layers_X.RANK);
					final int idAnnotations = cursor.getColumnIndex(Patterns_Layers_X.LAYERANNOTATIONS);
					final int idSentenceId = cursor.getColumnIndex(Patterns_Layers_X.SENTENCEID);
					final int idSentenceText = cursor.getColumnIndex(Patterns_Layers_X.SENTENCETEXT);

					// read cursor
					long focusSentenceId = -1;
					do
					{
						final String layerType = cursor.getString(idLayerType);
						final String annotations = cursor.getString(idAnnotations);
						final String rank = cursor.getString(idRank);
						final long sentenceId = cursor.getLong(idSentenceId);
						final String sentenceText = cursor.getString(idSentenceText);

						// sentence
						if (sentenceId != focusSentenceId)
						{
							Spanner.appendImage(sb, BasicModule.this.sentenceDrawable);
							sb.append(' ');
							Spanner.append(sb, sentenceText, 0, FrameNetFactories.sentenceFactory);
							sb.append('\n');
							focusSentenceId = sentenceId;
						}

						// layer
						sb.append('\t');
						Spanner.appendImage(sb, BasicModule.this.layerDrawable);
						sb.append(' ');
						Spanner.append(sb, processLayer(layerType), 0, FrameNetFactories.layerTypeFactory);
						if (VERBOSE)
						{
							sb.append(' ');
							sb.append('[');
							sb.append(rank);
							sb.append(']');
						}

						// annotations
						if (annotations != null)
						{
							final List<FnLabel> labels = Utils.parseLabels(annotations);
							if (labels != null)
							{
								for (final FnLabel label : labels)
								{
									sb.append('\n');
									sb.append('\t');
									sb.append('\t');
									sb.append('\t');

									Spanner.append(sb, label.label, 0, "FE".equals(layerType) ? FrameNetFactories.feFactory : FrameNetFactories.labelFactory); //
									sb.append(' ');

									final int from = Integer.parseInt(label.from);
									final int to = Integer.parseInt(label.to) + 1;
									final String subtext = sentenceText.substring(from, to);
									final int p = sb.length();
									Spanner.append(sb, subtext, 0, FrameNetFactories.subtextFactory);
									if (label.bgColor != null)
									{
										final int color = Integer.parseInt(label.bgColor, 16);
										sb.setSpan(new BackgroundColorSpan(color), p, p + subtext.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
									}
									if (label.fgColor != null)
									{
										final int color = Integer.parseInt(label.fgColor, 16);
										sb.setSpan(new ForegroundColorSpan(color), p, p + subtext.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
									}
								}
							}
						}
						sb.append('\n');
					}
					while (cursor.moveToNext());

					// attach result
					TreeFactory.addTextNode(parent, sb, BasicModule.this.context);

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
	 * AnnoSets for valence unit
	 *
	 * @param vuId   valence unit id
	 * @param parent parent node
	 */
	void annoSetsForValenceUnit(final long vuId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(ValenceUnits_Layers_X.CONTENT_URI);
				final String[] projection = { //
						ValenceUnits_Layers_X.SENTENCEID, //
						ValenceUnits_Layers_X.SENTENCETEXT, //
						ValenceUnits_Layers_X.LAYERID, //
						ValenceUnits_Layers_X.LAYERTYPE, //
						ValenceUnits_Layers_X.RANK, //
						ValenceUnits_Layers_X.LAYERANNOTATIONS, //
				};
				final String selection = null; // embedded selection
				final String[] selectionArgs = {Long.toString(vuId)};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				final SpannableStringBuilder sb = new SpannableStringBuilder();

				if (cursor.moveToFirst())
				{
					// column indices
					final int idLayerType = cursor.getColumnIndex(ValenceUnits_Layers_X.LAYERTYPE);
					final int idRank = cursor.getColumnIndex(ValenceUnits_Layers_X.RANK);
					final int idAnnotations = cursor.getColumnIndex(ValenceUnits_Layers_X.LAYERANNOTATIONS);
					final int idSentenceId = cursor.getColumnIndex(ValenceUnits_Layers_X.SENTENCEID);
					final int idSentenceText = cursor.getColumnIndex(ValenceUnits_Layers_X.SENTENCETEXT);

					// read cursor
					long focusSentenceId = -1;
					do
					{
						final String layerType = cursor.getString(idLayerType);
						final String annotations = cursor.getString(idAnnotations);
						final String rank = cursor.getString(idRank);
						final long sentenceId = cursor.getLong(idSentenceId);
						final String sentenceText = cursor.getString(idSentenceText);

						// sentence
						if (sentenceId != focusSentenceId)
						{
							Spanner.appendImage(sb, BasicModule.this.sentenceDrawable);
							sb.append(' ');
							Spanner.append(sb, sentenceText, 0, FrameNetFactories.sentenceFactory);
							sb.append('\n');
							focusSentenceId = sentenceId;
						}

						// layer
						sb.append('\t');
						Spanner.appendImage(sb, BasicModule.this.layerDrawable);
						sb.append(' ');
						Spanner.append(sb, processLayer(layerType), 0, FrameNetFactories.layerTypeFactory);
						if (VERBOSE)
						{
							sb.append(' ');
							sb.append('[');
							sb.append(rank);
							sb.append(']');
						}
						// annotations
						if (annotations != null)
						{
							final List<FnLabel> labels = Utils.parseLabels(annotations);
							if (labels != null)
							{
								for (final FnLabel label : labels)
								{
									sb.append('\n');
									sb.append('\t');
									sb.append('\t');
									sb.append('\t');

									// label
									Spanner.append(sb, label.label, 0, "FE".equals(layerType) ? FrameNetFactories.feFactory : FrameNetFactories.labelFactory); //
									sb.append(' ');

									// subtext
									final int from = Integer.parseInt(label.from);
									final int to = Integer.parseInt(label.to) + 1;
									final String subtext = sentenceText.substring(from, to);
									final int p = sb.length();
									Spanner.append(sb, subtext, 0, FrameNetFactories.subtextFactory);
									if (label.bgColor != null)
									{
										final int color = Integer.parseInt(label.bgColor, 16);
										sb.setSpan(new BackgroundColorSpan(color), p, p + subtext.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
									}
									if (label.fgColor != null)
									{
										final int color = Integer.parseInt(label.fgColor, 16);
										sb.setSpan(new ForegroundColorSpan(color), p, p + subtext.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
									}
								}
							}
						}
						sb.append('\n');
					}
					while (cursor.moveToNext());

					// attach result
					TreeFactory.addTextNode(parent, sb, BasicModule.this.context);

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

	// layers

	/**
	 * Layers for sentence
	 *
	 * @param sentenceId sentence id
	 * @param text       reference sentence text
	 * @param parent     parent node
	 */
	void layersForSentence(final long sentenceId, final String text, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(Sentences_Layers_X.CONTENT_URI);
				final String[] projection = { //
						Sentences_Layers_X.LAYERID, //
						Sentences_Layers_X.LAYERTYPE, //
						Sentences_Layers_X.RANK, //
						Sentences_Layers_X.LAYERANNOTATIONS, //
				};
				final String selection = null; // embedded selection
				final String[] selectionArgs = {Long.toString(sentenceId)};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				final SpannableStringBuilder sb = new SpannableStringBuilder();

				if (cursor.moveToFirst())
				{
					// column indices
					final int idLayerType = cursor.getColumnIndex(Sentences_Layers_X.LAYERTYPE);
					final int idRank = cursor.getColumnIndex(Sentences_Layers_X.RANK);
					final int idAnnotations = cursor.getColumnIndex(Sentences_Layers_X.LAYERANNOTATIONS);

					// read cursor
					while (true)
					{
						final String layerType = cursor.getString(idLayerType);
						final String annotations = cursor.getString(idAnnotations);
						final String rank = cursor.getString(idRank);

						// sentence
						Spanner.appendImage(sb, BasicModule.this.layerDrawable);
						sb.append(' ');
						Spanner.append(sb, processLayer(layerType), 0, FrameNetFactories.layerTypeFactory);
						if (VERBOSE)
						{
							sb.append(' ');
							sb.append('[');
							sb.append(rank);
							sb.append(']');
							sb.append('\n');
						}

						// annotations
						final List<FnLabel> labels = Utils.parseLabels(annotations);
						if (labels != null)
						{
							for (final FnLabel label : labels)
							{
								sb.append('\n');
								sb.append('\t');
								sb.append('\t');

								// label
								Spanner.append(sb, label.label, 0, "FE".equals(layerType) ? FrameNetFactories.feFactory : FrameNetFactories.labelFactory); //
								sb.append(' ');

								// subtext value
								final int from = Integer.parseInt(label.from);
								final int to = Integer.parseInt(label.to) + 1;
								final String subtext = text.substring(from, to);
								final int p = sb.length();
								Spanner.append(sb, subtext, 0, FrameNetFactories.subtextFactory);
								if (label.bgColor != null)
								{
									final int color = Integer.parseInt(label.bgColor, 16);
									sb.setSpan(new BackgroundColorSpan(color), p, p + subtext.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
								}
								if (label.fgColor != null)
								{
									final int color = Integer.parseInt(label.fgColor, 16);
									sb.setSpan(new ForegroundColorSpan(color), p, p + subtext.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
								}
							}
						}
						if (!cursor.moveToNext())
						{
							//noinspection BreakStatement
							break;
						}
						sb.append('\n');
					}

					// attach result
					TreeFactory.addTextNode(parent, sb, BasicModule.this.context);

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

	// agents

	/**
	 * Process definition
	 *
	 * @param text  text
	 * @param flags flags
	 * @return processed definition
	 */
	private CharSequence[] processDefinition(final CharSequence text, final long flags)
	{
		CharSequence[] texts = this.processor.split(text);
		CharSequence[] fields = new CharSequence[texts.length];

		for (int i = 0; i < texts.length; i++)
		{
			CharSequence field = (flags & FrameNetMarkupFactory.FEDEF) == 0 ? this.frameProcessor.process(texts[i]) : texts[i];
			fields[i] = this.spanner.process(field, flags);
		}
		return fields;
	}

	/**
	 * Process layer name
	 *
	 * @param name layer name
	 * @return processed layer name
	 */
	private CharSequence processLayer(final CharSequence name)
	{
		if ("FE".equals(name)) //
		{
			return "Frame element"; //
		}
		if ("PT".equals(name)) //
		{
			return "Phrase type"; //
		}
		if ("GF".equals(name)) //
		{
			return "Grammatical function"; //
		}
		return name;
	}

	/**
	 * Process PT
	 *
	 * @param name PT name
	 * @return processed PT
	 */
	private CharSequence processPT(final CharSequence name)
	{
		if ("CNI".equals(name)) //
		{
			return "constructional ∅"; //
		}
		if ("DNI".equals(name)) //
		{
			return "definite ∅"; //
		}
		if ("INI".equals(name)) //
		{
			return "indefinite ∅"; //
		}
		return name;
	}

	// Q U E R I E S

	/**
	 * Frame query data
	 */
	class FrameQueryData extends QueryRenderer.QueryData
	{
		public FrameQueryData(final long frameId, final int icon, final CharSequence text)
		{
			super(frameId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			frame((int) this.id, node);
		}
	}

	/**
	 * Related frame query data
	 */
	class RelatedQueryData extends QueryRenderer.QueryData
	{
		public RelatedQueryData(final long frameId, final int icon, final CharSequence text)
		{
			super(frameId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			relatedFrames((int) this.id, node);
		}
	}

	/**
	 * Lex units query data
	 */
	class LexUnitsQueryData extends QueryRenderer.QueryData
	{
		public LexUnitsQueryData(final long frameId, final int icon, final CharSequence text)
		{
			super(frameId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			lexUnitsForFrame((int) this.id, node, false);
		}
	}

	/**
	 * Frame elements query data
	 */
	class FEsQueryData extends QueryRenderer.QueryData
	{
		public FEsQueryData(final long frameId, final int icon, final CharSequence text)
		{
			super(frameId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			fesForFrame((int) this.id, node);
		}
	}

	/**
	 * Governors query data
	 */
	class GovernorsQueryData extends QueryRenderer.QueryData
	{
		public GovernorsQueryData(final long luId, final int icon, final CharSequence text)
		{
			super(luId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			governorsForLexUnit(this.id, node);
		}
	}

	/**
	 * Realizations query data
	 */
	class RealizationsQueryData extends QueryRenderer.QueryData
	{
		public RealizationsQueryData(final long luId, final int icon, final CharSequence text)
		{
			super(luId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			realizationsForLexicalunit(this.id, node);
		}
	}

	/**
	 * Group realizations query data
	 */
	class GroupRealizationsQueryData extends QueryRenderer.QueryData
	{
		public GroupRealizationsQueryData(final long luId, final int icon, final CharSequence text)
		{
			super(luId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			groupRealizationsForLexUnit(this.id, node);
		}
	}

	/**
	 * Sentences for pattern query data
	 */
	class SentencesForPatternQueryData extends QueryRenderer.QueryData
	{
		public SentencesForPatternQueryData(final long patternId, final int icon, final CharSequence text)
		{
			super(patternId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			sentencesForPattern(this.id, node);
		}
	}

	/**
	 * Sentences for valence unit query data
	 */
	class SentencesForValenceUnitQueryData extends QueryRenderer.QueryData
	{
		public SentencesForValenceUnitQueryData(final long vuId, final int icon, final CharSequence text)
		{
			super(vuId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			sentencesForValenceUnit(this.id, node);
		}
	}

	/**
	 * Sentences for lex unit query
	 */
	class SentencesForLexUnitQueryData extends QueryRenderer.QueryData
	{
		public SentencesForLexUnitQueryData(final long luId, final int icon, final CharSequence text)
		{
			super(luId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			sentencesForLexUnit(this.id, node);
		}
	}

	/**
	 * AnnoSet query data
	 */
	class AnnoSetQuery extends QueryRenderer.QueryData
	{
		private final boolean withSentence;

		public AnnoSetQuery(final long annoSetId, final int icon, final CharSequence text, final boolean withSentence)
		{
			super(annoSetId, icon, text);
			this.withSentence = withSentence;
		}

		@Override
		public void process(final TreeNode node)
		{
			annoSet(this.id, node, this.withSentence);
		}
	}

	/**
	 * AnnoSets for governor query data
	 */
	class AnnoSetsForGovernorQueryData extends QueryRenderer.QueryData
	{
		public AnnoSetsForGovernorQueryData(final long governorId, final int icon, final CharSequence text)
		{
			super(governorId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			annoSetsForGovernor(this.id, node);
		}
	}

	/**
	 * Dummy query data
	 */
	class DummyQueryData extends QueryRenderer.QueryData
	{
		private static final String TAG = "DummyQueryData"; //

		@SuppressWarnings("unused")
		public DummyQueryData(final long annoSetId, final int icon, final CharSequence text)
		{
			super(annoSetId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			Log.d(TAG, "QUERY " + this.id); //
		}
	}
}
