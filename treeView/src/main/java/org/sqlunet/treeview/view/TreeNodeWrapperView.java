package org.sqlunet.treeview.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.sqlunet.treeview.R;

/**
 * Tree node wrapper view
 * <p>
 * Created by Bogdan Melnychuk on 2/10/15.
 */
@SuppressWarnings("unused")
public class TreeNodeWrapperView extends LinearLayout
{
	private ViewGroup nodeContainer;

	private final int containerStyle;

	public TreeNodeWrapperView(Context context)
	{
		this(context, -1, null);
	}

	public TreeNodeWrapperView(Context context, int containerStyle)
	{
		this(context, containerStyle, null);
	}

	public TreeNodeWrapperView(Context context, int containerStyle, ViewGroup nodeContainer)
	{
		super(context);
		this.containerStyle = containerStyle;
		this.nodeContainer = nodeContainer;
		init();
	}

	public TreeNodeWrapperView(Context context, AttributeSet attrs, int containerStyle, ViewGroup nodeContainer)
	{
		super(context, attrs);
		this.containerStyle = containerStyle;
		this.nodeContainer = nodeContainer;
		init();
	}

	public TreeNodeWrapperView(Context context, AttributeSet attrs, int defStyleAttr, int containerStyle, ViewGroup nodeContainer)
	{
		super(context, attrs, defStyleAttr);
		this.containerStyle = containerStyle;
		this.nodeContainer = nodeContainer;
		init();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public TreeNodeWrapperView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int containerStyle, ViewGroup nodeContainer)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		this.containerStyle = containerStyle;
		this.nodeContainer = nodeContainer;
		init();
	}

	private void init()
	{
		setOrientation(LinearLayout.VERTICAL);

		// node container for node header
		this.nodeContainer = new RelativeLayout(getContext());
		this.nodeContainer.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		this.nodeContainer.setId(R.id.node_header);

		// node container for children
		ContextThemeWrapper newContext = new ContextThemeWrapper(getContext(), this.containerStyle);
		LinearLayout nodeItemsContainer = new LinearLayout(newContext, null, this.containerStyle);
		nodeItemsContainer.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		nodeItemsContainer.setId(R.id.node_items);
		nodeItemsContainer.setOrientation(LinearLayout.VERTICAL);
		nodeItemsContainer.setVisibility(View.GONE);

		addView(this.nodeContainer);
		addView(nodeItemsContainer);
	}

	public void insertNodeView(View nodeView)
	{
		this.nodeContainer.addView(nodeView);
	}
}
