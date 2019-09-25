/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.text.SpannableStringBuilder;

import org.sqlunet.browser.common.R;
import org.sqlunet.style.Factories;
import org.sqlunet.style.Spanner;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Providers
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class Providers
{
	/**
	 * Providers
	 *
	 * @param activity activity
	 */
	static public void listProviders(@NonNull final AppCompatActivity activity)
	{
		final SpannableStringBuilder sb = new SpannableStringBuilder();
		Spanner.append(sb, activity.getString(R.string.providers), 0, Factories.boldFactory);
		sb.append('\n').append('\n');
		final PackageManager manager = activity.getApplicationContext().getPackageManager();
		final String packageName = activity.getApplicationContext().getPackageName();
		final PackageInfo pack;
		try
		{
			pack = manager.getPackageInfo(packageName, PackageManager.GET_PROVIDERS);
			if (pack.providers != null)
			{
				for (ProviderInfo provider : pack.providers)
				{
					if (provider.name.contains("android"))
					{
						continue;
					}
					try
					{
						sb //
								.append(provider.name) //;
								.append('\n');
						Spanner.append(sb, activity.getString(R.string.provider_authority), 0, Factories.boldFactory) //
								.append(':') //
								.append(' ') //
								.append(provider.authority) //;
								.append('\n')  //
								.append('\n')  //
						;
					}
					catch (IOException e)
					{
						//
					}
				}
			}
		}
		catch (PackageManager.NameNotFoundException e)
		{
			//
		}

		// suggestion (this activity may not be searchable)
		final SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
		final SearchableInfo info = searchManager.getSearchableInfo(activity.getComponentName());
		if (info != null)
		{
			final String suggestAuthority = info.getSuggestAuthority();
			final String suggestPath = info.getSuggestPath();
			final String suggestPack = info.getSuggestPackage();

			// message
			try
			{
				Spanner.append(sb, activity.getString(R.string.suggestions), 0, Factories.boldFactory) //
						.append('\n')//
						.append('\n');
				Spanner.append(sb, activity.getString(R.string.suggestion_provider_pack), 0, Factories.boldFactory) //
						.append(' ') //
						.append(suggestPack) //
						.append('\n');
				Spanner.append(sb, activity.getString(R.string.suggestion_provider_authority), 0, Factories.boldFactory) //
						.append(' ') //
						.append(suggestAuthority) //
						.append('\n');
				Spanner.append(sb, activity.getString(R.string.suggestion_provider_path), 0, Factories.boldFactory) //
						.append(' ') //
						.append(suggestPath) //
				;
			}
			catch (IOException e)
			{
				//
			}
		}

		// dialog
		new AlertDialog.Builder(activity) //
				.setTitle(R.string.action_provider_info) //
				.setMessage(sb) //
				.setNegativeButton(R.string.action_dismiss, (dialog, whichButton) -> { /* canceled */ }) //
				.show();
	}
}
