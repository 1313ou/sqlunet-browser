package org.sqlunet.browser.xselector;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.Loader.OnLoadCompleteListener;
import android.util.Log;
import android.util.SparseArray;

import org.sqlunet.browser.Module;
import org.sqlunet.provider.XSqlUNetContract;
import org.sqlunet.provider.XSqlUNetContract.PredicateMatrix_PropBank;
import org.sqlunet.provider.XSqlUNetContract.Words_PbWords_PbRolesets;
import org.sqlunet.provider.XSqlUNetContract.Words_VnWords_VnClasses;
import org.sqlunet.provider.XSqlUNetContract.Words_XNet;
import org.sqlunet.provider.XSqlUNetProvider;
import org.sqlunet.wordnet.provider.WordNetContract;
import org.sqlunet.wordnet.provider.WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains;
import org.sqlunet.wordnet.provider.WordNetProvider;

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
			final Uri uri = Uri.parse(WordNetProvider.makeUri(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.CONTENT_URI_TABLE));
			final String[] projection = { //
					"'wn' AS " + Words_XNet.SOURCES, //
					Words_VnWords_VnClasses.WORDID, //
					Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID, //
					Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID + " AS " + Words_XNet.XID, //
					"NULL AS " + Words_XNet.XCLASSID, //
					"NULL AS " + Words_XNet.XMEMBERID, //
					Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEMMA + "|| '.' ||" + WordNetContract.POS + '.' + Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.POS + " AS " + Words_XNet.XNAME, //
					Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEXDOMAIN + " AS " + Words_XNet.XHEADER, //
					Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SENSEKEY + " AS " + Words_XNet.XINFO, //
					Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.DEFINITION + " AS " + Words_XNet.XDEFINITION, //
					Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID + " AS _id"};
			final String selection = Words_VnWords_VnClasses.WORDID + " = ?";
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
			final Uri uri = Uri.parse(XSqlUNetProvider.makeUri(Words_VnWords_VnClasses.CONTENT_URI_TABLE));
			final String[] projection = { //
					Words_VnWords_VnClasses.WORDID, //
					Words_VnWords_VnClasses.SYNSETID, //
					Words_VnWords_VnClasses.CLASSID + " AS " + Words_XNet.XID, //
					Words_VnWords_VnClasses.CLASSID + " AS " + Words_XNet.XCLASSID, //
					"NULL AS " + Words_XNet.XMEMBERID, //
					"TRIM(" + Words_VnWords_VnClasses.CLASS + ",'-.0123456789')" + " AS " + Words_XNet.XNAME, //
					Words_VnWords_VnClasses.CLASS + " AS " + Words_XNet.XHEADER, //
					Words_VnWords_VnClasses.CLASSTAG + " AS " + Words_XNet.XINFO, //
					Words_VnWords_VnClasses.DEFINITION + " AS " + Words_XNet.XDEFINITION, //
					"'vn' AS " + Words_XNet.SOURCES, //
					XSqlUNetContract.CLASS + ".rowid AS _id",};
			final String selection = Words_VnWords_VnClasses.WORDID + " = ?";
			final String[] selectionArgs = {Long.toString(this.wordId)};
			final String sortOrder = Words_VnWords_VnClasses.CLASSID;
			return new CursorLoader(this.context, uri, projection, selection, selectionArgs, sortOrder);
		}
	}

	/**
	 * PropBank loader callbacks
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
			final Uri uri = Uri.parse(XSqlUNetProvider.makeUri(Words_PbWords_PbRolesets.CONTENT_URI_TABLE));
			final String[] projection = { //
					Words_PbWords_PbRolesets.WORDID, //
					"NULL AS " + Words_PbWords_PbRolesets.SYNSETID, //
					Words_PbWords_PbRolesets.ROLESETID + " AS " + Words_XNet.XID, //
					Words_PbWords_PbRolesets.ROLESETID + " AS " + Words_XNet.XCLASSID, //
					"NULL AS " + Words_XNet.XMEMBERID, //
					"TRIM(" + Words_PbWords_PbRolesets.ROLESETNAME + ",'.0123456789')" + " AS " + Words_XNet.XNAME, //
					Words_PbWords_PbRolesets.ROLESETNAME + " AS " + Words_XNet.XHEADER, //
					//Words_PbWords_PbRolesets.ROLESETHEAD + " AS " + Words_XNet.XHEADER, //
					Words_PbWords_PbRolesets.ROLESETDESCR + " AS " + Words_XNet.XINFO, //
					"NULL AS " + Words_XNet.XDEFINITION, //
					"'pb' AS " + Words_XNet.SOURCES, //
					XSqlUNetContract.CLASS + ".rowid AS _id",};
			final String selection = PredicateMatrix_PropBank.WORDID + " = ?";
			final String[] selectionArgs = {Long.toString(this.wordId)};
			final String sortOrder = Words_PbWords_PbRolesets.ROLESETID;
			return new CursorLoader(this.context, uri, projection, selection, selectionArgs, sortOrder);
		}
	}

	/**
	 * Full loader callbacks
	 */
	abstract static class FullLoaderCallbacks implements LoaderCallbacks<Cursor>
	{
		final FragmentActivity activity;
		final long wordId;
		final Loader<Cursor> loader;
		final SparseArray<Cursor> cursors;

		@SuppressWarnings("unused")
		public FullLoaderCallbacks(final FragmentActivity activity, final long wordId, final Loader<Cursor> loader)
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
			public void onLoadComplete(@NonNull final Loader<Cursor> subloader, final Cursor cursor)
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
			final Loader<Cursor> vnloader = this.activity.getSupportLoaderManager().restartLoader(++Module.loaderId, null, new VnLoaderCallbacks(this.activity, this.wordId)
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

			final Loader<Cursor> pbloader = this.activity.getSupportLoaderManager().restartLoader(++Module.loaderId, null, new PbLoaderCallbacks(this.activity, this.wordId)
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

			return null;
		}
	}

	/**
	 * Dump utility
	 */
	@SuppressWarnings("unused")
	static public void dump(final Cursor cursor)
	{
		if (cursor.moveToFirst())
		{
			final int idWordId = cursor.getColumnIndex(Words_XNet.WORDID);
			final int idSynsetId = cursor.getColumnIndex(Words_XNet.SYNSETID);
			final int idXId = cursor.getColumnIndex(Words_XNet.XID);
			final int idXName = cursor.getColumnIndex(Words_XNet.XNAME);
			final int idXHeader = cursor.getColumnIndex(Words_XNet.XHEADER);
			final int idXInfo = cursor.getColumnIndex(Words_XNet.XINFO);
			final int idDefinition = cursor.getColumnIndex(Words_XNet.XDEFINITION);
			final int idSources = cursor.getColumnIndex(Words_XNet.SOURCES);

			do
			{
				long wordId = cursor.getLong(idWordId);
				long synsetId = cursor.isNull(idSynsetId) ? 0 : cursor.getLong(idSynsetId);
				long xId = cursor.isNull(idXId) ? 0 : cursor.getLong(idXId);
				String xName = cursor.isNull(idXName) ? null : cursor.getString(idXName);
				String xHeader = cursor.isNull(idXHeader) ? null : cursor.getString(idXHeader);
				String xInfo = cursor.isNull(idXInfo) ? null : cursor.getString(idXInfo);
				String definition = cursor.isNull(idXInfo) ? null : cursor.getString(idDefinition);
				String sources = cursor.isNull(idSources) ? "" : //
						cursor.getString(idSources);
				Log.i("xloader", "sources=" + sources +  //
						" wordid=" + wordId +  //
						" synsetid=" + synsetId +  //
						" xid=" + xId +  //
						" name=" + xName +  //
						" header=" + xHeader +  //
						" info=" + xInfo +  //
						" definition=" + definition);
			}
			while (cursor.moveToNext());
			cursor.moveToFirst();
		}
	}
}
