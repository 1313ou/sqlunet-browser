package org.sqlunet.browser.xselector;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.Loader.OnLoadCompleteListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

import org.sqlunet.browser.Module;
import org.sqlunet.provider.XSqlUNetContract.PredicateMatrix_Propbank;
import org.sqlunet.provider.XSqlUNetContract.Words_FnWords_FnFrames_U;
import org.sqlunet.provider.XSqlUNetContract.Words_PbWords_PbRolesets_U;
import org.sqlunet.provider.XSqlUNetContract.Words_VnWords_VnClasses_U;
import org.sqlunet.provider.XSqlUNetContract.Words_XNet_U;
import org.sqlunet.wordnet.provider.WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains;

/**
 * X loader
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class XLoader
{
	/**
	 * Xn callbacks
	 */
	public abstract static class XnLoaderCallbacks implements LoaderCallbacks<Cursor>
	{
		final Context context;
		final long wordId;

		public XnLoaderCallbacks(final Context context, final long wordId)
		{
			this.context = context;
			this.wordId = wordId;
		}
	}

	/**
	 * WordNet callbacks
	 */
	public abstract static class WnLoaderCallbacks extends XnLoaderCallbacks
	{
		public WnLoaderCallbacks(final Context context, final long wordId)
		{
			super(context, wordId);
		}

		@Override
		public Loader<Cursor> onCreateLoader(final int id, final Bundle args)
		{
			final Uri uri = Uri.parse(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.CONTENT_URI);
			final String[] projection = { //
					"'wn' AS " + Words_XNet_U.SOURCES, // //$NON-NLS-1$
					Words_VnWords_VnClasses_U.WORDID, //
					Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID, //
					Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID + " AS " + Words_XNet_U.XID, // //$NON-NLS-1$
					"NULL AS " + Words_XNet_U.XCLASSID, // //$NON-NLS-1$
					"NULL AS " + Words_XNet_U.XINSTANCEID, // //$NON-NLS-1$
					Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEMMA + "|| '.' ||" + Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.POS + " AS " + Words_XNet_U.XNAME, // //$NON-NLS-1$ //$NON-NLS-2$
					Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEXDOMAIN + " AS " + Words_XNet_U.XHEADER, // //$NON-NLS-1$
					Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SENSEKEY + " AS " + Words_XNet_U.XINFO, // //$NON-NLS-1$
					Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.DEFINITION + " AS " + Words_XNet_U.XDEFINITION, // //$NON-NLS-1$
					Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID + " AS _id"}; //$NON-NLS-1$
			final String selection = Words_VnWords_VnClasses_U.WORDID + " = ?"; //$NON-NLS-1$
			final String[] selectionArgs = {Long.toString(this.wordId)};
			final String sortOrder = null;
			return new CursorLoader(this.context, uri, projection, selection, selectionArgs, sortOrder);
		}
	}

	/**
	 * VerbNet callbacks
	 */
	public abstract static class VnLoaderCallbacks extends XnLoaderCallbacks
	{
		public VnLoaderCallbacks(final Context context, final long wordId)
		{
			super(context, wordId);
		}

		@Override
		public Loader<Cursor> onCreateLoader(final int id, final Bundle args)
		{
			final Uri uri = Uri.parse(Words_VnWords_VnClasses_U.CONTENT_URI);
			final String[] projection = { //
					Words_VnWords_VnClasses_U.WORDID, //
					Words_VnWords_VnClasses_U.SYNSETID, //
					Words_VnWords_VnClasses_U.CLASSID + " AS " + Words_XNet_U.XID, // //$NON-NLS-1$
					Words_VnWords_VnClasses_U.CLASSID + " AS " + Words_XNet_U.XCLASSID, // //$NON-NLS-1$
					Words_VnWords_VnClasses_U.WORDID + " AS " + Words_XNet_U.XINSTANCEID, // //$NON-NLS-1$
					"TRIM(" + Words_VnWords_VnClasses_U.CLASS + ",'-.0123456789')" + " AS " + Words_XNet_U.XNAME, // //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					Words_VnWords_VnClasses_U.CLASS + " AS " + Words_XNet_U.XHEADER, // //$NON-NLS-1$
					Words_VnWords_VnClasses_U.CLASSTAG + " AS " + Words_XNet_U.XINFO, // //$NON-NLS-1$
					Words_VnWords_VnClasses_U.DEFINITION + " AS " + Words_XNet_U.XDEFINITION, // //$NON-NLS-1$
					"rowid AS _id",}; //$NON-NLS-1$
			final String selection = Words_VnWords_VnClasses_U.WORDID + " = ?"; //$NON-NLS-1$
			final String[] selectionArgs = {Long.toString(this.wordId)};
			final String sortOrder = null;
			return new CursorLoader(this.context, uri, projection, selection, selectionArgs, sortOrder);
		}
	}

	/**
	 * Propbank loader callbacks
	 */
	public abstract static class PbLoaderCallbacks extends XnLoaderCallbacks
	{
		public PbLoaderCallbacks(final Context context, final long wordId)
		{
			super(context, wordId);
		}

		@Override
		public Loader<Cursor> onCreateLoader(final int id, final Bundle args)
		{
			final Uri uri = Uri.parse(Words_PbWords_PbRolesets_U.CONTENT_URI);
			final String[] projection = { //
					Words_PbWords_PbRolesets_U.WORDID, //
					Words_PbWords_PbRolesets_U.SYNSETID, //
					Words_PbWords_PbRolesets_U.ROLESETID + " AS " + Words_XNet_U.XID, // //$NON-NLS-1$
					"NULL AS " + Words_XNet_U.XCLASSID, // //$NON-NLS-1$
					Words_PbWords_PbRolesets_U.ROLESETID + " AS " + Words_XNet_U.XINSTANCEID, // //$NON-NLS-1$
					"TRIM(" + Words_PbWords_PbRolesets_U.ROLESETNAME + ",'.0123456789')" + " AS " + Words_XNet_U.XNAME, // //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					Words_PbWords_PbRolesets_U.ROLESETNAME + " AS " + Words_XNet_U.XHEADER, // //$NON-NLS-1$
					//Words_PbWords_PbRolesets_U.ROLESETHEAD + " AS " + Words_XNet_U.XHEADER, //
					Words_PbWords_PbRolesets_U.ROLESETDESCR + " AS " + Words_XNet_U.XINFO, // //$NON-NLS-1$
					Words_PbWords_PbRolesets_U.DEFINITION + " AS " + Words_XNet_U.XDEFINITION, // //$NON-NLS-1$
					"rowid AS _id",}; //$NON-NLS-1$
			final String selection = PredicateMatrix_Propbank.WORDID + " = ?"; //$NON-NLS-1$
			final String[] selectionArgs = {Long.toString(this.wordId)};
			final String sortOrder = null;
			return new CursorLoader(this.context, uri, projection, selection, selectionArgs, sortOrder);
		}
	}

	/**
	 * FrameNet loader callbacks
	 */
	abstract static class FnLoaderCallbacks extends XnLoaderCallbacks
	{
		public FnLoaderCallbacks(final Context context, final long wordId)
		{
			super(context, wordId);
		}

		@Override
		public Loader<Cursor> onCreateLoader(final int id, final Bundle args)
		{
			final Uri uri = Uri.parse(Words_FnWords_FnFrames_U.CONTENT_URI);
			final String[] projection = { //
					Words_FnWords_FnFrames_U.WORDID, //
					Words_FnWords_FnFrames_U.SYNSETID, //
					Words_FnWords_FnFrames_U.FRAMEID + " AS " + Words_XNet_U.XID, // //$NON-NLS-1$
					Words_FnWords_FnFrames_U.FRAMEID + " AS " + Words_XNet_U.XCLASSID, // //$NON-NLS-1$
					Words_FnWords_FnFrames_U.LUID + " AS " + Words_XNet_U.XINSTANCEID, // //$NON-NLS-1$
					"GROUP_CONCAT(" + Words_FnWords_FnFrames_U.LEXUNIT + ",'\n')" + " AS " + Words_XNet_U.XNAME, // //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					Words_FnWords_FnFrames_U.FRAME + " AS " + Words_XNet_U.XHEADER, // //$NON-NLS-1$
					"GROUP_CONCAT(" + Words_FnWords_FnFrames_U.LUDEFINITION + ",'\n') AS " + Words_XNet_U.XINFO, // //$NON-NLS-1$ //$NON-NLS-2$
					Words_FnWords_FnFrames_U.DEFINITION + " AS " + Words_XNet_U.XDEFINITION, // //$NON-NLS-1$
					"rowid AS _id",}; //$NON-NLS-1$
			final String selection = Words_FnWords_FnFrames_U.WORDID + " = ?"; //$NON-NLS-1$
			final String[] selectionArgs = {Long.toString(this.wordId)};
			final String sortOrder = null;
			return new CursorLoader(this.context, uri, projection, selection, selectionArgs, sortOrder);
		}
	}

	/**
	 * Full loader callbacks
	 */
	abstract static class FullLoaderCallbacks implements LoaderCallbacks<Cursor>
	{
		final Activity activity;
		final long wordId;
		final Loader<Cursor> loader;
		final SparseArray<Cursor> cursors;

		@SuppressWarnings("unused")
		public FullLoaderCallbacks(Activity activity, long wordId, final Loader<Cursor> loader)
		{
			this.activity = activity;
			this.wordId = wordId;
			this.cursors = new SparseArray<>();
			this.loader = loader;
		}

		/**
		 * Listener to subloader complete events
		 */
		final OnLoadCompleteListener<Cursor> listener = new OnLoadCompleteListener<Cursor>()
		{
			@Override
			public void onLoadComplete(final Loader<Cursor> subloader, final Cursor cursor)
			{
				int id = subloader.getId();
				FullLoaderCallbacks.this.cursors.put(id, cursor);

				// fire load complete if we have all cursors
				if (FullLoaderCallbacks.this.cursors.size() == 3)
				{
					onLoadFinished(FullLoaderCallbacks.this.loader, null);
				}
			}
		};

		// load the contents
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args)
		{
			final Loader<Cursor> vnloader = this.activity.getLoaderManager().restartLoader(++Module.loaderId, null, new VnLoaderCallbacks(this.activity, this.wordId)
			{
				@Override
				public void onLoadFinished(Loader<Cursor> loader, Cursor data)
				{
					loader.deliverResult(data);
				}

				@Override
				public void onLoaderReset(Loader<Cursor> loader)
				{
					loader.deliverResult(null);
				}
			});
			vnloader.registerListener(1, this.listener);

			final Loader<Cursor> pbloader = this.activity.getLoaderManager().restartLoader(++Module.loaderId, null, new PbLoaderCallbacks(this.activity, this.wordId)
			{
				@Override
				public void onLoadFinished(Loader<Cursor> loader, Cursor data)
				{
					loader.deliverResult(data);
				}

				@Override
				public void onLoaderReset(Loader<Cursor> loader)
				{
					loader.deliverResult(null);
				}
			});
			pbloader.registerListener(2, this.listener);

			final Loader<Cursor> fnloader = this.activity.getLoaderManager().restartLoader(++Module.loaderId, null, new FnLoaderCallbacks(this.activity, this.wordId)
			{
				@Override
				public void onLoadFinished(Loader<Cursor> loader, Cursor data)
				{
					loader.deliverResult(data);
				}

				@Override
				public void onLoaderReset(Loader<Cursor> loader)
				{
					loader.deliverResult(null);
				}
			});
			fnloader.registerListener(3, this.listener);
			return null;
		}
	}

	/**
	 * Dump utility
	 */
	static public void dump(final Cursor cursor)
	{
		if (cursor.moveToFirst())
		{
			final int idWordId = cursor.getColumnIndex(Words_XNet_U.WORDID);
			final int idSynsetId = cursor.getColumnIndex(Words_XNet_U.SYNSETID);
			final int idXId = cursor.getColumnIndex(Words_XNet_U.XID);
			final int idXName = cursor.getColumnIndex(Words_XNet_U.XNAME);
			final int idXHeader = cursor.getColumnIndex(Words_XNet_U.XHEADER);
			final int idXInfo = cursor.getColumnIndex(Words_XNet_U.XINFO);
			final int idDefinition = cursor.getColumnIndex(Words_XNet_U.XDEFINITION);
			final int idSources = cursor.getColumnIndex(Words_XNet_U.SOURCES);

			do
			{
				long wordId = cursor.getLong(idWordId);
				long synsetId = cursor.isNull(idSynsetId) ? -1 : cursor.getLong(idSynsetId);
				long xId = cursor.isNull(idXId) ? -1 : cursor.getLong(idXId);
				String xName = cursor.isNull(idXName) ? null : cursor.getString(idXName);
				String xHeader = cursor.isNull(idXHeader) ? null : cursor.getString(idXHeader);
				String xInfo = cursor.isNull(idXInfo) ? null : cursor.getString(idXInfo);
				String definition = cursor.isNull(idXInfo) ? null : cursor.getString(idDefinition);
				String sources = cursor.isNull(idSources) ? "" : //$NON-NLS-1$
						cursor.getString(idSources);
				Log.i("DUMP", "sources=" + sources +  //$NON-NLS-1$
						" wordId=" + wordId +  //$NON-NLS-1$
						" synsetid=" + synsetId +  //$NON-NLS-1$
						" xid=" + xId +  //$NON-NLS-1$
						" name=" + xName +  //$NON-NLS-1$
						" header=" + xHeader +  //$NON-NLS-1$
						" info=" + xInfo +  //$NON-NLS-1$
						" definition=" + definition); //$NON-NLS-1$
			}
			while (cursor.moveToNext());
			cursor.moveToFirst();
		}
	}
}
