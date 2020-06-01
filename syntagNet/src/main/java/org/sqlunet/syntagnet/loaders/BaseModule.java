/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.syntagnet.loaders;

import android.annotation.SuppressLint;
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
import org.sqlunet.treeview.control.Link;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.view.TreeOp;
import org.sqlunet.view.TreeOp.TreeOps;
import org.sqlunet.view.TreeOpExecute;
import org.sqlunet.wordnet.loaders.BaseModule.BaseSynsetLink;
import org.sqlunet.wordnet.loaders.BaseModule.BaseWordLink;

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

	/*
	 * Drawable info
	 */
	//private final Drawable infoDrawable;

	// agents

	// view models

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
		//final Context context = BaseModule.this.fragment.requireContext();
		//this.infoDrawable = Spanner.getDrawable(context, R.drawable.info);
	}

	/**
	 * Whether target comes second in collocation
	 */
	abstract protected boolean isTargetSecond(final long word1Id, final long word2Id);

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
		final String[] selectionArgs = selectionArgs(word1Id, word2Id, synset1Id, synset2Id, word2Id);
		String sortOrder = SyntagNetContract.W1 + '.' + SnCollocations_X.LEMMA + ',' + SyntagNetContract.W2 + '.' + SnCollocations_X.LEMMA;
		if (word2Id != null)
		{
			sortOrder = SnCollocations_X.WORD2ID + " = ?" + ',' + sortOrder;
		}
		this.collocationFromCollocationIdModel.loadData(uri, projection, selection, selectionArgs, sortOrder, cursor -> collocationsCursorToTreeModel(cursor, parent));
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
		final String[] selectionArgs = {Long.toString(wordId), Long.toString(wordId), Long.toString(wordId)};
		final String sortOrder = SnCollocations_X.WORD2ID + " = ?" + ',' + SyntagNetContract.W1 + '.' + SnCollocations_X.LEMMA + ',' + SyntagNetContract.W2 + '.' + SnCollocations_X.LEMMA;
		this.collocationsFromWordIdModel.loadData(uri, projection, selection, selectionArgs, sortOrder, cursor -> collocationsCursorToTreeModel(cursor, parent));
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
		final String sortOrder = SnCollocations_X.WORD2ID + " = ?" + ',' + SyntagNetContract.W1 + '.' + SnCollocations_X.LEMMA + ',' + SyntagNetContract.W2 + '.' + SnCollocations_X.LEMMA;
		this.collocationsFromWordModel.loadData(uri, projection, selection, selectionArgs, sortOrder, cursor -> collocationsCursorToTreeModel(cursor, parent));
	}

	private TreeOp[] collocationCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		if (cursor.getCount() > 1)
		{
			throw new RuntimeException("Unexpected number of rows");
		}
		return collocationsCursorToTreeModel(cursor, parent);
	}

	private TreeOp[] collocationsCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

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
			boolean isSingle = cursor.getCount() == 1;
			do
			{
				// data
				final int collocationId = cursor.getInt(idCollocationId);
				final String word1 = cursor.getString(idWord1);
				final String word2 = cursor.getString(idWord2);
				final long word1Id = cursor.getLong(idWord1Id);
				final long word2Id = cursor.getLong(idWord2Id);
				final long synset1Id = cursor.getLong(idSynset1Id);
				final long synset2Id = cursor.getLong(idSynset2Id);
				final String pos1 = cursor.getString(idPos1);
				final String pos2 = cursor.getString(idPos2);
				final String definition1 = cursor.getString(idDefinition1);
				final String definition2 = cursor.getString(idDefinition2);
				boolean isTargetSecond = isTargetSecond(word1Id, word2Id);

				makeContent(collocationId, word1, word2, word1Id, word2Id, synset1Id, synset2Id, pos1, pos2, definition1, definition2, isSingle, isTargetSecond, parent, changedList);

				// sub nodes
				//TODO
				//final TreeNode moreNode = TreeFactory.makeHotQueryNode("More", R.drawable.more, false, new MoreQuery(collocationId)).addTo(parent);
				//final TreeNode more2Node = TreeFactory.makeQueryNode("More2", R.drawable.more2, false, new More2Query(collocationId)).addTo(parent);
				//changed = TreeOp.seq(NEWMAIN, node, NEWEXTRA, moreNode, NEWEXTRA, more2Node, NEWTREE, parent);
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

		return wordSelection.isEmpty() || synsetSelection.isEmpty() ? (wordSelection + synsetSelection) : ('(' + wordSelection + ") AND (" + synsetSelection + ')');
	}

	/**
	 * Make selection arguments
	 *
	 * @param word1Id   word 1 id
	 * @param word2Id   word 2 id
	 * @param synset1Id synset 1 id
	 * @param synset2Id synset 2 id
	 * @param orderId   id param used in order clause
	 * @return selection arguments
	 */
	private String[] selectionArgs(final Long word1Id, final Long word2Id, final Long synset1Id, final Long synset2Id, final Long orderId)
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
		if (orderId != null)
		{
			args.add(Long.toString(orderId));
		}
		return args.toArray(new String[0]);
	}

	@SuppressLint("DefaultLocale")
	private void makeContent(final int collocationId, //
			final String word1, final String word2,  //
			final long word1Id, final long word2Id, //
			final long synset1Id, final long synset2Id, //
			final String pos1, final String pos2,  //
			final String definition1, final String definition2, //
			final boolean isSingle, final boolean isTargetSecond, final TreeNode parent, final TreeOps changedList)
	{
		final Context context = BaseModule.this.fragment.requireContext();

		// header
		final SpannableStringBuilder sbh = new SpannableStringBuilder();
		Spanner.append(sbh, word1, 0, SyntagNetFactories.collocationFactory);
		sbh.append(' ');
		sbh.append(pos1);
		sbh.append(' ');
		Spanner.append(sbh, word2, 0, SyntagNetFactories.collocationFactory);
		sbh.append(' ');
		sbh.append(pos2);
		sbh.append(' ');
		Spanner.append(sbh, Long.toString(collocationId), 0, SyntagNetFactories.idsFactory);

		// collocation
		final TreeNode collocationNode = TreeFactory.makeTreeNode(sbh, R.drawable.collocation, !isSingle).addTo(parent);
		changedList.add(NEWCHILD, collocationNode);

		// contents

		// collocation 1
		final SpannableStringBuilder sb1w = new SpannableStringBuilder();
		Spanner.append(sb1w, word1, 0, isTargetSecond ? SyntagNetFactories.word2Factory : SyntagNetFactories.word1Factory);
		final Link link1w = new BaseWordLink(word1Id, context);
		final TreeNode collocation1wNode = TreeFactory.makeLinkNode(sb1w, isTargetSecond ? R.drawable.collocation2 : R.drawable.collocation1, false, link1w).addTo(collocationNode);
		changedList.add(NEWCHILD, collocation1wNode);

		final SpannableStringBuilder sb1s = new SpannableStringBuilder();
		Spanner.append(sb1s, definition1, 0, isTargetSecond ? SyntagNetFactories.definition2Factory : SyntagNetFactories.definition1Factory);
		final Link link1s = new BaseSynsetLink(synset1Id, Integer.MAX_VALUE, context);
		final TreeNode collocation1sNode = TreeFactory.makeLinkNode(sb1s, isTargetSecond ? R.drawable.definition2 : R.drawable.definition1, false, link1s).addTo(collocationNode);
		changedList.add(NEWCHILD, collocation1sNode);

		// collocation 2
		final SpannableStringBuilder sb2w = new SpannableStringBuilder();
		Spanner.append(sb2w, word2, 0, isTargetSecond ? SyntagNetFactories.word1Factory : SyntagNetFactories.word2Factory);
		final Link link2w = new BaseWordLink(word2Id, context);
		final TreeNode collocation2wNode = TreeFactory.makeLinkNode(sb2w, isTargetSecond ? R.drawable.collocation1 : R.drawable.collocation2, false, link2w).addTo(collocationNode);
		changedList.add(NEWCHILD, collocation2wNode);

		final SpannableStringBuilder sb2s = new SpannableStringBuilder();
		Spanner.append(sb2s, definition2, 0, isTargetSecond ? SyntagNetFactories.definition1Factory : SyntagNetFactories.definition2Factory);
		final Link link2s = new BaseSynsetLink(synset2Id, Integer.MAX_VALUE, context);
		final TreeNode collocation2sNode = TreeFactory.makeLinkNode(sb2s, isTargetSecond ? R.drawable.definition1 : R.drawable.definition2, false, link2s).addTo(collocationNode);
		changedList.add(NEWCHILD, collocation2sNode);

		// ids
		//final SpannableStringBuilder sbi = new SpannableStringBuilder();
		//Spanner.appendImage(sbi, BaseModule.this.infoDrawable);
		//Spanner.append(sbi, String.format(" %d %d , %d %d", word1Id, synset1Id, word2Id, synset2Id), 0, SyntagNetFactories.idsFactory);

		//final TreeNode extraNode = TreeFactory.makeTextNode(sbi, false).addTo(collocationNode);
		//changedList.add(NEWCHILD, extraNode);
	}
}
