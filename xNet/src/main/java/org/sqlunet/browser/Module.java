package org.sqlunet.browser;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.os.Parcelable;

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
	private final Fragment fragment;

	/**
	 * Action
	 */
	protected int action;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	protected Module(final Fragment fragment)
	{
		this.fragment = fragment;
	}

	/**
	 * Get context
	 *
	 * @return context
	 */
	protected Context getContext()
	{
		return this.fragment.getActivity();
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
	 * @param action  action
	 * @param pointer parceled pointer
	 */
	public void init(final int action, final Parcelable pointer)
	{
		this.action = action;
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