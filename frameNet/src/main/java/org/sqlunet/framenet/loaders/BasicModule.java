package org.sqlunet.framenet.loaders;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import org.sqlunet.browser.Module;
import org.sqlunet.framenet.R;
import org.sqlunet.framenet.Utils;
import org.sqlunet.framenet.provider.FrameNetContract.AnnoSets_Layers_X;
import org.sqlunet.framenet.provider.FrameNetContract.Frames_FEs;
import org.sqlunet.framenet.provider.FrameNetContract.Frames_Related;
import org.sqlunet.framenet.provider.FrameNetContract.Frames_X;
import org.sqlunet.framenet.provider.FrameNetContract.Governors_AnnoSets_Sentences;
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits;
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_FERealizations_ValenceUnits;
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_Governors;
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_Sentences_Annosets_Layers_Labels;
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
import org.sqlunet.treeview.renderer.QueryHolder;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.view.TreeFactory;

import java.util.List;
import java.util.Locale;

/**
 * A module to retrieve frame
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract public class BasicModule extends Module
{
	private static boolean VERBOSE = false;

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

	// private static final String TAG = "BasicModule";

	// target layer

	private static final String FOCUSLAYER = "FE"; // "Target"; //$NON-NLS-1$

	// agents

	/**
	 * Processor
	 */
	private FrameNetProcessor processor;

	/**
	 * Frame Processor
	 */
	private FrameNetFrameProcessor frameProcessor;

	/**
	 * Spanner
	 */
	private FrameNetSpanner spanner;

	// resources

	/**
	 * Drawable for frame
	 */
	private Drawable frameDrawable;

	/**
	 * Drawable for FE
	 */
	private Drawable feDrawable;

	/**
	 * Drawable for FE
	 */
	private Drawable fe2Drawable;

	/**
	 * Drawable for core FE
	 */
	private Drawable corefeDrawable;

	/**
	 * Drawable for lexunit
	 */
	private Drawable lexunitDrawable;

	/**
	 * Drawable for definition
	 */
	private Drawable definitionDrawable;

	/**
	 * Drawable for meta definition
	 */
	private Drawable metadefinitionDrawable;

	/**
	 * Drawable for realization
	 */
	private Drawable realizationDrawable;

	/**
	 * Drawable for sentence
	 */
	private Drawable sentenceDrawable;

	/**
	 * Drawable for semtype
	 */
	private Drawable semtypeDrawable;

	/**
	 * Drawable for coreset
	 */
	private Drawable coresetDrawable;

	/**
	 * Drawable for layer
	 */
	private Drawable layerDrawable;

	// C R E A T I O N

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	BasicModule(final Fragment fragment)
	{
		super(fragment);
	}

	@Override
	public void init(final Parcelable arguments)
	{
		super.init(arguments);

		// drawables
		this.frameDrawable = Spanner.getDrawable(getContext(), R.drawable.fnframe);
		this.feDrawable = Spanner.getDrawable(getContext(), R.drawable.role);
		this.fe2Drawable = Spanner.getDrawable(getContext(), R.drawable.role2);
		this.corefeDrawable = Spanner.getDrawable(getContext(), R.drawable.corerole);
		this.lexunitDrawable = Spanner.getDrawable(getContext(), R.drawable.lexunit);
		this.definitionDrawable = Spanner.getDrawable(getContext(), R.drawable.definition);
		this.metadefinitionDrawable = Spanner.getDrawable(getContext(), R.drawable.metadefinition);
		this.sentenceDrawable = Spanner.getDrawable(getContext(), R.drawable.sentence);
		this.realizationDrawable = Spanner.getDrawable(getContext(), R.drawable.realization);
		this.semtypeDrawable = Spanner.getDrawable(getContext(), R.drawable.semtype);
		this.coresetDrawable = Spanner.getDrawable(getContext(), R.drawable.coreset);
		this.layerDrawable = Spanner.getDrawable(getContext(), R.drawable.layer);

		// create agents
		this.processor = new FrameNetProcessor();
		this.frameProcessor = new FrameNetFrameProcessor();
		this.spanner = new FrameNetSpanner(getContext());
	}

	// L O A D E R S

	// frame

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
				final String selection = Frames_X.FRAMEID + " = ?"; //$NON-NLS-1$
				final String[] selectionArgs = {Long.toString(frameId)};
				final String sortOrder = Frames_X.FRAME;
				return new CursorLoader(BasicModule.this.getContext(), uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.getCount() > 1)
				{
					throw new RuntimeException("Unexpected number of rows"); //$NON-NLS-1$
				}
				if (cursor.moveToFirst())
				{
					final SpannableStringBuilder sb = new SpannableStringBuilder();

					// column indices
					final int idFrameId = cursor.getColumnIndex(Frames_X.FRAMEID);
					final int idFrame = cursor.getColumnIndex(Frames_X.FRAME);
					final int idFrameDefinition = cursor.getColumnIndex(Frames_X.FRAMEDEFINITION);

					// data
					final int frameId = cursor.getInt(idFrameId);

					// frame
					Spanner.appendImage(sb, BasicModule.this.frameDrawable);
					sb.append(' ');
					Spanner.append(sb, cursor.getString(idFrame), 0, FrameNetFactories.frameFactory);
					if (VERBOSE)
					{
						sb.append(' ');
						sb.append(Integer.toString(frameId));
					}
					sb.append('\n');

					// definition
					Spanner.appendImage(sb, BasicModule.this.metadefinitionDrawable);
					sb.append(' ');
					final String frameDefinition = cursor.getString(idFrameDefinition);
					final CharSequence[] frameDefinitionFields = processDefinition(frameDefinition, 0);
					Spanner.append(sb, frameDefinitionFields[0], 0, FrameNetFactories.metadefinitionFactory);
					if (frameDefinitionFields.length > 1)
					{
						sb.append('\n');
						sb.append(frameDefinitionFields[1]);
					}

					// sub nodes
					final TreeNode fesNode = TreeFactory.newQueryNode(new FEsQuery(frameId, R.drawable.roles, "Frame Elements"), BasicModule.this.getContext()); //$NON-NLS-1$
					final TreeNode sentencesNode = TreeFactory.newQueryNode(new LexUnitsQuery(frameId, R.drawable.lexunit, "Lex Units"), BasicModule.this.getContext()); //$NON-NLS-1$
					final TreeNode relatedNode = TreeFactory.newQueryNode(new RelatedQuery(frameId, R.drawable.fnframe, "Related"), BasicModule.this.getContext()); //$NON-NLS-1$

					// attach result
					TreeFactory.addTextNode(parent, sb, BasicModule.this.getContext(), fesNode, sentencesNode, relatedNode);

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					parent.disable();
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

	// frame

	private void frame_related(final long frameId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(Frames_Related.CONTENT_URI);
				final String[] projection = { //
						Frames_Related.FRAMEID + " AS " + "i1", // //$NON-NLS-1$ //$NON-NLS-2$
						Frames_Related.FRAME + " AS " + "f1", // //$NON-NLS-1$ //$NON-NLS-2$
						Frames_Related.FRAME2ID + " AS " + "i2", // //$NON-NLS-1$ //$NON-NLS-2$
						Frames_Related.FRAME2 + " AS " + "f2", // //$NON-NLS-1$ //$NON-NLS-2$
						Frames_Related.RELATIONID, //
						Frames_Related.RELATION, //
				};
				final String selection = Frames_Related.FRAMEID + " = ?" + " OR " + Frames_Related.FRAME2ID + " = ?"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				final String[] selectionArgs = {Long.toString(frameId), Long.toString(frameId)};
				final String sortOrder = Frames_Related.FRAME;
				return new CursorLoader(BasicModule.this.getContext(), uri, projection, selection, selectionArgs, sortOrder);
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
					final int idFrameId = cursor.getColumnIndex("i1"); //$NON-NLS-1$
					final int idFrame = cursor.getColumnIndex("f1"); //$NON-NLS-1$
					final int idFrame2Id = cursor.getColumnIndex("i2"); //$NON-NLS-1$
					final int idFrame2 = cursor.getColumnIndex("f2"); //$NON-NLS-1$
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
							sb.append("it"); //$NON-NLS-1$
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
						if ("see also".equals(relation)) //$NON-NLS-1$
						{
							relation = "has see-also relation to"; //$NON-NLS-1$
						}
						if ("perspective on".equals(relation)) //$NON-NLS-1$
						{
							relation = "perpectivizes"; //$NON-NLS-1$
						}
						if ("subframe of".equals(relation)) //$NON-NLS-1$
						{
							relation = "is subframe of"; //$NON-NLS-1$
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
							sb.append("it"); //$NON-NLS-1$
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
					TreeFactory.addTextNode(parent, sb, BasicModule.this.getContext());

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					parent.disable();
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

	private void fes_for_frame(final int frameId, final TreeNode parent)
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
						"GROUP_CONCAT(" + Frames_FEs.SEMTYPE + ",'|') AS " + Frames_FEs.SEMTYPES, // //$NON-NLS-1$ //$NON-NLS-2$
						Frames_FEs.CORETYPEID, //
						Frames_FEs.CORETYPE, //
						Frames_FEs.CORESET, //
				};
				final String selection = Frames_FEs.FRAMEID + " = ? "; //$NON-NLS-1$
				final String[] selectionArgs = {Integer.toString(frameId)};
				final String sortOrder = Frames_FEs.CORETYPEID + ',' + Frames_FEs.FETYPE;
				return new CursorLoader(BasicModule.this.getContext(), uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				final SpannableStringBuilder sb = new SpannableStringBuilder();

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
					while (true)
					{
						final String feType = cursor.getString(idFeType);
						final String feAbbrev = cursor.getString(idFeAbbrev);
						final String feDefinition = cursor.getString(idDefinition).trim().replaceAll("\n+", "\n").replaceAll("\n$", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						final String feSemTypes = cursor.getString(idSemTypes);
						final boolean isInCoreSet = !cursor.isNull(idCoreset);
						final int coreTypeId = cursor.getInt(idCoreTypeId);
						final String coreType = cursor.getString(idCoreType);

						// fe
						Spanner.appendImage(sb, coreTypeId == 1 ? BasicModule.this.corefeDrawable : BasicModule.this.feDrawable);
						sb.append(' ');
						Spanner.append(sb, feType, 0, FrameNetFactories.feFactory);
						sb.append(' ');
						Spanner.append(sb, feAbbrev, 0, FrameNetFactories.feAbbrevFactory);

						// fe definition
						sb.append('\n');
						final CharSequence[] frameDefinitionFields = processDefinition(feDefinition, FrameNetMarkupFactory.FEDEF);
						sb.append('\t');
						Spanner.appendImage(sb, BasicModule.this.metadefinitionDrawable);
						sb.append(' ');
						Spanner.append(sb, frameDefinitionFields[0], 0, FrameNetFactories.metadefinitionFactory);
						if (frameDefinitionFields.length > 1)
						{
							sb.append('\n');
							sb.append('\t');
							sb.append(frameDefinitionFields[1]);
						}

						// core type
						sb.append('\n');
						sb.append('\t');
						Spanner.appendImage(sb, BasicModule.this.coresetDrawable);
						sb.append(' ');
						sb.append(coreType);

						// coreset
						if (isInCoreSet)
						{
							final int coreset = cursor.getInt(idCoreset);
							sb.append('\n');
							sb.append('\t');
							Spanner.appendImage(sb, BasicModule.this.coresetDrawable);
							sb.append("[coreset] "); //$NON-NLS-1$
							sb.append(Integer.toString(coreset));
						}

						// sem types
						if (feSemTypes != null)
						{
							sb.append('\n');
							sb.append('\t');
							Spanner.appendImage(sb, BasicModule.this.semtypeDrawable);
							sb.append(' ');
							sb.append(feSemTypes);
						}

						if (!cursor.moveToNext())
						{
							//noinspection BreakStatement
							break;
						}

						sb.append('\n');
					}

					// attach result
					TreeFactory.addTextNode(parent, sb, BasicModule.this.getContext());

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					parent.disable();
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

	void lexunit(final long luId, final TreeNode parent, @SuppressWarnings("SameParameterValue") final boolean withFrame, @SuppressWarnings("SameParameterValue") final boolean withFEs)
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
						"lu." + LexUnits_X.FRAMEID, // //$NON-NLS-1$
						"lu." + LexUnits_X.POSID, // //$NON-NLS-1$
						LexUnits_X.LUDEFINITION, //
						LexUnits_X.LUDICT, //
						LexUnits_X.INCORPORATEDFETYPE, //
						LexUnits_X.INCORPORATEDFEDEFINITION, //
				};
				final String selection = LexUnits_X.LUID + " = ?"; //$NON-NLS-1$
				final String[] selectionArgs = {Long.toString(luId)};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.getContext(), uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.getCount() > 1)
				{
					throw new RuntimeException("Unexpected number of rows"); //$NON-NLS-1$
				}
				if (cursor.moveToFirst())
				{
					final SpannableStringBuilder sb = new SpannableStringBuilder();

					// column indices
					final int idLuId = cursor.getColumnIndex(LexUnits_X.LUID);
					final int idLexUnit = cursor.getColumnIndex(LexUnits_X.LEXUNIT);
					final int idDefinition = cursor.getColumnIndex(LexUnits_X.LUDEFINITION);
					final int idDictionary = cursor.getColumnIndex(LexUnits_X.LUDICT);
					final int idFrameId = cursor.getColumnIndex(LexUnits_X.FRAMEID);
					final int idIncorporatedFEType = cursor.getColumnIndex(LexUnits_X.INCORPORATEDFETYPE);
					final int idIncorporatedFEDefinition = cursor.getColumnIndex(LexUnits_X.INCORPORATEDFEDEFINITION);

					// data
					final int luId = cursor.getInt(idLuId);
					final int frameId = cursor.getInt(idFrameId);
					final String definition = cursor.getString(idDefinition);
					final String dictionary = cursor.getString(idDictionary);
					final String incorporatedFEType = cursor.getString(idIncorporatedFEType);
					final String incorporatedFEDefinition = cursor.getString(idIncorporatedFEDefinition);

					// lexunit
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
					sb.append(' ');
					sb.append('[');
					sb.append(dictionary);
					sb.append(']');

					// incorporated fe
					if (incorporatedFEType != null)
					{
						sb.append('\n');
						Spanner.appendImage(sb, BasicModule.this.fe2Drawable);
						sb.append(' ');
						sb.append("Incorporated"); //$NON-NLS-1$
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
					final TreeNode governorsNode = TreeFactory.newQueryNode(new GovernorsQuery(luId, R.drawable.governor, "Governors"), BasicModule.this.getContext()); //$NON-NLS-1$
					final TreeNode realizationsNode = TreeFactory.newQueryNode(new RealizationsQuery(luId, R.drawable.realization, "Realizations"), BasicModule.this.getContext()); //$NON-NLS-1$
					final TreeNode groupRealizationsNode = TreeFactory.newQueryNode(new GroupRealizationsQuery(luId, R.drawable.grouprealization, "Group realizations"), BasicModule.this.getContext()); //$NON-NLS-1$
					final TreeNode sentencesNode = TreeFactory.newQueryNode(new SentencesQuery(luId, R.drawable.sentence, "Sentences"), BasicModule.this.getContext()); //$NON-NLS-1$

					// attach result
					if (withFrame)
					{
						final TreeNode frameNode = TreeFactory.newQueryNode(new FrameQuery(frameId, R.drawable.fnframe, "Frame"), BasicModule.this.getContext()); //$NON-NLS-1$
						if (withFEs)
						{
							final TreeNode fesNode = TreeFactory.newQueryNode(new FEsQuery(frameId, R.drawable.roles, "Frame Elements"), BasicModule.this.getContext()); //$NON-NLS-1$
							TreeFactory.addTextNode(parent, sb, BasicModule.this.getContext(), frameNode, fesNode, governorsNode, realizationsNode, groupRealizationsNode, sentencesNode);
						}
						else
						{
							TreeFactory.addTextNode(parent, sb, BasicModule.this.getContext(), frameNode, governorsNode, realizationsNode, groupRealizationsNode, sentencesNode);
						}

					}
					else
					{
						TreeFactory.addTextNode(parent, sb, BasicModule.this.getContext(), governorsNode, realizationsNode, groupRealizationsNode, sentencesNode);
					}

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					parent.disable();
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

	private void lexunits_for_frame(final long frameId, final TreeNode parent, @SuppressWarnings("SameParameterValue") final boolean withFrame)
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
						"lu." + LexUnits_X.FRAMEID, // //$NON-NLS-1$
						"lu." + LexUnits_X.POSID, // //$NON-NLS-1$
						LexUnits_X.LUDEFINITION, //
						LexUnits_X.LUDICT, //
						LexUnits_X.INCORPORATEDFETYPE, //
						LexUnits_X.INCORPORATEDFEDEFINITION, //
				};
				final String selection = "f." + LexUnits_X.FRAMEID + " = ?"; //$NON-NLS-1$ //$NON-NLS-2$
				final String[] selectionArgs = {Long.toString(frameId)};
				final String sortOrder = LexUnits_X.LEXUNIT;
				return new CursorLoader(BasicModule.this.getContext(), uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.moveToFirst())
				{
					// column indices
					final int idFrameId = cursor.getColumnIndex(LexUnits_X.FRAMEID);
					final int idLuId = cursor.getColumnIndex(LexUnits_X.LUID);
					final int idLexUnit = cursor.getColumnIndex(LexUnits_X.LEXUNIT);
					final int idDefinition = cursor.getColumnIndex(LexUnits_X.LUDEFINITION);
					final int idIncorporatedFEType = cursor.getColumnIndex(LexUnits_X.INCORPORATEDFETYPE);
					final int idIncorporatedFEDefinition = cursor.getColumnIndex(LexUnits_X.INCORPORATEDFEDEFINITION);

					// data
					do
					{
						final SpannableStringBuilder sb = new SpannableStringBuilder();

						final int frameId = cursor.getInt(idFrameId);
						final long luId = cursor.getLong(idLuId);
						final String lexUnit = cursor.getString(idLexUnit);
						final String incorporatedFEType = cursor.getString(idIncorporatedFEType);
						final String incorporatedFEDefinition = cursor.getString(idIncorporatedFEDefinition);

						// lexunit
						Spanner.appendImage(sb, BasicModule.this.lexunitDrawable);
						sb.append(' ');
						Spanner.append(sb, lexUnit, 0, FrameNetFactories.lexunitFactory);
						if (VERBOSE)
						{
							sb.append(' ');
							sb.append(Long.toString(luId));
						}

						// definition
						sb.append('\n');
						Spanner.appendImage(sb, BasicModule.this.definitionDrawable);
						sb.append(' ');
						Spanner.append(sb, cursor.getString(idDefinition).trim(), 0, FrameNetFactories.definitionFactory);

						if (incorporatedFEType != null)
						{
							sb.append('\n');
							Spanner.appendImage(sb, BasicModule.this.fe2Drawable);
							sb.append(' ');
							sb.append("Incorporated"); //$NON-NLS-1$
							sb.append(' ');
							Spanner.append(sb, incorporatedFEType, 0, FrameNetFactories.fe2Factory);
							if (incorporatedFEDefinition != null)
							{
								sb.append(' ');
								sb.append('-');
								sb.append(' ');
								final CharSequence[] definitionFields = processDefinition(incorporatedFEDefinition, FrameNetMarkupFactory.FEDEF);
								Spanner.append(sb, definitionFields[0], 0, FrameNetFactories.definitionFactory);
								// if (definitionFields.length > 1)
								// {
								// sb.append('\n');
								// sb.append('\t');
								// sb.append(definitionFields[1]);
								// }
							}
						}

						// sub nodes
						final TreeNode governorsNode = TreeFactory.newQueryNode(new GovernorsQuery(luId, R.drawable.governor, "Governors"), BasicModule.this.getContext()); //$NON-NLS-1$
						final TreeNode realizationsNode = TreeFactory.newQueryNode(new RealizationsQuery(luId, R.drawable.realization, "Realizations"), BasicModule.this.getContext()); //$NON-NLS-1$
						final TreeNode groupRealizationsNode = TreeFactory.newQueryNode(new GroupRealizationsQuery(luId, R.drawable.grouprealization, "Group realizations"), BasicModule.this.getContext()); //$NON-NLS-1$
						final TreeNode sentencesNode = TreeFactory.newQueryNode(new SentencesQuery(luId, R.drawable.sentence, "Sentences"), BasicModule.this.getContext()); //$NON-NLS-1$

						// attach result
						if (withFrame)
						{
							final TreeNode frameNode = TreeFactory.newQueryNode(new FrameQuery(frameId, R.drawable.fnframe, "Frame"), BasicModule.this.getContext()); //$NON-NLS-1$
							final TreeNode fesNode = TreeFactory.newQueryNode(new FEsQuery(frameId, R.drawable.roles, "Frame Elements"), BasicModule.this.getContext()); //$NON-NLS-1$
							TreeFactory.addTextNode(parent, sb, BasicModule.this.getContext(), frameNode, fesNode, governorsNode, realizationsNode, groupRealizationsNode, sentencesNode);
						}
						else
						{
							TreeFactory.addTextNode(parent, sb, BasicModule.this.getContext(), governorsNode, realizationsNode, groupRealizationsNode, sentencesNode);
						}
					}
					while (cursor.moveToNext());

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					parent.disable();
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

	void lexunits_for_word_pos(final long wordId, final Character pos, final TreeNode parent)
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
						"lu." + Words_LexUnits_Frames.FRAMEID, // //$NON-NLS-1$
						"lu." + Words_LexUnits_Frames.POSID, // //$NON-NLS-1$
						Words_LexUnits_Frames.LUDEFINITION, //
						Words_LexUnits_Frames.LUDICT, //
						Words_LexUnits_Frames.INCORPORATEDFETYPE, //
						Words_LexUnits_Frames.INCORPORATEDFEDEFINITION, //
				};
				final String selection = pos == null ? Words_LexUnits_Frames.WORDID + " = ?" : Words_LexUnits_Frames.WORDID + " = ? AND " + "lu." + Words_LexUnits_Frames.POSID + " = ?"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				final String[] selectionArgs = pos == null ? new String[]{Long.toString(wordId)} : new String[]{Long.toString(wordId), Integer.toString(Utils.posToPosId(pos))};
				final String sortOrder = Words_LexUnits_Frames.FRAME + ',' + Words_LexUnits_Frames.LUID;
				return new CursorLoader(BasicModule.this.getContext(), uri, projection, selection, selectionArgs, sortOrder);
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

						// lexunit
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
							Spanner.appendImage(sb, BasicModule.this.fe2Drawable);
							sb.append(' ');
							sb.append("Incorporated"); //$NON-NLS-1$
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
						final TreeNode frameNode = TreeFactory.newQueryNode(new FrameQuery(frameId, R.drawable.fnframe, "Frame"), BasicModule.this.getContext()); //$NON-NLS-1$
						final TreeNode fesNode = TreeFactory.newQueryNode(new FEsQuery(frameId, R.drawable.roles, "Frame Elements"), BasicModule.this.getContext()); //$NON-NLS-1$
						final TreeNode governorsNode = TreeFactory.newQueryNode(new GovernorsQuery(luId, R.drawable.governor, "Governors"), BasicModule.this.getContext()); //$NON-NLS-1$
						final TreeNode realizationsNode = TreeFactory.newQueryNode(new RealizationsQuery(luId, R.drawable.realization, "Realizations"), BasicModule.this.getContext()); //$NON-NLS-1$
						final TreeNode groupRealizationsNode = TreeFactory.newQueryNode(new GroupRealizationsQuery(luId, R.drawable.grouprealization, "Group realizations"), BasicModule.this.getContext()); //$NON-NLS-1$
						final TreeNode sentencesNode = TreeFactory.newQueryNode(new SentencesQuery(luId, R.drawable.sentence, "Sentences"), BasicModule.this.getContext()); //$NON-NLS-1$

						// attach result
						TreeFactory.addTextNode(parent, sb, BasicModule.this.getContext(), frameNode, fesNode, governorsNode, realizationsNode, groupRealizationsNode, sentencesNode);
					}
					while (cursor.moveToNext());

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					parent.disable();
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

	private void governors_for_lexicalunit(final long luId, final TreeNode parent)
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
				final String selection = LexUnits_Governors.LUID + " = ?"; //$NON-NLS-1$
				final String[] selectionArgs = {Long.toString(luId)};
				final String sortOrder = LexUnits_Governors.GOVERNORID;
				return new CursorLoader(BasicModule.this.getContext(), uri, projection, selection, selectionArgs, sortOrder);
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

						// governor
						final TreeNode governorNode = TreeFactory.newQueryNode(new GovernorsAnnoSetsQuery(governorId, R.drawable.governor, sb), BasicModule.this.getContext());
						parent.addChild(governorNode);
					}
					while (cursor.moveToNext());

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					parent.disable();
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

	private void annosets_for_governor(final long governorId, final TreeNode parent)
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
				final String selection = Governors_AnnoSets_Sentences.GOVERNORID + " = ?"; //$NON-NLS-1$
				final String[] selectionArgs = {Long.toString(governorId)};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.getContext(), uri, projection, selection, selectionArgs, sortOrder);
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
							sb.append("sentenceid="); //$NON-NLS-1$
							sb.append(cursor.getString(idSentenceId));
							sb.append(' ');
							sb.append("annoSetId="); //$NON-NLS-1$
							sb.append(Long.toString(annoSetId));
						}

						// attach sentence node
						final TreeNode sentenceNode = TreeFactory.newQueryNode(new AnnoSetQuery(annoSetId, R.drawable.annoset, sb, false), BasicModule.this.getContext());
						parent.addChild(sentenceNode);
					}
					while (cursor.moveToNext());

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					parent.disable();
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

	private void realizations_for_lexicalunit(final long luId, final TreeNode parent)
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
						"GROUP_CONCAT(IFNULL(" +  //$NON-NLS-1$
								LexUnits_FERealizations_ValenceUnits.PT +
								",'') || ':' || IFNULL(" + //$NON-NLS-1$
								LexUnits_FERealizations_ValenceUnits.GF +
								",'') || ':' || " +  //$NON-NLS-1$
								LexUnits_FERealizations_ValenceUnits.VUID +
								") AS " +  //$NON-NLS-1$
								LexUnits_FERealizations_ValenceUnits.FERS, //
						LexUnits_FERealizations_ValenceUnits.TOTAL, //
				};
				final String selection = LexUnits_FERealizations_ValenceUnits.LUID + " = ?"; //$NON-NLS-1$
				final String[] selectionArgs = {Long.toString(luId)};
				final String sortOrder = LexUnits_FERealizations_ValenceUnits.FERID;
				return new CursorLoader(BasicModule.this.getContext(), uri, projection, selection, selectionArgs, sortOrder);
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
						sb.append("[annotated] "); //$NON-NLS-1$
						sb.append(Integer.toString(cursor.getInt(idTotal)));
						if (VERBOSE)
						{
							sb.append(' ');
							sb.append(cursor.getString(idFerId));
						}

						// fe
						final TreeNode feNode = TreeFactory.addTreeItemNode(parent, sb, R.drawable.role, BasicModule.this.getContext());

						// fe realizations
						final String fers = cursor.getString(idFers);
						for (String fer : fers.split(",")) //$NON-NLS-1$
						{
							// pt:gf:valenceUnit id

							// valenceUnit id
							long vuId = -1;
							String[] fields = fer.split(":"); //$NON-NLS-1$
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
							final TreeNode ferNode = TreeFactory.newQueryNode(new ValenceUnitQuery(vuId, R.drawable.realization, sb1), BasicModule.this.getContext());
							feNode.addChild(ferNode);
						}
					}
					while (cursor.moveToNext());

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					parent.disable();
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

	private void grouprealizations_for_lexicalunit(final long luId, final TreeNode parent)
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
						"GROUP_CONCAT(" + //$NON-NLS-1$
								LexUnits_FEGroupRealizations_Patterns_ValenceUnits.FETYPE +
								" || '.' || " + //$NON-NLS-1$
								LexUnits_FEGroupRealizations_Patterns_ValenceUnits.PT +
								" || '.'|| IFNULL(" + //$NON-NLS-1$
								LexUnits_FEGroupRealizations_Patterns_ValenceUnits.GF + ", '--')) AS " + //$NON-NLS-1$
								LexUnits_FEGroupRealizations_Patterns_ValenceUnits.GROUPREALIZATIONS,}; //$NON-NLS-1$
				final String selection = LexUnits_FEGroupRealizations_Patterns_ValenceUnits.LUID + " = ?"; //$NON-NLS-1$
				final String[] selectionArgs = {Long.toString(luId)};
				final String sortOrder = null; // LexUnits_FEGroupRealizations_Patterns_ValenceUnits.FEGRID;
				return new CursorLoader(BasicModule.this.getContext(), uri, projection, selection, selectionArgs, sortOrder);
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
						// System.out.println("GROUP REALIZATIONS " + feGroupId + ' ' + groupRealizations + ' ' + patternId); //$NON-NLS-1$
						if (groupRealizations == null)
						{
							continue;
						}

						// group
						if (groupId != feGroupId)
						{
							final Editable sb1 = new SpannableStringBuilder();
							sb1.append("group"); //$NON-NLS-1$
							sb1.append(' ');
							sb1.append(Integer.toString(++groupNumber));

							if (VERBOSE)
							{
								sb1.append(' ');
								sb1.append(Long.toString(feGroupId));
							}

							groupId = feGroupId;
							groupNode = TreeFactory.addTreeItemNode(parent, sb1, R.drawable.grouprealization, BasicModule.this.getContext());
						}

						// group realization
						parseGroupRealizations(groupRealizations, sb);

						// node
						final TreeNode patternNode = TreeFactory.newQueryNode(new PatternQuery(patternId, R.drawable.grouprealization, sb), BasicModule.this.getContext());
						assert groupNode != null;
						groupNode.addChild(patternNode);
					}
					while (cursor.moveToNext());

					// expand
					TreeView.expand(parent, 2);
				}
				else
				{
					parent.disable();
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

	@SuppressWarnings("UnusedReturnValue")
	private CharSequence parseGroupRealizations(final String aggregate, final SpannableStringBuilder sb)
	{
		// fe.pt.gf,fe.pt.gf,...
		final String[] groupRealizations = aggregate.split(","); //$NON-NLS-1$
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
			final String[] components = groupRealization.split("\\."); //$NON-NLS-1$
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

	private void sentences_for_lexunit(final long luId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(LexUnits_Sentences_Annosets_Layers_Labels.CONTENT_URI_BY_SENTENCE);
				final String[] projection = { //
						LexUnits_Sentences_Annosets_Layers_Labels.SENTENCEID, //
						LexUnits_Sentences_Annosets_Layers_Labels.TEXT, //
						LexUnits_Sentences_Annosets_Layers_Labels.LAYERTYPE, //
						LexUnits_Sentences_Annosets_Layers_Labels.RANK, //
						"GROUP_CONCAT(" + // //$NON-NLS-1$
								LexUnits_Sentences_Annosets_Layers_Labels.START + "||':'||" + // //$NON-NLS-1$
								LexUnits_Sentences_Annosets_Layers_Labels.END + "||':'||" + // //$NON-NLS-1$
								LexUnits_Sentences_Annosets_Layers_Labels.LABELTYPE + "||':'||" + // //$NON-NLS-1$
								"CASE WHEN " + LexUnits_Sentences_Annosets_Layers_Labels.LABELITYPE + " IS NULL THEN '' ELSE " + LexUnits_Sentences_Annosets_Layers_Labels.LABELITYPE + " END||':'||" + // //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
								"CASE WHEN " + LexUnits_Sentences_Annosets_Layers_Labels.BGCOLOR + " IS NULL THEN '' ELSE " + LexUnits_Sentences_Annosets_Layers_Labels.BGCOLOR + " END||':'||" + // //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
								"CASE WHEN " + LexUnits_Sentences_Annosets_Layers_Labels.FGCOLOR + " IS NULL THEN '' ELSE " + LexUnits_Sentences_Annosets_Layers_Labels.FGCOLOR + " END" + // //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
								",'|')" + // //$NON-NLS-1$
								" AS " + LexUnits_Sentences_Annosets_Layers_Labels.LAYERANNOTATION, // //$NON-NLS-1$
				};
				final String selection = "u." + LexUnits_Sentences_Annosets_Layers_Labels.LUID + " = ? AND " + LexUnits_Sentences_Annosets_Layers_Labels.LAYERTYPE + " = ?"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				final String[] selectionArgs = {Long.toString(luId), BasicModule.FOCUSLAYER};
				final String sortOrder = LexUnits_Sentences_Annosets_Layers_Labels.CORPUSID + ',' + //
						LexUnits_Sentences_Annosets_Layers_Labels.DOCUMENTID + ',' + //
						LexUnits_Sentences_Annosets_Layers_Labels.PARAGNO + ',' + //
						LexUnits_Sentences_Annosets_Layers_Labels.SENTNO; //
				return new CursorLoader(BasicModule.this.getContext(), uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				final SpannableStringBuilder sb = new SpannableStringBuilder();

				if (cursor.moveToFirst())
				{
					// column indices
					final int idText = cursor.getColumnIndex(LexUnits_Sentences_Annosets_Layers_Labels.TEXT);
					final int idLayerType = cursor.getColumnIndex(LexUnits_Sentences_Annosets_Layers_Labels.LAYERTYPE);
					final int idAnnotations = cursor.getColumnIndex(LexUnits_Sentences_Annosets_Layers_Labels.LAYERANNOTATION);
					final int idSentenceId = cursor.getColumnIndex(LexUnits_Sentences_Annosets_Layers_Labels.SENTENCEID);

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
								Spanner.setSpan(sb, sentenceStart + from, sentenceStart + to, 0, "Target".equals(layerType) ? FrameNetFactories.targetFactory : FrameNetFactories.highlightTextFactory); //$NON-NLS-1$

								// label
								sb.append('\t');
								Spanner.append(sb, label.label, 0, "FE".equals(layerType) ? FrameNetFactories.feFactory : FrameNetFactories.labelFactory); //$NON-NLS-1$
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
					TreeFactory.addTextNode(parent, sb, BasicModule.this.getContext());

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					parent.disable();
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

	private void sentences_for_pattern(final long patternId, final TreeNode parent)
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
				final String selection = Patterns_Sentences.PATTERNID + " = ?"; //$NON-NLS-1$
				final String[] selectionArgs = {Long.toString(patternId)};
				final String sortOrder = Patterns_Sentences.SENTENCEID;
				return new CursorLoader(BasicModule.this.getContext(), uri, projection, selection, selectionArgs, sortOrder);
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

						// attach sentence
						final TreeNode sentenceNode = TreeFactory.newQueryNode(new AnnoSetQuery(annotationId, R.drawable.sentence, sb, false), BasicModule.this.getContext());
						parent.addChild(sentenceNode);
					}
					while (cursor.moveToNext());

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					parent.disable();
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

	private void sentences_for_valenceunit(final long vuId, final TreeNode parent)
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
				final String selection = ValenceUnits_Sentences.VUID + " = ?"; //$NON-NLS-1$
				final String[] selectionArgs = {Long.toString(vuId)};
				final String sortOrder = ValenceUnits_Sentences.SENTENCEID;
				return new CursorLoader(BasicModule.this.getContext(), uri, projection, selection, selectionArgs, sortOrder);
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
						final TreeNode sentenceNode = TreeFactory.newQueryNode(new AnnoSetQuery(annotationId, R.drawable.sentence, sb, false), BasicModule.this.getContext());
						parent.addChild(sentenceNode);
					}
					while (cursor.moveToNext());

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					parent.disable();
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

	// annosets

	void annoset(final long annoSetId, final TreeNode parent, final boolean withSentence)
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
				return new CursorLoader(BasicModule.this.getContext(), uri, projection, selection, selectionArgs, sortOrder);
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
								Spanner.append(sb, label.label, 0, "FE".equals(layerType) ? FrameNetFactories.feFactory : FrameNetFactories.labelFactory); //$NON-NLS-1$
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
					TreeFactory.addTextNode(parent, sb, BasicModule.this.getContext());

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					parent.disable();
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

	void annosets_for_pattern(final long patternId, final TreeNode parent)
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
				return new CursorLoader(BasicModule.this.getContext(), uri, projection, selection, selectionArgs, sortOrder);
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

									Spanner.append(sb, label.label, 0, "FE".equals(layerType) ? FrameNetFactories.feFactory : FrameNetFactories.labelFactory); //$NON-NLS-1$
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
					TreeFactory.addTextNode(parent, sb, BasicModule.this.getContext());

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					parent.disable();
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

	void annosets_for_valenceunit(final long vuId, final TreeNode parent)
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
				return new CursorLoader(BasicModule.this.getContext(), uri, projection, selection, selectionArgs, sortOrder);
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
									Spanner.append(sb, label.label, 0, "FE".equals(layerType) ? FrameNetFactories.feFactory : FrameNetFactories.labelFactory); //$NON-NLS-1$
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
					TreeFactory.addTextNode(parent, sb, BasicModule.this.getContext());

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					parent.disable();
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

	void layers_for_sentence(final long sentenceId, final String text, final TreeNode parent)
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
				return new CursorLoader(BasicModule.this.getContext(), uri, projection, selection, selectionArgs, sortOrder);
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
								Spanner.append(sb, label.label, 0, "FE".equals(layerType) ? FrameNetFactories.feFactory : FrameNetFactories.labelFactory); //$NON-NLS-1$
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
					TreeFactory.addTextNode(parent, sb, BasicModule.this.getContext());

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					parent.disable();
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

	private CharSequence processLayer(final CharSequence name)
	{
		if ("FE".equals(name)) //$NON-NLS-1$
		{
			return "Frame element"; //$NON-NLS-1$
		}
		if ("PT".equals(name)) //$NON-NLS-1$
		{
			return "Phrase type"; //$NON-NLS-1$
		}
		if ("GF".equals(name)) //$NON-NLS-1$
		{
			return "Grammatical function"; //$NON-NLS-1$
		}
		return name;
	}

	private CharSequence processPT(final CharSequence name)
	{
		if ("CNI".equals(name)) //$NON-NLS-1$
		{
			return "constructional ∅"; //$NON-NLS-1$
		}
		if ("DNI".equals(name)) //$NON-NLS-1$
		{
			return "definite ∅"; //$NON-NLS-1$
		}
		if ("INI".equals(name)) //$NON-NLS-1$
		{
			return "indefinite ∅"; //$NON-NLS-1$
		}
		return name;
	}

	// Q U E R I E S

	class FrameQuery extends QueryHolder.Query
	{
		public FrameQuery(final long frameId, final int icon, @SuppressWarnings("SameParameterValue") final CharSequence text)
		{
			super(frameId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			frame((int) this.id, node);
		}
	}

	class RelatedQuery extends QueryHolder.Query
	{
		public RelatedQuery(final long frameId, final int icon, @SuppressWarnings("SameParameterValue") final CharSequence text)
		{
			super(frameId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			frame_related((int) this.id, node);
		}
	}

	class LexUnitsQuery extends QueryHolder.Query
	{
		public LexUnitsQuery(final long frameId, final int icon, @SuppressWarnings("SameParameterValue") final CharSequence text)
		{
			super(frameId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			lexunits_for_frame((int) this.id, node, false);
		}
	}

	class FEsQuery extends QueryHolder.Query
	{
		public FEsQuery(final long frameId, final int icon, @SuppressWarnings("SameParameterValue") final CharSequence text)
		{
			super(frameId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			fes_for_frame((int) this.id, node);
		}
	}

	class GovernorsQuery extends QueryHolder.Query
	{
		public GovernorsQuery(final long luId, final int icon, @SuppressWarnings("SameParameterValue") final CharSequence text)
		{
			super(luId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			governors_for_lexicalunit(this.id, node);
		}
	}

	class GovernorsAnnoSetsQuery extends QueryHolder.Query
	{
		public GovernorsAnnoSetsQuery(final long governorId, final int icon, final CharSequence text)
		{
			super(governorId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			annosets_for_governor(this.id, node);
		}
	}

	class RealizationsQuery extends QueryHolder.Query
	{
		public RealizationsQuery(final long luId, final int icon, @SuppressWarnings("SameParameterValue") final CharSequence text)
		{
			super(luId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			realizations_for_lexicalunit(this.id, node);
		}
	}

	class GroupRealizationsQuery extends QueryHolder.Query
	{
		public GroupRealizationsQuery(final long luId, final int icon, @SuppressWarnings("SameParameterValue") final CharSequence text)
		{
			super(luId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			grouprealizations_for_lexicalunit(this.id, node);
		}
	}

	class PatternQuery extends QueryHolder.Query
	{
		public PatternQuery(final long patternId, final int icon, final CharSequence text)
		{
			super(patternId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			sentences_for_pattern(this.id, node);
		}
	}

	class ValenceUnitQuery extends QueryHolder.Query
	{
		public ValenceUnitQuery(final long vuId, final int icon, final CharSequence text)
		{
			super(vuId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			sentences_for_valenceunit(this.id, node);
		}
	}

	class AnnoSetQuery extends QueryHolder.Query
	{
		private final boolean withSentence;

		public AnnoSetQuery(final long annosetId, final int icon, final CharSequence text, @SuppressWarnings("SameParameterValue") final boolean withSentence)
		{
			super(annosetId, icon, text);
			this.withSentence = withSentence;
		}

		@Override
		public void process(final TreeNode node)
		{
			annoset(this.id, node, this.withSentence);
		}
	}

	class SentencesQuery extends QueryHolder.Query
	{
		public SentencesQuery(final long luId, final int icon, @SuppressWarnings("SameParameterValue") final CharSequence text)
		{
			super(luId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			sentences_for_lexunit(this.id, node);
		}
	}

	class DummyQuery extends QueryHolder.Query
	{
		private static final String TAG = "DummyQuery"; //$NON-NLS-1$

		@SuppressWarnings("unused")
		public DummyQuery(final long annosetId, final int icon, final CharSequence text)
		{
			super(annosetId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			Log.d(TAG, "QUERY " + this.id); //$NON-NLS-1$
		}
	}
}
