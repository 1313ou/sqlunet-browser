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
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.verbnet.R;
import org.sqlunet.verbnet.provider.VerbNetContract.Words_VnClasses_VnGroupings;
import org.sqlunet.verbnet.style.VerbNetFactories;
import org.sqlunet.view.TreeFactory;

public class ClassFromWordModule extends BasicModule
{
	/**
	 * Query
	 */
	private Long wordid;

	private Long synsetid;

	/**
	 * Constructor
	 *
	 * @param fragment0 host fragment
	 */
	public ClassFromWordModule(final Fragment fragment0)
	{
		super(fragment0);
	}

	@Override
	void unmarshall(final Parcelable query)
	{
		if (query instanceof HasSynsetId)
		{
			final HasSynsetId synsetQuery = (HasSynsetId) query;
			this.synsetid = synsetQuery.getSynsetId();
		}
		if (query instanceof HasWordId)
		{
			final HasWordId wordidQuery = (HasWordId) query;
			this.wordid = wordidQuery.getWordId();
		}
	}

	@Override
	public void process(final TreeNode node)
	{
		if (this.wordid != null)
		{
			vnclasses(this.wordid, this.synsetid, node);
		}
		else
		{
			node.disable();
		}
	}

	// L O A D E R S

	// vnclasses

	private void vnclasses(final long wordid0, final long synsetid0, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId0, final Bundle args)
			{
				final Uri uri = Uri.parse(Words_VnClasses_VnGroupings.CONTENT_URI);
				final String[] projection = { //
						Words_VnClasses_VnGroupings.CLASSID, //
						Words_VnClasses_VnGroupings.CLASS, //
						Words_VnClasses_VnGroupings.CLASSTAG, //
						"(" + Words_VnClasses_VnGroupings.SYNSETID + " IS NULL) AS " + Words_VnClasses_VnGroupings.NULLSYNSET, // //$NON-NLS-1$ //$NON-NLS-2$
						Words_VnClasses_VnGroupings.SENSENUM, //
						Words_VnClasses_VnGroupings.SENSEKEY, //
						Words_VnClasses_VnGroupings.QUALITY, //
						"GROUP_CONCAT(" + Words_VnClasses_VnGroupings.GROUPING + ", '|') AS " + Words_VnClasses_VnGroupings.GROUPINGS, // //$NON-NLS-1$ //$NON-NLS-2$
				};
				String selection = Words_VnClasses_VnGroupings.WORDID + " = ?"; //$NON-NLS-1$
				String[] selectionArgs;
				if (synsetid0 != 0)
				{
					selection += " AND (" + Words_VnClasses_VnGroupings.SYNSETID + " = ? OR " + Words_VnClasses_VnGroupings.SYNSETID + " IS NULL)"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					selectionArgs = new String[]{ //
							Long.toString(wordid0), //
							Long.toString(synsetid0)};
				}
				else
				{
					selectionArgs = new String[]{ //
							Long.toString(wordid0)};
				}
				final String sortOrder = null;
				return new CursorLoader(ClassFromWordModule.this.getContext(), uri, projection, selection, selectionArgs, sortOrder);
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
						final int classid1 = cursor.getInt(idClassId);
						final String vnclass = cursor.getString(idClass);
						final String groupings = cursor.getString(idGroupings);

						// sb.append("[class]");
						Spanner.appendImage(sb, ClassFromWordModule.this.drawableRoles);
						sb.append(' ');
						Spanner.append(sb, vnclass, 0, VerbNetFactories.classFactory);
						// sb.append(" tag=");
						// sb.append(cursor.getString(idClassTag));
						sb.append(" id="); //$NON-NLS-1$
						sb.append(Integer.toString(classid1));

						// groupings
						final TreeNode itemsNode = groupings(groupings);

						// sub nodes
						final TreeNode rolesNode = TreeFactory.newQueryNode(new RolesQuery(classid1, R.drawable.role, "Roles"), ClassFromWordModule.this.getContext()); //$NON-NLS-1$
						final TreeNode framesNode = TreeFactory.newQueryNode(new FramesQuery(classid1, R.drawable.vnframe, "Frames"), ClassFromWordModule.this.getContext()); //$NON-NLS-1$

						// attach result
						TreeFactory.addTextNode(parent, sb, ClassFromWordModule.this.getContext(), itemsNode, rolesNode, framesNode);
					}
					while (cursor.moveToNext());

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					parent.disable();
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> arg0)
			{
				//
			}
		});
	}
}
