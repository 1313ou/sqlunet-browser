/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.sqlunet.browser.common.R;
import org.sqlunet.provider.BaseProvider;
import org.sqlunet.sql.SqlFormatter;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Sql fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SqlFragment extends Fragment
{
	static public final String TAG = "SqlF";

	static public final String FRAGMENT_TAG = "sql";

	private ActivityResultLauncher<String> exportLauncher;

	public SqlFragment()
	{
	}

	@Override
	public void onCreate(@Nullable final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// launchers
		this.exportLauncher = registerExportLauncher();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_sql, container, false);
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// swipe refresh
		final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
		swipeRefreshLayout.setOnRefreshListener(() -> {

			if (!isAdded())
			{
				return;
			}
			Fragment fragment = getChildFragmentManager().findFragmentByTag(FRAGMENT_TAG);
			if (fragment instanceof SqlStatementsFragment)
			{
				SqlStatementsFragment sqlStatementsFragment = (SqlStatementsFragment) fragment;
				sqlStatementsFragment.update();
			}
			// stop the refreshing indicator
			swipeRefreshLayout.setRefreshing(false);
		});

		// sub fragment
		if (savedInstanceState == null)
		{
			// splash fragment
			final Fragment fragment = new SqlStatementsFragment();
			assert isAdded();
			getChildFragmentManager() //
					.beginTransaction() //
					.setReorderingAllowed(true) //
					.replace(R.id.container_sql_statements, fragment, FRAGMENT_TAG) //
					// .addToBackStack(FRAGMENT_TAG) //
					.commit();
		}

		// menu
		final MenuHost menuHost = requireActivity();
		menuHost.addMenuProvider(new MenuProvider()
		{
			@Override
			public void onCreateMenu(@NonNull final Menu menu, @NonNull final MenuInflater menuInflater)
			{
				// inflate
				menu.clear();
				menuInflater.inflate(R.menu.main, menu);
				menuInflater.inflate(R.menu.sql, menu);
				// MenuCompat.setGroupDividerEnabled(menu, true);
			}

			@Override
			public boolean onMenuItemSelected(@NonNull final MenuItem menuItem)
			{
				int itemId = menuItem.getItemId();
				if (itemId == R.id.action_copy)
				{
					final CharSequence sqls = stylizedSqls();

					final ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
					final ClipData clip = ClipData.newPlainText("SQL", sqls);
					assert clipboard != null;
					clipboard.setPrimaryClip(clip);
					return true;
				}
				else if (itemId == R.id.action_sql_export)
				{
					export();
					return true;
				}
				else if (itemId == R.id.action_sql_send)
				{
					SqlFragment.send(requireContext());
					return true;
				}
				else if (itemId == R.id.action_sql_clear)
				{
					BaseProvider.sqlBuffer.clear();
					return true;
				}
				else
				{
					return MenuHandler.menuDispatch((AppCompatActivity) requireActivity(), menuItem);
				}
			}

		}, this.getViewLifecycleOwner(), Lifecycle.State.RESUMED);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		this.exportLauncher.unregister();
	}

	// S E N D

	public static void send(@NonNull final Context context)
	{
		final CharSequence sqls = stylizedSqls();
		Sender.send(context, "Semantikos SQL", sqls);
	}

	// I M P O R T / E X P O R T

	/**
	 * Export/import text file
	 */
	private static final String SQL_FILE = "semantikos.sql";

	private static final String MIME_TYPE = "text/plain";

	/**
	 * Export history
	 */
	@NonNull
	private ActivityResultLauncher<String> registerExportLauncher()
	{
		final ActivityResultContract<String, Uri> createContract = new ActivityResultContracts.CreateDocument(MIME_TYPE)
		{
			@NonNull
			@Override
			public Intent createIntent(@NonNull final Context context, @NonNull final String input)
			{
				final Intent intent = super.createIntent(context, input);
				intent.putExtra(Intent.EXTRA_TITLE, SQL_FILE);
				//intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
				return intent;
			}
		};

		return registerForActivityResult(createContract, uri -> {

			// The result data contains a URI for the document or directory that the user selected.
			if (uri != null)
			{
				doExportSql(requireContext(), uri);
			}
		});
	}

	/**
	 * Export history
	 */
	public void export()
	{
		this.exportLauncher.launch(MIME_TYPE);
	}

	/**
	 * Export Sql
	 */
	private static void doExportSql(@NonNull final Context context, @NonNull final Uri uri)
	{
		Log.d(TAG, "Exporting to " + uri);
		try ( //
		      final ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "w") //
		)
		{
			assert pfd != null;
			try (final OutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());//
			     final Writer writer = new OutputStreamWriter(fileOutputStream);//
			     final BufferedWriter bw = new BufferedWriter(writer))
			{
				String sqls = textSqls();
				bw.write(sqls);

				Log.i(TAG, "Exported to " + uri);
				Toast.makeText(context, context.getResources().getText(R.string.title_history_export) + " " + uri, Toast.LENGTH_SHORT).show();
			}
		}
		catch (@NonNull final IOException e)
		{
			Log.e(TAG, "While writing", e);
			Toast.makeText(context, context.getResources().getText(R.string.error_export) + " " + uri, Toast.LENGTH_SHORT).show();
		}
	}

	// F A C T O R Y

	@NonNull
	private static String textSqls()
	{
		final StringBuilder sb = new StringBuilder();
		final CharSequence[] sqls = BaseProvider.sqlBuffer.reverseItems();
		for (CharSequence sql : sqls)
		{
			sb.append(sql);
			sb.append(";\n\n");
		}
		return sb.toString();
	}

	@NonNull
	private static CharSequence stylizedSqls()
	{
		final SpannableStringBuilder sb = new SpannableStringBuilder();
		final CharSequence[] sqls = BaseProvider.sqlBuffer.reverseItems();
		for (CharSequence sql : sqls)
		{
			sb.append(SqlFormatter.styledFormat(sql));
			sb.append(";\n\n");
		}
		return sb;
	}
}
