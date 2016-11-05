package org.sqlunet.verbnet.loaders;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;

import org.sqlunet.HasSynsetId;
import org.sqlunet.HasWordId;
import org.sqlunet.browser.Module;
import org.sqlunet.style.Spanner;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.verbnet.R;
import org.sqlunet.verbnet.provider.VerbNetContract.Words_VnClasses_VnGroupings;
import org.sqlunet.verbnet.style.VerbNetFactories;
import org.sqlunet.view.TreeFactory;
import org.sqlunet.view.Update;

/**
 * VerbNet class from word/sense module
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class ClassFromWordModule extends BasicModule
{
	/**
	 * Word id
	 */
	private Long wordId;

	/**
	 * Synset id (null=ignore)
	 */
	private Long synsetId;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	public ClassFromWordModule(final Fragment fragment)
	{
		super(fragment);
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
	public void process(final TreeNode node)
	{
		if (this.wordId != null)
		{
			vnClasses(this.wordId, this.synsetId, node);
		}
		else
		{
			Update.onNoResult(node, true);
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
	private void vnClasses(final long wordId, final Long synsetId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(Words_VnClasses_VnGroupings.CONTENT_URI);
				final String[] projection = { //
						Words_VnClasses_VnGroupings.CLASSID, //
						Words_VnClasses_VnGroupings.CLASS, //
						Words_VnClasses_VnGroupings.CLASSTAG, //
						"(" + Words_VnClasses_VnGroupings.SYNSETID + " IS NULL) AS " + Words_VnClasses_VnGroupings.NULLSYNSET, //
						Words_VnClasses_VnGroupings.SENSENUM, //
						Words_VnClasses_VnGroupings.SENSEKEY, //
						Words_VnClasses_VnGroupings.QUALITY, //
						"GROUP_CONCAT(" + Words_VnClasses_VnGroupings.GROUPING + ", '|') AS " + Words_VnClasses_VnGroupings.GROUPINGS, //
				};
				String selection = Words_VnClasses_VnGroupings.WORDID + " = ?"; //
				String[] selectionArgs;
				if (synsetId != null && synsetId != 0)
				{
					selection += " AND (" + Words_VnClasses_VnGroupings.SYNSETID + " = ? OR " + Words_VnClasses_VnGroupings.SYNSETID + " IS NULL)"; //
					selectionArgs = new String[]{ //
							Long.toString(wordId), //
							Long.toString(synsetId)};
				}
				else
				{
					selectionArgs = new String[]{ //
							Long.toString(wordId)};
				}
				final String sortOrder = null;
				return new CursorLoader(ClassFromWordModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.moveToFirst())
				{
					// column indices
					final int idClassId = cursor.getColumnIndex(Words_VnClasses_VnGroupings.CLASSID);
					final int idClass = cursor.getColumnIndex(Words_VnClasses_VnGroupings.CLASS);
					final int idGroupings = cursor.getColumnIndex(Words_VnClasses_VnGroupings.GROUPINGS);
					// final int idClassTag = cursor.getColumnIndex(Words_VnClasses.CLASSTAG);

					// read cursor
					do
					{
						final SpannableStringBuilder sb = new SpannableStringBuilder();

						// data
						final int classId = cursor.getInt(idClassId);
						final String vnClass = cursor.getString(idClass);
						final String groupings = cursor.getString(idGroupings);

						// sb.append("[class]");
						Spanner.appendImage(sb, ClassFromWordModule.this.drawableRoles);
						sb.append(' ');
						Spanner.append(sb, vnClass, 0, VerbNetFactories.classFactory);
						// sb.append(" tag=");
						// sb.append(cursor.getString(idClassTag));
						sb.append(" id="); //
						sb.append(Integer.toString(classId));

						// attach result
						TreeFactory.addTextNode(parent, sb, ClassFromWordModule.this.context);

						// groupings
						final TreeNode itemsNode = groupings(groupings);
						itemsNode.addTo(parent);

						// sub nodes
						final TreeNode rolesNode = TreeFactory.newQueryNode(new RolesQuery(classId, R.drawable.role, "Roles"), true, ClassFromWordModule.this.context).addTo(parent);
						final TreeNode framesNode = TreeFactory.newQueryNode(new FramesQuery(classId, R.drawable.vnframe, "Frames"), false, ClassFromWordModule.this.context).addTo(parent);

						// fire event
						Update.onQueryReady(rolesNode);
						Update.onQueryReady(framesNode);
					}
					while (cursor.moveToNext());

					// fire event
					Update.onResults(parent);
				}
				else
				{
					Update.onNoResult(parent, true);
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
}
