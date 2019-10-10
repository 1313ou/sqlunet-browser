package org.sqlunet.browser;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.platform.app.InstrumentationRegistry;

public class DataUtils
{
	static private final String LIST_FILE = "tests/sqlunet.list";

	@NonNull
	static public String arrayToString(@NonNull int... a)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append('{');
		boolean first = true;
		for (int i : a)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				sb.append(',');
			}
			sb.append(i);
		}
		sb.append('}');
		return sb.toString();
	}

	// S A M P L E S

	@Nullable
	static String[] WORD_LIST = {"abandon", "leave", "inveigle", "foist", "flounder", "flout"};

	@Nullable
	static public String[] getWordList()
	{
		return readWordList();
	}

	@Nullable
	static private String[] readWordList()
	{
		final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
		final AssetManager assets = context.getResources().getAssets();
		final List<String> list = new ArrayList<>();
		try (final InputStream is = assets.open(DataUtils.LIST_FILE); //
		     final Reader reader = new InputStreamReader(is); //
		     final BufferedReader br = new BufferedReader(reader) //
		)
		{
			String line;
			while ((line = br.readLine()) != null)
			{
				list.add(line.trim());
			}
			return list.toArray(new String[0]);
		}
		catch (IOException e)
		{
			//Log.d("Read", "Error " + dataFile.getAbsolutePath(), e);
			Log.e("Read", "Error " + DataUtils.LIST_FILE, e);
			return null;
		}
	}

	@Nullable
	static private String[] readWordListAlt()
	{
		final List<String> list = new ArrayList<>();
		final File dataFile = new File(Environment.getExternalStorageDirectory(), DataUtils.LIST_FILE);
		try (final FileReader reader = new FileReader(dataFile); //
		     final BufferedReader br = new BufferedReader(reader))
		{
			String line;
			while ((line = br.readLine()) != null)
			{
				list.add(line.trim());
			}
			return list.toArray(new String[0]);
		}
		catch (@NonNull final IOException e)
		{
			Log.d("Read", "Error " + dataFile.getAbsolutePath(), e);
		}
		return null;
	}
}
