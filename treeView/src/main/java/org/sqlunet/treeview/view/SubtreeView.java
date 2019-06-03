/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.treeview.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.sqlunet.treeview.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * SubtreeView (Tree node wrapper view)
 * -container for label
 * -container for node's children
 *
 * @author Bogdan Melnychuk on 2/10/15.
 */
//noinspection ViewConstructor
public class SubtreeView extends LinearLayout
{
	/**
	 * Container style
	 */
	private final int containerStyle;

	/**
	 * Node label view
	 */
	@Nullable
	public final View nodeView;

	/**
	 * Node container
	 */
	@Nullable
	public ViewGroup childrenContainer;

	/**
	 * Constructor
	 *
	 * @param context context
	 */
	public SubtreeView(final Context context)
	{
		this(context, -1, new View(context));
	}

	/**
	 * Constructor
	 *
	 * @param context        context
	 * @param containerStyle container style
	 */
	public SubtreeView(final Context context, final int containerStyle)
	{
		this(context, containerStyle, new View(context));
	}

	/**
	 * Constructor
	 *
	 * @param context        context
	 * @param containerStyle container style
	 * @param nodeView       node view (group)
	 */
	public SubtreeView(final Context context, final int containerStyle, @NonNull final View nodeView)
	{
		super(context);
		this.containerStyle = containerStyle;
		this.nodeView = nodeView;
		init(context);
	}

	/**
	 * Constructor
	 *
	 * @param context        context
	 * @param attrs          attributes
	 * @param containerStyle container style
	 * @param nodeView       node view (group)
	 */
	public SubtreeView(final Context context, final AttributeSet attrs, final int containerStyle, @NonNull final View nodeView)
	{
		super(context, attrs);
		this.containerStyle = containerStyle;
		this.nodeView = nodeView;
		init(context);
	}

	/**
	 * Constructor
	 *
	 * @param context        context
	 * @param attrs          attributes
	 * @param defStyleAttr   def style attribute
	 * @param containerStyle container style
	 * @param nodeView       node view (group)
	 */
	public SubtreeView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int containerStyle, @NonNull final View nodeView)
	{
		super(context, attrs, defStyleAttr);
		this.containerStyle = containerStyle;
		this.nodeView = nodeView;
		init(context);
	}

	/**
	 * Constructor
	 *
	 * @param context        context
	 * @param attrs          attributes
	 * @param defStyleAttr   def style attribute
	 * @param defStyleRes    def style resource
	 * @param containerStyle container style
	 * @param nodeView       node view (group)
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public SubtreeView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes, final int containerStyle, @NonNull final View nodeView)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		this.containerStyle = containerStyle;
		this.nodeView = nodeView;
		init(context);
	}

	/**
	 * Init
	 */
	private void init(final Context context)
	{
		setOrientation(LinearLayout.VERTICAL);
		setFocusable(true);
		//setFocusable(View.FOCUSABLE);

		// node view
		if (this.nodeView != null)
		{
			insertNodeView(this.nodeView);
		}

		// node container for children
		ContextThemeWrapper containerContext = new ContextThemeWrapper(context, this.containerStyle);
		final LinearLayout nodeChildrenContainer = new LinearLayout(containerContext, null, this.containerStyle);
		nodeChildrenContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		nodeChildrenContainer.setId(R.id.node_children);
		nodeChildrenContainer.setOrientation(LinearLayout.VERTICAL);
		nodeChildrenContainer.setVisibility(View.GONE);
		this.childrenContainer = nodeChildrenContainer;

		addView(this.childrenContainer);
	}

	/**
	 * Insert node view
	 *
	 * @param nodeView node view
	 */
	public void insertNodeView(@NonNull final View nodeView)
	{
		nodeView.setId(R.id.node_label);
		addView(nodeView, 0, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	}

	/**
	 * Remove node view
	 *
	 * @param nodeView node view
	 */
	public void removeNodeView(final View nodeView)
	{
		removeView(nodeView);
	}

	@NonNull
	@Override
	public String toString()
	{
		return "subtreeview for " + getTag().toString();
	}
}
