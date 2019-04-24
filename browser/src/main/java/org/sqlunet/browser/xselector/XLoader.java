package org.sqlunet.browser.xselector;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.sqlunet.provider.XSqlUNetContract.PredicateMatrix_PropBank;
import org.sqlunet.provider.XSqlUNetContract.Words_FnWords_FnFrames_U;
import org.sqlunet.provider.XSqlUNetContract.Words_PbWords_PbRolesets_U;
import org.sqlunet.provider.XSqlUNetContract.Words_VnWords_VnClasses_U;
import org.sqlunet.provider.XSqlUNetContract.Words_XNet_U;
import org.sqlunet.provider.XSqlUNetProvider;
import org.sqlunet.wordnet.provider.WordNetContract;
import org.sqlunet.wordnet.provider.WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains;
import org.sqlunet.wordnet.provider.WordNetProvider;

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
	 * WordNet callbacks
	 */
	public abstract static class WnLoaderCallbacks extends XnLoaderCallbacks
	{
		public WnLoaderCallbacks(final Context context, final long wordId)
		{
			super(context, wordId);
		}

		@NonNull
		@Override
		public Loader<Cursor> onCreateLoader(final int id, final Bundle args)
		{
			final Uri uri = Uri.parse(WordNetProvider.makeUri(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.CONTENT_URI_TABLE));
			final String[] projection = { //
					"'wn' AS " + Words_XNet_U.SOURCES, //
					Words_VnWords_VnClasses_U.WORDID, //
					Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID, //
					Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID + " AS " + Words_XNet_U.XID, //
					"NULL AS " + Words_XNet_U.XCLASSID, //
					"NULL AS " + Words_XNet_U.XMEMBERID, //
					Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEMMA + "|| '.' ||" + WordNetContract.POS + '.' + Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.POS + " AS " + Words_XNet_U.XNAME, //
					Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEXDOMAIN + " AS " + Words_XNet_U.XHEADER, //
					Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SENSEKEY + " AS " + Words_XNet_U.XINFO, //
					Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.DEFINITION + " AS " + Words_XNet_U.XDEFINITION, //
					Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID + " AS _id"};
			final String selection = Words_VnWords_VnClasses_U.WORDID + " = ?";
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

		@NonNull
		@Override
		public Loader<Cursor> onCreateLoader(final int id, final Bundle args)
		{
			final Uri uri = Uri.parse(XSqlUNetProvider.makeUri(Words_VnWords_VnClasses_U.CONTENT_URI_TABLE));
			final String[] projection = { //
					Words_VnWords_VnClasses_U.WORDID, //
					Words_VnWords_VnClasses_U.SYNSETID, //
					Words_VnWords_VnClasses_U.CLASSID + " AS " + Words_XNet_U.XID, //
					Words_VnWords_VnClasses_U.CLASSID + " AS " + Words_XNet_U.XCLASSID, //
					"NULL AS " + Words_XNet_U.XMEMBERID, //
					"TRIM(" + Words_VnWords_VnClasses_U.CLASS + ",'-.0123456789')" + " AS " + Words_XNet_U.XNAME, //
					Words_VnWords_VnClasses_U.CLASS + " AS " + Words_XNet_U.XHEADER, //
					Words_VnWords_VnClasses_U.CLASSTAG + " AS " + Words_XNet_U.XINFO, //
					Words_VnWords_VnClasses_U.DEFINITION + " AS " + Words_XNet_U.XDEFINITION, //
					"rowid AS _id",};
			final String selection = Words_VnWords_VnClasses_U.WORDID + " = ?";
			final String[] selectionArgs = {Long.toString(this.wordId)};
			final String sortOrder = Words_VnWords_VnClasses_U.CLASSID;
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
			final Uri uri = Uri.parse(XSqlUNetProvider.makeUri(Words_PbWords_PbRolesets_U.CONTENT_URI_TABLE));
			final String[] projection = { //
					Words_PbWords_PbRolesets_U.WORDID, //
					Words_PbWords_PbRolesets_U.SYNSETID, //
					Words_PbWords_PbRolesets_U.ROLESETID + " AS " + Words_XNet_U.XID, //
					Words_PbWords_PbRolesets_U.ROLESETID + " AS " + Words_XNet_U.XCLASSID, //
					"NULL AS " + Words_XNet_U.XMEMBERID, //
					"TRIM(" + Words_PbWords_PbRolesets_U.ROLESETNAME + ",'.0123456789')" + " AS " + Words_XNet_U.XNAME, //
					Words_PbWords_PbRolesets_U.ROLESETNAME + " AS " + Words_XNet_U.XHEADER, //
					//Words_PbWords_PbRolesets_U.ROLESETHEAD + " AS " + Words_XNet_U.XHEADER, //
					Words_PbWords_PbRolesets_U.ROLESETDESCR + " AS " + Words_XNet_U.XINFO, //
					Words_PbWords_PbRolesets_U.DEFINITION + " AS " + Words_XNet_U.XDEFINITION, //
					"rowid AS _id",};
			final String selection = PredicateMatrix_PropBank.WORDID + " = ?";
			final String[] selectionArgs = {Long.toString(this.wordId)};
			final String sortOrder = Words_PbWords_PbRolesets_U.ROLESETID;
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

		@NonNull
		@Override
		public Loader<Cursor> onCreateLoader(final int id, final Bundle args)
		{
			final Uri uri = Uri.parse(XSqlUNetProvider.makeUri(Words_FnWords_FnFrames_U.CONTENT_URI_TABLE));
			final String[] projection = { //
					Words_FnWords_FnFrames_U.WORDID, //
					Words_FnWords_FnFrames_U.SYNSETID, //
					Words_FnWords_FnFrames_U.FRAMEID + " AS " + Words_XNet_U.XID, //
					Words_FnWords_FnFrames_U.FRAMEID + " AS " + Words_XNet_U.XCLASSID, //
					Words_FnWords_FnFrames_U.LUID + " AS " + Words_XNet_U.XMEMBERID, //
					"GROUP_CONCAT(" + Words_FnWords_FnFrames_U.LEXUNIT + ",'\n')" + " AS " + Words_XNet_U.XNAME, //
					Words_FnWords_FnFrames_U.FRAME + " AS " + Words_XNet_U.XHEADER, //
					"GROUP_CONCAT(" + Words_FnWords_FnFrames_U.LUDEFINITION + ",'\n') AS " + Words_XNet_U.XINFO, //
					Words_FnWords_FnFrames_U.DEFINITION + " AS " + Words_XNet_U.XDEFINITION, //
					"rowid AS _id",};
			final String selection = Words_FnWords_FnFrames_U.WORDID + " = ?";
			final String[] selectionArgs = {Long.toString(this.wordId)};
			final String sortOrder = Words_FnWords_FnFrames_U.LUID + ' ' + "IS NULL" + ',' + Words_FnWords_FnFrames_U.SOURCES + ',' + Words_FnWords_FnFrames_U.FRAMEID;
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
