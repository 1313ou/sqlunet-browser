package org.sqlunet.browser.config;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.sqlunet.browser.R;
import org.sqlunet.settings.Storage;
import org.sqlunet.settings.StorageStyle;

/**
 * Storage activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class StorageFragment extends Fragment
{
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_storage, container, false);
	}

	@Override
	public void onResume()
	{
		super.onResume();

		// view
		final View view = getView();

		// db
		final TextView db = (TextView) view.findViewById(R.id.database);
		db.setText(Storage.getSqlUNetStorage(getActivity()).getAbsolutePath());

		// storage
		final TextView storage = (TextView) view.findViewById(R.id.storage);
		storage.setText(StorageStyle.reportStyledCandidateStorage(getActivity()));

		// storage devices
		final TextView storageDevices = (TextView) view.findViewById(R.id.storage_devices);
		storageDevices.setText(StorageStyle.reportExternalStorage(getActivity()));
	}
}
