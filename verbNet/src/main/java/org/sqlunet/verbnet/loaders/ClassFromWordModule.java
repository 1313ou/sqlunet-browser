/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.verbnet.loaders;

import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;

import org.sqlunet.HasSynsetId;
import org.sqlunet.HasWordId;
import org.sqlunet.browser.SqlunetViewTreeModel;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.model.TreeFactory;
import org.sqlunet.style.Spanner;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.verbnet.R;
import org.sqlunet.verbnet.provider.VerbNetContract.Words_VnClasses;
import org.sqlunet.verbnet.provider.VerbNetProvider;
import org.sqlunet.verbnet.style.VerbNetFactories;
import org.sqlunet.view.TreeOp;
import org.sqlunet.view.TreeOp.TreeOps;
import org.sqlunet.view.TreeOpExecute;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import static org.sqlunet.view.TreeOp.TreeOpCode.NEWTREE;
import static org.sqlunet.view.TreeOp.TreeOpCode.NEWCHILD;
import static org.sqlunet.view.TreeOp.TreeOpCode.REMOVE;

/**
 * VerbNet class from word/sense module
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class ClassFromWordModule extends BaseModule
{
	/**
	 * Word id
	 */
	@Nullable
	private Long wordId;

	/**
	 * Synset id (null=ignore)
	 */
	@Nullable
	private Long synsetId;

	// View models

	private SqlunetViewTreeModel vnClassesFromWordIdSynsetIdModel;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	public ClassFromWordModule(@NonNull final TreeFragment fragment)
	{
		super(fragment);

		// models
		makeModels();
	}

	/**
	 * Make view models
	 */
	private void makeModels()
	{
		this.vnClassesFromWordIdSynsetIdModel = ViewModelProviders.of(this.fragment).get("vn.classes(wordid,synsetid)", SqlunetViewTreeModel.class);
		this.vnClassesFromWordIdSynsetIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));
	}

	@Override
	protected void unmarshal(final Parcelable pointer)
	{
		this.wordId = null;
		this.synsetId = null;
		if (pointer instanceof HasWordId)
		{
			final HasWordId wordPointer = (HasWordId) pointer;
			this.wordId = wordPointer.getWordId();
		}
		if (pointer instanceof HasSynsetId)
		{
			final HasSynsetId synsetPointer = (HasSynsetId) pointer;
			this.synsetId = synsetPointer.getSynsetId();
		}
	}

	@Override
	public void process(@NonNull final TreeNode parent)
	{
		if (this.wordId != null)
		{
			vnClasses(this.wordId, this.synsetId, parent);
		}
		else
		{
			TreeFactory.setNoResult(parent);
		}
	}

	// L O A D E R S

	/**
	 * Classes from word id and synset id
	 *
	 * @param wordId   word id
	 * @param synsetId synset id (null or 0 means ignore)
	 * @param parent   parent node
	 */
	private void vnClasses(final long wordId, @Nullable final Long synsetId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(VerbNetProvider.makeUri(Words_VnClasses.CONTENT_URI_TABLE));
		final String[] projection = { //
				Words_VnClasses.CLASSID, //
				Words_VnClasses.CLASS, //
				Words_VnClasses.CLASSTAG, //
				"(" + Words_VnClasses.SYNSETID + " IS NULL) AS " + Words_VnClasses.NULLSYNSET, //
				Words_VnClasses.SENSENUM, //
				Words_VnClasses.SENSEKEY, //
				Words_VnClasses.QUALITY, //
		};
		String selection = Words_VnClasses.WORDID + " = ?";
		String[] selectionArgs;
		if (synsetId != null && synsetId != 0)
		{
			selection += " AND (" + Words_VnClasses.SYNSETID + " = ? OR " + Words_VnClasses.SYNSETID + " IS NULL)";
			selectionArgs = new String[]{ //
					Long.toString(wordId), //
					Long.toString(synsetId)};
		}
		else
		{
			selectionArgs = new String[]{ //
					Long.toString(wordId)};
		}
		this.vnClassesFromWordIdSynsetIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> vnClassesCursorToTreeModel(cursor, parent));
	}

	@Nullable
	private TreeOp[] vnClassesCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOp.TreeOps changedList = new TreeOps(NEWTREE, parent);

			// column indices
			final int idClassId = cursor.getColumnIndex(Words_VnClasses.CLASSID);
			final int idClass = cursor.getColumnIndex(Words_VnClasses.CLASS);
			// final int idClassTag = cursor.getColumnIndex(Words_VnClasses.CLASSTAG);

			// read cursor
			do
			{
				final SpannableStringBuilder sb = new SpannableStringBuilder();

				// data
				final int classId = cursor.getInt(idClassId);
				final String vnClass = cursor.getString(idClass);

				// sb.append("[class]");
				Spanner.appendImage(sb, ClassFromWordModule.this.drawableRoles);
				sb.append(' ');
				Spanner.append(sb, vnClass, 0, VerbNetFactories.classFactory);
				// sb.append(" tag=");
				// sb.append(cursor.getString(idClassTag));
				sb.append(" id=");
				sb.append(Integer.toString(classId));

				// attach result
				final TreeNode node = TreeFactory.makeTextNode(sb, false).addTo(parent);
				changedList.add(NEWCHILD, node);

				// sub nodes
				final TreeNode membersNode = TreeFactory.makeHotQueryNode("Members", R.drawable.members, false, new MembersQuery(classId)).addTo(parent);
				changedList.add(NEWCHILD, membersNode);
				final TreeNode rolesNode = TreeFactory.makeHotQueryNode("Roles", R.drawable.roles, false, new RolesQuery(classId)).addTo(parent);
				changedList.add(NEWCHILD, rolesNode);
				final TreeNode framesNode = TreeFactory.makeQueryNode("Frames", R.drawable.vnframe, false, new FramesQuery(classId)).addTo(parent);
				changedList.add(NEWCHILD, framesNode);
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
