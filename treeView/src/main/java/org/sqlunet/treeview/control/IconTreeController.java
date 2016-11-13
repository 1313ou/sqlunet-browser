package org.sqlunet.treeview.control;

import android.annotation.SuppressLint;
import android.content.Context;
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
public class IconTreeController extends Controller<Value>
{
	/// static private final String TAG = "IconTreeController";

	/**
	 * Junction view
	 */
	private ImageView junctionView;

	/**
	 * Constructor
	 *
	 * @param context context
	 */
	public IconTreeController(Context context)
	{
		super(context);
	}

	@Override
	public View createNodeView(final TreeNode node, final Value value)
	{
		final LayoutInflater inflater = LayoutInflater.from(this.context);
		@SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.layout_tree, null, false);

		// junction icon (arrow)
		this.junctionView = (ImageView) view.findViewById(R.id.junction_icon);

		// icon
		final ImageView iconView = (ImageView) view.findViewById(R.id.icon);
		iconView.setImageResource(value.icon);

		// value (label)
		TextView valueView = (TextView) view.findViewById(R.id.node_value);
		valueView.setText(value.text);

		// listener
		view.findViewById(R.id.node_more).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// extra button
			}
		});

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
