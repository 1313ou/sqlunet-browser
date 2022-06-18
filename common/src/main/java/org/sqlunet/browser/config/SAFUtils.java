package org.sqlunet.browser.config;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;
import androidx.core.util.Consumer;

public class SAFUtils
{
	private static final String TAG = "SAFUtils";

	// L I S T E N E R

	public static ActivityResultLauncher<Intent> makeListener(final AppCompatActivity activity, final Consumer<Uri> consumer)
	{
		return activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

			if (result.getResultCode() == Activity.RESULT_OK)
			{
				Intent resultData = result.getData();
				if (resultData != null)
				{
					Uri uri = resultData.getData();
					Log.i(TAG, "Uri: " + uri.toString());

					consumer.accept(uri);
				}
			}
		});
	}

	// P I C K

	public static void pick(@NonNull final String mimeType, @NonNull final ActivityResultLauncher<Intent> launcher)
	{
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

		// Filter to only show results that can be "opened", such as a file (as opposed to a list of contacts or timezones)
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		// Filter to show only images, using the image MIME data type.
		// If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
		// To search for all documents available via installed storage providers,
		// it would be "*/*".
		intent.setType(mimeType);

		launcher.launch(intent);
	}

	// Q U E R Y

	@Nullable
	public static String querySize(@NonNull final Uri uri, @NonNull final ContentResolver resolver)
	{
		// The query, since it only applies to a single document, will only return one row. There's no need to filter, sort, or select fields, since we want all fields for one document.
		try (Cursor cursor = resolver.query(uri, null, null, null, null, null))
		{
			// moveToFirst() returns false if the cursor has 0 rows.  Very handy for "if there's anything to look at, look at it" conditionals.
			if (cursor != null && cursor.moveToFirst())
			{
				int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
				// If the size is unknown, the value stored is null.
				// But since an int can't be null in Java, the behavior is implementation-specific, which is just a fancy term for "unpredictable".
				// So as a rule, check if it's null before assigning to an int.
				// This will happen often: The storage API allows for remote files, whose size might not be locally known.
				if (!cursor.isNull(sizeIndex))
				{
					// Technically the column stores an int, but cursor.getString() will do the conversion automatically.
					return cursor.getString(sizeIndex);
				}
				else
				{
					return "N/A";
				}
			}
		}
		return "Error";
	}

	@Nullable
	public static String queryName(@NonNull final Uri uri, @NonNull final ContentResolver resolver)
	{
		// The query, since it only applies to a single document, will only return one row. There's no need to filter, sort, or select fields, since we want all fields for one document.
		try (Cursor cursor = resolver.query(uri, null, null, null, null, null))
		{
			// moveToFirst() returns false if the cursor has 0 rows.  Very handy for "if there's anything to look at, look at it" conditionals.
			if (cursor != null && cursor.moveToFirst())
			{
				// Note it's called "Display Name".  This is provider-specific, and might not necessarily be the file name.
				int displayNameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
				return cursor.getString(displayNameIdx);
			}
		}
		return "Error";
	}

	@Nullable
	public static String getType(@NonNull final Uri uri, @NonNull final ContentResolver resolver)
	{
		return resolver.getType(uri);
	}

	// F I L E   D E S C R I P T O R

	@Nullable
	public static <R> R applyFileDescriptor(@NonNull final Uri uri, @NonNull final ContentResolver resolver, @NonNull final Function<FileDescriptor, R> f) throws IOException
	{
		try (ParcelFileDescriptor parcelFileDescriptor = resolver.openFileDescriptor(uri, "r"))
		{
			return f.apply(parcelFileDescriptor.getFileDescriptor());
		}
	}

	// I N P U T S T R E A M

	@Nullable
	public static <R> R applyInputStream(@NonNull Uri uri, final ContentResolver resolver, @NonNull final Function<InputStream, R> f) throws IOException
	{
		try (InputStream is = resolver.openInputStream(uri))
		{
			return f.apply(is);
		}
	}
}
