package org.sqlunet.treeview.control;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.sqlunet.treeview.R;
import org.sqlunet.treeview.model.TreeNode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Tree controller with icon
 *
 * @author Bogdan Melnychuk on 2/12/15.
 */
public class TreeController extends Controller<Value>
{
	/// static private final String TAG = "TreeController";

	/**
	 * Junction view
	 */
	protected ImageView junctionView;

	/**
	 * Resource used (changed by derived classes)
	 */
	int layoutRes = R.layout.layout_tree;

	/**
	 * Constructor
	 */
	public TreeController()
	{
		super();
	}

	@Nullable
	@Override
	protected View createNodeView(@NonNull final Context context, final TreeNode node, @NonNull final Value value)
	{
		final LayoutInflater inflater = LayoutInflater.from(context);
		@SuppressLint("InflateParams") final View view = inflater.inflate(this.layoutRes, null, false);

		// junction icon (arrow)
		this.junctionView = view.findViewById(R.id.junction_icon);
		if (node.isZombie())
		{
			this.junctionView.setImageResource(R.drawable.ic_leaf_zombie);
		}
		else if (node.isDeadend())
		{
			this.junctionView.setImageResource(R.drawable.ic_leaf_deadend);
		}
		else
		{
			markCollapsed(); // this.junctionView.setImageResource(R.drawable.ic_collapsed);
		}

		// icon
		if(value.icon != 0)
		{
			final ImageView iconView = view.findViewById(R.id.node_icon);
			iconView.setImageResource(value.icon);
		}

		// text
		TextView valueView = view.findViewById(R.id.node_value);
		valueView.setText(value.text);

		return view;
	}

	@Override
	public void onExpandEvent()
	{
		markExpanded();
	}

	@Override
	public void onCollapseEvent()
	{
		markCollapsed();
	}

	@Override
	public void disable()
	{
		markDisabled();
	}

	protected void markExpanded()
	{
		this.junctionView.setImageResource(this.node.isEnabled() && !this.node.isDeadend() ? R.drawable.ic_expanded : R.drawable.ic_leaf);
	}

	protected void markCollapsed()
	{
		this.junctionView.setImageResource(this.node.isEnabled() && !this.node.isDeadend() ? R.drawable.ic_collapsed : R.drawable.ic_leaf);
	}

	protected void markDisabled()
	{
		this.junctionView.setImageResource(R.drawable.ic_leaf);
	}
}
