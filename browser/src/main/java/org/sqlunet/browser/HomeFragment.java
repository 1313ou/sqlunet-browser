package org.sqlunet.browser;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.config.ManageActivity;
import org.sqlunet.browser.config.SettingsActivity;
import org.sqlunet.browser.config.SetupActivity;
import org.sqlunet.browser.config.SetupSqlActivity;
import org.sqlunet.browser.config.StorageActivity;
import org.sqlunet.settings.Settings;

/**
 * Home fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class HomeFragment extends Fragment
{
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public HomeFragment()
	{
		//
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		setHasOptionsMenu(true);
		return inflater.inflate(R.layout.fragment_home, container, false);
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater)
	{
		inflater.inflate(R.menu.home, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		Intent intent;

		// handle item selection
		switch (item.getItemId())
		{
			// settings

			case R.id.action_settings:
				intent = new Intent(getActivity(), SettingsActivity.class); break;

			// database

			case R.id.action_storage:
				intent = new Intent(getActivity(), StorageActivity.class);
				break;

			case R.id.action_status:
				intent = new Intent(getActivity(), StatusActivity.class);
				break;

			case R.id.action_manage:
				intent = new Intent(getActivity(), ManageActivity.class);
				break;

			case R.id.action_setup:
				intent = new Intent(getActivity(), SetupActivity.class);
				break;

			case R.id.action_setup_sql:
				intent = new Intent(getActivity(), SetupSqlActivity.class);
				break;

			// guide

			case R.id.action_help:
				intent = new Intent(getActivity(), HelpActivity.class);
				break;

			case R.id.action_about:
				intent = new Intent(getActivity(), AboutActivity.class);
				break;

			// lifecycle

			case R.id.action_quit:
				getActivity().finish();
				return true;

			case R.id.action_appsettings:
				Settings.applicationSettings(getActivity(), "org.sqlunet.browser");
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}

		// start activity
		startActivity(intent);
		return true;
	}
}

