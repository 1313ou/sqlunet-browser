package org.sqlunet.browser;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcelable;
import android.util.Log;

import org.sqlunet.treeview.model.TreeNode;

import java.util.AbstractMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MediatorLiveData;

/**
 * Abstract module to perform queries
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public abstract class Module
{
	static private final String TAG = "Module";

	/**
	 * Loader id allocator
	 */
	static public int loaderId = 0;

	/**
	 * Fragment
	 */
	@NonNull
	protected final Fragment fragment;

	/**
	 * Context
	 */
	@Nullable
	protected final Context context;

	/**
	 * MediatorLiveData
	 */
	@NonNull
	protected final MediatorLiveData<AbstractMap.SimpleEntry<String, Cursor>> mediatorLiveData;

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
		this.context = fragment.getContext();
		this.mediatorLiveData = new MediatorLiveData();
		this.mediatorLiveData.observe(this.fragment, entry -> {

			final String key = entry.getKey();
			Log.d(TAG, key);
			if (key == null)
			{
				return;
			}
			final Cursor cursor = entry.getValue();
			Log.d(TAG, cursor.toString());
		});
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