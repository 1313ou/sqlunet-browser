package org.sqlunet.browser.xselector;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.sqlunet.provider.XSqlUNetContract;
import org.sqlunet.provider.XSqlUNetContract.PredicateMatrix_PropBank;
import org.sqlunet.provider.XSqlUNetContract.Words_PbWords_PbRolesets;
import org.sqlunet.provider.XSqlUNetContract.Words_VnWords_VnClasses;
import org.sqlunet.provider.XSqlUNetContract.Words_XNet;
import org.sqlunet.provider.XSqlUNetProvider;

import androidx.annotation.NonNull;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

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
	abstract static class XnLoaderCallbacks implements LoaderCallbacks<Cursor>
	{
		final Context context;
		final long wordId;

		XnLoaderCallbacks(final Context context, final long wordId)
		{
			this.context = context;
			this.wordId = wordId;
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

		@NonNull
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

		@NonNull
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
	 * Dump utility
	 */
	@SuppressWarnings("unused")
	static public void dump(@NonNull final Cursor cursor)
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
