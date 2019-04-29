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
	 * Node label view
	 */
	public ViewGroup nodeView;

	/**
	 * Node container
	 */
	public ViewGroup childrenContainer;

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
	 * @param nodeView       node view (group)
	 */
	public SubtreeView(final Context context, final int containerStyle, final ViewGroup nodeView)
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
	public SubtreeView(final Context context, final AttributeSet attrs, final int containerStyle, final ViewGroup nodeView)
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
	public SubtreeView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int containerStyle, final ViewGroup nodeView)
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
	public SubtreeView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes, final int containerStyle, final ViewGroup nodeView)
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

		// node view
		this.nodeView = new RelativeLayout(context);
		this.nodeView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		this.nodeView.setId(R.id.node_label);

		// node container for children
		ContextThemeWrapper containerContext = new ContextThemeWrapper(context, this.containerStyle);
		final LinearLayout nodeChildrenContainer = new LinearLayout(containerContext, null, this.containerStyle);
		nodeChildrenContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		nodeChildrenContainer.setId(R.id.node_children);
		nodeChildrenContainer.setOrientation(LinearLayout.VERTICAL);
		nodeChildrenContainer.setVisibility(View.GONE);
		this.childrenContainer = nodeChildrenContainer;

		addView(this.nodeView);
		addView(this.childrenContainer);
	}

	/**
	 * Insert node view
	 *
	 * @param nodeView node view
	 */
	public void insertNodeView(final View nodeView)
	{
		this.nodeView.addView(nodeView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		// this.nodeView.addView(nodeView);
	}
}
