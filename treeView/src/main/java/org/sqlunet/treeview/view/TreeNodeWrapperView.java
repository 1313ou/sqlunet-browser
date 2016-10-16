package org.sqlunet.treeview.view;

import org.sqlunet.treeview.R;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Tree node wrapper view
 *
 * Created by Bogdan Melnychuk on 2/10/15.
 */
public class TreeNodeWrapperView extends LinearLayout
{
	private ViewGroup nodeContainer;
	private final int containerStyle;

	public TreeNodeWrapperView(Context context, int containerStyle)
	{
		super(context);
		this.containerStyle = containerStyle;
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
