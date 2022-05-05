/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.framenet.loaders;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcelable;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import org.sqlunet.browser.Module;
import org.sqlunet.browser.SqlunetViewTreeModel;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.framenet.FnFramePointer;
import org.sqlunet.framenet.FnLexUnitPointer;
import org.sqlunet.framenet.FnSentencePointer;
import org.sqlunet.framenet.R;
import org.sqlunet.framenet.Utils;
import org.sqlunet.framenet.browser.FnFrameActivity;
import org.sqlunet.framenet.browser.FnLexUnitActivity;
import org.sqlunet.framenet.browser.FnSentenceActivity;
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
import org.sqlunet.framenet.provider.FrameNetProvider;
import org.sqlunet.framenet.sql.FnLabel;
import org.sqlunet.framenet.style.FrameNetFactories;
import org.sqlunet.framenet.style.FrameNetFrameProcessor;
import org.sqlunet.framenet.style.FrameNetMarkupFactory;
import org.sqlunet.framenet.style.FrameNetProcessor;
import org.sqlunet.framenet.style.FrameNetSpanner;
import org.sqlunet.model.TreeFactory;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.style.Spanner;
import org.sqlunet.treeview.control.Link;
import org.sqlunet.treeview.control.Query;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.view.TreeOp;
import org.sqlunet.view.TreeOp.TreeOps;
import org.sqlunet.view.TreeOpExecute;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import static org.sqlunet.view.TreeOp.TreeOpCode.NEWCHILD;
import static org.sqlunet.view.TreeOp.TreeOpCode.NEWEXTRA;
import static org.sqlunet.view.TreeOp.TreeOpCode.NEWMAIN;
import static org.sqlunet.view.TreeOp.TreeOpCode.NEWTREE;
import static org.sqlunet.view.TreeOp.TreeOpCode.NEWUNIQUE;
import static org.sqlunet.view.TreeOp.TreeOpCode.REMOVE;

/**
 * Base framenet module
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract public class BaseModule extends Module
{
	static private final String TAG = "BaseModule";

	/**
	 * Verbose flag
	 */
	static private boolean VERBOSE = false;

	// agents

	/**
	 * Processor
	 */
	@NonNull
	private final FrameNetProcessor processor;

	/**
	 * Frame Processor
	 */
	@NonNull
	private final FrameNetFrameProcessor frameProcessor;

	/**
	 * Spanner
	 */
	@Nullable
	private final FrameNetSpanner spanner;

	// resources

	/**
	 * Drawable for frame
	 */
	@NonNull
	private final Drawable frameDrawable;

	/**
	 * Drawable for FE
	 */
	@NonNull
	private final Drawable feDrawable;

	/**
	 * Drawable for lexUnit
	 */
	@NonNull
	private final Drawable lexunitDrawable;

	/**
	 * Drawable for definition
	 */
	@NonNull
	private final Drawable definitionDrawable;

	/**
	 * Drawable for meta definition
	 */
	@NonNull
	private final Drawable metadefinitionDrawable;

	/**
	 * Drawable for realization
	 */
	@NonNull
	private final Drawable realizationDrawable;

	/**
	 * Drawable for sentence
	 */
	@NonNull
	private final Drawable sentenceDrawable;

	/**
	 * Drawable for semtype
	 */
	@NonNull
	private final Drawable semtypeDrawable;

	/**
	 * Drawable for coreset
	 */
	@NonNull
	private final Drawable coresetDrawable;

	/**
	 * Drawable for layer
	 */
	@NonNull
	private final Drawable layerDrawable;

	// View models

	private SqlunetViewTreeModel frameFromFrameIdModel;

	private SqlunetViewTreeModel relatedFramesFromFrameIdModel;

	private SqlunetViewTreeModel fesFromFrameIdModel;

	private SqlunetViewTreeModel lexUnitFromLuIdModel;

	private SqlunetViewTreeModel lexUnitsFromFrameIdModel;

	private SqlunetViewTreeModel lexUnitsFromWordIdPosModel;

	private SqlunetViewTreeModel governorsFromLuIdModel;

	private SqlunetViewTreeModel realizationsFromLuIdModel;

	private SqlunetViewTreeModel groupRealizationsFromLuIdModel;

	private SqlunetViewTreeModel sentencesFromLuIdModel;

	private SqlunetViewTreeModel sentencesFromPatternIdModel;

	private SqlunetViewTreeModel sentencesFromVuIdModel;

	private SqlunetViewTreeModel annoSetFromAnnoSetIdModel;

	private SqlunetViewTreeModel annoSetsFromGovernorIdModel;

	private SqlunetViewTreeModel annoSetsFromPatternIdModel;

	private SqlunetViewTreeModel annoSetsFromVuIdModel;

	private SqlunetViewTreeModel layersFromSentenceIdModel;

	/**
	 * Constructor
	 *
	 * @param fragment containing fragment
	 */
	BaseModule(@NonNull final TreeFragment fragment)
	{
		super(fragment);

		// models
		makeModels();

		// drawables
		final Context context = this.fragment.requireContext();
		this.frameDrawable = Spanner.getDrawable(context, R.drawable.roleclass);
		this.feDrawable = Spanner.getDrawable(context, R.drawable.role);
		this.lexunitDrawable = Spanner.getDrawable(context, R.drawable.member);
		this.definitionDrawable = Spanner.getDrawable(context, R.drawable.definition);
		this.metadefinitionDrawable = Spanner.getDrawable(context, R.drawable.metadefinition);
		this.sentenceDrawable = Spanner.getDrawable(context, R.drawable.sentence);
		this.realizationDrawable = Spanner.getDrawable(context, R.drawable.realization);
		this.semtypeDrawable = Spanner.getDrawable(context, R.drawable.semtype);
		this.coresetDrawable = Spanner.getDrawable(context, R.drawable.coreset);
		this.layerDrawable = Spanner.getDrawable(context, R.drawable.layer);

		// agents
		this.processor = new FrameNetProcessor();
		this.frameProcessor = new FrameNetFrameProcessor();
		this.spanner = new FrameNetSpanner(context);
	}

	/**
	 * Make view models
	 */
	private void makeModels()
	{
		this.frameFromFrameIdModel = new ViewModelProvider(this.fragment).get("fn.frame(frameid)", SqlunetViewTreeModel.class);
		this.frameFromFrameIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.relatedFramesFromFrameIdModel = new ViewModelProvider(this.fragment).get("fn.relatedframes(frameid)", SqlunetViewTreeModel.class);
		this.relatedFramesFromFrameIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.fesFromFrameIdModel = new ViewModelProvider(this.fragment).get("fn.fes(frameid)", SqlunetViewTreeModel.class);
		this.fesFromFrameIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.lexUnitFromLuIdModel = new ViewModelProvider(this.fragment).get("fn.lexunit(luid)", SqlunetViewTreeModel.class);
		this.lexUnitFromLuIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.lexUnitsFromFrameIdModel = new ViewModelProvider(this.fragment).get("fn.lexunits(frameid)", SqlunetViewTreeModel.class);
		this.lexUnitsFromFrameIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.lexUnitsFromWordIdPosModel = new ViewModelProvider(this.fragment).get("fn.lexunits(wordid,pos)", SqlunetViewTreeModel.class);
		this.lexUnitsFromWordIdPosModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.governorsFromLuIdModel = new ViewModelProvider(this.fragment).get("fn.governors(luid)", SqlunetViewTreeModel.class);
		this.governorsFromLuIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.realizationsFromLuIdModel = new ViewModelProvider(this.fragment).get("fn.realizations(luid)", SqlunetViewTreeModel.class);
		this.realizationsFromLuIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.groupRealizationsFromLuIdModel = new ViewModelProvider(this.fragment).get("fn.grouprealizations(luid)", SqlunetViewTreeModel.class);
		this.groupRealizationsFromLuIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.sentencesFromLuIdModel = new ViewModelProvider(this.fragment).get("fn.sentences(luid)", SqlunetViewTreeModel.class);
		this.sentencesFromLuIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.sentencesFromPatternIdModel = new ViewModelProvider(this.fragment).get("fn.sentences(patternid)", SqlunetViewTreeModel.class);
		this.sentencesFromPatternIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.sentencesFromVuIdModel = new ViewModelProvider(this.fragment).get("fn.sentences(vuid)", SqlunetViewTreeModel.class);
		this.sentencesFromVuIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.annoSetFromAnnoSetIdModel = new ViewModelProvider(this.fragment).get("fn.annoset(annosetid)", SqlunetViewTreeModel.class);
		this.annoSetFromAnnoSetIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.annoSetsFromGovernorIdModel = new ViewModelProvider(this.fragment).get("fn.annosets(governorid)", SqlunetViewTreeModel.class);
		this.annoSetsFromGovernorIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.annoSetsFromPatternIdModel = new ViewModelProvider(this.fragment).get("fn.annosets(patternid)", SqlunetViewTreeModel.class);
		this.annoSetsFromPatternIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.annoSetsFromVuIdModel = new ViewModelProvider(this.fragment).get("fn.annosets(vuid)", SqlunetViewTreeModel.class);
		this.annoSetsFromVuIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.layersFromSentenceIdModel = new ViewModelProvider(this.fragment).get("fn.layers(sentenceid)", SqlunetViewTreeModel.class);
		this.layersFromSentenceIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));
	}

	// C R E A T I O N

	/**
	 * Set always-simple-preferences flag
	 *
	 * @param verbose flag
	 */
	static public void setVerbose(boolean verbose)
	{
		VERBOSE = verbose;
	}

	// F R A M E

	/**
	 * FrameNet frame
	 *
	 * @param frameId frame id
	 * @param parent  parent node
	 */
	void frame(final long frameId, @NonNull final TreeNode parent)
	{
		final ContentProviderSql sql = Queries.prepareFrame(frameId);
		final Uri uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri));
		this.frameFromFrameIdModel.loadData(uri, sql, cursor -> frameCursorToTreeModel(cursor, frameId, parent));
	}

	private TreeOp[] frameCursorToTreeModel(@NonNull final Cursor cursor, final long frameId, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.getCount() > 1)
		{
			throw new RuntimeException("Unexpected number of rows");
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
			Spanner.appendImage(sb, BaseModule.this.frameDrawable);
			sb.append(' ');
			Spanner.append(sb, cursor.getString(idFrame), 0, FrameNetFactories.frameFactory);
			if (VERBOSE)
			{
				sb.append(' ');
				sb.append(Long.toString(frameId));
			}
			sb.append('\n');

			// definition
			Spanner.appendImage(sb, BaseModule.this.metadefinitionDrawable);
			sb.append(' ');
			String frameDefinition = cursor.getString(idFrameDefinition);
			frameDefinition = frameDefinition.replaceAll("\n*<ex></ex>\n*", ""); // TODO remove in sqlunet database

			final CharSequence[] frameDefinitionFields = processDefinition(frameDefinition, 0);
			sb.append(frameDefinitionFields[0]);

			// examples in definition
			for (int i = 1; i < frameDefinitionFields.length; i++)
			{
				sb.append('\n');
				sb.append(frameDefinitionFields[i]);
			}

			// attach result
			final TreeNode node = TreeFactory.makeTextNode(sb, false).addTo(parent);

			// sub nodes
			final TreeNode fesNode = TreeFactory.makeHotQueryNode("Frame Elements", R.drawable.roles, false, new FEsQuery(frameId)).addTo(parent);
			final TreeNode lexUnitsNode = TreeFactory.makeHotQueryNode("Lex Units", R.drawable.members, false, new LexUnitsQuery(frameId)).addTo(parent);
			final TreeNode relatedNode = TreeFactory.makeQueryNode("Related", R.drawable.roleclass, false, new RelatedQuery(frameId)).addTo(parent);

			changed = TreeOp.seq(NEWMAIN, node, NEWEXTRA, fesNode, NEWEXTRA, lexUnitsNode, NEWEXTRA, relatedNode, NEWTREE, parent);
		}
		else
		{
			TreeFactory.setNoResult(parent);
			changed = TreeOp.seq(REMOVE, parent);
		}

		cursor.close();
		return changed;
	}

	private static final int[] FRAMERELATION_RANK = { //
			20,  // 1  - Has Subframe(s)
			11,  // 2  - Inherits from
			60,  // 3  - Is Causative of
			70,  // 4  - Is Inchoative of
			10,  // 5  - Is Inherited by
			51,  // 6  - Is Perspectivized in
			41,  // 7  - Is Preceded by
			31,  // 8  - Is Used by
			50,  // 9  - Perspective on
			40,  // 10 - Precedes
			80,  // 11 - See also
			21,  // 12 - Subframe of
			30,  // 13 - Uses
	};

	private static final String[] FRAMERELATION_GLOSS = { //
			"%s has %s as subframe", // 1  - Has Subframe(s)
			"%s inherits %s", // 2  - Inherits from
			"%s is causative of %s", // 3  - Is Causative of
			"%s is inchoative of %s", // 4  - Is Inchoative of
			"%s is inherited by %s", // 5  - Is Inherited by
			"%s is perspectivized in %s", // 6  - Is Perspectivized in
			"%s is preceded by %s", // 7  - Is Preceded by
			"%s is used by %s", // 8  - Is Used by
			"%s perspectivizes %s", // 9  - Perspective on
			"%s precedes %s", // 10 - Precedes
			"%s has see-also relation to %s", // 11 - See also
			"%s is subframe of %s", // 12 - Subframe of
			"%s uses %s", // 13 - Uses
	};

	/**
	 * Related frames
	 *
	 * @param frameId frame id
	 * @param parent  parent node
	 */
	private void relatedFrames(final long frameId, @NonNull final TreeNode parent)
	{
		final ContentProviderSql sql = Queries.prepareRelatedFrames(frameId);
		final Uri uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri));
		this.relatedFramesFromFrameIdModel.loadData(uri, sql, cursor -> relatedFramesCursorToTreeModel(cursor, frameId, parent));
	}

	@NonNull
	private TreeOp[] relatedFramesCursorToTreeModel(@NonNull final Cursor cursor, final long frameId, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

			// column indices
			// final int idFrameId = cursor.getColumnIndex(Frames_Related.FRAMEID);
			// final int idFrame = cursor.getColumnIndex(Frames_Related.FRAME);
			// final int idFrame2Id = cursor.getColumnIndex(Frames_Related.FRAME2ID);
			// final int idFrame2 = cursor.getColumnIndex(Frames_Related.FRAME2);
			final int idFrameId = cursor.getColumnIndex("i1");
			final int idFrame = cursor.getColumnIndex("f1");
			final int idFrame2Id = cursor.getColumnIndex("i2");
			final int idFrame2 = cursor.getColumnIndex("f2");
			final int idRelationId = cursor.getColumnIndex(Frames_Related.RELATIONID);
			// final int idRelation = cursor.getColumnIndex(Frames_Related.RELATION);

			Map<Integer, TreeNode> relatedNodes = new TreeMap<>();
			do
			{
				final Editable sb = new SpannableStringBuilder();

				// data
				final int frame1Id = cursor.getInt(idFrameId);
				final int frame2Id = cursor.getInt(idFrame2Id);
				final int relationId = cursor.getInt(idRelationId);
				final String frame1 = cursor.getString(idFrame);
				final String frame2 = cursor.getString(idFrame2);
				// final String relation = cursor.getString(idRelation);
				final String gloss = FRAMERELATION_GLOSS[relationId - 1];

				// related
				if (VERBOSE)
				{
					sb.append(Integer.toString(relationId));
					sb.append(' ');
				}

				// slots
				boolean slot1 = frame1Id == frameId;
				boolean slot2 = frame2Id == frameId;

				// arg 1
				final SpannableStringBuilder sb1 = new SpannableStringBuilder();
				if (slot1)
				{
					sb1.append("it");
				}
				else
				{
					Spanner.append(sb1, frame1, 0, FrameNetFactories.frameFactory);
					if (VERBOSE)
					{
						sb1.append(' ');
						sb1.append(Integer.toString(frame1Id));
					}
				}

				// arg 2
				final SpannableStringBuilder sb2 = new SpannableStringBuilder();
				if (slot2)
				{
					sb2.append("it");
				}
				else
				{
					Spanner.append(sb2, frame2, 0, FrameNetFactories.frameFactory);
					if (VERBOSE)
					{
						sb2.append(' ');
						sb2.append(Integer.toString(frame2Id));
					}
				}

				// relation
				int position = gloss.indexOf("%s");
				final SpannableStringBuilder sbr = new SpannableStringBuilder(gloss);
				sbr.replace(position, position + 2, sb1);
				position = sbr.toString().indexOf("%s");
				sbr.replace(position, position + 2, sb2);

				sb.append(sbr);

				// result
				long targetFrameId = slot1 ? frame2Id : frame1Id;
				final TreeNode memberNode = TreeFactory.makeLinkNode(sb, R.drawable.roleclass, false, new FnFrameLink(targetFrameId));
				relatedNodes.put(FRAMERELATION_RANK[relationId], memberNode);
			}
			while (cursor.moveToNext());

			for (TreeNode relatedNode : relatedNodes.values())
			{
				relatedNode.addTo(parent);
				changedList.add(NEWCHILD, relatedNode);
			}
			changed = changedList.toArray();
		}
		else
		{
			TreeFactory.setNoResult(parent);
			changed = TreeOp.seq(REMOVE, parent);
		}

		cursor.close();
		return changed;
	}

	// F E S

	/**
	 * Frame elements for frame
	 *
	 * @param frameId frame id
	 * @param parent  parent node
	 */
	private void fesForFrame(final int frameId, @NonNull final TreeNode parent)
	{
		final ContentProviderSql sql = Queries.prepareFesForFrame(frameId);
		final Uri uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri));
		this.fesFromFrameIdModel.loadData(uri, sql, cursor -> fesCursorToTreeModel(cursor, parent));
	}

	@NonNull
	private TreeOp[] fesCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

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
				final String feDefinition = cursor.getString(idDefinition).trim().replaceAll("\n+", "\n").replaceAll("\n$", "");
				final String feSemTypes = cursor.getString(idSemTypes);
				final boolean isInCoreSet = !cursor.isNull(idCoreset);
				final int coreTypeId = cursor.getInt(idCoreTypeId);
				final String coreType = cursor.getString(idCoreType);

				// fe
				Spanner.append(sb, feType, 0, FrameNetFactories.feFactory);
				sb.append(' ');
				Spanner.append(sb, feAbbrev, 0, FrameNetFactories.feAbbrevFactory);

				// attach fe
				final TreeNode feNode = TreeFactory.makeTreeNode(sb, coreTypeId == 1 ? R.drawable.rolex : R.drawable.role, true).addTo(parent);
				changedList.add(NEWCHILD, feNode);

				// more info
				final SpannableStringBuilder sb2 = new SpannableStringBuilder();

				// fe definition
				final CharSequence[] frameDefinitionFields = processDefinition(feDefinition, FrameNetMarkupFactory.FEDEF);
				sb2.append('\t');
				Spanner.appendImage(sb2, BaseModule.this.metadefinitionDrawable);
				sb2.append(' ');
				sb2.append(frameDefinitionFields[0]);
				if (frameDefinitionFields.length > 1)
				{
					sb2.append('\n');
					sb2.append('\t');
					sb2.append(frameDefinitionFields[1]);
				}

				// core type
				sb2.append('\n');
				sb2.append('\t');
				Spanner.appendImage(sb2, BaseModule.this.coresetDrawable);
				sb2.append(' ');
				sb2.append(coreType);

				// coreset
				if (isInCoreSet)
				{
					final int coreset = cursor.getInt(idCoreset);
					sb2.append('\n');
					sb2.append('\t');
					Spanner.appendImage(sb2, BaseModule.this.coresetDrawable);
					sb2.append("[coreset] ");
					sb2.append(Integer.toString(coreset));
				}

				// sem types
				if (feSemTypes != null)
				{
					sb2.append('\n');
					sb2.append('\t');
					Spanner.appendImage(sb2, BaseModule.this.semtypeDrawable);
					sb2.append(' ');
					sb2.append(feSemTypes);
				}

				// attach extra node to fe node
				final TreeNode extraNode = TreeFactory.makeTextNode(sb2, false).addTo(feNode);
				changedList.add(NEWCHILD, extraNode);
			}
			while (cursor.moveToNext());
			changed = changedList.toArray();
		}
		else
		{
			TreeFactory.setNoResult(parent);
			changed = TreeOp.seq(REMOVE, parent);
		}

		cursor.close();
		return changed;
	}

	// L E X U N I T S

	/**
	 * Lex unit
	 *
	 * @param luId      lu id
	 * @param parent    parent node
	 * @param withFrame whether to include frames
	 * @param withFes   whether to include frame elements
	 */
	void lexUnit(final long luId, @NonNull final TreeNode parent, @SuppressWarnings("SameParameterValue") final boolean withFrame, @SuppressWarnings("SameParameterValue") final boolean withFes)
	{
		final ContentProviderSql sql = Queries.prepareLexUnit(luId);
		final Uri uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri));
		this.lexUnitFromLuIdModel.loadData(uri, sql, cursor -> lexUnitCursorToTreeModel(cursor, luId, parent, withFrame, withFes));
	}

	@NonNull
	private TreeOp[] lexUnitCursorToTreeModel(@NonNull final Cursor cursor, final long luId, @NonNull final TreeNode parent, @SuppressWarnings("SameParameterValue") final boolean withFrame, @SuppressWarnings("SameParameterValue") final boolean withFes)
	{
		if (cursor.getCount() > 1)
		{
			throw new RuntimeException("Unexpected number of rows");
		}

		TreeOp[] changed;

		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

			final SpannableStringBuilder sb = new SpannableStringBuilder();

			// column indices
			// final int idLuId = cursor.getColumnIndex(LexUnits_X.LUID);
			final int idLexUnit = cursor.getColumnIndex(LexUnits_X.LEXUNIT);
			final int idDefinition = cursor.getColumnIndex(LexUnits_X.LUDEFINITION);
			final int idDictionary = cursor.getColumnIndex(LexUnits_X.LUDICT);
			final int idIncorporatedFEType = cursor.getColumnIndex(LexUnits_X.INCORPORATEDFETYPE);
			final int idIncorporatedFEDefinition = cursor.getColumnIndex(LexUnits_X.INCORPORATEDFEDEFINITION);
			final int idFrameId = cursor.getColumnIndex(LexUnits_X.FRAMEID);
			final int idFrame = cursor.getColumnIndex(LexUnits_X.FRAME);

			// data
			// final int luId = cursor.getInt(idLuId);
			final String definition = cursor.getString(idDefinition);
			final String dictionary = cursor.getString(idDictionary);
			final String incorporatedFEType = cursor.getString(idIncorporatedFEType);
			final String incorporatedFEDefinition = cursor.getString(idIncorporatedFEDefinition);
			final int frameId = cursor.getInt(idFrameId);
			final String frame = cursor.getString(idFrame);

			// lexUnit
			Spanner.appendImage(sb, BaseModule.this.lexunitDrawable);
			sb.append(' ');
			Spanner.append(sb, cursor.getString(idLexUnit), 0, FrameNetFactories.lexunitFactory);
			if (VERBOSE)
			{
				sb.append(' ');
				sb.append(Long.toString(luId));
			}

			// definition
			sb.append('\n');
			Spanner.appendImage(sb, BaseModule.this.definitionDrawable);
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
				Spanner.appendImage(sb, BaseModule.this.feDrawable);
				sb.append(' ');
				sb.append("Incorporated");
				sb.append(' ');
				Spanner.append(sb, incorporatedFEType, 0, FrameNetFactories.fe2Factory);
				if (incorporatedFEDefinition != null)
				{
					sb.append(' ');
					sb.append('-');
					sb.append(' ');
					final CharSequence[] definitionFields = processDefinition(incorporatedFEDefinition, FrameNetMarkupFactory.FEDEF);
					sb.append(definitionFields[0]);
					// if (definitionFields.length > 1)
					// {
					// sb.append('\n');
					// sb.append('\t');
					// sb.append(definitionFields[1]);
					// }
				}
			}

			// with-frame option
			if (withFrame)
			{
				final SpannableStringBuilder sb2 = new SpannableStringBuilder();
				sb2.append("Frame");
				sb2.append(' ');
				Spanner.append(sb2, frame, 0, FrameNetFactories.boldFactory);
				final TreeNode frameNode = TreeFactory.makeHotQueryNode(sb2, R.drawable.roleclass, false, new FrameQuery(frameId)).addTo(parent);
				changedList.add(NEWCHILD, frameNode);

				if (withFes)
				{
					final TreeNode fesNode = TreeFactory.makeQueryNode("Frame Elements", R.drawable.roles, false, new FEsQuery(frameId)).addTo(parent);
					changedList.add(NEWCHILD, fesNode);
				}
			}
			else
			{
				final TreeNode node = TreeFactory.makeTextNode(sb, false).addTo(parent);
				changedList.add(NEWCHILD, node);
			}

			// sub nodes
			final TreeNode realizationsNode = TreeFactory.makeQueryNode("Realizations", R.drawable.realization, false, new RealizationsQuery(luId)).addTo(parent);
			changedList.add(NEWCHILD, realizationsNode);
			final TreeNode groupRealizationsNode = TreeFactory.makeQueryNode("Group realizations", R.drawable.grouprealization, false, new GroupRealizationsQuery(luId)).addTo(parent);
			changedList.add(NEWCHILD, groupRealizationsNode);
			final TreeNode governorsNode = TreeFactory.makeQueryNode("Governors", R.drawable.governor, false, new GovernorsQuery(luId)).addTo(parent);
			changedList.add(NEWCHILD, governorsNode);
			final TreeNode sentencesNode = TreeFactory.makeQueryNode("Sentences", R.drawable.sentence, false, new SentencesForLexUnitQuery(luId)).addTo(parent);
			changedList.add(NEWCHILD, sentencesNode);

			changed = changedList.toArray();
		}
		else
		{
			TreeFactory.setNoResult(parent);
			changed = TreeOp.seq(REMOVE, parent);
		}

		cursor.close();
		return changed;
	}

	/**
	 * Lex units for frame
	 *
	 * @param frameId   frame id
	 * @param parent    parent node
	 * @param withFrame whether to include frame
	 */
	private void lexUnitsForFrame(final long frameId, @NonNull final TreeNode parent, @SuppressWarnings("SameParameterValue") final boolean withFrame)
	{
		final ContentProviderSql sql = Queries.prepareLexUnitsForFrame(frameId);
		final Uri uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri));
		this.lexUnitsFromFrameIdModel.loadData(uri, sql, cursor -> lexUnitsCursorToTreeModel(cursor, frameId, parent, withFrame));
	}

	@NonNull
	private TreeOp[] lexUnitsCursorToTreeModel(@NonNull final Cursor cursor, final long frameId, @NonNull final TreeNode parent, @SuppressWarnings("SameParameterValue") final boolean withFrame)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

			// column indices
			// final int idFrameId = cursor.getColumnIndex(LexUnits_X.FRAMEID);
			final int idLuId = cursor.getColumnIndex(LexUnits_X.LUID);
			final int idLexUnit = cursor.getColumnIndex(LexUnits_X.LEXUNIT);
			final int idDefinition = cursor.getColumnIndex(LexUnits_X.LUDEFINITION);
			final int idDictionary = cursor.getColumnIndex(LexUnits_X.LUDICT);
			final int idIncorporatedFEType = cursor.getColumnIndex(LexUnits_X.INCORPORATEDFETYPE);
			final int idIncorporatedFEDefinition = cursor.getColumnIndex(LexUnits_X.INCORPORATEDFEDEFINITION);

			// data
			do
			{
				final SpannableStringBuilder sb = new SpannableStringBuilder();

				// final int frameId = cursor.getInt(idFrameId);
				final long luId = cursor.getLong(idLuId);
				final String lexUnit = cursor.getString(idLexUnit);
				final String definition = cursor.getString(idDefinition);
				final String dictionary = cursor.getString(idDictionary);
				final String incorporatedFEType = cursor.getString(idIncorporatedFEType);
				final String incorporatedFEDefinition = cursor.getString(idIncorporatedFEDefinition);

				// lex unit
				Spanner.append(sb, lexUnit, 0, FrameNetFactories.lexunitFactory);
				if (VERBOSE)
				{
					sb.append(' ');
					sb.append(Long.toString(luId));
				}

				// attach lex unit
				final TreeNode luNode = TreeFactory.makeLinkTreeNode(sb, R.drawable.member, true, new FnLexUnitLink(luId)).addTo(parent);
				changedList.add(NEWCHILD, luNode);

				// frame
				if (withFrame)
				{
					final TreeNode frameNode = TreeFactory.makeQueryNode("Frame", R.drawable.roleclass, false, new FrameQuery(frameId)).addTo(luNode);
					changedList.add(NEWCHILD, frameNode);
					final TreeNode fesNode = TreeFactory.makeQueryNode("Frame Elements", R.drawable.roles, false, new FEsQuery(frameId)).addTo(luNode);
					changedList.add(NEWCHILD, fesNode);
				}

				// more info
				final SpannableStringBuilder sb2 = new SpannableStringBuilder();

				// definition
				Spanner.appendImage(sb2, BaseModule.this.definitionDrawable);
				sb2.append(' ');
				Spanner.append(sb2, definition.trim(), 0, FrameNetFactories.definitionFactory);
				if (dictionary != null)
				{
					sb2.append(' ');
					sb2.append('[');
					sb2.append(dictionary);
					sb2.append(']');
				}

				// incorporated fe
				if (incorporatedFEType != null)
				{
					sb2.append('\n');
					Spanner.appendImage(sb2, BaseModule.this.feDrawable);
					sb2.append(' ');
					sb2.append("Incorporated");
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

				// attach extra node to lex unit node
				final TreeNode extraNode = TreeFactory.makeTextNode(sb2, false).addTo(luNode);
				changedList.add(NEWCHILD, extraNode);

				// sub nodes
				final TreeNode realizationsNode = TreeFactory.makeQueryNode("Realizations", R.drawable.realization, false, new RealizationsQuery(luId)).addTo(luNode);
				changedList.add(NEWCHILD, realizationsNode);
				final TreeNode groupRealizationsNode = TreeFactory.makeQueryNode("Group realizations", R.drawable.grouprealization, false, new GroupRealizationsQuery(luId)).addTo(luNode);
				changedList.add(NEWCHILD, groupRealizationsNode);
				final TreeNode governorsNode = TreeFactory.makeQueryNode("Governors", R.drawable.governor, false, new GovernorsQuery(luId)).addTo(luNode);
				changedList.add(NEWCHILD, governorsNode);
				final TreeNode sentencesNode = TreeFactory.makeQueryNode("Sentences", R.drawable.sentence, false, new SentencesForLexUnitQuery(luId)).addTo(luNode);
				changedList.add(NEWCHILD, sentencesNode);
			}
			while (cursor.moveToNext());
			changed = changedList.toArray();
		}
		else
		{
			TreeFactory.setNoResult(parent);
			changed = TreeOp.seq(REMOVE, parent);
		}

		cursor.close();
		return changed;
	}

	/**
	 * Lex units for word and pos
	 *
	 * @param wordId word id
	 * @param pos    pos
	 * @param parent parent node
	 */
	void lexUnitsForWordAndPos(final long wordId, @Nullable final Character pos, @NonNull final TreeNode parent)
	{
		final ContentProviderSql sql = Queries.prepareLexUnitsForWordAndPos(wordId, pos);
		final Uri uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri));
		this.lexUnitsFromWordIdPosModel.loadData(uri, sql, cursor -> lexUnitsFromWordIdPosCursorToTreeModel(cursor, parent));
	}

	@NonNull
	private TreeOp[] lexUnitsFromWordIdPosCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

			// column indices
			final int idLuId = cursor.getColumnIndex(Words_LexUnits_Frames.LUID);
			final int idLexUnit = cursor.getColumnIndex(Words_LexUnits_Frames.LEXUNIT);
			final int idDefinition = cursor.getColumnIndex(Words_LexUnits_Frames.LUDEFINITION);
			final int idDictionary = cursor.getColumnIndex(Words_LexUnits_Frames.LUDICT);
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
				final String dictionary = cursor.getString(idDictionary);
				final String incorporatedFEType = cursor.getString(idIncorporatedFEType);
				final String incorporatedFEDefinition = cursor.getString(idIncorporatedFEDefinition);

				// lex unit
				Spanner.appendImage(sb, BaseModule.this.lexunitDrawable);
				sb.append(' ');
				Spanner.append(sb, cursor.getString(idLexUnit), 0, FrameNetFactories.lexunitFactory);
				if (VERBOSE)
				{
					sb.append(' ');
					sb.append(Integer.toString(luId));
				}

				// definition
				sb.append('\n');
				Spanner.appendImage(sb, BaseModule.this.definitionDrawable);
				sb.append(' ');
				Spanner.append(sb, definition.trim(), 0, FrameNetFactories.definitionFactory);
				if (dictionary != null)
				{
					sb.append(' ');
					sb.append('[');
					sb.append(dictionary);
					sb.append(']');
				}

				// incorporated FE
				if (incorporatedFEType != null)
				{
					sb.append('\n');
					Spanner.appendImage(sb, BaseModule.this.feDrawable);
					sb.append(' ');
					sb.append("Incorporated");
					sb.append(' ');
					Spanner.append(sb, incorporatedFEType, 0, FrameNetFactories.fe2Factory);
					if (incorporatedFEDefinition != null)
					{
						sb.append(' ');
						sb.append('-');
						sb.append(' ');
						final CharSequence[] definitionFields = processDefinition(incorporatedFEDefinition, FrameNetMarkupFactory.FEDEF);
						sb.append(definitionFields[0]);
						// if (definitionFields.length > 1)
						// {
						// sb.append('\n');
						// sb.append('\t');
						// sb.append(definitionFields[1]);
						// }
					}
				}
				// attach result
				final TreeNode node = TreeFactory.makeTextNode(sb, false).addTo(parent);
				changedList.add(NEWCHILD, node);

				// sub nodes
				final TreeNode frameNode = TreeFactory.makeHotQueryNode("Frame", R.drawable.roleclass, false, new FrameQuery(frameId)).addTo(parent);
				changedList.add(NEWCHILD, frameNode);
				final TreeNode fesNode = TreeFactory.makeQueryNode("Frame Elements", R.drawable.roles, false, new FEsQuery(frameId)).addTo(parent);
				changedList.add(NEWCHILD, fesNode);
				final TreeNode realizationsNode = TreeFactory.makeQueryNode("Realizations", R.drawable.realization, false, new RealizationsQuery(luId)).addTo(parent);
				changedList.add(NEWCHILD, realizationsNode);
				final TreeNode groupRealizationsNode = TreeFactory.makeQueryNode("Group realizations", R.drawable.grouprealization, false, new GroupRealizationsQuery(luId)).addTo(parent);
				changedList.add(NEWCHILD, groupRealizationsNode);
				final TreeNode governorsNode = TreeFactory.makeQueryNode("Governors", R.drawable.governor, false, new GovernorsQuery(luId)).addTo(parent);
				changedList.add(NEWCHILD, governorsNode);
				final TreeNode sentencesNode = TreeFactory.makeQueryNode("Sentences", R.drawable.sentence, false, new SentencesForLexUnitQuery(luId)).addTo(parent);
				changedList.add(NEWCHILD, sentencesNode);
			}
			while (cursor.moveToNext());
			changed = changedList.toArray();
		}
		else
		{
			TreeFactory.setNoResult(parent);
			changed = TreeOp.seq(REMOVE, parent);
		}

		cursor.close();
		return changed;
	}

	// G O V E R N O R S

	/**
	 * Governors for lex unit
	 *
	 * @param luId   lex unit id
	 * @param parent parent node
	 */
	private void governorsForLexUnit(final long luId, @NonNull final TreeNode parent)
	{
		final ContentProviderSql sql = Queries.prepareGovernorsForLexUnit(luId);
		final Uri uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri));
		this.governorsFromLuIdModel.loadData(uri, sql, cursor -> governorsCursorToTreeModel(cursor, parent));
	}

	@NonNull
	private TreeOp[] governorsCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

			// column indices
			final int idGovernorId = cursor.getColumnIndex(LexUnits_Governors.GOVERNORID);
			final int idGovernorType = cursor.getColumnIndex(LexUnits_Governors.GOVERNORTYPE);
			final int idWord = cursor.getColumnIndex(LexUnits_Governors.WORD);
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
				final TreeNode annoSetsNode = TreeFactory.makeQueryNode(sb, R.drawable.governor, false, new AnnoSetsForGovernorQuery(governorId)).addTo(parent);
				changedList.add(NEWCHILD, annoSetsNode);
			}
			while (cursor.moveToNext());
			changed = changedList.toArray();
		}
		else
		{
			TreeFactory.setNoResult(parent);
			changed = TreeOp.seq(REMOVE, parent);
		}

		cursor.close();
		return changed;
	}

	// R E A L I Z A T I O N S

	/**
	 * Realizations for lex unit
	 *
	 * @param luId   lex unit id
	 * @param parent parent node
	 */
	private void realizationsForLexicalUnit(final long luId, @NonNull final TreeNode parent)
	{
		final ContentProviderSql sql = Queries.prepareRealizationsForLexicalUnit(luId);
		final Uri uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri));
		this.realizationsFromLuIdModel.loadData(uri, sql, cursor -> realizationsCursorToTreeModel(cursor, parent));
	}

	@NonNull
	private TreeOp[] realizationsCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

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
				sb.append("[annotated] ");
				sb.append(Integer.toString(cursor.getInt(idTotal)));
				if (VERBOSE)
				{
					sb.append(' ');
					sb.append(cursor.getString(idFerId));
				}

				// fe
				final TreeNode feNode = TreeFactory.makeTreeNode(sb, R.drawable.role, false).addTo(parent);
				changedList.add(NEWCHILD, feNode);

				// fe realizations
				final String fers = cursor.getString(idFers);
				for (String fer : fers.split(",")) //
				{
					// pt:gf:valenceUnit id

					// valenceUnit id
					long vuId = -1;
					String[] fields = fer.split(":");
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
					final TreeNode ferNode = TreeFactory.makeQueryNode(sb1, R.drawable.realization, false, new SentencesForValenceUnitQuery(vuId)).addTo(feNode);
					changedList.add(NEWCHILD, ferNode);
				}
			}
			while (cursor.moveToNext());
			changed = changedList.toArray();
		}
		else
		{
			TreeFactory.setNoResult(parent);
			changed = TreeOp.seq(REMOVE, parent);
		}

		cursor.close();
		return changed;
	}

	/**
	 * Group realizations for lex unit
	 *
	 * @param luId   lex unit id
	 * @param parent parent node
	 */
	private void groupRealizationsForLexUnit(final long luId, @NonNull final TreeNode parent)
	{
		final ContentProviderSql sql = Queries.prepareGroupRealizationsForLexUnit(luId);
		final Uri uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri));
		this.groupRealizationsFromLuIdModel.loadData(uri, sql, cursor -> groupRealizationsCursorToTreeModel(cursor, parent));
	}

	@NonNull
	private TreeOp[] groupRealizationsCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

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
				// Log.d(TAG, "GROUP REALIZATIONS " + feGroupId + ' ' + groupRealizations + ' ' + patternId);
				if (groupRealizations == null)
				{
					continue;
				}

				// group
				if (groupId != feGroupId)
				{
					final Editable sb1 = new SpannableStringBuilder();
					sb1.append("group");
					sb1.append(' ');
					sb1.append(Integer.toString(++groupNumber));

					if (VERBOSE)
					{
						sb1.append(' ');
						sb1.append(Long.toString(feGroupId));
					}

					groupId = feGroupId;
					groupNode = TreeFactory.makeTreeNode(sb1, R.drawable.grouprealization, false).addTo(parent);
					changedList.add(NEWCHILD, groupNode);
				}
				assert groupNode != null;

				// group realization
				parseGroupRealizations(groupRealizations, sb);

				// attach sentences node
				final TreeNode sentencesNode = TreeFactory.makeQueryNode(sb, R.drawable.grouprealization, false, new SentencesForPatternQuery(patternId)).addTo(groupNode);
				changedList.add(NEWCHILD, sentencesNode);
			}
			while (cursor.moveToNext());
			changed = changedList.toArray();
		}
		else
		{
			TreeFactory.setNoResult(parent);
			changed = TreeOp.seq(REMOVE, parent);
		}

		cursor.close();
		return changed;
	}

	/**
	 * Parse group realizations
	 *
	 * @param aggregate aggregate to parse
	 * @param sb        builder to host result
	 * @return builder
	 */
	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	private CharSequence parseGroupRealizations(@NonNull final String aggregate, @NonNull final SpannableStringBuilder sb)
	{
		// fe.pt.gf,fe.pt.gf,...
		final String[] groupRealizations = aggregate.split(",");
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

			Spanner.appendImage(sb, BaseModule.this.realizationDrawable);

			// fe.pt.gf
			final String[] components = groupRealization.split("\\.");
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

	// S E N T E N C E S

	/**
	 * Sentences for lex unit
	 *
	 * @param luId   lex unit id
	 * @param parent parent node
	 */
	private void sentencesForLexUnit(final long luId, @NonNull final TreeNode parent)
	{
		final ContentProviderSql sql = Queries.prepareSentencesForLexUnit(luId);
		final Uri uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri));
		this.sentencesFromLuIdModel.loadData(uri, sql, cursor -> sentencesCursor1ToTreeModel(cursor, parent));
	}

	@NonNull
	private TreeOp[] sentencesCursor1ToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

			// column indices
			final int idText = cursor.getColumnIndex(LexUnits_Sentences_AnnoSets_Layers_Labels.TEXT);
			final int idLayerType = cursor.getColumnIndex(LexUnits_Sentences_AnnoSets_Layers_Labels.LAYERTYPE);
			final int idAnnotations = cursor.getColumnIndex(LexUnits_Sentences_AnnoSets_Layers_Labels.LAYERANNOTATION);
			final int idSentenceId = cursor.getColumnIndex(LexUnits_Sentences_AnnoSets_Layers_Labels.SENTENCEID);

			// read cursor
			do
			{
				final SpannableStringBuilder sb = new SpannableStringBuilder();

				final String text = cursor.getString(idText);
				final String layerType = cursor.getString(idLayerType);
				final String annotations = cursor.getString(idAnnotations);
				final long sentenceId = cursor.getLong(idSentenceId);

				// sentence text
				final int sentenceStart = sb.length();
				Spanner.append(sb, text, 0, FrameNetFactories.sentenceFactory);
				if (VERBOSE)
				{
					sb.append(Long.toString(sentenceId));
					sb.append(' ');
				}

				// labels
				final List<FnLabel> labels = Utils.parseLabels(annotations);
				if (labels != null)
				{
					for (final FnLabel label : labels)
					{
						sb.append('\n');

						// segment
						String subtext;
						final int from = Integer.parseInt(label.from);
						final int to = Integer.parseInt(label.to) + 1;
						final int len = text.length();
						if (from < 0 || to > len || from > to)
						{
							final int idAnnoSetId = cursor.getColumnIndex(LexUnits_Sentences_AnnoSets_Layers_Labels.ANNOSETID);
							final long annoSetId = cursor.getLong(idAnnoSetId);
							Log.d(TAG, "annoSetId=" + annoSetId + "annotations=" + annotations + "label=" + label + "text=" + text);
							subtext = label + " ERROR [" + label.from + ',' + label.to + ']';
						}
						else
						{
							subtext = text.substring(from, to);
						}

						// span text
						Spanner.setSpan(sb, sentenceStart + from, sentenceStart + to, 0, "Target".equals(layerType) ? FrameNetFactories.targetHighlightTextFactory : FrameNetFactories.highlightTextFactory);

						// label
						sb.append('\t');
						Spanner.append(sb, label.label, 0, "FE".equals(layerType) ? FrameNetFactories.feFactory : FrameNetFactories.labelFactory);
						sb.append(' ');

						// subtext value
						final int p = sb.length();
						Spanner.append(sb, subtext, 0, FrameNetFactories.subtextFactory);

						// value colors
						if (label.bgColor != null)
						{
							final int color = Integer.parseInt(label.bgColor, 16);
							sb.setSpan(new BackgroundColorSpan(color | 0xFF000000), p, p + subtext.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
						if (label.fgColor != null)
						{
							final int color = Integer.parseInt(label.fgColor, 16);
							sb.setSpan(new ForegroundColorSpan(color | 0xFF000000), p, p + subtext.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
					}
				}

				// attach result
				final TreeNode sentenceNode = TreeFactory.makeLinkNode(sb, R.drawable.sentence, false, new FnSentenceLink(sentenceId)).addTo(parent);
				changedList.add(NEWCHILD, sentenceNode);
			}
			while (cursor.moveToNext());
			changed = changedList.toArray();
		}
		else
		{
			TreeFactory.setNoResult(parent);
			changed = TreeOp.seq(REMOVE, parent);
		}

		cursor.close();
		return changed;
	}

	/**
	 * Sentences for pattern
	 *
	 * @param patternId pattern id
	 * @param parent    parent node
	 */
	private void sentencesForPattern(final long patternId, @NonNull final TreeNode parent)
	{
		final ContentProviderSql sql = Queries.prepareSentencesForPattern(patternId);
		final Uri uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri));
		this.sentencesFromPatternIdModel.loadData(uri, sql, cursor -> sentencesCursor2ToTreeModel(cursor, parent));
	}

	@NonNull
	private TreeOp[] sentencesCursor2ToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

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
				final TreeNode annoSetNode = TreeFactory.makeQueryNode(sb, R.drawable.sentence, false, new AnnoSetQuery(annotationId, false)).addTo(parent);
				changedList.add(NEWCHILD, annoSetNode);
			}
			while (cursor.moveToNext());
			changed = changedList.toArray();
		}
		else
		{
			TreeFactory.setNoResult(parent);
			changed = TreeOp.seq(REMOVE, parent);
		}

		cursor.close();
		return changed;
	}

	/**
	 * Sentences for valence unit
	 *
	 * @param vuId   valence unit id
	 * @param parent parent node
	 */
	private void sentencesForValenceUnit(final long vuId, @NonNull final TreeNode parent)
	{
		final ContentProviderSql sql = Queries.prepareSentencesForValenceUnit(vuId);
		final Uri uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri));
		this.sentencesFromVuIdModel.loadData(uri, sql, cursor -> sentencesCursor3ToTreeModel(cursor, parent));
	}

	@NonNull
	private TreeOp[] sentencesCursor3ToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

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
				final TreeNode annoSetNode = TreeFactory.makeQueryNode(sb, R.drawable.sentence, false, new AnnoSetQuery(annotationId, false)).addTo(parent);
				changedList.add(NEWCHILD, annoSetNode);
			}
			while (cursor.moveToNext());
			changed = changedList.toArray();
		}
		else
		{
			TreeFactory.setNoResult(parent);
			changed = TreeOp.seq(REMOVE, parent);
		}

		cursor.close();
		return changed;
	}

	// A N N O S E T S

	/**
	 * AnnoSet
	 *
	 * @param annoSetId    annoSetId
	 * @param parent       parent node
	 * @param withSentence whether to include sentence
	 */
	void annoSet(final long annoSetId, @NonNull final TreeNode parent, final boolean withSentence)
	{
		final ContentProviderSql sql = Queries.prepareAnnoSet(annoSetId);
		final Uri uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri));
		this.annoSetFromAnnoSetIdModel.loadData(uri, sql, cursor -> annoSetCursorToTreeModel(cursor, parent, withSentence));
	}

	private TreeOp[] annoSetCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent, final boolean withSentence)
	{
		TreeOp[] changed;
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
				final boolean isTarget = "Target".equals(layerType);
				final boolean isFE = "FE".equals(layerType);

				// sentence
				if (withSentence && first)
				{
					Spanner.appendImage(sb, BaseModule.this.sentenceDrawable);
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
				Spanner.appendImage(sb, BaseModule.this.layerDrawable);
				sb.append(' ');
				Spanner.append(sb, processLayer(layerType), 0, isTarget ? FrameNetFactories.targetFactory : FrameNetFactories.layerTypeFactory);
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
						Spanner.append(sb, label.label, 0, isFE ? FrameNetFactories.feFactory : FrameNetFactories.labelFactory);
						sb.append(' ');

						// subtext value
						String subtext;
						final int from = Integer.parseInt(label.from);
						final int to = Integer.parseInt(label.to) + 1;
						final int len = sentenceText.length();
						if (from < 0 || to > len || from > to)
						{
							Log.d(TAG, "annotations=" + annotations + "label=" + label + "text=" + sentenceText);
							subtext = label + " ERROR [" + label.from + ',' + label.to + ']';
						}
						else
						{
							subtext = sentenceText.substring(from, to);
						}

						final int p = sb.length();
						Spanner.append(sb, subtext, 0, FrameNetFactories.subtextFactory);

						// value color
						if (label.bgColor != null)
						{
							final int color = Integer.parseInt(label.bgColor, 16);
							sb.setSpan(new BackgroundColorSpan(color | 0xFF000000), p, p + subtext.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
						if (label.fgColor != null)
						{
							final int color = Integer.parseInt(label.fgColor, 16);
							sb.setSpan(new ForegroundColorSpan(color | 0xFF000000), p, p + subtext.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
			final TreeNode node = TreeFactory.makeTextNode(sb, false).addTo(parent);
			changed = TreeOp.seq(NEWUNIQUE, node);
		}
		else
		{
			TreeFactory.setNoResult(parent);
			changed = TreeOp.seq(REMOVE, parent);
		}

		cursor.close();
		return changed;
	}

	/**
	 * AnnoSets for governor
	 *
	 * @param governorId governor id
	 * @param parent     parent id
	 */
	private void annoSetsForGovernor(final long governorId, @NonNull final TreeNode parent)
	{
		final ContentProviderSql sql = Queries.prepareAnnoSetsForGovernor(governorId);
		final Uri uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri));
		this.annoSetsFromGovernorIdModel.loadData(uri, sql, cursor -> annoSetsCursor1ToTreeModel(cursor, parent));
	}

	@NonNull
	private TreeOp[] annoSetsCursor1ToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

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
					sb.append("sentenceid=");
					sb.append(cursor.getString(idSentenceId));
					sb.append(' ');
					sb.append("annosetid=");
					sb.append(Long.toString(annoSetId));
				}

				// attach annoSet node
				final TreeNode annoSetNode = TreeFactory.makeQueryNode(sb, R.drawable.annoset, false, new AnnoSetQuery(annoSetId, false)).addTo(parent);
				changedList.add(NEWCHILD, annoSetNode);
			}
			while (cursor.moveToNext());
			changed = changedList.toArray();
		}
		else
		{
			TreeFactory.setNoResult(parent);
			changed = TreeOp.seq(REMOVE, parent);
		}

		cursor.close();
		return changed;
	}

	/**
	 * AnnoSets for pattern
	 *
	 * @param patternId pattern id
	 * @param parent    parent node
	 */
	void annoSetsForPattern(final long patternId, @NonNull final TreeNode parent)
	{
		final ContentProviderSql sql = Queries.prepareAnnoSetsForPattern(patternId);
		final Uri uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri));
		this.annoSetsFromPatternIdModel.loadData(uri, sql, cursor -> annoSetsCursor2ToTreeModel(cursor, parent));
	}

	@NonNull
	private TreeOp[] annoSetsCursor2ToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		// column indices
		final int idLayerType = cursor.getColumnIndex(Patterns_Layers_X.LAYERTYPE);
		final int idRank = cursor.getColumnIndex(Patterns_Layers_X.RANK);
		final int idAnnotations = cursor.getColumnIndex(Patterns_Layers_X.LAYERANNOTATIONS);
		final int idAnnoSetId = cursor.getColumnIndex(Patterns_Layers_X.ANNOSETID);
		final int idSentenceText = cursor.getColumnIndex(Patterns_Layers_X.SENTENCETEXT);

		final TreeOp[] changed = annoSets(parent, cursor, null, idSentenceText, idLayerType, idRank, idAnnotations, idAnnoSetId);

		cursor.close();
		return changed;
	}

	/**
	 * AnnoSets for valence unit
	 *
	 * @param vuId   valence unit id
	 * @param parent parent node
	 */
	void annoSetsForValenceUnit(final long vuId, @NonNull final TreeNode parent)
	{
		final ContentProviderSql sql = Queries.prepareAnnoSetsForValenceUnit(vuId);
		final Uri uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri));
		this.annoSetsFromVuIdModel.loadData(uri, sql, cursor -> annoSetsCursor3ToTreeModel(cursor, parent));
	}

	@NonNull
	private TreeOp[] annoSetsCursor3ToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		// column indices
		final int idLayerType = cursor.getColumnIndex(ValenceUnits_Layers_X.LAYERTYPE);
		final int idRank = cursor.getColumnIndex(ValenceUnits_Layers_X.RANK);
		final int idAnnotations = cursor.getColumnIndex(ValenceUnits_Layers_X.LAYERANNOTATIONS);
		final int idAnnoSetId = cursor.getColumnIndex(ValenceUnits_Layers_X.ANNOSETID);
		final int idSentenceText = cursor.getColumnIndex(ValenceUnits_Layers_X.SENTENCETEXT);

		final TreeOp[] changed = annoSets(parent, cursor, null, idSentenceText, idLayerType, idRank, idAnnotations, idAnnoSetId);

		cursor.close();
		return changed;
	}

	/**
	 * AnnoSets helper function
	 *
	 * @param parent         parent node
	 * @param cursor         cursor
	 * @param sentenceText   sentence text
	 * @param idSentenceText id of sentence column
	 * @param idLayerType    id of layer type column
	 * @param idRank         id of rank column
	 * @param idAnnotations  id of annotations column
	 * @param idAnnoSetId    id of annoSet i column
	 * @return changed nodes
	 */
	@NonNull
	private TreeOp[] annoSets(@NonNull final TreeNode parent, @NonNull final Cursor cursor, //
			@Nullable final String sentenceText, final int idSentenceText, final int idLayerType, final int idRank, final int idAnnotations, final int idAnnoSetId)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

			long currentAnnoSetId = -1;
			TreeNode annoSetNode = null;
			SpannableStringBuilder sb = null;
			SpannableStringBuilder sba = null;

			// read cursor
			do
			{
				final String layerType = cursor.getString(idLayerType);
				final String annotations = cursor.getString(idAnnotations);
				final long annoSetId = cursor.getLong(idAnnoSetId);
				final String rank = cursor.getString(idRank);
				final boolean isTarget = "Target".equals(layerType);
				final boolean isFE = "FE".equals(layerType);
				final String text = sentenceText != null ? sentenceText : cursor.getString(idSentenceText);

				// annoSet grouping
				if (currentAnnoSetId != annoSetId)
				{
					if (sb != null)
					{
						// attach result
						final TreeNode node = TreeFactory.makeTextNode(sb, false).addTo(annoSetNode);
						changedList.add(NEWCHILD, node);
					}
					sb = new SpannableStringBuilder();

					sba = new SpannableStringBuilder();
					Spanner.append(sba, "AnnoSet", 0, FrameNetFactories.annoSetFactory);
					sba.append(' ');
					Spanner.append(sba, Long.toString(annoSetId), 0, FrameNetFactories.dataFactory);
					annoSetNode = TreeFactory.makeTreeNode(sba, R.drawable.annoset, false).addTo(parent);
					changedList.add(NEWCHILD, annoSetNode);
					currentAnnoSetId = annoSetId;
				}

				assert sb != null;
				if (sb.length() > 0)
				{
					sb.append('\n');
				}

				// layer
				Spanner.appendImage(sb, BaseModule.this.layerDrawable);
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
				final List<FnLabel> labels = Utils.parseLabels(annotations);
				if (labels != null)
				{
					for (final FnLabel label : labels)
					{
						sb.append('\n');
						sb.append('\t');
						sb.append('\t');

						// label
						Spanner.append(sb, label.label, 0, isFE ? //
								FrameNetFactories.feFactory :  //
								FrameNetFactories.labelFactory);
						sb.append(' ');

						// subtext value
						final int from = Integer.parseInt(label.from);
						final int to = Integer.parseInt(label.to) + 1;
						String subtext;
						final int len = text.length();
						if (from < 0 || to > len || from > to)
						{
							Log.d(TAG, "annoSetId=" + annoSetId + "annotations=" + annotations + "label=" + label + "text=" + text);
							subtext = label + " ERROR [" + label.from + ',' + label.to + ']';
						}
						else
						{
							subtext = text.substring(from, to);
						}
						final int p = sb.length();
						Spanner.append(sb, subtext, 0, (isTarget ?  //
								FrameNetFactories.targetFactory :  //
								FrameNetFactories.subtextFactory));

						// target
						if (isTarget)
						{
							sba.append(' ');
							Spanner.append(sba, subtext, 0, FrameNetFactories.targetFactory);
						}

						// value colors
						if (label.bgColor != null)
						{
							final int color = Integer.parseInt(label.bgColor, 16);
							sb.setSpan(new BackgroundColorSpan(color | 0xFF000000), p, p + subtext.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
						if (label.fgColor != null)
						{
							final int color = Integer.parseInt(label.fgColor, 16);
							sb.setSpan(new ForegroundColorSpan(color | 0xFF000000), p, p + subtext.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
					}
				}
			}
			while (cursor.moveToNext());

			// attach remaining result
			if (sb.length() > 0)
			{
				// attach result
				final TreeNode node = TreeFactory.makeTextNode(sb, false).addTo(annoSetNode);
				changedList.add(NEWCHILD, node);
			}

			changed = changedList.toArray();
		}
		else
		{
			TreeFactory.setNoResult(parent);
			changed = TreeOp.seq(REMOVE, parent);
		}
		return changed;
	}

	// L A Y E R S

	/**
	 * Layers for sentence
	 *
	 * @param sentenceId sentence id
	 * @param text       reference sentence text
	 * @param parent     parent node
	 */
	void layersForSentence(final long sentenceId, final String text, @NonNull final TreeNode parent)
	{
		final ContentProviderSql sql = Queries.prepareLayersForSentence(sentenceId);
		final Uri uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri));
		this.layersFromSentenceIdModel.loadData(uri, sql, cursor -> layersCursorToTreeModel(cursor, text, parent));
	}

	@NonNull
	private TreeOp[] layersCursorToTreeModel(@NonNull final Cursor cursor, final String text, @NonNull final TreeNode parent)
	{
		// column indices
		final int idLayerType = cursor.getColumnIndex(Sentences_Layers_X.LAYERTYPE);
		final int idRank = cursor.getColumnIndex(Sentences_Layers_X.RANK);
		final int idAnnotations = cursor.getColumnIndex(Sentences_Layers_X.LAYERANNOTATIONS);
		final int idAnnoSetId = cursor.getColumnIndex(Sentences_Layers_X.ANNOSETID);

		final TreeOp[] changed = annoSets(parent, cursor, text, -1, idLayerType, idRank, idAnnotations, idAnnoSetId);

		cursor.close();
		return changed;
	}

	// A G E N T S

	/**
	 * Process definition
	 *
	 * @param text  text
	 * @param flags flags
	 * @return processed definition
	 */
	@NonNull
	private CharSequence[] processDefinition(final CharSequence text, final long flags)
	{
		boolean isFrame = (flags & FrameNetMarkupFactory.FEDEF) == 0;
		CharSequence[] texts = this.processor.split(text);
		CharSequence[] fields = new CharSequence[texts.length];

		assert this.spanner != null;
		for (int i = 0; i < texts.length; i++)
		{
			CharSequence field = isFrame ? this.frameProcessor.process(texts[i]) : texts[i];
			assert field != null;
			if (i == 0)
			{
				fields[i] = this.spanner.process(field, flags, isFrame ? FrameNetFactories.metaFrameDefinitionFactory : FrameNetFactories.metaFeDefinitionFactory);
			}
			else
			{
				fields[i] = this.spanner.process(field, flags, null);
			}
		}
		return fields;
	}

	/**
	 * Process layer name
	 *
	 * @param name layer name
	 * @return processed layer name
	 */
	@NonNull
	private CharSequence processLayer(@NonNull final CharSequence name)
	{
		if ("FE".contentEquals(name)) //
		{
			return "Frame element";
		}
		if ("PT".contentEquals(name)) //
		{
			return "Phrase type";
		}
		if ("GF".contentEquals(name)) //
		{
			return "Grammatical function";
		}
		return name;
	}

	/**
	 * Process PT
	 *
	 * @param name PT name
	 * @return processed PT
	 */
	@NonNull
	private CharSequence processPT(@NonNull final CharSequence name)
	{
		if ("CNI".contentEquals(name)) //
		{
			return "constructional ∅";
		}
		if ("DNI".contentEquals(name)) //
		{
			return "definite ∅";
		}
		if ("INI".contentEquals(name)) //
		{
			return "indefinite ∅";
		}
		return name;
	}

	// Q U E R I E S

	/**
	 * Frame query
	 */
	class FrameQuery extends Query
	{
		/**
		 * Constructor
		 *
		 * @param frameId frame id
		 */
		FrameQuery(final long frameId)
		{
			super(frameId);
		}

		@Override
		public void process(@NonNull final TreeNode node)
		{
			frame((int) this.id, node);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "frame for " + this.id;
		}
	}

	/**
	 * Related frame query
	 */
	class RelatedQuery extends Query
	{
		/**
		 * Constructor
		 *
		 * @param frameId frame id
		 */
		RelatedQuery(final long frameId)
		{
			super(frameId);
		}

		@Override
		public void process(@NonNull final TreeNode node)
		{
			relatedFrames((int) this.id, node);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "related for " + this.id;
		}
	}

	/**
	 * Lex units query
	 */
	class LexUnitsQuery extends Query
	{
		/**
		 * Constructor
		 *
		 * @param frameId frame id
		 */
		LexUnitsQuery(final long frameId)
		{
			super(frameId);
		}

		@Override
		public void process(@NonNull final TreeNode node)
		{
			lexUnitsForFrame((int) this.id, node, false);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "lexunits for frame " + this.id;
		}
	}

	/**
	 * Frame elements query
	 */
	class FEsQuery extends Query
	{
		/**
		 * Constructor
		 *
		 * @param frameId frame id
		 */
		FEsQuery(final long frameId)
		{
			super(frameId);
		}

		@Override
		public void process(@NonNull final TreeNode node)
		{
			fesForFrame((int) this.id, node);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "fes for frame " + this.id;
		}
	}

	/**
	 * Governors query
	 */
	class GovernorsQuery extends Query
	{
		/**
		 * Constructor
		 *
		 * @param luId lex unit id
		 */
		GovernorsQuery(final long luId)
		{
			super(luId);
		}

		@Override
		public void process(@NonNull final TreeNode node)
		{
			governorsForLexUnit(this.id, node);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "govs for lu " + this.id;
		}
	}

	/**
	 * Realizations query
	 */
	class RealizationsQuery extends Query
	{
		/**
		 * Constructor
		 *
		 * @param luId lex unit id
		 */
		RealizationsQuery(final long luId)
		{
			super(luId);
		}

		@Override
		public void process(@NonNull final TreeNode node)
		{
			realizationsForLexicalUnit(this.id, node);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "realizations for lu " + this.id;
		}
	}

	/**
	 * Group realizations query
	 */
	class GroupRealizationsQuery extends Query
	{
		/**
		 * Constructor
		 *
		 * @param luId lex unit id
		 */
		GroupRealizationsQuery(final long luId)
		{
			super(luId);
		}

		@Override
		public void process(@NonNull final TreeNode node)
		{
			groupRealizationsForLexUnit(this.id, node);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "grouprealizations for lu " + this.id;
		}
	}

	/**
	 * Sentences for pattern query
	 */
	class SentencesForPatternQuery extends Query
	{
		/**
		 * Constructor
		 *
		 * @param patternId pattern id
		 */
		SentencesForPatternQuery(final long patternId)
		{
			super(patternId);
		}

		@Override
		public void process(@NonNull final TreeNode node)
		{
			sentencesForPattern(this.id, node);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "sentences for pattern " + this.id;
		}
	}

	/**
	 * Sentences for valence unit query
	 */
	class SentencesForValenceUnitQuery extends Query
	{
		/**
		 * Constructor
		 *
		 * @param vuId valence unit id
		 */
		SentencesForValenceUnitQuery(final long vuId)
		{
			super(vuId);
		}

		@Override
		public void process(@NonNull final TreeNode node)
		{
			sentencesForValenceUnit(this.id, node);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "sentences for vu " + this.id;
		}
	}

	/**
	 * Sentences for lex unit query
	 */
	class SentencesForLexUnitQuery extends Query
	{
		/**
		 * Constructor
		 *
		 * @param luId lex unit id
		 */
		SentencesForLexUnitQuery(final long luId)
		{
			super(luId);
		}

		@Override
		public void process(@NonNull final TreeNode node)
		{
			sentencesForLexUnit(this.id, node);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "sentences for lu " + this.id;
		}
	}

	/**
	 * AnnoSet query
	 */
	class AnnoSetQuery extends Query
	{
		private final boolean withSentence;

		/**
		 * Constructor
		 *
		 * @param annoSetId annoSet id
		 */
		AnnoSetQuery(final long annoSetId, @SuppressWarnings("SameParameterValue") final boolean withSentence)
		{
			super(annoSetId);
			this.withSentence = withSentence;
		}

		@Override
		public void process(@NonNull final TreeNode node)
		{
			annoSet(this.id, node, this.withSentence);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "annoset for " + this.id;
		}
	}

	/**
	 * AnnoSets for governor query
	 */
	class AnnoSetsForGovernorQuery extends Query
	{
		/**
		 * Constructor
		 *
		 * @param governorId governor id
		 */
		AnnoSetsForGovernorQuery(final long governorId)
		{
			super(governorId);
		}

		@Override
		public void process(@NonNull final TreeNode node)
		{
			annoSetsForGovernor(this.id, node);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "annosets for gov " + this.id;
		}
	}

	// R E L A T I O N S

	/**
	 * Fn frame link data
	 */
	class FnFrameLink extends Link
	{
		/**
		 * Constructor
		 *
		 * @param frameId frame id
		 */
		FnFrameLink(final long frameId)
		{
			super(frameId);
		}

		@Override
		public void process()
		{
			final Context context = BaseModule.this.fragment.getContext();
			if (context == null)
			{
				return;
			}

			final Parcelable pointer = new FnFramePointer(this.id);
			final Intent intent = new Intent(context, FnFrameActivity.class);
			intent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_FNFRAME);
			intent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer);
			intent.setAction(ProviderArgs.ACTION_QUERY);
			context.startActivity(intent);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "frame for " + this.id;
		}
	}

	/**
	 * Fn lex unit link data
	 */
	class FnLexUnitLink extends Link
	{
		/**
		 * Constructor
		 *
		 * @param luId lex unit id
		 */
		FnLexUnitLink(final long luId)
		{
			super(luId);
		}

		@Override
		public void process()
		{
			final Context context = BaseModule.this.fragment.getContext();
			if (context == null)
			{
				return;
			}

			final Parcelable pointer = new FnLexUnitPointer(this.id);
			final Intent intent = new Intent(context, FnLexUnitActivity.class);
			intent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_FNLEXUNIT);
			intent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer);
			intent.setAction(ProviderArgs.ACTION_QUERY);
			context.startActivity(intent);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "lexunit for " + this.id;
		}
	}

	/**
	 * Fn sentence link data
	 */
	class FnSentenceLink extends Link
	{
		/**
		 * Constructor
		 *
		 * @param sentenceId sentence id
		 */
		FnSentenceLink(final long sentenceId)
		{
			super(sentenceId);
		}

		@Override
		public void process()
		{
			final Context context = BaseModule.this.fragment.getContext();
			if (context == null)
			{
				return;
			}

			final Parcelable pointer = new FnSentencePointer(this.id);
			final Intent intent = new Intent(context, FnSentenceActivity.class);
			intent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_FNSENTENCE);
			intent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer);
			intent.setAction(ProviderArgs.ACTION_QUERY);
			context.startActivity(intent);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "sentence for " + this.id;
		}
	}

	/*
	 * Dummy query
	 */
	/*
	class DummyQuery extends Query
	{
		static private final String TAG = "DummyQuery";

		@SuppressWarnings("unused")
		public DummyQuery(final long annoSetId)
		{
			super(annoSetId);
		}

		@Override
		public void process(final TreeNode node)
		{
			Log.d(TAG, "QUERY " + this.id);
		}
	}
	*/
}
