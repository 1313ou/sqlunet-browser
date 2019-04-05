package org.sqlunet.browser;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Simple factory, which calls empty constructor on the give class.
 */
public class SqlunetViewModelFactory extends ViewModelProvider.AndroidViewModelFactory
{
	private Uri uri;

	private String[] projection;

	private String selection;

	private String[] selectionArgs;

	private String sortOrder;

	public SqlunetViewModelFactory(final Fragment fragment, final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder)
	{
		super(fragment.getActivity().getApplication());
		this.uri = uri;
		this.projection = projection;
		this.selection = selection;
		this.selectionArgs = selectionArgs;
		this.sortOrder = sortOrder;
	}

	@SuppressWarnings("ClassNewInstance")
	@NonNull
	@Override
	public <T extends ViewModel> T create(@NonNull Class<T> modelClass)
	{
		T model = super.create(modelClass);
		if (SqlunetViewModel.class.equals(modelClass))
		{
			SqlunetViewModel sqlunetModel = (SqlunetViewModel) model;
			sqlunetModel.set(this.uri, this.projection, this.selection, this.selectionArgs, this.sortOrder);
		}
		return model;
	}
}

