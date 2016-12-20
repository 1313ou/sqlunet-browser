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
	/**
	 * Write long text to log file
	 *
	 * @param text   text to write
	 * @param append whether to append to file
	 */
	static public String writeLog(final String text, final boolean append)
	{
		final File storage = Environment.getExternalStorageDirectory();
		final File logFile = new File(storage, "sqlunet.log");
		if (!logFile.exists())
		{
			try
			{
				logFile.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
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
			e.printStackTrace();
		}
		final String logFilePath = logFile.getAbsolutePath();
		Log.d("LOG", logFilePath);
		return logFilePath;
	}
}
