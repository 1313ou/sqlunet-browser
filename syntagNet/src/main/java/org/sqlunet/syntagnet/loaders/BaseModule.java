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
import org.sqlunet.syntagnet.provider.SyntagNetContract.SnCollocations_X;
import org.sqlunet.syntagnet.provider.SyntagNetProvider;
import org.sqlunet.syntagnet.style.SyntagNetFactories;
import org.sqlunet.syntagnet.style.SyntagNetSpanner;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.view.TreeOp;
import org.sqlunet.view.TreeOp.TreeOps;
import org.sqlunet.view.TreeOpExecute;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import static org.sqlunet.view.TreeOp.TreeOpCode.NEWCHILD;
import static org.sqlunet.view.TreeOp.TreeOpCode.NEWMAIN;
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
		this.afterDrawable = Spanner.getDrawable(context, R.drawable.info);
		this.beforeDrawable = Spanner.getDrawable(context, R.drawable.info);

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
				SnCollocations_X.WORD1ID, //
				SnCollocations_X.WORD2ID, //
				SnCollocations_X.SYNSET1ID, //
				SnCollocations_X.SYNSET2ID, //
				SnCollocations_X.WORD1, //
				SnCollocations_X.WORD2,};
		final String selection = SnCollocations_X.COLLOCATIONID + " = ?";
		final String[] selectionArgs = {Long.toString(collocationId)};
		this.collocationFromCollocationIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> collocationCursorToTreeModel(cursor, collocationId, parent));
	}

	private TreeOp[] collocationCursorToTreeModel(@NonNull final Cursor cursor, final long collocationId, @NonNull final TreeNode parent)
	{
		if (cursor.getCount() > 1)
		{
			throw new RuntimeException("Unexpected number of rows");
		}

		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			// column indices
			//final int idCollocationId = cursor.getColumnIndex(SnCollocations_X.COLLOCATIONID);
			final int idWord1Id = cursor.getColumnIndex(SnCollocations_X.WORD1ID);
			final int idWord2Id = cursor.getColumnIndex(SnCollocations_X.WORD2ID);
			final int idSynset1Id = cursor.getColumnIndex(SnCollocations_X.SYNSET1ID);
			final int idSynset2Id = cursor.getColumnIndex(SnCollocations_X.SYNSET2ID);
			final int idWord1 = cursor.getColumnIndex(SnCollocations_X.WORD1);
			final int idWord2 = cursor.getColumnIndex(SnCollocations_X.WORD2);

			// read cursor
			final SpannableStringBuilder sb = new SpannableStringBuilder();

			// data
			//final int collocationId = cursor.getInt(idCollocationId);

			// words
			Spanner.appendImage(sb, BaseModule.this.beforeDrawable);
			sb.append(' ');
			Spanner.append(sb, cursor.getString(idWord1), 0, SyntagNetFactories.roleSetFactory);
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

			// sub nodes
			//TODO
			//final TreeNode rolesNode = TreeFactory.makeHotQueryNode("More", R.drawable.roles, false, new RolesQuery(roleSetId)).addTo(parent);
			//final TreeNode examplesNode = TreeFactory.makeQueryNode("More", R.drawable.sample, false, new ExamplesQuery(roleSetId)).addTo(parent);
			//changed = TreeOp.seq(NEWMAIN, node, NEWEXTRA, rolesNode, NEWEXTRA, examplesNode, NEWTREE, parent);

			changed = TreeOp.seq(NEWMAIN, node, NEWTREE, parent);
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
				SnCollocations_X.WORD1, //
				SnCollocations_X.WORD2,};
		final String selection = SnCollocations_X.WORD1ID + " = ? OR " + SnCollocations_X.WORD2ID + " = ?";
		final String[] selectionArgs = {Long.toString(wordId)};
		this.collocationsFromWordIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> collocationsCursorToTreeModel(cursor, parent));
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
				SnCollocations_X.WORD1, //
				SnCollocations_X.WORD2,};
		final String selection = SnCollocations_X.WORD1ID + " = ? OR " + SnCollocations_X.WORD2ID + " = ?";
		final String[] selectionArgs = {word};
		this.collocationsFromWordModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> collocationsCursorToTreeModel(cursor, parent));
	}

	@NonNull
	private TreeOp[] collocationsCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
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
			final int idWord1 = cursor.getColumnIndex(SnCollocations_X.WORD1);
			final int idWord2 = cursor.getColumnIndex(SnCollocations_X.WORD2);

			// read cursor
			do
			{
				final SpannableStringBuilder sb = new SpannableStringBuilder();

				// data
				// final int collocationId = cursor.getInt(idCollocationId);

				// words
				Spanner.appendImage(sb, BaseModule.this.beforeDrawable);
				sb.append(' ');
				Spanner.append(sb, cursor.getString(idWord1), 0, SyntagNetFactories.roleSetFactory);
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
