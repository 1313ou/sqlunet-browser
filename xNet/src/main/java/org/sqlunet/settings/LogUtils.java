package org.sqlunet.settings;

import android.os.Environment;
import android.util.Log;

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
	 * @param text   text to write
	 * @param append whether to append to file
	 */
	static public String writeLog(final CharSequence text, final boolean append)
	{
		final File storage = Environment.getExternalStorageDirectory();
		final File logFile = new File(storage, "sqlunet.log");
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
}
