package org.sqlunet.settings;

import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import org.sqlunet.dom.DomTransformer;
import org.w3c.dom.Document;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * File utilities
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class LogUtils
{
	static private final String TAG = "";

	/**
	 * Write long text to log file
	 *
	 * @param text     text to write
	 * @param append   whether to append to file
	 * @param fileName file name
	 */
	static public String writeLog(final CharSequence text, final boolean append, @Nullable final String fileName)
	{
		final File storage = Environment.getExternalStorageDirectory();
		final File logFile = new File(storage, fileName != null ? fileName : "sqlunet.log");
		try
		{
			//noinspection ResultOfMethodCallIgnored
			logFile.createNewFile();
		}
		catch (IOException e)
		{
			Log.e(TAG, "Cannot create file", e);
			return null;
		}
		try
		{
			// BufferedWriter for performance, true to set append to file flag
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, append));
			buf.append(text);
			buf.newLine();
			buf.close();
		}
		catch (IOException e)
		{
			Log.e(TAG, "Cannot write", e);
		}
		final String logFilePath = logFile.getAbsolutePath();
		Log.d("LOG", logFilePath);
		return logFilePath;
	}

	/**
	 * Write long text to log file
	 *
	 * @param append    whether to append to file
	 * @param fileName0 file name
	 */
	@Nullable
	@SuppressWarnings("UnusedReturnValue")
	static public String writeLog(@SuppressWarnings("SameParameterValue") final boolean append, @Nullable @SuppressWarnings("SameParameterValue") final String fileName0, @NonNull final Document... docs)
	{
		final String fileName = fileName0 != null ? fileName0 : "sqlunetx.log";
		final StringBuilder sb = new StringBuilder();
		for (Document doc : docs)
		{
			if (doc != null)
			{
				sb.append(DomTransformer.docToXml(doc));
			}
		}
		final String data = sb.toString();
		return writeLog(data, append, fileName);
	}
}
