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
public class TreeController extends Controller<CompositeValue>
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
	public View createNodeView(@NonNull final Context context, final TreeNode node, @NonNull final CompositeValue value)
	{
		final LayoutInflater inflater = LayoutInflater.from(context);
		@SuppressLint("InflateParams") final View view = inflater.inflate(this.layoutRes, null, false);

		// junction icon (arrow)
		this.junctionView = view.findViewById(R.id.junction_icon);
		if (node.isDeadend())
		{
			markDeadend();
		}
		else
		{
			markCollapsed(); // this.junctionView.setImageResource(R.drawable.ic_collapsed);
		}

		// icon
		final CompositeValue composite = (CompositeValue) value;
		if (composite.icon != 0)
		{
			final ImageView iconView = view.findViewById(R.id.node_icon);
			iconView.setImageResource(composite.icon);
		}

		// text
		TextView valueView = view.findViewById(R.id.node_value);
		valueView.setText(composite.text);

		return view;
	}

	@Override
	public void onExpandEvent()
	{
		if (this.node.isDeadend())
		{
			markDeadend();
		}
		else
		{
			markExpanded();
		}
	}

	@Override
	public void onCollapseEvent()
	{
		if (this.node.isDeadend())
		{
			markDeadend();
		}
		else
		{
			markCollapsed();
		}
	}

	@Override
	public void deadend()
	{
		markDeadend();
	}

	protected void markExpanded()
	{
		this.junctionView.setImageResource(R.drawable.ic_expanded);
	}

	protected void markCollapsed()
	{
		this.junctionView.setImageResource(R.drawable.ic_collapsed);
	}

	protected void markDeadend()
	{
		this.junctionView.setImageResource(R.drawable.ic_deadend);
	}
}
