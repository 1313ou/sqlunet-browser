/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.syntagnet.loaders;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.SpannableStringBuilder;

import org.sqlunet.browser.Module;
import org.sqlunet.browser.SqlunetViewTreeModel;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.model.TreeFactory;
import org.sqlunet.style.Spanner;
import org.sqlunet.syntagnet.R;
import org.sqlunet.syntagnet.provider.SyntagNetContract;
import org.sqlunet.syntagnet.provider.SyntagNetContract.SnCollocations_X;
import org.sqlunet.syntagnet.provider.SyntagNetProvider;
import org.sqlunet.syntagnet.style.SyntagNetFactories;
import org.sqlunet.syntagnet.style.SyntagNetSpanner;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.view.TreeOp;
import org.sqlunet.view.TreeOp.TreeOps;
import org.sqlunet.view.TreeOpExecute;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import static org.sqlunet.view.TreeOp.TreeOpCode.NEWCHILD;
import static org.sqlunet.view.TreeOp.TreeOpCode.NEWTREE;
import static org.sqlunet.view.TreeOp.TreeOpCode.REMOVE;

/**
 * Module for SyntagNet collocations
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract class BaseModule extends Module
{
	// resources

	/**
	 * Drawable for following collocation (after)
	 */
	private final Drawable afterDrawable;

	/**
	 * Drawable for preceding collocation (before)
	 */
	private final Drawable beforeDrawable;

	/**
	 * Drawable for following collocation definition (after)
	 */
	private final Drawable afterDefinitionDrawable;

	/**
	 * Drawable for preceding collocation definition (before)
	 */
	private final Drawable beforeDefinitionDrawable;

	/**
	 * Drawable info
	 */
	private final Drawable infoDrawable;

	// agents

	/**
	 * Spanner
	 */
	@NonNull
	private final SyntagNetSpanner spanner;

	// View models

	private SqlunetViewTreeModel collocationFromCollocationIdModel;

	private SqlunetViewTreeModel collocationsFromWordIdModel;

	private SqlunetViewTreeModel collocationsFromWordModel;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	BaseModule(@NonNull final TreeFragment fragment)
	{
		super(fragment);

		// models
		makeModels();

		// drawables
		final Context context = BaseModule.this.fragment.requireContext();
		this.beforeDrawable = Spanner.getDrawable(context, R.drawable.before);
		this.afterDrawable = Spanner.getDrawable(context, R.drawable.after);
		this.beforeDefinitionDrawable = Spanner.getDrawable(context, R.drawable.definition1);
		this.afterDefinitionDrawable = Spanner.getDrawable(context, R.drawable.definition2);
		this.infoDrawable = Spanner.getDrawable(context, R.drawable.info);

		// spanner
		this.spanner = new SyntagNetSpanner(context);
	}

	/**
	 * Make view models
	 */
	private void makeModels()
	{
		this.collocationFromCollocationIdModel = new ViewModelProvider(this.fragment).get("sn.collocation(collocationid)", SqlunetViewTreeModel.class);
		this.collocationFromCollocationIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.collocationsFromWordIdModel = new ViewModelProvider(this.fragment).get("sn.collocations(wordid)", SqlunetViewTreeModel.class);
		this.collocationsFromWordIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.collocationsFromWordModel = new ViewModelProvider(this.fragment).get("sn.collocations(word)", SqlunetViewTreeModel.class);
		this.collocationsFromWordModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));
	}

	// C O L L O C A T I O N S

	/**
	 * Collocation from id
	 *
	 * @param collocationId collocation id
	 * @param parent        parent node
	 */
	void collocation(final long collocationId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(SyntagNetProvider.makeUri(SnCollocations_X.CONTENT_URI_TABLE));
		final String[] projection = { //
				SnCollocations_X.COLLOCATIONID, //
				SnCollocations_X.WORD1ID, //
				SnCollocations_X.WORD2ID, //
				SnCollocations_X.SYNSET1ID, //
				SnCollocations_X.SYNSET2ID, //
				SyntagNetContract.W1 + '.' + SnCollocations_X.LEMMA + " AS " + SyntagNetContract.WORD1, //
				SyntagNetContract.W2 + '.' + SnCollocations_X.LEMMA + " AS " + SyntagNetContract.WORD2, //
				SyntagNetContract.S1 + '.' + SnCollocations_X.DEFINITION + " AS " + SyntagNetContract.DEFINITION1, //
				SyntagNetContract.S2 + '.' + SnCollocations_X.DEFINITION + " AS " + SyntagNetContract.DEFINITION2, //
				SyntagNetContract.S1 + '.' + SnCollocations_X.POS + " AS " + SyntagNetContract.POS1, //
				SyntagNetContract.S2 + '.' + SnCollocations_X.POS + " AS " + SyntagNetContract.POS2, //
		};
		final String selection = SnCollocations_X.COLLOCATIONID + " = ?";
		final String[] selectionArgs = {Long.toString(collocationId)};
		this.collocationFromCollocationIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> collocationCursorToTreeModel(cursor, parent));
	}

	private TreeOp[] collocationCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		if (cursor.getCount() > 1)
		{
			throw new RuntimeException("Unexpected number of rows");
		}
		return collocationsCursorToTreeModel(cursor, parent);
	}

	/**
	 * Collocation from ids
	 *
	 * @param word1Id   word 1 id
	 * @param word2Id   word 2 id
	 * @param synset1Id synset 1 id
	 * @param synset2Id synset 2 id
	 * @param parent    parent node
	 */
	void collocations(final Long word1Id, final Long word2Id, final Long synset1Id, final Long synset2Id, final TreeNode parent)
	{
		final Uri uri = Uri.parse(SyntagNetProvider.makeUri(SnCollocations_X.CONTENT_URI_TABLE));
		final String[] projection = { //
				SnCollocations_X.COLLOCATIONID, //
				SnCollocations_X.WORD1ID, //
				SnCollocations_X.WORD1ID, //
				SnCollocations_X.WORD2ID, //
				SnCollocations_X.SYNSET1ID, //
				SnCollocations_X.SYNSET2ID, //
				SyntagNetContract.W1 + '.' + SnCollocations_X.LEMMA + " AS " + SyntagNetContract.WORD1, //
				SyntagNetContract.W2 + '.' + SnCollocations_X.LEMMA + " AS " + SyntagNetContract.WORD2, //
				SyntagNetContract.S1 + '.' + SnCollocations_X.DEFINITION + " AS " + SyntagNetContract.DEFINITION1, //
				SyntagNetContract.S2 + '.' + SnCollocations_X.DEFINITION + " AS " + SyntagNetContract.DEFINITION2, //
				SyntagNetContract.S1 + '.' + SnCollocations_X.POS + " AS " + SyntagNetContract.POS1, //
				SyntagNetContract.S2 + '.' + SnCollocations_X.POS + " AS " + SyntagNetContract.POS2, //
		};
		final String selection = selection(word1Id, word2Id, synset1Id, synset2Id);
		final String[] selectionArgs = selectionArgs(word1Id, word2Id, synset1Id, synset2Id);
		this.collocationFromCollocationIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> collocationsCursorToTreeModel(cursor, parent));
	}

	private TreeOp[] collocationsCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			// column indices
			final int idCollocationId = cursor.getColumnIndex(SnCollocations_X.COLLOCATIONID);
			final int idWord1Id = cursor.getColumnIndex(SnCollocations_X.WORD1ID);
			final int idWord2Id = cursor.getColumnIndex(SnCollocations_X.WORD2ID);
			final int idSynset1Id = cursor.getColumnIndex(SnCollocations_X.SYNSET1ID);
			final int idSynset2Id = cursor.getColumnIndex(SnCollocations_X.SYNSET2ID);
			final int idWord1 = cursor.getColumnIndex(SyntagNetContract.WORD1);
			final int idWord2 = cursor.getColumnIndex(SyntagNetContract.WORD2);
			final int idDefinition1 = cursor.getColumnIndex(SyntagNetContract.DEFINITION1);
			final int idDefinition2 = cursor.getColumnIndex(SyntagNetContract.DEFINITION2);
			final int idPos1 = cursor.getColumnIndex(SyntagNetContract.POS1);
			final int idPos2 = cursor.getColumnIndex(SyntagNetContract.POS2);

			// read cursor
			do
			{
				final SpannableStringBuilder sb = new SpannableStringBuilder();

				// data
				final int collocationId = cursor.getInt(idCollocationId);
				final String word1 = cursor.getString(idWord1);
				final String word2 = cursor.getString(idWord2);

				final TreeNode node = TreeFactory.makeTreeNode(word1 + " " + word2, R.drawable.info, false).addTo(parent);

				// words
				Spanner.appendImage(sb, BaseModule.this.beforeDrawable);
				sb.append(' ');
				Spanner.append(sb, word1, 0, SyntagNetFactories.beforeFactory);
				sb.append('\n');

				Spanner.appendImage(sb, BaseModule.this.beforeDefinitionDrawable);
				sb.append(' ');
				Spanner.append(sb, cursor.getString(idDefinition1), 0, SyntagNetFactories.beforeDefinitionFactory);
				sb.append('\n');

				Spanner.appendImage(sb, BaseModule.this.afterDrawable);
				sb.append(' ');
				Spanner.append(sb, word2, 0, SyntagNetFactories.afterFactory);
				sb.append('\n');

				Spanner.appendImage(sb, BaseModule.this.afterDefinitionDrawable);
				sb.append(' ');
				Spanner.append(sb, cursor.getString(idDefinition2), 0, SyntagNetFactories.afterDefinitionFactory);
				sb.append('\n');

				// ids
				Spanner.appendImage(sb, BaseModule.this.infoDrawable);
				sb.append(' ');
				sb.append(Long.toString(cursor.getLong(idWord1Id)));
				sb.append(' ');
				sb.append(Long.toString(cursor.getLong(idSynset1Id)));
				sb.append(" > ");
				sb.append(cursor.getString(idWord2Id));
				sb.append(' ');
				sb.append(cursor.getString(idSynset2Id));
				sb.append('\n');

				// attach result
				final TreeNode subnode = TreeFactory.makeTextNode(sb, true).addTo(node);

				// sub nodes
				//TODO
				//final TreeNode rolesNode = TreeFactory.makeHotQueryNode("More", R.drawable.roles, false, new RolesQuery(roleSetId)).addTo(parent);
				//final TreeNode examplesNode = TreeFactory.makeQueryNode("More", R.drawable.sample, false, new ExamplesQuery(roleSetId)).addTo(parent);
				//changed = TreeOp.seq(NEWMAIN, node, NEWEXTRA, rolesNode, NEWEXTRA, examplesNode, NEWTREE, parent);
			}
			while (cursor.moveToNext());
			changed = TreeOp.seq(NEWTREE, parent);
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
	 * Collocations for word id
	 *
	 * @param wordId word id
	 * @param parent parent node
	 */
	void collocations(final long wordId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(SyntagNetProvider.makeUri(SnCollocations_X.CONTENT_URI_TABLE));
		final String[] projection = { //
				SnCollocations_X.WORD1ID, //
				SnCollocations_X.WORD2ID, //
				SnCollocations_X.SYNSET1ID, //
				SnCollocations_X.SYNSET2ID, //
				SyntagNetContract.WORD1, //
				SyntagNetContract.WORD2,};
		final String selection = SnCollocations_X.WORD1ID + " = ? OR " + SnCollocations_X.WORD2ID + " = ?";
		final String[] selectionArgs = {Long.toString(wordId)};
		this.collocationsFromWordIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> collocationsCursorToTreeModel(cursor, parent));
	}

	/**
	 * Make selection. When both arg1 and arg2 are equal, take it to mean the position (after, before) is indifferent (so OR)
	 *
	 * @param word1Id   word 1 id
	 * @param word2Id   word 2 id
	 * @param synset1Id synset 1 id
	 * @param synset2Id synset 2 id
	 * @return selection string
	 */
	private String selection(final Long word1Id, final Long word2Id, final Long synset1Id, final Long synset2Id)
	{
		String wordSelection1 = word1Id == null ? "" : SnCollocations_X.WORD1ID + " = ?";
		String wordSelection2 = word2Id == null ? "" : SnCollocations_X.WORD2ID + " = ?";
		String wordSelection = wordSelection1.isEmpty() || wordSelection2.isEmpty() ? wordSelection1 + wordSelection2 : wordSelection1 + (word1Id.equals(word2Id) ? " OR " : " AND ") + wordSelection2;

		String synsetSelection1 = synset1Id == null ? "" : SnCollocations_X.SYNSET1ID + " = ?";
		String synsetSelection2 = synset2Id == null ? "" : SnCollocations_X.SYNSET2ID + " = ?";
		String synsetSelection = synsetSelection1.isEmpty() || synsetSelection2.isEmpty() ? synsetSelection1 + synsetSelection2 : synsetSelection1 + (synset1Id.equals(synset2Id) ? " OR " : " AND ") + synsetSelection2;

		return wordSelection.isEmpty() || synsetSelection.isEmpty() ? (wordSelection + synsetSelection) : (wordSelection + " AND " + synsetSelection);
	}

	/**
	 * Make selection arguments
	 *
	 * @param word1Id   word 1 id
	 * @param word2Id   word 2 id
	 * @param synset1Id synset 1 id
	 * @param synset2Id synset 2 id
	 * @return selection arguments
	 */
	private String[] selectionArgs(final Long word1Id, final Long word2Id, final Long synset1Id, final Long synset2Id)
	{
		List<String> args = new ArrayList<>();
		if (word1Id != null)
		{
			args.add(Long.toString(word1Id));
		}
		if (word2Id != null)
		{
			args.add(Long.toString(word2Id));
		}
		if (synset1Id != null)
		{
			args.add(Long.toString(synset1Id));
		}
		if (synset2Id != null)
		{
			args.add(Long.toString(synset2Id));
		}
		return args.toArray(new String[0]);
	}

	/**
	 * Collocations for word
	 *
	 * @param word word	 * @param parent parent node
	 */
	void collocations(final String word, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(SyntagNetProvider.makeUri(SnCollocations_X.CONTENT_URI_TABLE));
		final String[] projection = { //
				SnCollocations_X.WORD1ID, //
				SnCollocations_X.WORD2ID, //
				SnCollocations_X.SYNSET1ID, //
				SnCollocations_X.SYNSET2ID, //
				SyntagNetContract.WORD1, //
				SyntagNetContract.WORD2,};
		final String selection = SnCollocations_X.WORD1ID + " = ? OR " + SnCollocations_X.WORD2ID + " = ?";
		final String[] selectionArgs = {word};
		this.collocationsFromWordModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> collocationsCursorToTreeModel(cursor, parent));
	}

	@NonNull
	private TreeOp[] collocations2CursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOp.TreeOps changedList = new TreeOps(NEWTREE, parent);

			// column indices
			// final int idCollocationId = cursor.getColumnIndex(SnCollocations_X.COLLOCATIONID);
			final int idWord1Id = cursor.getColumnIndex(SnCollocations_X.WORD1ID);
			final int idWord2Id = cursor.getColumnIndex(SnCollocations_X.WORD2ID);
			final int idSynset1Id = cursor.getColumnIndex(SnCollocations_X.SYNSET1ID);
			final int idSynset2Id = cursor.getColumnIndex(SnCollocations_X.SYNSET2ID);
			final int idWord1 = cursor.getColumnIndex(SyntagNetContract.WORD1);
			final int idWord2 = cursor.getColumnIndex(SyntagNetContract.WORD2);

			// read cursor
			do
			{
				final SpannableStringBuilder sb = new SpannableStringBuilder();

				// data
				// final int collocationId = cursor.getInt(idCollocationId);

				// words
				Spanner.appendImage(sb, BaseModule.this.beforeDrawable);
				sb.append(' ');
				Spanner.append(sb, cursor.getString(idWord1), 0, SyntagNetFactories.beforeFactory);
				sb.append(' ');
				Spanner.appendImage(sb, BaseModule.this.afterDrawable);
				sb.append(' ');
				sb.append(cursor.getString(idWord2));
				sb.append('\n');

				// ids
				Spanner.appendImage(sb, BaseModule.this.beforeDrawable);
				sb.append(' ');
				sb.append(Long.toString(cursor.getLong(idWord1Id)));
				sb.append(' ');
				sb.append(Long.toString(cursor.getLong(idSynset1Id)));
				sb.append(" > ");
				Spanner.appendImage(sb, BaseModule.this.afterDrawable);
				sb.append(' ');
				sb.append(cursor.getString(idWord2Id));
				sb.append(' ');
				sb.append(cursor.getString(idSynset2Id));
				sb.append('\n');

				// attach result
				final TreeNode node = TreeFactory.makeTextNode(sb, false).addTo(parent);
				changedList.add(NEWCHILD, node);

				// sub nodes
				// TODO
				//final TreeNode rolesNode = TreeFactory.makeHotQueryNode("Roles", R.drawable.roles, false, new RolesQuery(roleSetId)).addTo(parent);
				//changedList.add(NEWCHILD, rolesNode);
				//final TreeNode examplesNode = TreeFactory.makeQueryNode("Examples", R.drawable.sample, false, new ExamplesQuery(roleSetId)).addTo(parent);
				//changedList.add(NEWCHILD, examplesNode);
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
}
