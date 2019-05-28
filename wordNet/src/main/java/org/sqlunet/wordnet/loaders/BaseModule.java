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
import org.sqlunet.wordnet.provider.WordNetContract.AdjPositions_AdjPositionTypes;
import org.sqlunet.wordnet.provider.WordNetContract.LexDomains;
import org.sqlunet.wordnet.provider.WordNetContract.LexLinks_Senses_Words_X;
import org.sqlunet.wordnet.provider.WordNetContract.LinkTypes;
import org.sqlunet.wordnet.provider.WordNetContract.Links_Senses_Words_X;
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
import org.sqlunet.wordnet.provider.WordNetProvider;
import org.sqlunet.wordnet.style.WordNetFactories;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import static org.sqlunet.view.TreeOp.TreeOpCode.NEWTREE;
import static org.sqlunet.view.TreeOp.TreeOpCode.DEADEND;
import static org.sqlunet.view.TreeOp.TreeOpCode.NEWCHILD;
import static org.sqlunet.view.TreeOp.TreeOpCode.NEWUNIQUE;
import static org.sqlunet.view.TreeOp.TreeOpCode.NEWMAIN;
import static org.sqlunet.view.TreeOp.TreeOpCode.NEWEXTRA;
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

	private final Drawable synsetDrawable;

	private final Drawable memberDrawable;

	private final Drawable definitionDrawable;

	private final Drawable sampleDrawable;

	private final Drawable posDrawable;

	private final Drawable lexdomainDrawable;

	private final Drawable verbframeDrawable;

	private final Drawable morphDrawable;

	/**
	 * Whether members2 are grouped
	 */
	private boolean membersGrouped = false;

	/**
	 * Max link recursion
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

	private SqlunetViewTreeModel linksFromSynsetIdWordIdModel;

	private SqlunetViewTreeModel semLinksFromSynsetIdModel;

	private SqlunetViewTreeModel semLinksFromSynsetIdLinkIdModel;

	private SqlunetViewTreeModel lexLinksFromSynsetIdWordIdModel;

	private SqlunetViewTreeModel lexLinksFromSynsetIdModel;

	private SqlunetViewTreeModel vFramesFromSynsetIdModel;

	private SqlunetViewTreeModel vFramesFromSynsetIdWordIdModel;

	private SqlunetViewTreeModel vFrameSentencesFromSynsetIdModel;

	private SqlunetViewTreeModel vFrameSentencesFromSynsetIdWordIdModel;

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
		this.lexdomainDrawable = Spanner.getDrawable(context, R.drawable.domain);
		this.verbframeDrawable = Spanner.getDrawable(context, R.drawable.verbframe);
		this.morphDrawable = this.verbframeDrawable;
	}

	/**
	 * Make view models
	 */
	private void makeModels()
	{
		this.wordModel = ViewModelProviders.of(this.fragment).get("wn.word(wordid)", SqlunetViewTreeModel.class);
		this.wordModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.sensesFromWordModel = ViewModelProviders.of(this.fragment).get("wn.senses(word)", SqlunetViewTreeModel.class);
		this.sensesFromWordModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.sensesFromWordIdModel = ViewModelProviders.of(this.fragment).get("wn.senses(wordid)", SqlunetViewTreeModel.class);
		this.sensesFromWordIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.senseFromSenseIdModel = ViewModelProviders.of(this.fragment).get("wn.sense(senseid)", SqlunetViewTreeModel.class);
		this.senseFromSenseIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.senseFromSenseKeyModel = ViewModelProviders.of(this.fragment).get("wn.sense(sensekey)", SqlunetViewTreeModel.class);
		this.senseFromSenseKeyModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.senseFromSynsetIdWordIdModel = ViewModelProviders.of(this.fragment).get("wn.sense(synsetid,wordid)", SqlunetViewTreeModel.class);
		this.senseFromSynsetIdWordIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.synsetFromSynsetIdModel = ViewModelProviders.of(this.fragment).get("wn.synset(synsetid)", SqlunetViewTreeModel.class);
		this.synsetFromSynsetIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.membersFromSynsetIdModel = ViewModelProviders.of(this.fragment).get("wn.members2(synsetid)", SqlunetViewTreeModel.class);
		this.membersFromSynsetIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.members2FromSynsetIdModel = ViewModelProviders.of(this.fragment).get("wn.members2(synsetid)", SqlunetViewTreeModel.class);
		this.members2FromSynsetIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.samplesfromSynsetIdModel = ViewModelProviders.of(this.fragment).get("wn.samples(synsetid)", SqlunetViewTreeModel.class);
		this.samplesfromSynsetIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.linksFromSynsetIdWordIdModel = ViewModelProviders.of(this.fragment).get("wn.links(synsetid,wordid)", SqlunetViewTreeModel.class);
		this.linksFromSynsetIdWordIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.semLinksFromSynsetIdModel = ViewModelProviders.of(this.fragment).get("wn.semlinks(synsetid)", SqlunetViewTreeModel.class);
		this.semLinksFromSynsetIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.semLinksFromSynsetIdLinkIdModel = ViewModelProviders.of(this.fragment).get("wn.semlinks(synsetid,linkid)", SqlunetViewTreeModel.class);
		this.semLinksFromSynsetIdLinkIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.lexLinksFromSynsetIdWordIdModel = ViewModelProviders.of(this.fragment).get("wn.lexlinks(synsetid,wordid)", SqlunetViewTreeModel.class);
		this.lexLinksFromSynsetIdWordIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.lexLinksFromSynsetIdModel = ViewModelProviders.of(this.fragment).get("wn.lexlinks(synsetid)", SqlunetViewTreeModel.class);
		this.lexLinksFromSynsetIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.vFramesFromSynsetIdModel = ViewModelProviders.of(this.fragment).get("wn.vframes(synsetid)", SqlunetViewTreeModel.class);
		this.vFramesFromSynsetIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.vFramesFromSynsetIdWordIdModel = ViewModelProviders.of(this.fragment).get("wn.vframes(synsetid,wordid)", SqlunetViewTreeModel.class);
		this.vFramesFromSynsetIdWordIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.vFrameSentencesFromSynsetIdModel = ViewModelProviders.of(this.fragment).get("wn.vframesentences(synsetid)", SqlunetViewTreeModel.class);
		this.vFrameSentencesFromSynsetIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.vFrameSentencesFromSynsetIdWordIdModel = ViewModelProviders.of(this.fragment).get("wn.vframesentences(synsetid,wordid)", SqlunetViewTreeModel.class);
		this.vFrameSentencesFromSynsetIdWordIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.adjPositionFromSynsetIdModel = ViewModelProviders.of(this.fragment).get("wn.adjposition(synsetid)", SqlunetViewTreeModel.class);
		this.adjPositionFromSynsetIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.adjPositionFromSynsetIdWordIdModel = ViewModelProviders.of(this.fragment).get("wn.adjposition(synsetid,wordid)", SqlunetViewTreeModel.class);
		this.adjPositionFromSynsetIdWordIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.morphsFromWordIdModel = ViewModelProviders.of(this.fragment).get("wn.morphs(wordid)", SqlunetViewTreeModel.class);
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
	@SuppressWarnings("unused")
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
		final Uri uri = Uri.parse(WordNetProvider.makeUri(Words_MorphMaps_Morphs.CONTENT_URI_TABLE_BY_WORD));
		final String[] projection = { //
				Words_MorphMaps_Morphs.LEMMA, //
				Words_MorphMaps_Morphs.WORDID, //
				"GROUP_CONCAT(" + Words_MorphMaps_Morphs.MORPH + "||'-'||" + Words_MorphMaps_Morphs.POS + ") AS " + BaseModule.ALLMORPHS};
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
			final int idLemma = cursor.getColumnIndex(Words_MorphMaps_Morphs.LEMMA);
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
	@SuppressWarnings("unused")
	protected void senses(final String word, @NonNull final TreeNode parent)
	{
		// load the contents
		final Uri uri = Uri.parse(WordNetProvider.makeUri(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.CONTENT_URI_TABLE));
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
		final Uri uri = Uri.parse(WordNetProvider.makeUri(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.CONTENT_URI_TABLE));
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
		this.sensesFromWordIdModel.loadData(uri, projection, selection, selectionArgs, sortOrder, cursor -> sensesCursorToTreeModel(cursor, parent));
	}

	private TreeOp[] sensesCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

			final int idWordId = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.WORDID);
			final int idSynsetId = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID);
			final int idPosName = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.POSNAME);
			final int idLexDomain = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEXDOMAIN);
			final int idDefinition = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.DEFINITION);
			final int idTagCount = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.TAGCOUNT);
			final int idCased = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.CASED);

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
				final TreeNode synsetNode = TreeFactory.makeLinkNode(sb, R.drawable.synset, false, new SenseLink(synsetId, wordId, this.maxRecursion)).addTo(parent);
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
	@SuppressWarnings("unused")
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
			final int idWordId = cursor.getColumnIndex(PosTypes.POSNAME);
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
		final Uri uri = Uri.parse(WordNetProvider.makeUri(Synsets_PosTypes_LexDomains.CONTENT_URI_TABLE));
		final String[] projection = { //
				Synsets.DEFINITION, //
				PosTypes.POSNAME, //
				LexDomains.LEXDOMAIN, //
		};
		final String selection = Synsets_PosTypes_LexDomains.SYNSETID + " = ?";
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
			final int idPosName = cursor.getColumnIndex(PosTypes.POSNAME);
			final int idLexDomain = cursor.getColumnIndex(LexDomains.LEXDOMAIN);
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
			final TreeNode linksNode = TreeFactory.makeHotQueryNode("Links", R.drawable.ic_links, false, new LinksQuery(synsetId, wordId)).addTo(parent);
			final TreeNode samplesNode = TreeFactory.makeHotQueryNode("Samples", R.drawable.sample, false, new SamplesQuery(synsetId)).addTo(parent);

			changed = TreeOp.seq(parent, NEWMAIN, node, NEWEXTRA, linksNode, NEWEXTRA, samplesNode, NEWTREE);
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
		final Uri uri = Uri.parse(WordNetProvider.makeUri(Synsets_PosTypes_LexDomains.CONTENT_URI_TABLE));
		final String[] projection = { //
				Synsets.DEFINITION, //
				PosTypes.POSNAME, //
				LexDomains.LEXDOMAIN, //
		};
		final String selection = Synsets_PosTypes_LexDomains.SYNSETID + " = ?";
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
			final int idPosName = cursor.getColumnIndex(PosTypes.POSNAME);
			final int idLexDomain = cursor.getColumnIndex(LexDomains.LEXDOMAIN);
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
		Spanner.appendImage(sb, BaseModule.this.lexdomainDrawable);
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
				final TreeNode memberNode = TreeFactory.makeLinkNode(sb, R.drawable.member, false, new WordLink(wordId)).addTo(parent);
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
	@SuppressWarnings("unused")
	void members2(final long synsetId, @NonNull final TreeNode parent, final boolean addNewNode)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(BaseModule.this.membersGrouped ? Senses_Words.CONTENT_URI_TABLE_BY_SYNSET : Senses_Words.CONTENT_URI_TABLE));
		final String[] projection = BaseModule.this.membersGrouped ? //
				new String[]{Senses_Words.MEMBERS} : new String[]{Words.LEMMA};
		final String selection = Senses_Words.SYNSETID + " = ?";
		final String[] selectionArgs = {Long.toString(synsetId)};
		final String sortOrder = Words.LEMMA;
		this.members2FromSynsetIdModel.loadData(uri, projection, selection, selectionArgs, sortOrder, cursor -> members2CursorToTreeModel(cursor, parent, addNewNode));
	}

	private TreeOp[] members2CursorToTreeModel(final Cursor cursor, @NonNull final TreeNode parent, final boolean addNewNode)
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
				final int lemmaId = cursor.getColumnIndex(Words.LEMMA);
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
	static public final String TARGET_LEMMA = "d_lemma";
	static public final String TARGET_WORDID = "d_wordid";

	/**
	 * Links (union)
	 *
	 * @param synsetId                synset id
	 * @param wordId                  word id
	 * @param parent                  parent node
	 * @param deadendParentIfNoResult mark parent node as deadend if there is no result
	 */
	private void links(final long synsetId, final long wordId, @NonNull final TreeNode parent, @SuppressWarnings("SameParameterValue") final boolean deadendParentIfNoResult)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(Links_Senses_Words_X.CONTENT_URI_TABLE));
		final String[] projection = { //
				WordNetContract.TYPE, LinkTypes.LINKID, //
				LinkTypes.LINK, //
				WordNetContract.DEST + '.' + Synsets.SYNSETID + " AS " + BaseModule.TARGET_SYNSETID, //
				WordNetContract.DEST + '.' + Synsets.DEFINITION + " AS " + BaseModule.TARGET_DEFINITION, //
				"GROUP_CONCAT(" + WordNetContract.WORD + '.' + Words.LEMMA + ") AS " + Links_Senses_Words_X.MEMBERS2, //
				Links_Senses_Words_X.RECURSES, //
				WordNetContract.WORD2 + '.' + Words.WORDID + " AS " + BaseModule.TARGET_WORDID, //
				WordNetContract.WORD2 + '.' + Words.LEMMA + " AS " + BaseModule.TARGET_LEMMA, //
		};
		final String selection = "synset1id = ? /**/|/**/ synset1id = ? AND word1id = ?";
		final String[] selectionArgs = {Long.toString(synsetId), Long.toString(synsetId), Long.toString(wordId)};
		final String sortOrder = LinkTypes.LINKID;
		this.linksFromSynsetIdWordIdModel.loadData(uri, projection, selection, selectionArgs, sortOrder, cursor -> linksCursorToTreeModel(cursor, parent, deadendParentIfNoResult));
	}

	private TreeOp[] linksCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent, final boolean deadendParentIfNoResult)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

			final int idLinkId = cursor.getColumnIndex(LinkTypes.LINKID);
			// final int idLink = cursor.getColumnIndex(LinkTypes.LINK);
			final int idTargetSynsetId = cursor.getColumnIndex(BaseModule.TARGET_SYNSETID);
			final int idTargetDefinition = cursor.getColumnIndex(BaseModule.TARGET_DEFINITION);
			final int idTargetMembers = cursor.getColumnIndex(Links_Senses_Words_X.MEMBERS2);
			final int idRecurses = cursor.getColumnIndex(Links_Senses_Words_X.RECURSES);
			final int idTargetWordId = cursor.getColumnIndex(BaseModule.TARGET_WORDID);
			final int idTargetLemma = cursor.getColumnIndex(BaseModule.TARGET_LEMMA);

			do
			{
				final SpannableStringBuilder sb = new SpannableStringBuilder();

				final int linkId = cursor.getInt(idLinkId);
				//final String link = cursor.getString(idLink);

				final long targetSynsetId = cursor.getLong(idTargetSynsetId);
				final String targetDefinition = cursor.getString(idTargetDefinition);
				String targetMembers = cursor.getString(idTargetMembers);
				final boolean linkCanRecurse = !cursor.isNull(idRecurses) && cursor.getInt(idRecurses) != 0;
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
				if (linkCanRecurse)
				{
					final TreeNode linksNode = TreeFactory.makeLinkQueryNode(sb, getLinkRes(linkId), false, new SubLinksQuery(targetSynsetId, linkId, BaseModule.this.maxRecursion), new SynsetLink(targetSynsetId, BaseModule.this.maxRecursion)).addTo(parent);
					changedList.add(NEWCHILD, linksNode);
				}
				else
				{
					final TreeNode node = TreeFactory.makeLinkLeafNode(sb, getLinkRes(linkId), false, targetWordId == null ?
							new SynsetLink(targetSynsetId, BaseModule.this.maxRecursion) :
							new SenseLink(targetSynsetId, targetWordId, BaseModule.this.maxRecursion)).addTo(parent);
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

	// S E M L I N K S

	/**
	 * Semantic links
	 *
	 * @param synsetId                synset id
	 * @param parent                  parent node
	 * @param deadendParentIfNoResult mark parent node as deadend if there is no result
	 */
	private void semLinks(final long synsetId, @NonNull final TreeNode parent, @SuppressWarnings("SameParameterValue") final boolean deadendParentIfNoResult)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(SemLinks_Synsets_Words_X.CONTENT_URI_TABLE));
		final String[] projection = { //
				LinkTypes.LINKID, //
				LinkTypes.LINK, //
				WordNetContract.DEST + '.' + Synsets.SYNSETID + " AS " + BaseModule.TARGET_SYNSETID, //
				WordNetContract.DEST + '.' + Synsets.DEFINITION + " AS " + BaseModule.TARGET_DEFINITION, //
				LinkTypes.RECURSES, //
		};
		final String selection = WordNetContract.LINK + '.' + SemLinks_Synsets_Words_X.SYNSET1ID + " = ?";  ////
		final String[] selectionArgs = {Long.toString(synsetId)};
		final String sortOrder = LinkTypes.LINKID;
		this.semLinksFromSynsetIdModel.loadData(uri, projection, selection, selectionArgs, sortOrder, cursor -> semLinksCursorToTreeModel(cursor, parent, deadendParentIfNoResult));
	}

	private TreeOp[] semLinksCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent, final boolean deadendParentIfNoResult)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

			// final int idLink = cursor.getColumnIndex(LinkTypes.LINK);
			final int idLinkId = cursor.getColumnIndex(LinkTypes.LINKID);
			final int idTargetSynsetId = cursor.getColumnIndex(BaseModule.TARGET_SYNSETID);
			final int idTargetDefinition = cursor.getColumnIndex(BaseModule.TARGET_DEFINITION);
			final int idTargetMembers = cursor.getColumnIndex(SemLinks_Synsets_Words_X.MEMBERS2);
			final int idRecurses = cursor.getColumnIndex(SemLinks_Synsets_Words_X.RECURSES);

			do
			{
				final SpannableStringBuilder sb = new SpannableStringBuilder();

				// final String link = cursor.getString(idLink);
				final int linkId = cursor.getInt(idLinkId);
				final long targetSynsetId = cursor.getLong(idTargetSynsetId);
				final String targetDefinition = cursor.getString(idTargetDefinition);
				final String targetMembers = cursor.getString(idTargetMembers);
				final boolean linkCanRecurse = cursor.getInt(idRecurses) != 0;

				Spanner.append(sb, targetMembers, 0, WordNetFactories.membersFactory);
				sb.append(' ');
				Spanner.append(sb, targetDefinition, 0, WordNetFactories.definitionFactory);

				// recursion
				if (linkCanRecurse)
				{
					final TreeNode linksNode = TreeFactory.makeLinkQueryNode(sb, getLinkRes(linkId), false, new SubLinksQuery(targetSynsetId, linkId, BaseModule.this.maxRecursion), new SynsetLink(targetSynsetId, BaseModule.this.maxRecursion)).addTo(parent);
					changedList.add(NEWCHILD, linksNode);
				}
				else
				{
					final TreeNode node = TreeFactory.makeLeafNode(sb, getLinkRes(linkId), false).addTo(parent);
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
	 * Semantic links
	 *
	 * @param synsetId                synset id
	 * @param linkId                  link id
	 * @param parent                  parent node
	 * @param deadendParentIfNoResult mark parent node as deadend if there is no result
	 */
	private void semLinks(final long synsetId, final int linkId, final int recurseLevel, @NonNull final TreeNode parent, @SuppressWarnings("SameParameterValue") final boolean deadendParentIfNoResult)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(SemLinks_Synsets_Words_X.CONTENT_URI_TABLE));
		final String[] projection = { //
				LinkTypes.LINKID, //
				LinkTypes.LINK, //
				WordNetContract.DEST + '.' + Synsets.SYNSETID + " AS " + BaseModule.TARGET_SYNSETID, //
				WordNetContract.DEST + '.' + Synsets.DEFINITION + " AS " + BaseModule.TARGET_DEFINITION, //
				LinkTypes.RECURSES, //
		};
		final String selection = WordNetContract.LINK + '.' + SemLinks_Synsets_Words_X.SYNSET1ID + " = ? AND " + LinkTypes.LINKID + " = ?";
		final String[] selectionArgs = {Long.toString(synsetId), Integer.toString(linkId)};
		this.semLinksFromSynsetIdLinkIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> semLinksFromSynsetIdLinkIdCursorToTreeModel(cursor, linkId, recurseLevel, parent, deadendParentIfNoResult));
	}

	private TreeOp[] semLinksFromSynsetIdLinkIdCursorToTreeModel(@NonNull final Cursor cursor, final int linkId, final int recurseLevel, @NonNull final TreeNode parent, final boolean deadendParentIfNoResult)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

			// final int idLinkId = cursor.getColumnIndex(LinkTypes.LINKID);
			// final int idLink = cursor.getColumnIndex(LinkTypes.LINK);
			final int idTargetSynsetId = cursor.getColumnIndex(BaseModule.TARGET_SYNSETID);
			final int idTargetDefinition = cursor.getColumnIndex(BaseModule.TARGET_DEFINITION);
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
				final boolean linkCanRecurse = cursor.getInt(idRecurses) != 0;

				Spanner.append(sb, targetMembers, 0, WordNetFactories.membersFactory);
				sb.append(' ');
				Spanner.append(sb, targetDefinition, 0, WordNetFactories.definitionFactory);

				// recurse
				if (linkCanRecurse)
				{
					if (recurseLevel > 1)
					{
						final int newRecurseLevel = recurseLevel - 1;
						final TreeNode linksNode = TreeFactory.makeLinkQueryNode(sb, getLinkRes(linkId), false, new SubLinksQuery(targetSynsetId, linkId, newRecurseLevel), new SynsetLink(targetSynsetId, BaseModule.this.maxRecursion)).addTo(parent);
						changedList.add(NEWCHILD, linksNode);
					}
					else
					{
						final TreeNode moreNode = TreeFactory.makeMoreNode(sb, getLinkRes(linkId), false).addTo(parent);
						changedList.add(NEWCHILD, moreNode);
					}
				}
				else
				{
					final TreeNode node = TreeFactory.makeLeafNode(sb, getLinkRes(linkId), false).addTo(parent);
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

	// L E X L I N K S

	/**
	 * Lexical links
	 *
	 * @param synsetId                synset id
	 * @param wordId                  word id
	 * @param parent                  parent node
	 * @param deadendParentIfNoResult mark parent node as deadend if there is no result
	 */
	private void lexLinks(final long synsetId, final long wordId, @NonNull final TreeNode parent, final boolean deadendParentIfNoResult)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(LexLinks_Senses_Words_X.CONTENT_URI_TABLE));
		final String[] projection = { //
				LinkTypes.LINKID, //
				LinkTypes.LINK, //
				WordNetContract.DEST + '.' + Synsets.SYNSETID + " AS " + BaseModule.TARGET_SYNSETID, //
				WordNetContract.DEST + '.' + Synsets.DEFINITION + " AS " + BaseModule.TARGET_DEFINITION, //
				WordNetContract.WORD + '.' + Words.WORDID + " AS " + BaseModule.TARGET_WORDID, //
				WordNetContract.WORD + '.' + Words.LEMMA + " AS " + BaseModule.TARGET_LEMMA, //
		};
		final String selection = WordNetContract.LINK + ".synset1id = ? AND " + WordNetContract.LINK + ".word1id = ?";
		final String[] selectionArgs = {Long.toString(synsetId), Long.toString(wordId)};
		final String sortOrder = LinkTypes.LINKID;
		this.lexLinksFromSynsetIdWordIdModel.loadData(uri, projection, selection, selectionArgs, sortOrder, cursor -> lexLinksCursorToTreeModel(cursor, parent, deadendParentIfNoResult));
	}

	private TreeOp[] lexLinksCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent, final boolean deadendParentIfNoResult)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

			// final int idLink = cursor.getColumnIndex(LinkTypes.LINK);
			final int idLinkId = cursor.getColumnIndex(LinkTypes.LINKID);
			final int idTargetSynsetId = cursor.getColumnIndex(BaseModule.TARGET_SYNSETID);
			final int idTargetDefinition = cursor.getColumnIndex(BaseModule.TARGET_DEFINITION);
			final int idTargetMembers = cursor.getColumnIndex(LexLinks_Senses_Words_X.MEMBERS2);
			final int idTargetWordId = cursor.getColumnIndex(BaseModule.TARGET_WORDID);
			final int idTargetLemma = cursor.getColumnIndex(BaseModule.TARGET_LEMMA);

			do
			{
				final SpannableStringBuilder sb = new SpannableStringBuilder();

				// final String link = cursor.getString(idLink);
				// final String targetWordId = cursor.getString(idTargetWordId);
				final int linkId = cursor.getInt(idLinkId);
				final long targetSynsetId = cursor.getLong(idTargetSynsetId);
				final String targetDefinition = cursor.getString(idTargetDefinition);
				final String targetLemma = cursor.getString(idTargetLemma);
				String targetMembers = cursor.getString(idTargetMembers);
				if (targetLemma != null)
				{
					targetMembers = targetMembers.replaceAll("\\b" + targetLemma + "\\b", targetLemma + '*');
				}
				// final String formattedTarget = String.format(Locale.ENGLISH, "[%s] %s (%s)\n\t%s (synset %s) {%s}", link, targetLemma, targetWordId,targetDefinition, targetSynsetId, targetMembers);

				if (sb.length() != 0)
				{
					sb.append('\n');
				}
				// Spanner.appendImage(sb, getLinkDrawable(linkId));
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
				final TreeNode linkNode = TreeFactory.makeLinkLeafNode(sb, getLinkRes(linkId), false, new SenseLink(targetSynsetId, idTargetWordId, BaseModule.this.maxRecursion)).addTo(parent);
				changedList.add(NEWCHILD, linkNode);
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
	 * Lexical links
	 *
	 * @param synsetId                synset id
	 * @param parent                  parent
	 * @param deadendParentIfNoResult mark parent node as deadend if there is no result
	 */
	@SuppressWarnings("unused")
	void lexLinks(final long synsetId, @NonNull final TreeNode parent, final boolean deadendParentIfNoResult)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(LexLinks_Senses_Words_X.CONTENT_URI_TABLE));
		final String[] projection = { //
				LinkTypes.LINKID, //
				LinkTypes.LINK, //
				WordNetContract.DEST + '.' + Synsets.SYNSETID + " AS " + BaseModule.TARGET_SYNSETID, //
				WordNetContract.DEST + '.' + Synsets.DEFINITION + " AS " + BaseModule.TARGET_DEFINITION, //
				WordNetContract.WORD + '.' + Words.WORDID + " AS " + BaseModule.TARGET_WORDID, //
				WordNetContract.WORD + '.' + Words.LEMMA + " AS " + BaseModule.TARGET_LEMMA, //
		};
		final String selection = WordNetContract.LINK + '.' + LexLinks_Senses_Words_X.SYNSET1ID + " = ?";  ////
		final String[] selectionArgs = {Long.toString(synsetId)};
		this.lexLinksFromSynsetIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> lexLinksFromSynsetIdCursorToTreeModel(cursor, parent, deadendParentIfNoResult));
	}

	private TreeOp[] lexLinksFromSynsetIdCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent, final boolean deadendParentIfNoResult)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

			// final int idLink = cursor.getColumnIndex(LinkTypes.LINK);
			// final int idTargetSynsetId = cursor.getColumnIndex(BaseModule.TARGET_SYNSETID);
			// final int idTargetWordId = cursor.getColumnIndex(BaseModule.TARGET_WORDID);
			final int idLinkId = cursor.getColumnIndex(LinkTypes.LINKID);
			final int idTargetDefinition = cursor.getColumnIndex(BaseModule.TARGET_DEFINITION);
			final int idTargetMembers = cursor.getColumnIndex(LexLinks_Senses_Words_X.MEMBERS2);
			final int idTargetLemma = cursor.getColumnIndex(BaseModule.TARGET_LEMMA);

			final SpannableStringBuilder sb = new SpannableStringBuilder();
			do
			{
				// final String link = cursor.getString(idLink);
				// final String targetSynsetId = cursor.getString(idTargetSynsetId);
				// final String targetWordId = cursor.getString(idTargetWordId);
				final int linkId = cursor.getInt(idLinkId);
				final String targetDefinition = cursor.getString(idTargetDefinition);
				final String targetLemma = cursor.getString(idTargetLemma);
				String targetMembers = cursor.getString(idTargetMembers);
				if (targetLemma != null)
				{
					targetMembers = targetMembers.replaceAll("\\b" + targetLemma + "\\b", targetLemma + '*');
				}
				// final String formattedTarget = String.format(Locale.ENGLISH, "[%s] %s (%s)\n\t%s (synset %s) {%s}", link, targetLemma, targetWordId, targetDefinition, targetSynsetId, targetMembers);

				if (sb.length() != 0)
				{
					sb.append('\n');
				}
				// Spanner.appendImage(sb, getLinkDrawable(linkId));
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
				final TreeNode linkNode = TreeFactory.makeLeafNode(sb, getLinkRes(linkId), false).addTo(parent);
				changedList.add(NEWCHILD, linkNode);
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
		final Uri uri = Uri.parse(WordNetProvider.makeUri(VerbFrameMaps_VerbFrames.CONTENT_URI_TABLE));
		final String[] projection = {VerbFrameMaps_VerbFrames.FRAME};
		final String selection = VerbFrameMaps_VerbFrames.SYNSETID + " = ?";
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
		final Uri uri = Uri.parse(WordNetProvider.makeUri(VerbFrameMaps_VerbFrames.CONTENT_URI_TABLE));
		final String[] projection = {VerbFrameMaps_VerbFrames.FRAME};
		final String selection = VerbFrameMaps_VerbFrames.SYNSETID + " = ? AND " + VerbFrameMaps_VerbFrames.WORDID + " = ?";
		final String[] selectionArgs = {Long.toString(synsetId), Long.toString(wordId)};
		this.vFramesFromSynsetIdWordIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> vframesCursorToTreeModel(cursor, parent));
	}

	private TreeOp[] vframesCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final int vframeId = cursor.getColumnIndex(VerbFrameMaps_VerbFrames.FRAME);

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

	// V F R A M E S E N TE N C E S

	/**
	 * Verb frame sentences
	 *
	 * @param synsetId synset id
	 * @param parent   parent node
	 */
	void vFrameSentences(final long synsetId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(VerbFrameSentenceMaps_VerbFrameSentences.CONTENT_URI_TABLE));
		final String[] projection = {VerbFrameSentenceMaps_VerbFrameSentences.SENTENCE};
		final String selection = VerbFrameSentenceMaps_VerbFrameSentences.SYNSETID + " = ?";
		final String[] selectionArgs = {Long.toString(synsetId)};
		this.vFrameSentencesFromSynsetIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> vFrameSentencesCursorToTreeModel(cursor, parent));
	}

	private TreeOp[] vFrameSentencesCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final int vframeId = cursor.getColumnIndex(VerbFrameSentenceMaps_VerbFrameSentences.SENTENCE);

			final SpannableStringBuilder sb = new SpannableStringBuilder();
			do
			{
				final String vframesentence = cursor.getString(vframeId);
				final String formattedVframesentence = String.format(Locale.ENGLISH, vframesentence, "[-]");
				if (sb.length() != 0)
				{
					sb.append('\n');
				}
				Spanner.appendImage(sb, BaseModule.this.verbframeDrawable);
				sb.append(' ');
				sb.append(formattedVframesentence);
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
	 * Verb frame sentences
	 *
	 * @param synsetId synset id
	 * @param wordId   word id
	 * @param parent   parent node
	 */
	void vFrameSentences(final long synsetId, final long wordId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(VerbFrameSentenceMaps_VerbFrameSentences.CONTENT_URI_TABLE));
		final String[] projection = {VerbFrameSentenceMaps_VerbFrameSentences.SENTENCE};
		final String selection = VerbFrameSentenceMaps_VerbFrameSentences.SYNSETID + " = ? AND " + VerbFrameSentenceMaps_VerbFrameSentences.WORDID + " = ?";
		final String[] selectionArgs = {Long.toString(synsetId), Long.toString(wordId)};
		this.vFrameSentencesFromSynsetIdWordIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> vFrameSentencesFromSynsetIdWordIdCursorToTreeModel(cursor, parent));
	}

	private TreeOp[] vFrameSentencesFromSynsetIdWordIdCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final String lemma = "---";
			final int vframeId = cursor.getColumnIndex(VerbFrameSentenceMaps_VerbFrameSentences.SENTENCE);

			final SpannableStringBuilder sb = new SpannableStringBuilder();
			do
			{
				final String vframesentence = cursor.getString(vframeId);
				final String formattedVframesentence = String.format(Locale.ENGLISH, vframesentence, '[' + lemma + ']');
				if (sb.length() != 0)
				{
					sb.append('\n');
				}
				Spanner.appendImage(sb, BaseModule.this.verbframeDrawable);
				sb.append(' ');
				sb.append(formattedVframesentence);
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
		final Uri uri = Uri.parse(WordNetProvider.makeUri(AdjPositions_AdjPositionTypes.CONTENT_URI_TABLE));
		final String[] projection = {AdjPositions_AdjPositionTypes.POSITIONNAME};
		final String selection = AdjPositions_AdjPositionTypes.SYNSETID + " = ?";
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
		final Uri uri = Uri.parse(WordNetProvider.makeUri(AdjPositions_AdjPositionTypes.CONTENT_URI_TABLE));
		final String[] projection = {AdjPositions_AdjPositionTypes.POSITIONNAME};
		final String selection = AdjPositions_AdjPositionTypes.SYNSETID + " = ? AND " + AdjPositions_AdjPositionTypes.WORDID + " = ?";
		final String[] selectionArgs = {Long.toString(synsetId), Long.toString(wordId)};
		this.adjPositionFromSynsetIdWordIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> adjPositionCursorToTreeModel(cursor, parent));
	}

	private TreeOp[] adjPositionCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final int positionId = cursor.getColumnIndex(AdjPositions_AdjPositionTypes.POSITIONNAME);

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
		final Uri uri = Uri.parse(WordNetProvider.makeUri(MorphMaps_Morphs.CONTENT_URI_TABLE));
		final String[] projection = {MorphMaps_Morphs.POS, MorphMaps_Morphs.MORPH};
		final String selection = MorphMaps_Morphs.WORDID + " = ?";
		final String[] selectionArgs = {Long.toString(wordId)};
		this.morphsFromWordIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> morphsCursorToTreeModel(cursor, parent));
	}

	private TreeOp[] morphsCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final int morphId = cursor.getColumnIndex(MorphMaps_Morphs.MORPH);
			final int posId = cursor.getColumnIndex(MorphMaps_Morphs.POS);

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
		public void process(@NonNull final TreeNode node)
		{
			links(this.id, this.wordId, node, true);

			// sem links
			//semLinks(this.id, node, false);

			// lex links
			//lexLinks(this.id, this.wordId, node, false);
		}

		@Override
		public String toString()
		{
			return "links for " + this.id + ',' + this.wordId;
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
		public void process(@NonNull final TreeNode node)
		{
			semLinks(this.id, node, true);
		}

		@Override
		public String toString()
		{
			return "semlinks for " + this.id;
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
		public void process(@NonNull final TreeNode node)
		{
			lexLinks(this.id, this.wordId, node, true);
		}

		@Override
		public String toString()
		{
			return "lexlinks for " + this.id + ',' + this.wordId;
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
		 * Recurse level
		 */
		final int recurseLevel;

		/**
		 * Constructor
		 *
		 * @param synsetId     synset id
		 * @param linkId       link id
		 * @param recurseLevel recurse level
		 */
		SubLinksQuery(final long synsetId, final int linkId, final int recurseLevel)
		{
			super(synsetId);
			this.linkId = linkId;
			this.recurseLevel = recurseLevel;
		}

		@Override
		public void process(@NonNull final TreeNode node)
		{
			semLinks(this.id, this.linkId, recurseLevel, node, true);
		}

		@Override
		public String toString()
		{
			return "sub semlinks of type " + this.linkId + " for " + this.id + " at level " + this.recurseLevel;
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

		@Override
		public String toString()
		{
			return "samples for " + this.id;
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
		WordLink(final long wordId)
		{
			super(wordId);
		}

		@Override
		public void process()
		{
			final Context context = BaseModule.this.fragment.requireContext();

			final Parcelable pointer = new WordPointer(this.id);
			final Intent intent = new Intent(context, WordActivity.class);
			intent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_WORD);
			intent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer);
			intent.setAction(ProviderArgs.ACTION_QUERY);

			context.startActivity(intent);
		}

		@Override
		public String toString()
		{
			return "word for " + this.id;
		}
	}

	/**
	 * Synset link data
	 */
	class SynsetLink extends Link
	{
		final int recurse;

		/**
		 * Constructor
		 *
		 * @param synsetId synset id
		 * @param recurse  max recursion level
		 */
		SynsetLink(final long synsetId, final int recurse)
		{
			super(synsetId);
			this.recurse = recurse;
		}

		@Override
		public void process()
		{
			final Context context = BaseModule.this.fragment.requireContext();

			final Parcelable pointer = new SynsetPointer(this.id);
			final Intent intent = new Intent(context, SynsetActivity.class);
			intent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_SYNSET);
			intent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer);
			intent.putExtra(ProviderArgs.ARG_QUERYRECURSE, this.recurse);
			intent.setAction(ProviderArgs.ACTION_QUERY);

			context.startActivity(intent);
		}

		@Override
		public String toString()
		{
			return "synset for " + this.id;
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
		 * @param recurse  max recursion level
		 */
		SenseLink(final long synsetId, final long wordId, final int recurse)
		{
			super(synsetId, recurse);
			this.wordId = wordId;
		}

		@Override
		public void process()
		{
			final Context context = BaseModule.this.fragment.requireContext();

			final Parcelable pointer = new SensePointer(this.id, this.wordId);
			final Intent intent = new Intent(context, SynsetActivity.class);
			intent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_SYNSET);
			intent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer);
			intent.putExtra(ProviderArgs.ARG_QUERYRECURSE, this.recurse);
			intent.setAction(ProviderArgs.ACTION_QUERY);

			context.startActivity(intent);
		}

		@Override
		public String toString()
		{
			return "sense for " + this.id + ',' + this.wordId;
		}
	}
}
