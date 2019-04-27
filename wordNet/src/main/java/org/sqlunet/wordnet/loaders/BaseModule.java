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
import org.sqlunet.view.FireEvent;
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
import org.sqlunet.wordnet.provider.WordNetProvider;
import org.sqlunet.wordnet.style.WordNetFactories;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

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
	 * Whether members are grouped
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

	private SqlunetViewTreeModel sensefromSenseKeyModel;

	private SqlunetViewTreeModel senseFromSynsetIdWordIdModel;

	private SqlunetViewTreeModel synsetFromSynsetIdModel;

	private SqlunetViewTreeModel membersFromSynsetIdModel;

	private SqlunetViewTreeModel membersFromSynsetIdModel2;

	private SqlunetViewTreeModel samplesfromSynsetIdModel;

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
		this.wordModel.getData().observe(this.fragment, data -> new FireEvent(this.fragment).live(data));

		this.sensesFromWordModel = ViewModelProviders.of(this.fragment).get("wn.senses(word)", SqlunetViewTreeModel.class);
		this.sensesFromWordModel.getData().observe(this.fragment, data -> new FireEvent(this.fragment).live(data));

		this.sensesFromWordIdModel = ViewModelProviders.of(this.fragment).get("wn.senses(wordid)", SqlunetViewTreeModel.class);
		this.sensesFromWordIdModel.getData().observe(this.fragment, data -> new FireEvent(this.fragment).live(data));

		this.senseFromSenseIdModel = ViewModelProviders.of(this.fragment).get("wn.sense(senseid)", SqlunetViewTreeModel.class);
		this.senseFromSenseIdModel.getData().observe(this.fragment, data -> new FireEvent(this.fragment).live(data));

		this.sensefromSenseKeyModel = ViewModelProviders.of(this.fragment).get("wn.sense(sensekey)", SqlunetViewTreeModel.class);
		this.sensefromSenseKeyModel.getData().observe(this.fragment, data -> new FireEvent(this.fragment).live(data));

		this.senseFromSynsetIdWordIdModel = ViewModelProviders.of(this.fragment).get("wn.sense(synsetid,wordid)", SqlunetViewTreeModel.class);
		this.senseFromSynsetIdWordIdModel.getData().observe(this.fragment, data -> new FireEvent(this.fragment).live(data));

		this.synsetFromSynsetIdModel = ViewModelProviders.of(this.fragment).get("wn.synset(synsetid)", SqlunetViewTreeModel.class);
		this.synsetFromSynsetIdModel.getData().observe(this.fragment, data -> new FireEvent(this.fragment).live(data));

		this.membersFromSynsetIdModel = ViewModelProviders.of(this.fragment).get("wn.members(synsetid)", SqlunetViewTreeModel.class);
		this.membersFromSynsetIdModel.getData().observe(this.fragment, data -> new FireEvent(this.fragment).live(data));

		this.membersFromSynsetIdModel2 = ViewModelProviders.of(this.fragment).get("wn.members2(synsetid)", SqlunetViewTreeModel.class);
		this.membersFromSynsetIdModel2.getData().observe(this.fragment, data -> new FireEvent(this.fragment).live(data));

		this.samplesfromSynsetIdModel = ViewModelProviders.of(this.fragment).get("wn.samples(synsetid)", SqlunetViewTreeModel.class);
		this.samplesfromSynsetIdModel.getData().observe(this.fragment, data -> new FireEvent(this.fragment).live(data));

		this.semLinksFromSynsetIdModel = ViewModelProviders.of(this.fragment).get("wn.semlinks(synsetid)", SqlunetViewTreeModel.class);
		this.semLinksFromSynsetIdModel.getData().observe(this.fragment, data -> new FireEvent(this.fragment).live(data));

		this.semLinksFromSynsetIdLinkIdModel = ViewModelProviders.of(this.fragment).get("wn.semlinks(synsetid,linkid)", SqlunetViewTreeModel.class);
		this.semLinksFromSynsetIdLinkIdModel.getData().observe(this.fragment, data -> new FireEvent(this.fragment).live(data));

		this.lexLinksFromSynsetIdWordIdModel = ViewModelProviders.of(this.fragment).get("wn.lexlinks(synsetid,wordid)", SqlunetViewTreeModel.class);
		this.lexLinksFromSynsetIdWordIdModel.getData().observe(this.fragment, data -> new FireEvent(this.fragment).live(data));

		this.lexLinksFromSynsetIdModel = ViewModelProviders.of(this.fragment).get("wn.lexlinks(synsetid)", SqlunetViewTreeModel.class);
		this.lexLinksFromSynsetIdModel.getData().observe(this.fragment, data -> new FireEvent(this.fragment).live(data));

		this.vFramesFromSynsetIdModel = ViewModelProviders.of(this.fragment).get("wn.vframes(synsetid)", SqlunetViewTreeModel.class);
		this.vFramesFromSynsetIdModel.getData().observe(this.fragment, data -> new FireEvent(this.fragment).live(data));

		this.vFramesFromSynsetIdWordIdModel = ViewModelProviders.of(this.fragment).get("wn.vframes(synsetid,wordid)", SqlunetViewTreeModel.class);
		this.vFramesFromSynsetIdWordIdModel.getData().observe(this.fragment, data -> new FireEvent(this.fragment).live(data));

		this.vFrameSentencesFromSynsetIdModel = ViewModelProviders.of(this.fragment).get("wn.vframesentences(synsetid)", SqlunetViewTreeModel.class);
		this.vFrameSentencesFromSynsetIdModel.getData().observe(this.fragment, data -> new FireEvent(this.fragment).live(data));

		this.vFrameSentencesFromSynsetIdWordIdModel = ViewModelProviders.of(this.fragment).get("wn.vframesentences(synsetid,wordid)", SqlunetViewTreeModel.class);
		this.vFrameSentencesFromSynsetIdWordIdModel.getData().observe(this.fragment, data -> new FireEvent(this.fragment).live(data));

		this.adjPositionFromSynsetIdModel = ViewModelProviders.of(this.fragment).get("wn.adjposition(synsetid)", SqlunetViewTreeModel.class);
		this.adjPositionFromSynsetIdModel.getData().observe(this.fragment, data -> new FireEvent(this.fragment).live(data));

		this.adjPositionFromSynsetIdWordIdModel = ViewModelProviders.of(this.fragment).get("wn.adjposition(synsetid,wordid)", SqlunetViewTreeModel.class);
		this.adjPositionFromSynsetIdWordIdModel.getData().observe(this.fragment, data -> new FireEvent(this.fragment).live(data));

		this.morphsFromWordIdModel = ViewModelProviders.of(this.fragment).get("wn.morphs(wordid)", SqlunetViewTreeModel.class);
		this.morphsFromWordIdModel.getData().observe(this.fragment, data -> new FireEvent(this.fragment).live(data));
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

	private TreeNode[] wordCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent, @SuppressWarnings("SameParameterValue") final boolean addNewNode)
	{
		if (cursor.getCount() > 1)
		{
			throw new RuntimeException("Unexpected number of rows");
		}

		TreeNode[] changed;
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
				final TreeNode node = TreeFactory.addTextNode(parent, sb);
				changed = new TreeNode[]{parent, node};
			}
			else
			{
				TreeFactory.setTextNode(parent, sb);
				changed = new TreeNode[]{parent};
			}
		}
		else
		{
			TreeFactory.setNoResult(parent, true, false);
			changed = new TreeNode[]{parent};
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

	private TreeNode[] sensesCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeNode[] changed;
		if (cursor.moveToFirst())
		{
			final List<TreeNode> nodes = new ArrayList<>();
			nodes.add(parent);

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
				final TreeNode synsetNode = TreeFactory.addLinkNode(parent, sb, R.drawable.synset, new SenseLink(synsetId, wordId, this.maxRecursion));
				nodes.add(synsetNode);
			}
			while (cursor.moveToNext());
			changed = nodes.toArray(new TreeNode[0]);
		}
		else
		{
			TreeFactory.setNoResult(parent, true, false);
			changed = new TreeNode[]{parent};
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
		this.senseFromSenseIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> senseCursor1ToTreeModel(cursor, parent));
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
		this.sensefromSenseKeyModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> senseCursor2ToTreeModel(cursor, parent));
	}

	/**
	 * Sense
	 *
	 * @param synsetId synsetCursorToTreeModel id
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
		this.senseFromSynsetIdWordIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> senseCursor3ToTreeModel(cursor, synsetId, wordId, parent));
	}

	private TreeNode[] senseCursor1ToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
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
			TreeFactory.setNoResult(parent, true, false);
		}

		cursor.close();
		return new TreeNode[]{parent};
	}

	private TreeNode[] senseCursor2ToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		if (cursor.getCount() > 1)
		{
			throw new RuntimeException("Unexpected number of rows");
		}
		TreeNode[] changed;
		if (cursor.moveToFirst())
		{
			final int idWordId = cursor.getColumnIndex(Senses.WORDID);
			final int idSynsetId = cursor.getColumnIndex(Senses.SYNSETID);
			final long wordId = cursor.getLong(idWordId);
			final long synsetId = cursor.getLong(idSynsetId);

			// sub nodes
			final TreeNode wordNode = TreeFactory.addTextNode(parent, "Word");

			// word
			word(wordId, wordNode, false);
			sense(synsetId, wordId, wordNode);

			changed = new TreeNode[]{parent, wordNode};
		}
		else
		{
			TreeFactory.setNoResult(parent, true, false);
			changed = new TreeNode[]{parent};
		}

		cursor.close();
		return changed;
	}

	private TreeNode[] senseCursor3ToTreeModel(@NonNull final Cursor cursor, final long synsetId, final long wordId, @NonNull final TreeNode parent)
	{
		if (cursor.getCount() > 1)
		{
			throw new RuntimeException("Unexpected number of rows");
		}
		TreeNode[] changed;
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
			final TreeNode node = TreeFactory.addTextNode(parent, sb);

			// subnodes
			final TreeNode linksNode = TreeFactory.addHotQueryNode(parent, "Links", R.drawable.ic_links, new LinksQuery(synsetId, wordId));
			final TreeNode samplesNode = TreeFactory.addHotQueryNode(parent, "Samples", R.drawable.sample, new SamplesQuery(synsetId));

			changed = new TreeNode[]{parent, node, linksNode, samplesNode};
		}
		else
		{
			TreeFactory.setNoResult(parent, true, false);
			changed = new TreeNode[]{parent};
		}

		cursor.close();
		return changed;
	}

	// S Y N S E T

	/**
	 * Synset
	 *
	 * @param synsetId   synsetCursorToTreeModel id
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

	private TreeNode[] synsetCursorToTreeModel(@NonNull final Cursor cursor, final long synsetId, @NonNull final TreeNode parent, @SuppressWarnings("SameParameterValue") final boolean addNewNode)
	{
		if (cursor.getCount() > 1)
		{
			throw new RuntimeException("Unexpected number of rows");
		}
		TreeNode[] changed;
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
				final TreeNode node = TreeFactory.addTextNode(parent, sb);
				changed = new TreeNode[]{parent, node};
			}
			else
			{
				TreeFactory.setTextNode(parent, sb);
				changed = new TreeNode[]{parent};
			}
		}
		else
		{
			TreeFactory.setNoResult(parent, addNewNode, true);
			changed = new TreeNode[]{parent};
		}

		cursor.close();
		return changed;
	}

	/**
	 * Sense to string builder
	 *
	 * @param sb         string builder
	 * @param synsetId   synsetCursorToTreeModel id
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

	/**
	 * Synset to string builder
	 *
	 * @param sb         string builder
	 * @param synsetId   synsetCursorToTreeModel id
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
	 * @param synsetId  synsetCursorToTreeModel id
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
	 * @param synsetId   synsetCursorToTreeModel
	 * @param parent     parent node
	 * @param addNewNode whether to addItem to (or set) node
	 */
	@SuppressWarnings("unused")
	void members(final long synsetId, @NonNull final TreeNode parent, final boolean addNewNode)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(BaseModule.this.membersGrouped ? Senses_Words.CONTENT_URI_TABLE_BY_SYNSET : Senses_Words.CONTENT_URI_TABLE));
		final String[] projection = BaseModule.this.membersGrouped ? //
				new String[]{Senses_Words.MEMBERS} : new String[]{Words.LEMMA};
		final String selection = Senses_Words.SYNSETID + " = ?";
		final String[] selectionArgs = {Long.toString(synsetId)};
		final String sortOrder = Words.LEMMA;
		this.membersFromSynsetIdModel.loadData(uri, projection, selection, selectionArgs, sortOrder, cursor -> membersCursor1ToTreeModel(cursor, parent, addNewNode));
	}

	/**
	 * Members
	 *
	 * @param synsetId synsetCursorToTreeModel
	 * @param parent   parent node
	 */
	void members(final long synsetId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(Senses_Words.CONTENT_URI_TABLE));
		final String[] projection = {Senses_Words.WORDID, Senses_Words.MEMBER};
		final String selection = Senses_Words.SYNSETID + " = ?";
		final String[] selectionArgs = {Long.toString(synsetId)};
		final String sortOrder = Senses_Words.MEMBER;
		this.membersFromSynsetIdModel2.loadData(uri, projection, selection, selectionArgs, sortOrder, cursor -> membersCursor2ToTreeModel(cursor, parent));
	}

	private TreeNode[] membersCursor1ToTreeModel(final Cursor cursor, @NonNull final TreeNode parent, final boolean addNewNode)
	{
		if (BaseModule.this.membersGrouped)
		{
			if (cursor.getCount() > 1)
			{
				throw new RuntimeException("Unexpected number of rows");
			}
		}

		TreeNode[] changed;
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
					// final String record = String.format(Locale.ENGLISH, "[%d] %s", i++, lemma);
					// sb.append(record);
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
				final TreeNode node = TreeFactory.addTextNode(parent, sb);
				changed = new TreeNode[]{parent, node};
			}
			else
			{
				TreeFactory.setTextNode(parent, sb);
				changed = new TreeNode[]{parent};
			}
		}
		else
		{
			TreeFactory.setNoResult(parent, addNewNode, true);
			changed = new TreeNode[]{parent};
		}

		cursor.close();
		return changed;
	}

	private TreeNode[] membersCursor2ToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		if (BaseModule.this.membersGrouped)
		{
			if (cursor.getCount() > 1)
			{
				throw new RuntimeException("Unexpected number of rows");
			}
		}

		TreeNode[] changed;
		if (cursor.moveToFirst())
		{
			final List<TreeNode> nodes = new ArrayList<>();
			nodes.add(parent);

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
				final TreeNode memberNode = TreeFactory.addLinkNode(parent, sb, R.drawable.member, new WordLink(wordId));
				nodes.add(memberNode);
			}
			while (cursor.moveToNext());
			changed = nodes.toArray(new TreeNode[0]);
		}
		else
		{
			TreeFactory.setNoResult(parent, true, false);
			changed = new TreeNode[]{parent};
		}

		cursor.close();
		return changed;
	}

	// S A M P L E S

	/**
	 * Samples
	 *
	 * @param synsetId   synsetCursorToTreeModel id
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

	private TreeNode[] samplesCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent, @SuppressWarnings("SameParameterValue") final boolean addNewNode)
	{
		TreeNode[] changed;
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
				// final String record = String.format(Locale.ENGLISH, "[%d] %s", sampleId, sample);
				// sb.append(record);
			}
			while (cursor.moveToNext());

			// result
			if (addNewNode)
			{
				final TreeNode node = TreeFactory.addTextNode(parent, sb);
				changed = new TreeNode[]{parent, node};
			}
			else
			{
				TreeFactory.setTextNode(parent, sb);
				changed = new TreeNode[]{parent};
			}
		}
		else
		{
			TreeFactory.setNoResult(parent, addNewNode, true);
			changed = new TreeNode[]{parent};
		}

		cursor.close();
		return changed;
	}

	// S E M L I N K S

	static private final String TARGET_SYNSETID = "d_synsetid";
	static private final String TARGET_DEFINITION = "d_definition";
	static private final String TARGET_LEMMA = "w_lemma";
	static private final String TARGET_WORDID = "w_wordid";

	/**
	 * Semantic links
	 *
	 * @param synsetId synsetCursorToTreeModel id
	 * @param parent   parent node
	 */
	private void semLinks(final long synsetId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(SemLinks_Synsets_Words_X.CONTENT_URI_TABLE));
		final String[] projection = { //
				WordNetContract.DEST + '.' + Synsets.SYNSETID + " AS " + BaseModule.TARGET_SYNSETID, //
				WordNetContract.DEST + '.' + Synsets.DEFINITION + " AS " + BaseModule.TARGET_DEFINITION, //
				LinkTypes.LINK, //
				LinkTypes.LINKID, //
				LinkTypes.RECURSES, //
		};
		final String selection = WordNetContract.LINK + '.' + SemLinks_Synsets_Words_X.SYNSET1ID + " = ?";  ////
		final String[] selectionArgs = {Long.toString(synsetId)};
		final String sortOrder = LinkTypes.LINKID;
		this.semLinksFromSynsetIdModel.loadData(uri, projection, selection, selectionArgs, sortOrder, cursor -> semLinksCursor1ToTreeModel(cursor, parent));
	}

	/**
	 * Semantic links
	 *
	 * @param synsetId synsetCursorToTreeModel id
	 * @param linkId   link id
	 * @param parent   parent node
	 */
	private void semLinks(final long synsetId, final int linkId, final int recurseLevel, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(SemLinks_Synsets_Words_X.CONTENT_URI_TABLE));
		final String[] projection = { //
				WordNetContract.DEST + '.' + Synsets.SYNSETID + " AS " + BaseModule.TARGET_SYNSETID, //
				WordNetContract.DEST + '.' + Synsets.DEFINITION + " AS " + BaseModule.TARGET_DEFINITION, //
				LinkTypes.LINK, //
				LinkTypes.LINKID, //
				LinkTypes.RECURSES, //
		};
		final String selection = WordNetContract.LINK + '.' + SemLinks_Synsets_Words_X.SYNSET1ID + " = ? AND " + LinkTypes.LINKID + " = ?";
		final String[] selectionArgs = {Long.toString(synsetId), Integer.toString(linkId)};
		this.semLinksFromSynsetIdLinkIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> semLinksCursor2ToTreeModel(cursor, linkId, recurseLevel, parent));
	}

	private TreeNode[] semLinksCursor1ToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeNode[] changed;
		if (cursor.moveToFirst())
		{
			final List<TreeNode> nodes = new ArrayList<>();
			nodes.add(parent);

			final int idLinkId = cursor.getColumnIndex(LinkTypes.LINKID);
			// final int idLink = cursor.getColumnIndex(LinkTypes.LINK);
			final int idTargetSynsetId = cursor.getColumnIndex(BaseModule.TARGET_SYNSETID);
			final int idTargetDefinition = cursor.getColumnIndex(BaseModule.TARGET_DEFINITION);
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
				final boolean linkCanRecurse = cursor.getInt(idRecurses) != 0;

				Spanner.append(sb, targetMembers, 0, WordNetFactories.membersFactory);
				sb.append(' ');
				Spanner.append(sb, targetDefinition, 0, WordNetFactories.definitionFactory);

				// recursion
				if (linkCanRecurse)
				{
					final TreeNode linksNode = TreeFactory.addLinkQueryNode(parent, sb, getLinkRes(linkId), new SubLinksQuery(targetSynsetId, linkId, BaseModule.this.maxRecursion), new SynsetLink(targetSynsetId, BaseModule.this.maxRecursion)).prependTo(parent);
					nodes.add(linksNode);
				}
				else
				{
					final TreeNode node = TreeFactory.addLeafNode(parent, sb, getLinkRes(linkId)).prependTo(parent);
					nodes.add(node);
				}
			}
			while (cursor.moveToNext());
			changed = nodes.toArray(new TreeNode[0]);
		}
		else
		{
			TreeFactory.setNoResult(parent, false, false);
			changed = new TreeNode[]{parent};
		}

		cursor.close();
		return changed;
	}

	private TreeNode[] semLinksCursor2ToTreeModel(@NonNull final Cursor cursor, final int linkId, final int recurseLevel, @NonNull final TreeNode parent)
	{
		TreeNode[] changed;
		if (cursor.moveToFirst())
		{
			final List<TreeNode> nodes = new ArrayList<>();
			nodes.add(parent);

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
						final TreeNode linksNode = TreeFactory.addLinkQueryNode(parent, sb, getLinkRes(linkId), new SubLinksQuery(targetSynsetId, linkId, newRecurseLevel), new SynsetLink(targetSynsetId, BaseModule.this.maxRecursion));
						nodes.add(linksNode);
					}
					else
					{
						final TreeNode moreNode = TreeFactory.addMoreNode(parent, sb, getLinkRes(linkId));
						nodes.add(moreNode);
					}
				}
				else
				{
					final TreeNode node = TreeFactory.addLeafNode(parent, sb, getLinkRes(linkId));
					nodes.add(node);
				}
			}
			while (cursor.moveToNext());
			changed = nodes.toArray(new TreeNode[0]);
		}
		else
		{
			TreeFactory.setNoResult(parent, false, true);
			changed = new TreeNode[]{parent};
		}

		cursor.close();
		return changed;
	}

	// L E X L I N K S

	/**
	 * Lexical links
	 *
	 * @param synsetId synsetCursorToTreeModel id
	 * @param wordId   word id
	 * @param parent   parent node
	 */
	private void lexLinks(final long synsetId, final long wordId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(LexLinks_Senses_Words_X.CONTENT_URI_TABLE));
		final String[] projection = { //
				WordNetContract.DEST + '.' + Synsets.SYNSETID + " AS " + BaseModule.TARGET_SYNSETID, //
				WordNetContract.DEST + '.' + Synsets.DEFINITION + " AS " + BaseModule.TARGET_DEFINITION, //
				WordNetContract.WORD + '.' + Words.LEMMA + " AS " + BaseModule.TARGET_LEMMA, //
				WordNetContract.WORD + '.' + Words.WORDID + " AS " + BaseModule.TARGET_WORDID, //
				LinkTypes.LINK, LinkTypes.LINKID,};
		final String selection = WordNetContract.LINK + ".synset1id = ? AND " + WordNetContract.LINK + ".word1id = ?";
		final String[] selectionArgs = {Long.toString(synsetId), Long.toString(wordId)};
		final String sortOrder = LinkTypes.LINKID;
		this.lexLinksFromSynsetIdWordIdModel.loadData(uri, projection, selection, selectionArgs, sortOrder, cursor -> lexLinksCursor1ToTreeModel(cursor, parent));
	}

	/**
	 * Lexical links
	 *
	 * @param synsetId synsetCursorToTreeModel id
	 * @param parent   parent
	 */
	@SuppressWarnings("unused")
	void lexLinks(final long synsetId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(LexLinks_Senses_Words_X.CONTENT_URI_TABLE));
		final String[] projection = { //
				WordNetContract.DEST + '.' + Synsets.SYNSETID + " AS " + BaseModule.TARGET_SYNSETID, //
				WordNetContract.DEST + '.' + Synsets.DEFINITION + " AS " + BaseModule.TARGET_DEFINITION, //
				WordNetContract.WORD + '.' + Words.LEMMA + " AS " + BaseModule.TARGET_LEMMA, //
				WordNetContract.WORD + '.' + Words.WORDID + " AS " + BaseModule.TARGET_WORDID, //
				LinkTypes.LINKID, //
				LinkTypes.LINK};
		final String selection = WordNetContract.LINK + '.' + LexLinks_Senses_Words_X.SYNSET1ID + " = ?";  ////
		final String[] selectionArgs = {Long.toString(synsetId)};
		this.lexLinksFromSynsetIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> lexLinksCursor2ToTreeModel(cursor, parent));
	}

	private TreeNode[] lexLinksCursor1ToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeNode[] changed;
		if (cursor.moveToFirst())
		{
			final List<TreeNode> nodes = new ArrayList<>();
			nodes.add(parent);

			final int idLinkId = cursor.getColumnIndex(LinkTypes.LINKID);
			// final int idLink = cursor.getColumnIndex(LinkTypes.LINK);

			final int idTargetSynsetId = cursor.getColumnIndex(BaseModule.TARGET_SYNSETID);
			final int idTargetDefinition = cursor.getColumnIndex(BaseModule.TARGET_DEFINITION);
			// final int idTargetMembers = cursor.getColumnIndex(LexLinks_Senses_Words_X.MEMBERS2);

			final int idTargetWordId = cursor.getColumnIndex(BaseModule.TARGET_WORDID);
			final int idTargetLemma = cursor.getColumnIndex(BaseModule.TARGET_LEMMA);

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

				// final String record = String.format(Locale.ENGLISH, "[%s] %s (%s)\n\t%s (synsetCursorToTreeModel %s) {%s}", link, targetLemma, targetWordId,targetDefinition, targetSynsetId, targetMembers);

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
				// Spanner.append(sb, membersCursor1ToTreeModel, 0, WordNetFactories.membersFactory);
				// sb.append('}');
				sb.append(' ');
				Spanner.append(sb, targetDefinition, 0, WordNetFactories.definitionFactory);

				// attach result
				final TreeNode linkNode = TreeFactory.addLinkLeafNode(parent, sb, getLinkRes(linkId), new SenseLink(targetSynsetId, idTargetWordId, BaseModule.this.maxRecursion));
				nodes.add(linkNode);
			}
			while (cursor.moveToNext());
			changed = nodes.toArray(new TreeNode[0]);
		}
		else
		{
			TreeFactory.setNoResult(parent, false, false);
			changed = new TreeNode[]{parent};
		}
		cursor.close();
		return changed;
	}

	private TreeNode[] lexLinksCursor2ToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeNode[] changed;
		if (cursor.moveToFirst())
		{
			final List<TreeNode> nodes = new ArrayList<>();
			nodes.add(parent);

			final int idLinkId = cursor.getColumnIndex(LinkTypes.LINKID);
			// final int idLink = cursor.getColumnIndex(LinkTypes.LINK);

			// final int idTargetSynsetId = cursor.getColumnIndex(BaseModule.TARGET_SYNSETID);
			final int idTargetDefinition = cursor.getColumnIndex(BaseModule.TARGET_DEFINITION);
			final int idTargetMembers = cursor.getColumnIndex(LexLinks_Senses_Words_X.MEMBERS2);

			// final int idTargetWordId = cursor.getColumnIndex(BaseModule.TARGET_WORDID);
			final int idTargetLemma = cursor.getColumnIndex(BaseModule.TARGET_LEMMA);

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

				// final String record = String.format(Locale.ENGLISH, "[%s] %s (%s)\n\t%s (synsetCursorToTreeModel %s) {%s}", link, targetLemma, targetWordId, targetDefinition, targetSynsetId, targetMembers);

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
				final TreeNode linkNode = TreeFactory.addLeafNode(parent, sb, getLinkRes(linkId));
				nodes.add(linkNode);
			}
			while (cursor.moveToNext());

			// attach result
			final TreeNode node = TreeFactory.addTextNode(parent, sb);
			nodes.add(node);
			changed = nodes.toArray(new TreeNode[0]);
		}
		else
		{
			TreeFactory.setNoResult(parent, false, true);
			changed = new TreeNode[]{parent};
		}
		cursor.close();
		return changed;
	}

	// V F R A M E S

	/**
	 * Verb frames
	 *
	 * @param synsetId synsetCursorToTreeModel id
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
	 * @param synsetId synsetCursorToTreeModel id
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

	private TreeNode[] vframesCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeNode[] changed;
		if (cursor.moveToFirst())
		{
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
				Spanner.appendImage(sb, BaseModule.this.verbframeDrawable);
				sb.append(' ');
				sb.append(record);
			}
			while (cursor.moveToNext());

			// attach result
			final TreeNode node = TreeFactory.addTextNode(parent, sb);
			changed = new TreeNode[]{parent, node};
		}
		else
		{
			TreeFactory.setNoResult(parent, true, false);
			changed = new TreeNode[]{parent};
		}

		cursor.close();
		return changed;
	}

	// V F R A M E S E N TE N C E S

	/**
	 * Verb frame sentences
	 *
	 * @param synsetId synsetCursorToTreeModel id
	 * @param parent   parent node
	 */
	void vFrameSentences(final long synsetId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(VerbFrameSentenceMaps_VerbFrameSentences.CONTENT_URI_TABLE));
		final String[] projection = {VerbFrameSentenceMaps_VerbFrameSentences.SENTENCE};
		final String selection = VerbFrameSentenceMaps_VerbFrameSentences.SYNSETID + " = ?";
		final String[] selectionArgs = {Long.toString(synsetId)};
		this.vFrameSentencesFromSynsetIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> vFrameSentencesCursor1ToTreeModel(cursor, parent));
	}

	/**
	 * Verb frame sentences
	 *
	 * @param synsetId synsetCursorToTreeModel id
	 * @param wordId   word id
	 * @param parent   parent node
	 */
	void vFrameSentences(final long synsetId, final long wordId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(WordNetProvider.makeUri(VerbFrameSentenceMaps_VerbFrameSentences.CONTENT_URI_TABLE));
		final String[] projection = {VerbFrameSentenceMaps_VerbFrameSentences.SENTENCE};
		final String selection = VerbFrameSentenceMaps_VerbFrameSentences.SYNSETID + " = ? AND " + VerbFrameSentenceMaps_VerbFrameSentences.WORDID + " = ?";
		final String[] selectionArgs = {Long.toString(synsetId), Long.toString(wordId)};
		this.vFrameSentencesFromSynsetIdWordIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> vFrameSentencesCursor2ToTreeModel(cursor, parent));
	}

	private TreeNode[] vFrameSentencesCursor1ToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeNode[] changed;
		if (cursor.moveToFirst())
		{
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
				Spanner.appendImage(sb, BaseModule.this.verbframeDrawable);
				sb.append(' ');
				sb.append(record);
			}
			while (cursor.moveToNext());

			// attach result
			final TreeNode node = TreeFactory.addTextNode(parent, sb);
			changed = new TreeNode[]{parent, node};
		}
		else
		{
			TreeFactory.setNoResult(parent, true, false);
			changed = new TreeNode[]{parent};
		}

		cursor.close();
		return changed;
	}

	private TreeNode[] vFrameSentencesCursor2ToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeNode[] changed;
		if (cursor.moveToFirst())
		{
			final String lemma = "---";
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
				Spanner.appendImage(sb, BaseModule.this.verbframeDrawable);
				sb.append(' ');
				sb.append(record);
			}
			while (cursor.moveToNext());

			// attach result
			final TreeNode node = TreeFactory.addTextNode(parent, sb);
			changed = new TreeNode[]{parent, node};
		}
		else
		{
			TreeFactory.setNoResult(parent, true, false);
			changed = new TreeNode[]{parent};
		}

		cursor.close();
		return changed;
	}

	// A D J P O S I T I O N S

	/**
	 * Adjective positions
	 *
	 * @param synsetId synsetCursorToTreeModel id
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
	 * @param synsetId synsetCursorToTreeModel id
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

	private TreeNode[] adjPositionCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeNode[] changed;
		if (cursor.moveToFirst())
		{
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
				Spanner.appendImage(sb, BaseModule.this.verbframeDrawable);
				sb.append(' ');
				sb.append(record);
			}
			while (cursor.moveToNext());

			// attach result
			final TreeNode node = TreeFactory.addTextNode(parent, sb);
			changed = new TreeNode[]{parent, node};
		}
		else
		{
			TreeFactory.setNoResult(parent, true, false);
			changed = new TreeNode[]{parent};
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

	private TreeNode[] morphsCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeNode[] changed;
		if (cursor.moveToFirst())
		{
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
				Spanner.appendImage(sb, BaseModule.this.morphDrawable);
				sb.append(' ');
				sb.append(record);
			}
			while (cursor.moveToNext());

			// attach result
			final TreeNode node = TreeFactory.addTextNode(parent, sb);
			changed = new TreeNode[]{parent, node};
		}
		else
		{
			TreeFactory.setNoResult(parent, true, false);
			changed = new TreeNode[]{parent};
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
		 * @param synsetId synsetCursorToTreeModel id
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
		 * @param synsetId synsetCursorToTreeModel id
		 */
		public SemLinksQuery(final long synsetId)
		{
			super(synsetId);
		}

		@Override
		public void process(@NonNull final TreeNode node)
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
		 * @param synsetId synsetCursorToTreeModel id
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
		 * Recurse level
		 */
		final int recurseLevel;

		/**
		 * Constructor
		 *
		 * @param synsetId     synsetCursorToTreeModel id
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
			semLinks(this.id, this.linkId, recurseLevel, node);
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
		 * @param synsetId synsetCursorToTreeModel id
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
		 * @param synsetId synsetCursorToTreeModel id
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
		 * @param synsetId synsetCursorToTreeModel id
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
	}
}
