/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.wordnet.loaders;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;

import org.sqlunet.browser.Module;
import org.sqlunet.browser.SqlunetViewTreeModel;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.model.TreeFactory;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.style.Spanner;
import org.sqlunet.treeview.control.Link;
import org.sqlunet.treeview.control.Query;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.view.TreeOp;
import org.sqlunet.view.TreeOp.TreeOps;
import org.sqlunet.view.TreeOpExecute;
import org.sqlunet.wordnet.R;
import org.sqlunet.wordnet.SensePointer;
import org.sqlunet.wordnet.SynsetPointer;
import org.sqlunet.wordnet.WordPointer;
import org.sqlunet.wordnet.browser.SynsetActivity;
import org.sqlunet.wordnet.browser.WordActivity;
import org.sqlunet.wordnet.provider.WordNetContract;
import org.sqlunet.wordnet.provider.WordNetContract.BaseRelations_Senses_Words_X;
import org.sqlunet.wordnet.provider.WordNetContract.Domains;
import org.sqlunet.wordnet.provider.WordNetContract.LexRelations_Senses_Words_X;
import org.sqlunet.wordnet.provider.WordNetContract.Lexes_Morphs;
import org.sqlunet.wordnet.provider.WordNetContract.Poses;
import org.sqlunet.wordnet.provider.WordNetContract.Relations;
import org.sqlunet.wordnet.provider.WordNetContract.Samples;
import org.sqlunet.wordnet.provider.WordNetContract.SemRelations_Synsets_Words_X;
import org.sqlunet.wordnet.provider.WordNetContract.Senses;
import org.sqlunet.wordnet.provider.WordNetContract.Senses_AdjPositions;
import org.sqlunet.wordnet.provider.WordNetContract.Senses_VerbFrames;
import org.sqlunet.wordnet.provider.WordNetContract.Senses_VerbTemplates;
import org.sqlunet.wordnet.provider.WordNetContract.Senses_Words;
import org.sqlunet.wordnet.provider.WordNetContract.Synsets;
import org.sqlunet.wordnet.provider.WordNetContract.Synsets_Poses_Domains;
import org.sqlunet.wordnet.provider.WordNetContract.Words;
import org.sqlunet.wordnet.provider.WordNetContract.Words_Lexes_Morphs;
import org.sqlunet.wordnet.provider.WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains;
import org.sqlunet.wordnet.provider.WordNetProvider;
import org.sqlunet.wordnet.style.WordNetFactories;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import static org.sqlunet.view.TreeOp.TreeOpCode.DEADEND;
import static org.sqlunet.view.TreeOp.TreeOpCode.NEWCHILD;
import static org.sqlunet.view.TreeOp.TreeOpCode.NEWEXTRA;
import static org.sqlunet.view.TreeOp.TreeOpCode.NEWMAIN;
import static org.sqlunet.view.TreeOp.TreeOpCode.NEWTREE;
import static org.sqlunet.view.TreeOp.TreeOpCode.NEWUNIQUE;
import static org.sqlunet.view.TreeOp.TreeOpCode.NOOP;
import static org.sqlunet.view.TreeOp.TreeOpCode.REMOVE;
import static org.sqlunet.view.TreeOp.TreeOpCode.UPDATE;

/**
 * Base module for WordNet
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract public class BaseModule extends Module
{
	// Resources

	@NonNull
	private final Drawable synsetDrawable;

	@NonNull
	private final Drawable memberDrawable;

	@NonNull
	private final Drawable definitionDrawable;

	@NonNull
	private final Drawable sampleDrawable;

	@NonNull
	private final Drawable posDrawable;

	@NonNull
	private final Drawable domainDrawable;

	@NonNull
	private final Drawable verbframeDrawable;

	@NonNull
	private final Drawable morphDrawable;

	/**
	 * Whether members2 are grouped
	 */
	private boolean membersGrouped = false;

	/**
	 * Max relation recursion
	 */
	private int maxRecursion = Integer.MAX_VALUE;

	// View models

	private SqlunetViewTreeModel wordModel;

	private SqlunetViewTreeModel sensesFromWordModel;

	private SqlunetViewTreeModel sensesFromWordIdModel;

	private SqlunetViewTreeModel senseFromSenseIdModel;

	private SqlunetViewTreeModel senseFromSenseKeyModel;

	private SqlunetViewTreeModel senseFromSynsetIdWordIdModel;

	private SqlunetViewTreeModel synsetFromSynsetIdModel;

	private SqlunetViewTreeModel membersFromSynsetIdModel;

	private SqlunetViewTreeModel members2FromSynsetIdModel;

	private SqlunetViewTreeModel samplesfromSynsetIdModel;

	private SqlunetViewTreeModel relationsFromSynsetIdWordIdModel;

	private SqlunetViewTreeModel semRelationsFromSynsetIdModel;

	private SqlunetViewTreeModel semRelationsFromSynsetIdRelationIdModel;

	private SqlunetViewTreeModel lexRelationsFromSynsetIdWordIdModel;

	private SqlunetViewTreeModel lexRelationsFromSynsetIdModel;

	private SqlunetViewTreeModel vFramesFromSynsetIdModel;

	private SqlunetViewTreeModel vFramesFromSynsetIdWordIdModel;

	private SqlunetViewTreeModel vTemplatesFromSynsetIdModel;

	private SqlunetViewTreeModel vTemplatesFromSynsetIdWordIdModel;

	private SqlunetViewTreeModel adjPositionFromSynsetIdModel;

	private SqlunetViewTreeModel adjPositionFromSynsetIdWordIdModel;

	private SqlunetViewTreeModel morphsFromWordIdModel;

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
		this.synsetDrawable = Spanner.getDrawable(context, R.drawable.synset);
		this.memberDrawable = Spanner.getDrawable(context, R.drawable.synsetmember);
		this.definitionDrawable = Spanner.getDrawable(context, R.drawable.definition);
		this.sampleDrawable = Spanner.getDrawable(context, R.drawable.sample);
		this.posDrawable = Spanner.getDrawable(context, R.drawable.pos);
		this.domainDrawable = Spanner.getDrawable(context, R.drawable.domain);
		this.verbframeDrawable = Spanner.getDrawable(context, R.drawable.verbframe);
		this.morphDrawable = this.verbframeDrawable;
	}

	/**
	 * Make view models
	 */
	private void makeModels()
	{
		this.wordModel = new ViewModelProvider(this.fragment).get("wn.word(wordid)", SqlunetViewTreeModel.class);
		this.wordModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.sensesFromWordModel = new ViewModelProvider(this.fragment).get("wn.senses(word)", SqlunetViewTreeModel.class);
		this.sensesFromWordModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.sensesFromWordIdModel = new ViewModelProvider(this.fragment).get("wn.senses(wordid)", SqlunetViewTreeModel.class);
		this.sensesFromWordIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.senseFromSenseIdModel = new ViewModelProvider(this.fragment).get("wn.sense(senseid)", SqlunetViewTreeModel.class);
		this.senseFromSenseIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.senseFromSenseKeyModel = new ViewModelProvider(this.fragment).get("wn.sense(sensekey)", SqlunetViewTreeModel.class);
		this.senseFromSenseKeyModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.senseFromSynsetIdWordIdModel = new ViewModelProvider(this.fragment).get("wn.sense(synsetid,wordid)", SqlunetViewTreeModel.class);
		this.senseFromSynsetIdWordIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.synsetFromSynsetIdModel = new ViewModelProvider(this.fragment).get("wn.synset(synsetid)", SqlunetViewTreeModel.class);
		this.synsetFromSynsetIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.membersFromSynsetIdModel = new ViewModelProvider(this.fragment).get("wn.members2(synsetid)", SqlunetViewTreeModel.class);
		this.membersFromSynsetIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.members2FromSynsetIdModel = new ViewModelProvider(this.fragment).get("wn.members2(synsetid)", SqlunetViewTreeModel.class);
		this.members2FromSynsetIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.samplesfromSynsetIdModel = new ViewModelProvider(this.fragment).get("wn.samples(synsetid)", SqlunetViewTreeModel.class);
		this.samplesfromSynsetIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.relationsFromSynsetIdWordIdModel = new ViewModelProvider(this.fragment).get("wn.relations(synsetid,wordid)", SqlunetViewTreeModel.class);
		this.relationsFromSynsetIdWordIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.semRelationsFromSynsetIdModel = new ViewModelProvider(this.fragment).get("wn.semrelations(synsetid)", SqlunetViewTreeModel.class);
		this.semRelationsFromSynsetIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.semRelationsFromSynsetIdRelationIdModel = new ViewModelProvider(this.fragment).get("wn.semrelations(synsetid,relationid)", SqlunetViewTreeModel.class);
		this.semRelationsFromSynsetIdRelationIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.lexRelationsFromSynsetIdWordIdModel = new ViewModelProvider(this.fragment).get("wn.lexrelations(synsetid,wordid)", SqlunetViewTreeModel.class);
		this.lexRelationsFromSynsetIdWordIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.lexRelationsFromSynsetIdModel = new ViewModelProvider(this.fragment).get("wn.lexrelations(synsetid)", SqlunetViewTreeModel.class);
		this.lexRelationsFromSynsetIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.vFramesFromSynsetIdModel = new ViewModelProvider(this.fragment).get("wn.vframes(synsetid)", SqlunetViewTreeModel.class);
		this.vFramesFromSynsetIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.vFramesFromSynsetIdWordIdModel = new ViewModelProvider(this.fragment).get("wn.vframes(synsetid,wordid)", SqlunetViewTreeModel.class);
		this.vFramesFromSynsetIdWordIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.vTemplatesFromSynsetIdModel = new ViewModelProvider(this.fragment).get("wn.vtemplates(synsetid)", SqlunetViewTreeModel.class);
		this.vTemplatesFromSynsetIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.vTemplatesFromSynsetIdWordIdModel = new ViewModelProvider(this.fragment).get("wn.vtemplates(synsetid,wordid)", SqlunetViewTreeModel.class);
		this.vTemplatesFromSynsetIdWordIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.adjPositionFromSynsetIdModel = new ViewModelProvider(this.fragment).get("wn.adjposition(synsetid)", SqlunetViewTreeModel.class);
		this.adjPositionFromSynsetIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.adjPositionFromSynsetIdWordIdModel = new ViewModelProvider(this.fragment).get("wn.adjposition(synsetid,wordid)", SqlunetViewTreeModel.class);
		this.adjPositionFromSynsetIdWordIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.morphsFromWordIdModel = new ViewModelProvider(this.fragment).get("wn.morphs(wordid)", SqlunetViewTreeModel.class);
		this.morphsFromWordIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));
	}

	/**
	 * Set max recursion level
	 *
	 * @param maxRecursion max recursion level
	 */
	public void setMaxRecursionLevel(final int maxRecursion)
	{
		this.maxRecursion = maxRecursion == -1 ? Integer.MAX_VALUE : maxRecursion;
	}

	/**
	 * Set member grouping
	 *
	 * @param membersGrouped member grouping flag
	 */
	public void setMembersGrouped(final boolean membersGrouped)
	{
		this.membersGrouped = membersGrouped;
	}

	// W O R D

	static private final String ALLMORPHS = "allmorphs";

	/**
	 * Word
	 *
	 * @param wordId     word id
	 * @param parent     tree parent node
	 * @param addNewNode whether to addItem to (or set) node
	 */
	void word(final long wordId, @NonNull final TreeNode parent, @SuppressWarnings("SameParameterValue") final boolean addNewNode)
	{
		// load the contents
		final Uri uri = Uri.parse(WordNetProvider.makeUri(Words_Lexes_Morphs.CONTENT_URI_TABLE_BY_WORD));
		final String[] projection = { //
				Words_Lexes_Morphs.WORD, //
				Words_Lexes_Morphs.WORDID, //
				"GROUP_CONCAT(" + Words_Lexes_Morphs.MORPH + "||'-'||" + Words_Lexes_Morphs.POSID + ") AS " + BaseModule.ALLMORPHS};
		final String selection = Words.WORDID + " = ?";
		final String[] selectionArgs = {Long.toString(wordId)};
		this.wordModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> wordCursorToTreeModel(cursor, parent, addNewNode));
	}

	private TreeOp[] wordCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent, @SuppressWarnings("SameParameterValue") final boolean addNewNode)
	{
		if (cursor.getCount() > 1)
		{
			throw new RuntimeException("Unexpected number of rows");
		}

		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final SpannableStringBuilder sb = new SpannableStringBuilder();

			// final int idWordId = cursor.getColumnIndex(Words.WORDID);
			final int idLemma = cursor.getColumnIndex(Words_Lexes_Morphs.WORD);
			final int idMorphs = cursor.getColumnIndex(BaseModule.ALLMORPHS);
			final String lemma = cursor.getString(idLemma);
			final String morphs = cursor.getString(idMorphs);

			Spanner.appendImage(sb, BaseModule.this.memberDrawable);
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
				final TreeNode node = TreeFactory.makeTextNode(sb, false).addTo(parent);
				changed = TreeOp.seq(NEWUNIQUE, node);
			}
			else
			{
				TreeFactory.setTextNode(parent, sb);
				changed = TreeOp.seq(UPDATE, parent);
			}
		}
		else
		{
			TreeFactory.setNoResult(parent);
			changed = TreeOp.seq(REMOVE, parent);
		}

		cursor.close();
		return changed;
	}

	// S E N S E

	/**
	 * Senses from word
	 *
	 * @param word   word
	 * @param parent tree parent node
	 */
	protected void senses(final String word, @NonNull final TreeNode parent)
	{
		// load the contents
		final Uri uri = Uri.parse(WordNetProvider.makeUri(Words_Senses_CasedWords_Synsets_Poses_Domains.CONTENT_URI_TABLE));
		final String[] projection = { //
				Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID + " AS _id", //
				Words_Senses_CasedWords_Synsets_Poses_Domains.WORDID, //
				Words_Senses_CasedWords_Synsets_Poses_Domains.SENSEID, //
				Words_Senses_CasedWords_Synsets_Poses_Domains.SENSENUM, //
				Words_Senses_CasedWords_Synsets_Poses_Domains.SENSEKEY, //
				Words_Senses_CasedWords_Synsets_Poses_Domains.LEXID, //
				Words_Senses_CasedWords_Synsets_Poses_Domains.TAGCOUNT, //
				Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID, //
				Words_Senses_CasedWords_Synsets_Poses_Domains.DEFINITION, //
				Words_Senses_CasedWords_Synsets_Poses_Domains.POS, //
				Words_Senses_CasedWords_Synsets_Poses_Domains.DOMAIN, //
				Words_Senses_CasedWords_Synsets_Poses_Domains.CASED};
		final String selection = WordNetContract.WORD + '.' + Words_Senses_CasedWords_Synsets_Poses_Domains.WORD + " = ?";
		final String[] selectionArgs = {word};
		final String sortOrder = Words_Senses_CasedWords_Synsets_Poses_Domains.POSID + ',' + Words_Senses_CasedWords_Synsets_Poses_Domains.SENSENUM;
		this.sensesFromWordModel.loadData(uri, projection, selection, selectionArgs, sortOrder, cursor -> sensesCursorToTreeModel(cursor, parent));
	}

	/**
	 * Senses from word id
	 *
	 * @param wordId word id
	 * @param parent tree parent node
	 */
	void senses(final long wordId, @NonNull final TreeNode parent)
	{
		// load the contents
		final Uri uri = Uri.parse(WordNetProvider.makeUri(Words_Senses_CasedWords_Synsets_Poses_Domains.CONTENT_URI_TABLE));
		final String[] projection = { //
				Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID + " AS _id", //
				Words_Senses_CasedWords_Synsets_Poses_Domains.WORDID, //
				Words_Senses_CasedWords_Synsets_Poses_Domains.SENSEID, //
				Words_Senses_CasedWords_Synsets_Poses_Domains.SENSENUM, //
				Words_Senses_CasedWords_Synsets_Poses_Domains.SENSEKEY, //
				Words_Senses_CasedWords_Synsets_Poses_Domains.LEXID, //
				Words_Senses_CasedWords_Synsets_Poses_Domains.TAGCOUNT, //
				Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID, //
				Words_Senses_CasedWords_Synsets_Poses_Domains.DEFINITION, //
				Words_Senses_CasedWords_Synsets_Poses_Domains.POS, //
				Words_Senses_CasedWords_Synsets_Poses_Domains.DOMAIN, //
				Words_Senses_CasedWords_Synsets_Poses_Domains.CASED};
		final String selection = Words_Senses_CasedWords_Synsets_Poses_Domains.WORDID + " = ?";
		final String[] selectionArgs = {Long.toString(wordId)};
		final String sortOrder = WordNetContract.POS + '.' + Words_Senses_CasedWords_Synsets_Poses_Domains.POSID + ',' + Words_Senses_CasedWords_Synsets_Poses_Domains.SENSENUM;
		this.sensesFromWordIdModel.loadData(uri, projection, selection, selectionArgs, sortOrder, cursor -> sensesCursorToTreeModel(cursor, parent));
	}

	@NonNull
	private TreeOp[] sensesCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

			final int idWordId = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_Poses_Domains.WORDID);
			final int idSynsetId = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID);
			final int idPosName = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_Poses_Domains.POS);
			final int idLexDomain = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_Poses_Domains.DOMAIN);
			final int idDefinition = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_Poses_Domains.DEFINITION);
			final int idTagCount = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_Poses_Domains.TAGCOUNT);
			final int idCased = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_Poses_Domains.CASED);

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
				final TreeNode synsetNode = TreeFactory.makeLinkNode(sb, R.drawable.synset, false, new SenseLink(synsetId, wordId, this.maxRecursion, this.fragment)).addTo(parent);
				changedList.add(NEWCHILD, synsetNode);
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
	 * Sense
	 *
	 * @param senseId sense id
	 * @param parent  parent node
	 */
	public void sense(final long senseId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(Senses.CONTENT_URI_TABLE));
		final String[] projection = { //
				Senses.WORDID, //
				Senses.SYNSETID, //
		};
		final String selection = Senses.SENSEID + " = ?";
		final String[] selectionArgs = {Long.toString(senseId)};
		this.senseFromSenseIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> senseFromSenseIdCursorToTreeModel(cursor, parent));
	}

	private TreeOp[] senseFromSenseIdCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		if (cursor.getCount() > 1)
		{
			throw new RuntimeException("Unexpected number of rows");
		}
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final int idWordId = cursor.getColumnIndex(Poses.POS);
			final int idSynsetId = cursor.getColumnIndex(Synsets.DEFINITION);
			final long wordId = cursor.getLong(idWordId);
			final long synsetId = cursor.getLong(idSynsetId);

			sense(synsetId, wordId, parent);
			changed = TreeOp.seq(NOOP, parent);
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
	 * Sense
	 *
	 * @param senseKey sense key
	 * @param parent   parent node
	 */
	void sense(final String senseKey, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(Senses.CONTENT_URI_TABLE));
		final String[] projection = { //
				Senses.WORDID, //
				Senses.SYNSETID, //
		};
		final String selection = Senses.SENSEKEY + " = ?";
		final String[] selectionArgs = {senseKey};
		this.senseFromSenseKeyModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> senseFromSenseKeyCursorToTreeModel(cursor, parent));
	}

	private TreeOp[] senseFromSenseKeyCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		if (cursor.getCount() > 1)
		{
			throw new RuntimeException("Unexpected number of rows");
		}
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final int idWordId = cursor.getColumnIndex(Senses.WORDID);
			final int idSynsetId = cursor.getColumnIndex(Senses.SYNSETID);
			final long wordId = cursor.getLong(idWordId);
			final long synsetId = cursor.getLong(idSynsetId);

			// sub nodes
			final TreeNode wordNode = TreeFactory.makeTextNode("Word", false).addTo(parent);

			// word
			word(wordId, wordNode, false);
			sense(synsetId, wordId, wordNode);

			changed = TreeOp.seq(NOOP, parent, NEWUNIQUE, wordNode);
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
	 * Sense
	 *
	 * @param synsetId synset id
	 * @param wordId   word id
	 * @param parent   parent node
	 */
	private void sense(final long synsetId, final long wordId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(Synsets_Poses_Domains.CONTENT_URI_TABLE));
		final String[] projection = { //
				Synsets.DEFINITION, //
				Poses.POS, //
				Domains.DOMAIN, //
		};
		final String selection = Synsets_Poses_Domains.SYNSETID + " = ?";
		final String[] selectionArgs = {Long.toString(synsetId)};
		this.senseFromSynsetIdWordIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> senseFromSynsetIdWordIdCursorToTreeModel(cursor, synsetId, wordId, parent));
	}

	private TreeOp[] senseFromSynsetIdWordIdCursorToTreeModel(@NonNull final Cursor cursor, final long synsetId, final long wordId, @NonNull final TreeNode parent)
	{
		if (cursor.getCount() > 1)
		{
			throw new RuntimeException("Unexpected number of rows");
		}
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final SpannableStringBuilder sb = new SpannableStringBuilder();
			final int idPosName = cursor.getColumnIndex(Poses.POS);
			final int idLexDomain = cursor.getColumnIndex(Domains.DOMAIN);
			final int idDefinition = cursor.getColumnIndex(Synsets.DEFINITION);
			final String posName = cursor.getString(idPosName);
			final String lexDomain = cursor.getString(idLexDomain);
			final String definition = cursor.getString(idDefinition);

			Spanner.appendImage(sb, BaseModule.this.synsetDrawable);
			sb.append(' ');
			synset(sb, synsetId, posName, lexDomain, definition);

			// attach result
			final TreeNode node = TreeFactory.makeTextNode(sb, false).addTo(parent);

			// subnodes
			final TreeNode relationsNode = TreeFactory.makeHotQueryNode("Relations", R.drawable.ic_relations, false, new RelationsQuery(synsetId, wordId)).addTo(parent);
			final TreeNode samplesNode = TreeFactory.makeHotQueryNode("Samples", R.drawable.sample, false, new SamplesQuery(synsetId)).addTo(parent);

			changed = TreeOp.seq(NEWMAIN, node, NEWEXTRA, relationsNode, NEWEXTRA, samplesNode, NEWTREE, parent);
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
	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	private SpannableStringBuilder sense(@NonNull final SpannableStringBuilder sb, final long synsetId, final CharSequence posName, final CharSequence lexDomain, final CharSequence definition, final int tagCount, @Nullable final CharSequence cased)
	{
		synset_head(sb, synsetId, posName, lexDomain);

		if (cased != null && cased.length() > 0)
		{
			Spanner.appendImage(sb, BaseModule.this.memberDrawable);
			sb.append(' ');
			Spanner.append(sb, cased, 0, WordNetFactories.wordFactory);
			sb.append(' ');
		}
		if (tagCount > 0)
		{
			sb.append(' ');
			Spanner.append(sb, "tagcount:" + tagCount, 0, WordNetFactories.dataFactory);
		}

		sb.append('\n');
		synset_definition(sb, definition);

		return sb;
	}

	// S Y N S E T

	/**
	 * Synset
	 *
	 * @param synsetId   synset id
	 * @param parent     parent node
	 * @param addNewNode whether to addItem to (or set) node
	 */
	void synset(final long synsetId, @NonNull final TreeNode parent, @SuppressWarnings("SameParameterValue") final boolean addNewNode)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(Synsets_Poses_Domains.CONTENT_URI_TABLE));
		final String[] projection = { //
				Synsets.DEFINITION, //
				Poses.POS, //
				Domains.DOMAIN, //
		};
		final String selection = Synsets_Poses_Domains.SYNSETID + " = ?";
		final String[] selectionArgs = {Long.toString(synsetId)};
		this.synsetFromSynsetIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> synsetCursorToTreeModel(cursor, synsetId, parent, addNewNode));
	}

	private TreeOp[] synsetCursorToTreeModel(@NonNull final Cursor cursor, final long synsetId, @NonNull final TreeNode parent, @SuppressWarnings("SameParameterValue") final boolean addNewNode)
	{
		if (cursor.getCount() > 1)
		{
			throw new RuntimeException("Unexpected number of rows");
		}
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final SpannableStringBuilder sb = new SpannableStringBuilder();
			final int idPosName = cursor.getColumnIndex(Poses.POS);
			final int idLexDomain = cursor.getColumnIndex(Domains.DOMAIN);
			final int idDefinition = cursor.getColumnIndex(Synsets.DEFINITION);
			final String posName = cursor.getString(idPosName);
			final String lexDomain = cursor.getString(idLexDomain);
			final String definition = cursor.getString(idDefinition);

			Spanner.appendImage(sb, BaseModule.this.synsetDrawable);
			sb.append(' ');
			synset(sb, synsetId, posName, lexDomain, definition);

			// result
			if (addNewNode)
			{
				final TreeNode node = TreeFactory.makeTextNode(sb, false).addTo(parent);
				changed = TreeOp.seq(NEWUNIQUE, node);
			}
			else
			{
				TreeFactory.setTextNode(parent, sb);
				changed = TreeOp.seq(UPDATE, parent);
			}
		}
		else
		{
			TreeFactory.setNoResult(parent);
			changed = TreeOp.seq(addNewNode ? DEADEND : REMOVE, parent);
		}

		cursor.close();
		return changed;
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
	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	private SpannableStringBuilder synset(@NonNull final SpannableStringBuilder sb, final long synsetId, final CharSequence posName, final CharSequence lexDomain, final CharSequence definition)
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
	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	private SpannableStringBuilder synset_head(@NonNull final SpannableStringBuilder sb, final long synsetId, final CharSequence posName, final CharSequence lexDomain)
	{
		Spanner.appendImage(sb, BaseModule.this.posDrawable);
		sb.append(' ');
		sb.append(posName);
		sb.append(' ');
		Spanner.appendImage(sb, BaseModule.this.domainDrawable);
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
	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	private SpannableStringBuilder synset_definition(@NonNull final SpannableStringBuilder sb, final CharSequence definition)
	{
		Spanner.appendImage(sb, BaseModule.this.definitionDrawable);
		sb.append(' ');
		Spanner.append(sb, definition, 0, WordNetFactories.definitionFactory);
		return sb;
	}

	// M E M B E R S

	/**
	 * Members
	 *
	 * @param synsetId synset id
	 * @param parent   parent node
	 */
	void members(final long synsetId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(Senses_Words.CONTENT_URI_TABLE));
		final String[] projection = {Senses_Words.WORDID, Senses_Words.MEMBER};
		final String selection = Senses_Words.SYNSETID + " = ?";
		final String[] selectionArgs = {Long.toString(synsetId)};
		final String sortOrder = Senses_Words.MEMBER;
		this.membersFromSynsetIdModel.loadData(uri, projection, selection, selectionArgs, sortOrder, cursor -> membersCursorToTreeModel(cursor, parent));
	}

	@NonNull
	private TreeOp[] membersCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		if (BaseModule.this.membersGrouped)
		{
			if (cursor.getCount() > 1)
			{
				throw new RuntimeException("Unexpected number of rows");
			}
		}

		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

			final int idWordId = cursor.getColumnIndex(Senses_Words.WORDID);
			final int idMember = cursor.getColumnIndex(Senses_Words.MEMBER);
			// int i = 1;
			do
			{
				final long wordId = cursor.getLong(idWordId);
				final String member = cursor.getString(idMember);

				final SpannableStringBuilder sb = new SpannableStringBuilder();
				// final String formattedLemma = String.format(Locale.ENGLISH, "[%d] %s", i++, lemma);
				// sb.append(formattedLemma);
				// sb.append(Integer.toString(i++));
				// sb.append('-');
				// sb.append(lemma);
				Spanner.append(sb, member, 0, WordNetFactories.membersFactory);

				// result
				final TreeNode memberNode = TreeFactory.makeLinkNode(sb, R.drawable.member, false, new WordLink(this.fragment, wordId)).addTo(parent);
				changedList.add(NEWCHILD, memberNode);
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
	 * Members
	 *
	 * @param synsetId   synset id
	 * @param parent     parent node
	 * @param addNewNode whether to addItem to (or set) node
	 */
	void members2(final long synsetId, @NonNull final TreeNode parent, final boolean addNewNode)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(BaseModule.this.membersGrouped ? Senses_Words.CONTENT_URI_TABLE_BY_SYNSET : Senses_Words.CONTENT_URI_TABLE));
		final String[] projection = BaseModule.this.membersGrouped ? //
				new String[]{Senses_Words.MEMBERS} : new String[]{Words.WORD};
		final String selection = Senses_Words.SYNSETID + " = ?";
		final String[] selectionArgs = {Long.toString(synsetId)};
		final String sortOrder = Words.WORD;
		this.members2FromSynsetIdModel.loadData(uri, projection, selection, selectionArgs, sortOrder, cursor -> members2CursorToTreeModel(cursor, parent, addNewNode));
	}

	private TreeOp[] members2CursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent, final boolean addNewNode)
	{
		if (BaseModule.this.membersGrouped)
		{
			if (cursor.getCount() > 1)
			{
				throw new RuntimeException("Unexpected number of rows");
			}
		}

		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final SpannableStringBuilder sb = new SpannableStringBuilder();
			if (BaseModule.this.membersGrouped)
			{
				final int idMembers = cursor.getColumnIndex(Senses_Words.MEMBERS);
				sb.append(cursor.getString(idMembers));
			}
			else
			{
				final int lemmaId = cursor.getColumnIndex(Words.WORD);
				// int i = 1;
				do
				{
					final String lemma = cursor.getString(lemmaId);
					if (sb.length() != 0)
					{
						sb.append('\n');
					}
					// final String formattedLemma = String.format(Locale.ENGLISH, "[%d] %s", i++, lemma);
					// sb.append(formattedLemma);
					Spanner.appendImage(sb, BaseModule.this.memberDrawable);
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
				final TreeNode node = TreeFactory.makeTextNode(sb, false).addTo(parent);
				changed = TreeOp.seq(NEWUNIQUE, node);
			}
			else
			{
				TreeFactory.setTextNode(parent, sb);
				changed = TreeOp.seq(UPDATE, parent);
			}
		}
		else
		{
			TreeFactory.setNoResult(parent);
			changed = TreeOp.seq(addNewNode ? DEADEND : REMOVE, parent);
		}

		cursor.close();
		return changed;
	}

	// S A M P L E S

	/**
	 * Samples
	 *
	 * @param synsetId   synset id
	 * @param parent     parent node
	 * @param addNewNode whether to addItem to (or set) node
	 */
	private void samples(final long synsetId, @NonNull final TreeNode parent, @SuppressWarnings("SameParameterValue") final boolean addNewNode)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(Samples.CONTENT_URI_TABLE));
		final String[] projection = { //
				Samples.SAMPLEID, //
				Samples.SAMPLE, //
		};
		final String selection = Samples.SYNSETID + " = ?";
		final String[] selectionArgs = {Long.toString(synsetId)};
		final String sortOrder = Samples.SAMPLEID;
		this.samplesfromSynsetIdModel.loadData(uri, projection, selection, selectionArgs, sortOrder, cursor -> samplesCursorToTreeModel(cursor, parent, addNewNode));
	}

	private TreeOp[] samplesCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent, @SuppressWarnings("SameParameterValue") final boolean addNewNode)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
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
				Spanner.appendImage(sb, BaseModule.this.sampleDrawable);
				sb.append(' ');
				// sb.append(Integer.toString(sampleId));
				// sb.append(' ');
				Spanner.append(sb, sample, 0, WordNetFactories.sampleFactory);
				// final String formattedSample = String.format(Locale.ENGLISH, "[%d] %s", sampleId, sample);
				// sb.append(formattedSample);
			}
			while (cursor.moveToNext());

			// result
			if (addNewNode)
			{
				final TreeNode node = TreeFactory.makeTextNode(sb, false).addTo(parent);
				changed = TreeOp.seq(NEWUNIQUE, node);
			}
			else
			{
				TreeFactory.setTextNode(parent, sb);
				changed = TreeOp.seq(UPDATE, parent);
			}
		}
		else
		{
			TreeFactory.setNoResult(parent);
			changed = TreeOp.seq(addNewNode ? DEADEND : REMOVE, parent);
		}

		cursor.close();
		return changed;
	}

	// L I N K S

	//static private final String SOURCE_SYNSETID = "s_synsetid";
	//static private final String SOURCE_DEFINITION = "s_definition";
	//static private final String SOURCE_LEMMA = "s_lemma";
	//static private final String SOURCE_WORDID = "s_wordid";
	static public final String TARGET_SYNSETID = "d_synsetid";
	@SuppressWarnings("WeakerAccess")
	static public final String TARGET_DEFINITION = "d_definition";
	static public final String TARGET_WORD = "d_word";
	static public final String TARGET_WORDID = "d_wordid";

	/**
	 * Relations (union)
	 *
	 * @param synsetId                synset id
	 * @param wordId                  word id
	 * @param parent                  parent node
	 * @param deadendParentIfNoResult mark parent node as deadend if there is no result
	 */
	private void relations(final long synsetId, final long wordId, @NonNull final TreeNode parent, @SuppressWarnings("SameParameterValue") final boolean deadendParentIfNoResult)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(BaseRelations_Senses_Words_X.CONTENT_URI_TABLE));
		final String[] projection = { //
				WordNetContract.TYPE, Relations.RELATIONID, //
				Relations.RELATION, //
				WordNetContract.SYNSET2 + '.' + Synsets.SYNSETID + " AS " + BaseModule.TARGET_SYNSETID, //
				WordNetContract.SYNSET2 + '.' + Synsets.DEFINITION + " AS " + BaseModule.TARGET_DEFINITION, //
				"GROUP_CONCAT(" + WordNetContract.WORD + '.' + Words.WORD + ") AS " + BaseRelations_Senses_Words_X.MEMBERS2, //
				BaseRelations_Senses_Words_X.RECURSES, //
				WordNetContract.WORD2 + '.' + Words.WORDID + " AS " + BaseModule.TARGET_WORDID, //
				WordNetContract.WORD2 + '.' + Words.WORD + " AS " + BaseModule.TARGET_WORD, //
		};
		final String selection = "synset1id = ? /**/|/**/ synset1id = ? AND word1id = ?";
		final String[] selectionArgs = {Long.toString(synsetId), Long.toString(synsetId), Long.toString(wordId)};
		final String sortOrder = Relations.RELATIONID;
		this.relationsFromSynsetIdWordIdModel.loadData(uri, projection, selection, selectionArgs, sortOrder, cursor -> relationsCursorToTreeModel(cursor, parent, deadendParentIfNoResult));
	}

	@NonNull
	private TreeOp[] relationsCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent, final boolean deadendParentIfNoResult)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

			final int idRelationId = cursor.getColumnIndex(Relations.RELATIONID);
			// final int idRelation = cursor.getColumnIndex(Relations.RELATION);
			final int idTargetSynsetId = cursor.getColumnIndex(BaseModule.TARGET_SYNSETID);
			final int idTargetDefinition = cursor.getColumnIndex(BaseModule.TARGET_DEFINITION);
			final int idTargetMembers = cursor.getColumnIndex(BaseRelations_Senses_Words_X.MEMBERS2);
			final int idRecurses = cursor.getColumnIndex(BaseRelations_Senses_Words_X.RECURSES);
			final int idTargetWordId = cursor.getColumnIndex(BaseModule.TARGET_WORDID);
			final int idTargetLemma = cursor.getColumnIndex(BaseModule.TARGET_WORD);

			do
			{
				final SpannableStringBuilder sb = new SpannableStringBuilder();

				final int relationId = cursor.getInt(idRelationId);
				//final String relation = cursor.getString(idRelation);

				final long targetSynsetId = cursor.getLong(idTargetSynsetId);
				final String targetDefinition = cursor.getString(idTargetDefinition);
				String targetMembers = cursor.getString(idTargetMembers);
				final boolean relationCanRecurse = !cursor.isNull(idRecurses) && cursor.getInt(idRecurses) != 0;
				final String targetLemma = cursor.isNull(idTargetLemma) ? null : cursor.getString(idTargetLemma);
				final Long targetWordId = cursor.isNull(idTargetWordId) ? null : cursor.getLong(idTargetWordId);

				if (targetLemma != null)
				{
					targetMembers = targetMembers.replaceAll("\\b" + targetLemma + "\\b", targetLemma + '*');
				}
				Spanner.append(sb, targetMembers, 0, WordNetFactories.membersFactory);
				sb.append(' ');
				Spanner.append(sb, targetDefinition, 0, WordNetFactories.definitionFactory);

				// recursion
				if (relationCanRecurse)
				{
					final TreeNode relationsNode = TreeFactory.makeLinkQueryNode(sb, getRelationRes(relationId), false, new SubRelationsQuery(targetSynsetId, relationId, BaseModule.this.maxRecursion), new SynsetLink(targetSynsetId, BaseModule.this.maxRecursion, this.fragment)).addTo(parent);
					changedList.add(NEWCHILD, relationsNode);
				}
				else
				{
					final TreeNode node = TreeFactory.makeLinkLeafNode(sb, getRelationRes(relationId), false, targetWordId == null ?
							new SynsetLink(targetSynsetId, BaseModule.this.maxRecursion, this.fragment) :
							new SenseLink(targetSynsetId, targetWordId, BaseModule.this.maxRecursion, this.fragment)).addTo(parent);
					changedList.add(NEWCHILD, node);
				}
			}
			while (cursor.moveToNext());
			changed = changedList.toArray();
		}
		else
		{
			if (deadendParentIfNoResult)
			{
				TreeFactory.setNoResult(parent);
				changed = TreeOp.seq(DEADEND, parent);
			}
			else
			{
				changed = TreeOp.seq(NOOP, parent);
			}
		}

		cursor.close();
		return changed;
	}

	// S E M R E L A T I O N S

	/**
	 * Semantic relations
	 *
	 * @param synsetId                synset id
	 * @param parent                  parent node
	 * @param deadendParentIfNoResult mark parent node as deadend if there is no result
	 */
	private void semRelations(final long synsetId, @NonNull final TreeNode parent, @SuppressWarnings("SameParameterValue") final boolean deadendParentIfNoResult)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(SemRelations_Synsets_Words_X.CONTENT_URI_TABLE));
		final String[] projection = { //
				Relations.RELATIONID, //
				Relations.RELATION, //
				WordNetContract.SYNSET2 + '.' + Synsets.SYNSETID + " AS " + BaseModule.TARGET_SYNSETID, //
				WordNetContract.SYNSET2 + '.' + Synsets.DEFINITION + " AS " + BaseModule.TARGET_DEFINITION, //
				Relations.RECURSES, //
		};
		final String selection = WordNetContract.RELATION + '.' + SemRelations_Synsets_Words_X.SYNSET1ID + " = ?";  ////
		final String[] selectionArgs = {Long.toString(synsetId)};
		final String sortOrder = Relations.RELATIONID;
		this.semRelationsFromSynsetIdModel.loadData(uri, projection, selection, selectionArgs, sortOrder, cursor -> semRelationsCursorToTreeModel(cursor, parent, deadendParentIfNoResult));
	}

	@NonNull
	private TreeOp[] semRelationsCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent, final boolean deadendParentIfNoResult)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

			// final int idRelation = cursor.getColumnIndex(Relations.RELATION);
			final int idRelationId = cursor.getColumnIndex(Relations.RELATIONID);
			final int idTargetSynsetId = cursor.getColumnIndex(BaseModule.TARGET_SYNSETID);
			final int idTargetDefinition = cursor.getColumnIndex(BaseModule.TARGET_DEFINITION);
			final int idTargetMembers = cursor.getColumnIndex(SemRelations_Synsets_Words_X.MEMBERS2);
			final int idRecurses = cursor.getColumnIndex(SemRelations_Synsets_Words_X.RECURSES);

			do
			{
				final SpannableStringBuilder sb = new SpannableStringBuilder();

				// final String relation = cursor.getString(idRelation);
				final int relationId = cursor.getInt(idRelationId);
				final long targetSynsetId = cursor.getLong(idTargetSynsetId);
				final String targetDefinition = cursor.getString(idTargetDefinition);
				final String targetMembers = cursor.getString(idTargetMembers);
				final boolean relationCanRecurse = cursor.getInt(idRecurses) != 0;

				Spanner.append(sb, targetMembers, 0, WordNetFactories.membersFactory);
				sb.append(' ');
				Spanner.append(sb, targetDefinition, 0, WordNetFactories.definitionFactory);

				// recursion
				if (relationCanRecurse)
				{
					final TreeNode relationsNode = TreeFactory.makeLinkQueryNode(sb, getRelationRes(relationId), false, new SubRelationsQuery(targetSynsetId, relationId, BaseModule.this.maxRecursion), new SynsetLink(targetSynsetId, BaseModule.this.maxRecursion, this.fragment)).addTo(parent);
					changedList.add(NEWCHILD, relationsNode);
				}
				else
				{
					final TreeNode node = TreeFactory.makeLeafNode(sb, getRelationRes(relationId), false).addTo(parent);
					changedList.add(NEWCHILD, node);
				}
			}
			while (cursor.moveToNext());
			changed = changedList.toArray();
		}
		else
		{
			if (deadendParentIfNoResult)
			{
				TreeFactory.setNoResult(parent);
				changed = TreeOp.seq(DEADEND, parent);
			}
			else
			{
				changed = TreeOp.seq(NOOP, parent);
			}
		}

		cursor.close();
		return changed;
	}

	/**
	 * Semantic relations
	 *
	 * @param synsetId                synset id
	 * @param relationId              relation id
	 * @param parent                  parent node
	 * @param deadendParentIfNoResult mark parent node as deadend if there is no result
	 */
	private void semRelations(final long synsetId, final int relationId, final int recurseLevel, @NonNull final TreeNode parent, @SuppressWarnings("SameParameterValue") final boolean deadendParentIfNoResult)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(SemRelations_Synsets_Words_X.CONTENT_URI_TABLE));
		final String[] projection = { //
				Relations.RELATIONID, //
				Relations.RELATION, //
				WordNetContract.SYNSET2 + '.' + Synsets.SYNSETID + " AS " + BaseModule.TARGET_SYNSETID, //
				WordNetContract.SYNSET2 + '.' + Synsets.DEFINITION + " AS " + BaseModule.TARGET_DEFINITION, //
				Relations.RECURSES, //
		};
		final String selection = WordNetContract.RELATION + '.' + SemRelations_Synsets_Words_X.SYNSET1ID + " = ? AND " + Relations.RELATIONID + " = ?";
		final String[] selectionArgs = {Long.toString(synsetId), Integer.toString(relationId)};
		this.semRelationsFromSynsetIdRelationIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> semRelationsFromSynsetIdRelationIdCursorToTreeModel(cursor, relationId, recurseLevel, parent, deadendParentIfNoResult));
	}

	@NonNull
	private TreeOp[] semRelationsFromSynsetIdRelationIdCursorToTreeModel(@NonNull final Cursor cursor, final int relationId, final int recurseLevel, @NonNull final TreeNode parent, final boolean deadendParentIfNoResult)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

			// final int idRelationId = cursor.getColumnIndex(Relations.RELATIONID);
			// final int idRelation = cursor.getColumnIndex(Relations.RELATION);
			final int idTargetSynsetId = cursor.getColumnIndex(BaseModule.TARGET_SYNSETID);
			final int idTargetDefinition = cursor.getColumnIndex(BaseModule.TARGET_DEFINITION);
			final int idTargetMembers = cursor.getColumnIndex(SemRelations_Synsets_Words_X.MEMBERS2);
			final int idRecurses = cursor.getColumnIndex(SemRelations_Synsets_Words_X.RECURSES);

			do
			{
				final SpannableStringBuilder sb = new SpannableStringBuilder();

				// final int relationId = cursor.getInt(idRelationId);
				// final String relation = cursor.getString(idRelation);
				final long targetSynsetId = cursor.getLong(idTargetSynsetId);
				final String targetDefinition = cursor.getString(idTargetDefinition);
				final String targetMembers = cursor.getString(idTargetMembers);
				final boolean relationCanRecurse = cursor.getInt(idRecurses) != 0;

				Spanner.append(sb, targetMembers, 0, WordNetFactories.membersFactory);
				sb.append(' ');
				Spanner.append(sb, targetDefinition, 0, WordNetFactories.definitionFactory);

				// recurse
				if (relationCanRecurse)
				{
					if (recurseLevel > 1)
					{
						final int newRecurseLevel = recurseLevel - 1;
						final TreeNode relationsNode = TreeFactory.makeLinkQueryNode(sb, getRelationRes(relationId), false, new SubRelationsQuery(targetSynsetId, relationId, newRecurseLevel), new SynsetLink(targetSynsetId, BaseModule.this.maxRecursion, this.fragment)).addTo(parent);
						changedList.add(NEWCHILD, relationsNode);
					}
					else
					{
						final TreeNode moreNode = TreeFactory.makeMoreNode(sb, getRelationRes(relationId), false).addTo(parent);
						changedList.add(NEWCHILD, moreNode);
					}
				}
				else
				{
					final TreeNode node = TreeFactory.makeLeafNode(sb, getRelationRes(relationId), false).addTo(parent);
					changedList.add(NEWCHILD, node);
				}
			}
			while (cursor.moveToNext());
			changed = changedList.toArray();
		}
		else
		{
			if (deadendParentIfNoResult)
			{
				TreeFactory.setNoResult(parent);
				changed = TreeOp.seq(DEADEND, parent);
			}
			else
			{
				changed = TreeOp.seq(NOOP, parent);
			}
		}

		cursor.close();
		return changed;
	}

	// L E X R E L A T I O N S

	/**
	 * Lexical relations
	 *
	 * @param synsetId                synset id
	 * @param wordId                  word id
	 * @param parent                  parent node
	 * @param deadendParentIfNoResult mark parent node as deadend if there is no result
	 */
	private void lexRelations(final long synsetId, final long wordId, @NonNull final TreeNode parent, @SuppressWarnings("SameParameterValue") final boolean deadendParentIfNoResult)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(LexRelations_Senses_Words_X.CONTENT_URI_TABLE));
		final String[] projection = { //
				Relations.RELATIONID, //
				Relations.RELATION, //
				WordNetContract.SYNSET2 + '.' + Synsets.SYNSETID + " AS " + BaseModule.TARGET_SYNSETID, //
				WordNetContract.SYNSET2 + '.' + Synsets.DEFINITION + " AS " + BaseModule.TARGET_DEFINITION, //
				WordNetContract.WORD + '.' + Words.WORDID + " AS " + BaseModule.TARGET_WORDID, //
				WordNetContract.WORD + '.' + Words.WORD + " AS " + BaseModule.TARGET_WORD, //
		};
		final String selection = WordNetContract.RELATION + ".synset1id = ? AND " + WordNetContract.RELATION + ".word1id = ?";
		final String[] selectionArgs = {Long.toString(synsetId), Long.toString(wordId)};
		final String sortOrder = Relations.RELATIONID;
		this.lexRelationsFromSynsetIdWordIdModel.loadData(uri, projection, selection, selectionArgs, sortOrder, cursor -> lexRelationsCursorToTreeModel(cursor, parent, deadendParentIfNoResult));
	}

	@NonNull
	private TreeOp[] lexRelationsCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent, final boolean deadendParentIfNoResult)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

			// final int idRelation = cursor.getColumnIndex(Relations.RELATION);
			final int idRelationId = cursor.getColumnIndex(Relations.RELATIONID);
			final int idTargetSynsetId = cursor.getColumnIndex(BaseModule.TARGET_SYNSETID);
			final int idTargetDefinition = cursor.getColumnIndex(BaseModule.TARGET_DEFINITION);
			final int idTargetMembers = cursor.getColumnIndex(LexRelations_Senses_Words_X.MEMBERS2);
			final int idTargetWordId = cursor.getColumnIndex(BaseModule.TARGET_WORDID);
			final int idTargetLemma = cursor.getColumnIndex(BaseModule.TARGET_WORD);

			do
			{
				final SpannableStringBuilder sb = new SpannableStringBuilder();

				// final String relation = cursor.getString(idRelation);
				// final String targetWordId = cursor.getString(idTargetWordId);
				final int relationId = cursor.getInt(idRelationId);
				final long targetSynsetId = cursor.getLong(idTargetSynsetId);
				final String targetDefinition = cursor.getString(idTargetDefinition);
				final String targetLemma = cursor.getString(idTargetLemma);
				String targetMembers = cursor.getString(idTargetMembers);
				if (targetLemma != null)
				{
					targetMembers = targetMembers.replaceAll("\\b" + targetLemma + "\\b", targetLemma + '*');
				}
				// final String formattedTarget = String.format(Locale.ENGLISH, "[%s] %s (%s)\n\t%s (synset %s) {%s}", relation, targetLemma, targetWordId,targetDefinition, targetSynsetId, targetMembers);

				if (sb.length() != 0)
				{
					sb.append('\n');
				}
				// Spanner.appendImage(sb, getRelationDrawable(relationId));
				// sb.append(formattedTarget);
				// sb.append(' ');
				Spanner.append(sb, targetMembers, 0, WordNetFactories.membersFactory);
				// Spanner.append(sb, targetLemma, 0, WordNetFactories.lemmaFactory);
				// sb.append(" in ");
				// sb.append(' ');
				// sb.append('{');
				// Spanner.append(sb, members2CursorToTreeModel, 0, WordNetFactories.membersFactory);
				// sb.append('}');
				sb.append(' ');
				Spanner.append(sb, targetDefinition, 0, WordNetFactories.definitionFactory);

				// attach result
				final TreeNode relationNode = TreeFactory.makeLinkLeafNode(sb, getRelationRes(relationId), false, new SenseLink(targetSynsetId, idTargetWordId, BaseModule.this.maxRecursion, this.fragment)).addTo(parent);
				changedList.add(NEWCHILD, relationNode);
			}
			while (cursor.moveToNext());
			changed = changedList.toArray();
		}
		else
		{
			if (deadendParentIfNoResult)
			{
				TreeFactory.setNoResult(parent);
				changed = TreeOp.seq(DEADEND, parent);
			}
			else
			{
				changed = TreeOp.seq(NOOP, parent);
			}
		}
		cursor.close();
		return changed;
	}

	/**
	 * Lexical relations
	 *
	 * @param synsetId                synset id
	 * @param parent                  parent
	 * @param deadendParentIfNoResult mark parent node as deadend if there is no result
	 */
	void lexRelations(final long synsetId, @NonNull final TreeNode parent, final boolean deadendParentIfNoResult)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(LexRelations_Senses_Words_X.CONTENT_URI_TABLE));
		final String[] projection = { //
				Relations.RELATIONID, //
				Relations.RELATION, //
				WordNetContract.SYNSET2 + '.' + Synsets.SYNSETID + " AS " + BaseModule.TARGET_SYNSETID, //
				WordNetContract.SYNSET2 + '.' + Synsets.DEFINITION + " AS " + BaseModule.TARGET_DEFINITION, //
				WordNetContract.WORD + '.' + Words.WORDID + " AS " + BaseModule.TARGET_WORDID, //
				WordNetContract.WORD + '.' + Words.WORD + " AS " + BaseModule.TARGET_WORD, //
		};
		final String selection = WordNetContract.RELATION + '.' + LexRelations_Senses_Words_X.SYNSET1ID + " = ?";  ////
		final String[] selectionArgs = {Long.toString(synsetId)};
		this.lexRelationsFromSynsetIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> lexRelationsFromSynsetIdCursorToTreeModel(cursor, parent, deadendParentIfNoResult));
	}

	@NonNull
	private TreeOp[] lexRelationsFromSynsetIdCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent, final boolean deadendParentIfNoResult)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

			// final int idRelation = cursor.getColumnIndex(Relations.RELATION);
			// final int idTargetSynsetId = cursor.getColumnIndex(BaseModule.TARGET_SYNSETID);
			// final int idTargetWordId = cursor.getColumnIndex(BaseModule.TARGET_WORDID);
			final int idRelationId = cursor.getColumnIndex(Relations.RELATIONID);
			final int idTargetDefinition = cursor.getColumnIndex(BaseModule.TARGET_DEFINITION);
			final int idTargetMembers = cursor.getColumnIndex(LexRelations_Senses_Words_X.MEMBERS2);
			final int idTargetLemma = cursor.getColumnIndex(BaseModule.TARGET_WORD);

			final SpannableStringBuilder sb = new SpannableStringBuilder();
			do
			{
				// final String relation = cursor.getString(idRelation);
				// final String targetSynsetId = cursor.getString(idTargetSynsetId);
				// final String targetWordId = cursor.getString(idTargetWordId);
				final int relationId = cursor.getInt(idRelationId);
				final String targetDefinition = cursor.getString(idTargetDefinition);
				final String targetLemma = cursor.getString(idTargetLemma);
				String targetMembers = cursor.getString(idTargetMembers);
				if (targetLemma != null)
				{
					targetMembers = targetMembers.replaceAll("\\b" + targetLemma + "\\b", targetLemma + '*');
				}
				// final String formattedTarget = String.format(Locale.ENGLISH, "[%s] %s (%s)\n\t%s (synset %s) {%s}", relation, targetLemma, targetWordId, targetDefinition, targetSynsetId, targetMembers);

				if (sb.length() != 0)
				{
					sb.append('\n');
				}
				// Spanner.appendImage(sb, getRelationDrawable(relationId));
				// sb.append(formattedTarget);
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
				final TreeNode relationNode = TreeFactory.makeLeafNode(sb, getRelationRes(relationId), false).addTo(parent);
				changedList.add(NEWCHILD, relationNode);
			}
			while (cursor.moveToNext());

			// attach result
			final TreeNode node = TreeFactory.makeTextNode(sb, false).addTo(parent);
			changedList.add(NEWCHILD, node);
			changed = changedList.toArray();
		}
		else
		{
			if (deadendParentIfNoResult)
			{
				TreeFactory.setNoResult(parent);
				changed = TreeOp.seq(DEADEND, parent);
			}
			else
			{
				changed = TreeOp.seq(NOOP, parent);
			}
		}
		cursor.close();
		return changed;
	}

	// V F R A M E S

	/**
	 * Verb frames
	 *
	 * @param synsetId synset id
	 * @param parent   parent node
	 */
	void vFrames(final long synsetId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(Senses_VerbFrames.CONTENT_URI_TABLE));
		final String[] projection = {Senses_VerbFrames.FRAME};
		final String selection = Senses_VerbFrames.SYNSETID + " = ?";
		final String[] selectionArgs = {Long.toString(synsetId)};
		this.vFramesFromSynsetIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> vframesCursorToTreeModel(cursor, parent));
	}

	/**
	 * Verb frames
	 *
	 * @param synsetId synset id
	 * @param wordId   word id
	 * @param parent   parent node
	 */
	void vFrames(final long synsetId, final long wordId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(Senses_VerbFrames.CONTENT_URI_TABLE));
		final String[] projection = {Senses_VerbFrames.FRAME};
		final String selection = Senses_VerbFrames.SYNSETID + " = ? AND " + Senses_VerbFrames.WORDID + " = ?";
		final String[] selectionArgs = {Long.toString(synsetId), Long.toString(wordId)};
		this.vFramesFromSynsetIdWordIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> vframesCursorToTreeModel(cursor, parent));
	}

	private TreeOp[] vframesCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final int vframeId = cursor.getColumnIndex(Senses_VerbFrames.FRAME);

			final SpannableStringBuilder sb = new SpannableStringBuilder();
			do
			{
				final String vframe = cursor.getString(vframeId);
				final String formattedVframe = String.format(Locale.ENGLISH, "%s", vframe);
				if (sb.length() != 0)
				{
					sb.append('\n');
				}
				Spanner.appendImage(sb, BaseModule.this.verbframeDrawable);
				sb.append(' ');
				sb.append(formattedVframe);
			}
			while (cursor.moveToNext());

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

	// V T E M P L A T E S

	/**
	 * Verb templates
	 *
	 * @param synsetId synset id
	 * @param parent   parent node
	 */
	void vTemplates(final long synsetId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(Senses_VerbTemplates.CONTENT_URI_TABLE));
		final String[] projection = {Senses_VerbTemplates.TEMPLATE};
		final String selection = Senses_VerbTemplates.SYNSETID + " = ?";
		final String[] selectionArgs = {Long.toString(synsetId)};
		this.vTemplatesFromSynsetIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> vTemplatesCursorToTreeModel(cursor, parent));
	}

	private TreeOp[] vTemplatesCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final int vTemplateId = cursor.getColumnIndex(Senses_VerbTemplates.TEMPLATE);

			final SpannableStringBuilder sb = new SpannableStringBuilder();
			do
			{
				final String vTemplate = cursor.getString(vTemplateId);
				final String formattedVTemplate = String.format(Locale.ENGLISH, vTemplate, "[-]");
				if (sb.length() != 0)
				{
					sb.append('\n');
				}
				Spanner.appendImage(sb, BaseModule.this.verbframeDrawable);
				sb.append(' ');
				sb.append(formattedVTemplate);
			}
			while (cursor.moveToNext());

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
	 * Verb templates
	 *
	 * @param synsetId synset id
	 * @param wordId   word id
	 * @param parent   parent node
	 */
	void vTemplates(final long synsetId, final long wordId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(Senses_VerbTemplates.CONTENT_URI_TABLE));
		final String[] projection = {Senses_VerbTemplates.TEMPLATE};
		final String selection = Senses_VerbTemplates.SYNSETID + " = ? AND " + Senses_VerbTemplates.WORDID + " = ?";
		final String[] selectionArgs = {Long.toString(synsetId), Long.toString(wordId)};
		this.vTemplatesFromSynsetIdWordIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> vTemplatesFromSynsetIdWordIdCursorToTreeModel(cursor, parent));
	}

	private TreeOp[] vTemplatesFromSynsetIdWordIdCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final String lemma = "---";
			final int vTemplateId = cursor.getColumnIndex(Senses_VerbTemplates.TEMPLATE);

			final SpannableStringBuilder sb = new SpannableStringBuilder();
			do
			{
				final String vTemplate = cursor.getString(vTemplateId);
				final String formattedVTemplate = String.format(Locale.ENGLISH, vTemplate, '[' + lemma + ']');
				if (sb.length() != 0)
				{
					sb.append('\n');
				}
				Spanner.appendImage(sb, BaseModule.this.verbframeDrawable);
				sb.append(' ');
				sb.append(formattedVTemplate);
			}
			while (cursor.moveToNext());

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

	// A D J P O S I T I O N S

	/**
	 * Adjective positions
	 *
	 * @param synsetId synset id
	 * @param parent   parent node
	 */
	void adjPosition(final long synsetId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(Senses_AdjPositions.CONTENT_URI_TABLE));
		final String[] projection = {Senses_AdjPositions.POSITION};
		final String selection = Senses_AdjPositions.SYNSETID + " = ?";
		final String[] selectionArgs = {Long.toString(synsetId)};
		this.adjPositionFromSynsetIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> adjPositionCursorToTreeModel(cursor, parent));
	}

	/**
	 * Adjective positions
	 *
	 * @param synsetId synset id
	 * @param wordId   word id
	 * @param parent   parent node
	 */
	void adjPosition(final long synsetId, final long wordId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(Senses_AdjPositions.CONTENT_URI_TABLE));
		final String[] projection = {Senses_AdjPositions.POSITION};
		final String selection = Senses_AdjPositions.SYNSETID + " = ? AND " + Senses_AdjPositions.WORDID + " = ?";
		final String[] selectionArgs = {Long.toString(synsetId), Long.toString(wordId)};
		this.adjPositionFromSynsetIdWordIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> adjPositionCursorToTreeModel(cursor, parent));
	}

	private TreeOp[] adjPositionCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final int positionId = cursor.getColumnIndex(Senses_AdjPositions.POSITION);

			final SpannableStringBuilder sb = new SpannableStringBuilder();
			do
			{
				final String position = cursor.getString(positionId);
				final String formattedPosition = String.format(Locale.ENGLISH, "%s", position);
				if (sb.length() != 0)
				{
					sb.append('\n');
				}
				Spanner.appendImage(sb, BaseModule.this.verbframeDrawable);
				sb.append(' ');
				sb.append(formattedPosition);
			}
			while (cursor.moveToNext());

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

	// M O R P H S

	/**
	 * Morphology
	 *
	 * @param wordId word id
	 * @param parent parent node
	 */
	void morphs(final long wordId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(Lexes_Morphs.CONTENT_URI_TABLE));
		final String[] projection = {Lexes_Morphs.POSID, Lexes_Morphs.MORPH};
		final String selection = Lexes_Morphs.WORDID + " = ?";
		final String[] selectionArgs = {Long.toString(wordId)};
		this.morphsFromWordIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> morphsCursorToTreeModel(cursor, parent));
	}

	private TreeOp[] morphsCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final int morphId = cursor.getColumnIndex(Lexes_Morphs.MORPH);
			final int posId = cursor.getColumnIndex(Lexes_Morphs.POSID);

			final SpannableStringBuilder sb = new SpannableStringBuilder();
			do
			{
				final String morph1 = cursor.getString(morphId);
				final String pos1 = cursor.getString(posId);
				final String formattedMorph = String.format(Locale.ENGLISH, "(%s) %s", pos1, morph1);
				if (sb.length() != 0)
				{
					sb.append('\n');
				}
				Spanner.appendImage(sb, BaseModule.this.morphDrawable);
				sb.append(' ');
				sb.append(formattedMorph);
			}
			while (cursor.moveToNext());

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

	// H E L P E R S

	/**
	 * Match relation id to drawable resource id
	 *
	 * @param relationId relation id
	 * @return kink res id
	 */
	private int getRelationRes(final int relationId)
	{
		switch (relationId)
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
			case 22:
				return R.drawable.ic_entailed;
			case 23:
				return R.drawable.ic_cause;
			case 24:
				return R.drawable.ic_caused;
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
	 * Relations query
	 */
	class RelationsQuery extends Query
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
		RelationsQuery(final long synsetId, final long wordId)
		{
			super(synsetId);
			this.wordId = wordId;
		}

		@Override
		public void process(@NonNull final TreeNode node)
		{
			relations(this.id, this.wordId, node, true);

			// sem relations
			//semRelations(this.id, node, false);

			// lex relations
			//lexRelations(this.id, this.wordId, node, false);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "relations for " + this.id + ',' + this.wordId;
		}
	}

	/**
	 * Semantic Relation query
	 */
	class SemRelationsQuery extends Query
	{
		/**
		 * Constructor
		 *
		 * @param synsetId synset id
		 */
		public SemRelationsQuery(final long synsetId)
		{
			super(synsetId);
		}

		@Override
		public void process(@NonNull final TreeNode node)
		{
			semRelations(this.id, node, true);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "semrelations for " + this.id;
		}
	}

	/**
	 * Lexical Relation query
	 */
	class LexRelationsQuery extends Query
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
		public LexRelationsQuery(final long synsetId, final long wordId)
		{
			super(synsetId);
			this.wordId = wordId;
		}

		@Override
		public void process(@NonNull final TreeNode node)
		{
			lexRelations(this.id, this.wordId, node, true);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "lexrelations for " + this.id + ',' + this.wordId;
		}
	}

	/**
	 * Sub relations of give type query
	 */
	class SubRelationsQuery extends Query
	{
		/**
		 * Relation id
		 */
		final int relationId;

		/**
		 * Recurse level
		 */
		final int recurseLevel;

		/**
		 * Constructor
		 *
		 * @param synsetId     synset id
		 * @param relationId   relation id
		 * @param recurseLevel recurse level
		 */
		SubRelationsQuery(final long synsetId, final int relationId, final int recurseLevel)
		{
			super(synsetId);
			this.relationId = relationId;
			this.recurseLevel = recurseLevel;
		}

		@Override
		public void process(@NonNull final TreeNode node)
		{
			semRelations(this.id, this.relationId, recurseLevel, node, true);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "sub semrelations of type " + this.relationId + " for " + this.id + " at level " + this.recurseLevel;
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
		SamplesQuery(final long synsetId)
		{
			super(synsetId);
		}

		@Override
		public void process(@NonNull final TreeNode node)
		{
			samples(this.id, node, true);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "samples for " + this.id;
		}
	}

	/**
	 * Word link data
	 */
	public static class BaseWordLink extends Link
	{
		protected final Fragment fragment;

		/**
		 * Constructor
		 *
		 * @param wordId   word id
		 * @param fragment fragment
		 */
		public BaseWordLink(final long wordId, final Fragment fragment)
		{
			super(wordId);
			this.fragment = fragment;
		}

		@Override
		public void process()
		{
			final Context context = this.fragment.getContext();
			if (context == null)
			{
				return;
			}

			final Parcelable pointer = new WordPointer(this.id);
			final Intent intent = new Intent(context, WordActivity.class);
			intent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_WORD);
			intent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer);
			intent.setAction(ProviderArgs.ACTION_QUERY);
			context.startActivity(intent);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "word for " + this.id;
		}
	}

	/**
	 * Word link data
	 */
	public static class WordLink extends BaseWordLink
	{
		/**
		 * Constructor
		 *
		 * @param wordId   word id
		 * @param fragment fragment
		 */
		WordLink(final Fragment fragment, final long wordId)
		{
			super(wordId, fragment);
		}
	}

	/**
	 * Synset link data
	 */
	public static class BaseSynsetLink extends Link
	{
		protected final Fragment fragment;

		final int recurse;

		/**
		 * Constructor
		 *
		 * @param synsetId synset id
		 * @param recurse  max recursion level
		 * @param fragment fragment
		 */
		public BaseSynsetLink(final long synsetId, final int recurse, final Fragment fragment)
		{
			super(synsetId);
			this.fragment = fragment;
			this.recurse = recurse;
		}

		@Override
		public void process()
		{
			final Context context = this.fragment.getContext();
			if (context == null)
			{
				return;
			}

			final Parcelable pointer = new SynsetPointer(this.id);
			final Intent intent = new Intent(context, SynsetActivity.class);
			intent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_SYNSET);
			intent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer);
			intent.putExtra(ProviderArgs.ARG_QUERYRECURSE, this.recurse);
			intent.setAction(ProviderArgs.ACTION_QUERY);
			context.startActivity(intent);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "synset for " + this.id;
		}
	}

	/**
	 * Synset link data
	 */
	public static class SynsetLink extends BaseSynsetLink
	{
		/**
		 * Constructor
		 *
		 * @param synsetId synset id
		 * @param recurse  max recursion level
		 * @param fragment fragment
		 */
		SynsetLink(final long synsetId, final int recurse, final Fragment fragment)
		{
			super(synsetId, recurse, fragment);
		}
	}

	/**
	 * Sense link data
	 */
	public static class BaseSenseLink extends BaseSynsetLink
	{
		final private long wordId;

		/**
		 * Constructor
		 *
		 * @param synsetId synset id
		 * @param wordId   word id
		 * @param fragment fragment
		 */
		public BaseSenseLink(final long synsetId, final long wordId, final int recurse, final Fragment fragment)
		{
			super(synsetId, recurse, fragment);
			this.wordId = wordId;
		}

		@Override
		public void process()
		{
			final Context context = this.fragment.getContext();
			if (context == null)
			{
				return;
			}

			final Parcelable pointer = new SensePointer(this.id, this.wordId);
			final Intent intent = new Intent(context, SynsetActivity.class);
			intent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_SYNSET);
			intent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer);
			intent.putExtra(ProviderArgs.ARG_QUERYRECURSE, this.recurse);
			intent.setAction(ProviderArgs.ACTION_QUERY);
			context.startActivity(intent);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "sense for " + this.id + ',' + this.wordId;
		}
	}

	/**
	 * Sense link data
	 */
	public static class SenseLink extends BaseSenseLink
	{
		/**
		 * Constructor
		 *
		 * @param synsetId synset id
		 * @param wordId   word id
		 * @param recurse  max recursion level
		 * @param fragment fragment
		 */
		SenseLink(final long synsetId, final long wordId, final int recurse, final Fragment fragment)
		{
			super(synsetId, wordId, recurse, fragment);
		}
	}
}
