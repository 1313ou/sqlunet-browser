/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.provider;

import android.database.Cursor;
import android.util.Log;

import androidx.annotation.Nullable;

public class CursorDump
{
	// static private final String TAG = "CursorDump";

	/**
	 * Dump any cursor (discover its schema)
	 */
	static public void dump(@Nullable final Cursor cursor)
	{
		if (cursor == null)
		{
			Log.i("dump", "null cursor");
			return;
		}

		if (cursor.isClosed())
		{
			Log.i("dump", "closed cursor=" + cursor);
			return;
		}

		if (cursor.moveToFirst())
		{
			do
			{
				int count = cursor.getColumnCount();
				for (int c = 0; c < count; c++)
				{
					final String name = cursor.getColumnName(c);
					final String value = cursor.getString(c);

					Log.i("dump", name + "=" + value);
				}
			}
			while (cursor.moveToNext());
			cursor.moveToFirst();
		}
	}

	/**
	 * Dump X cursor (x selector query)
	 */
	static public void dumpXCursor(@Nullable final Cursor cursor)
	{
		if (cursor == null)
		{
			Log.i("dump", "null cursor");
			return;
		}
		if (cursor.moveToFirst())
		{
			final int idWordId = cursor.getColumnIndex(XNetContract.Words_XNet_U.WORDID);
			final int idSynsetId = cursor.getColumnIndex(XNetContract.Words_XNet_U.SYNSETID);
			final int idXId = cursor.getColumnIndex(XNetContract.Words_XNet_U.XID);
			final int idXName = cursor.getColumnIndex(XNetContract.Words_XNet_U.XNAME);
			final int idXHeader = cursor.getColumnIndex(XNetContract.Words_XNet_U.XHEADER);
			final int idXInfo = cursor.getColumnIndex(XNetContract.Words_XNet_U.XINFO);
			final int idDefinition = cursor.getColumnIndex(XNetContract.Words_XNet_U.XDEFINITION);
			final int idSources = cursor.getColumnIndex(XNetContract.Words_XNet_U.SOURCES);

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
