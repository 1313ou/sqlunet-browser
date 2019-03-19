package org.sqlunet.browser;

import android.content.Context;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;

import org.sqlunet.treeview.model.TreeNode;

/**
 * Abstract module to perform queries
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public abstract class Module
{
	/**
	 * Loader id allocator
	 */
	static public int loaderId = 0;

	/**
	 * Fragment
	 */
	@NonNull
	private final Fragment fragment;

	/**
	 * Context
	 */
	@Nullable
	protected final Context context;

	/**
	 * Type of query (expected result)
	 */
	@SuppressWarnings("unused")
	protected int type;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	protected Module(@NonNull final Fragment fragment)
	{
		this.fragment = fragment;
		this.context = fragment.getActivity();
	}

	/**
	 * Get loader manager
	 *
	 * @return loader manager
	 */
	protected LoaderManager getLoaderManager()
	{
		return this.fragment.getLoaderManager();
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