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
import org.sqlunet.view.FireEvent;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

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
		this.vnClassesFromWordIdSynsetIdModel.getData().observe(this.fragment, data -> new FireEvent(this.fragment).live(data));
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
	public void process(@NonNull final TreeNode node)
	{
		if (this.wordId != null)
		{
			vnClasses(this.wordId, this.synsetId, node);
		}
		else
		{
			TreeFactory.setNoResult(node, true, false);
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

	private TreeNode[] vnClassesCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeNode[] changed;
		if (cursor.moveToFirst())
		{
			final List<TreeNode> nodes = new ArrayList<>();
			nodes.add(parent);

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
				final TreeNode node = TreeFactory.addTextNode(parent, sb);
				nodes.add(node);

				// sub nodes
				final TreeNode membersNode = TreeFactory.addHotQueryNode(parent, "Members", R.drawable.members, new MembersQuery(classId));
				nodes.add(membersNode);
				final TreeNode rolesNode = TreeFactory.addHotQueryNode(parent, "Roles", R.drawable.roles, new RolesQuery(classId));
				nodes.add(rolesNode);
				final TreeNode framesNode = TreeFactory.addQueryNode(parent, "Frames", R.drawable.vnframe, new FramesQuery(classId));
				nodes.add(framesNode);
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
}
