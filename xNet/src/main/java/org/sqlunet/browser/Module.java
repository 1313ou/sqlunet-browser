package org.sqlunet.browser;

import org.sqlunet.treeview.model.TreeNode;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.os.Parcelable;

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
	 * Android fragment
	 */
	private final Fragment fragment;

	/**
	 * Constructor
	 * 
	 * @param fragment0
	 *        fragment
	 */
	protected Module(final Fragment fragment0)
	{
		this.fragment = fragment0;
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
	 * @param arguments
	 *        parceled arguments
	 */
	public void init(final Parcelable arguments)
	{
		//
	}

	/**
	 * Load and process data
	 * 
	 * @param node
	 *        tree node to attach results to
	 */
	public abstract void process(final TreeNode node);
}