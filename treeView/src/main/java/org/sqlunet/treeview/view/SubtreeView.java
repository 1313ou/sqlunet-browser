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
 * SubtreeView (Tree node wrapper view)
 * -container for label
 * -container for node's children
 *
 * @author Bogdan Melnychuk on 2/10/15.
 */
@SuppressWarnings("unused")
public class SubtreeView extends LinearLayout
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
	public SubtreeView(final Context context)
	{
		this(context, -1, null);
	}

	/**
	 * Constructor
	 *
	 * @param context        context
	 * @param containerStyle container style
	 */
	public SubtreeView(final Context context, final int containerStyle)
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
	public SubtreeView(final Context context, final int containerStyle, final ViewGroup nodeContainer)
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
	public SubtreeView(final Context context, final AttributeSet attrs, final int containerStyle, final ViewGroup nodeContainer)
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
	public SubtreeView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int containerStyle, final ViewGroup nodeContainer)
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
	public SubtreeView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes, final int containerStyle, final ViewGroup nodeContainer)
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

		// node container for node label
		this.nodeContainer = new RelativeLayout(getContext());
		this.nodeContainer.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		this.nodeContainer.setId(R.id.node_label);

		// node container for children
		ContextThemeWrapper newContext = new ContextThemeWrapper(getContext(), this.containerStyle);
		LinearLayout nodeChildrenContainer = new LinearLayout(newContext, null, this.containerStyle);
		nodeChildrenContainer.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		nodeChildrenContainer.setId(R.id.node_children);
		nodeChildrenContainer.setOrientation(LinearLayout.VERTICAL);
		nodeChildrenContainer.setVisibility(View.GONE);

		addView(this.nodeContainer);
		addView(nodeChildrenContainer);
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
