/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.os.Parcelable;

import org.sqlunet.treeview.model.TreeNode;

import java.util.Arrays;

import androidx.annotation.NonNull;

/**
 * Abstract module to perform queries
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public abstract class Module
{
	// static private final String TAG = "Module";

	public static class ContentProviderSql
	{
		public String providerUri;
		public String[] projection;
		public String selection;
		public String[] selectionArgs;
		public String sortBy;

		public ContentProviderSql()
		{
		}

		public ContentProviderSql(final String providerUri, final String[] projection, final String selection, final String[] selectionArgs, final String sortBy)
		{
			this.providerUri = providerUri;
			this.projection = projection;
			this.selection = selection;
			this.selectionArgs = selectionArgs;
			this.sortBy = sortBy;
		}

		@NonNull
		@Override
		public String toString()
		{
			return "ContentProviderSql{" + "providerUri='" + providerUri + '\'' + ", projection=" + Arrays.toString(projection) + ", selection='" + selection + '\'' + ", selectionArgs=" + Arrays.toString(selectionArgs) + ", sortBy='" + sortBy + '\'' + '}';
		}
	}

	/**
	 * Fragment
	 */
	@NonNull
	protected final TreeFragment fragment;

	/**
	 * Type of query (expected result)
	 */
	protected int type;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	protected Module(@NonNull final TreeFragment fragment)
	{
		this.fragment = fragment;
	}

	/**
	 * Init
	 *
	 * @param type    type
	 * @param pointer parceled pointer
	 */
	public void init(final int type, final Parcelable pointer)
	{
		this.type = type;
		unmarshal(pointer);
	}

	/**
	 * Unmarshal
	 *
	 * @param pointer pointer
	 */
	abstract protected void unmarshal(final Parcelable pointer);

	/**
	 * Load and process data
	 *
	 * @param node tree node to attach results to
	 */
	abstract public void process(final TreeNode node);
}