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
 *
 * @author Bogdan Melnychuk on 2/10/15.
 */
@SuppressWarnings("unused")
public class TreeNodeWrapperView extends LinearLayout
{
	/**
	 * Container style
	 */
	private final int containerStyle;
	/**
	 * Node container
	 */
	private ViewGroup nodeContainer;

	/**
	 * Constructor
	 *
	 * @param context context
	 */
	public TreeNodeWrapperView(Context context)
	{
		this(context, -1, null);
	}

	/**
	 * Constructor
	 *
	 * @param context        context
	 * @param containerStyle container style
	 */
	public TreeNodeWrapperView(Context context, int containerStyle)
	{
		this(context, containerStyle, null);
	}

	/**
	 * Constructor
	 *
	 * @param context        context
	 * @param containerStyle container style
	 * @param nodeContainer  node container
	 */
	public TreeNodeWrapperView(Context context, int containerStyle, ViewGroup nodeContainer)
	{
		super(context);
		this.containerStyle = containerStyle;
		this.nodeContainer = nodeContainer;
		init();
	}

	/**
	 * Constructor
	 *
	 * @param context        context
	 * @param attrs          attributes
	 * @param containerStyle container style
	 * @param nodeContainer  node container
	 */
	public TreeNodeWrapperView(Context context, AttributeSet attrs, int containerStyle, ViewGroup nodeContainer)
	{
		super(context, attrs);
		this.containerStyle = containerStyle;
		this.nodeContainer = nodeContainer;
		init();
	}

	/**
	 * Constructor
	 *
	 * @param context        context
	 * @param attrs          attributes
	 * @param defStyleAttr   def style attribute
	 * @param containerStyle container style
	 * @param nodeContainer  node container
	 */
	public TreeNodeWrapperView(Context context, AttributeSet attrs, int defStyleAttr, int containerStyle, ViewGroup nodeContainer)
	{
		super(context, attrs, defStyleAttr);
		this.containerStyle = containerStyle;
		this.nodeContainer = nodeContainer;
		init();
	}

	/**
	 * Constructor
	 *
	 * @param context        context
	 * @param attrs          attributes
	 * @param defStyleAttr   def style attribute
	 * @param defStyleRes    def style resource
	 * @param containerStyle container style
	 * @param nodeContainer  node container
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public TreeNodeWrapperView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int containerStyle, ViewGroup nodeContainer)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		this.containerStyle = containerStyle;
		this.nodeContainer = nodeContainer;
		init();
	}

	/**
	 * Init
	 */
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

	/**
	 * Insert node view
	 *
	 * @param nodeView node view
	 */
	public void insertNodeView(final View nodeView)
	{
		this.nodeContainer.addView(nodeView);
	}
}
