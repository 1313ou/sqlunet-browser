package org.sqlunet.treeview.control;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.sqlunet.treeview.R;
import org.sqlunet.treeview.model.TreeNode;

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
	private ImageView junctionView;

	/**
	 * Resource used (changed by derived classes)
	 */
	int layoutRes = R.layout.layout_tree;

	/**
	 * Constructor
	 *
	 * @param context context
	 */
	public TreeController(Context context)
	{
		super(context);
	}

	@Nullable
	@Override
	protected View createNodeView(final TreeNode node, @NonNull final Value value)
	{
		final LayoutInflater inflater = LayoutInflater.from(this.context);
		@SuppressLint("InflateParams") final View view = inflater.inflate(this.layoutRes, null, false);

		// junction icon (arrow)
		this.junctionView = view.findViewById(R.id.junction_icon);

		// icon
		final ImageView iconView = view.findViewById(R.id.node_icon);
		iconView.setImageResource(value.icon);

		// text
		TextView valueView = view.findViewById(R.id.node_value);
		valueView.setText(value.text);

		return view;
	}

	@Override
	public void onExpandEvent(boolean expand)
	{
		this.junctionView.setImageResource(this.node.isEnabled() ? (expand ? R.drawable.ic_expanded : R.drawable.ic_collapsed) : R.drawable.ic_leaf);
	}

	@Override
	public void disable()
	{
		this.junctionView.setImageResource(R.drawable.ic_leaf);
	}
}
