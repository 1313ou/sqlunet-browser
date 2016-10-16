package org.sqlunet.treeview.renderer;

import org.sqlunet.treeview.R;
import org.sqlunet.treeview.model.TreeNode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Tree renderer with icon
 *
 * Created by Bogdan Melnychuk on 2/12/15.
 */
public class IconTreeRenderer extends TreeNode.Renderer<IconTreeItem>
{
	/// private static final String TAG = "IconTreeRenderer";

	private ImageView junctionView;

	public IconTreeRenderer(Context context)
	{
		super(context);
	}

	@Override
	public View createNodeView(final TreeNode node, final IconTreeItem value)
	{
		final LayoutInflater inflater = LayoutInflater.from(this.context);
		final View view = inflater.inflate(R.layout.layout_tree, null, false);

		// arrow
		this.junctionView = (ImageView) view.findViewById(R.id.junction_icon);

		// icon
		final ImageView iconView = (ImageView) view.findViewById(R.id.icon);
		iconView.setImageResource(value.icon);

		// value (label)
		TextView valueView = (TextView) view.findViewById(R.id.node_value);
		valueView.setText(value.text);

		// listener
		view.findViewById(R.id.btn_more).setOnClickListener(new View.OnClickListener()
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
	public void toggle(boolean active)
	{
		this.junctionView.setImageResource(active ? R.drawable.ic_expanded : R.drawable.ic_collapsed);
	}
	
	@Override
	public void disable()
	{
		this.junctionView.setImageResource(R.drawable.ic_leaf);
	}
}
