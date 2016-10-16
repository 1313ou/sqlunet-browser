package org.sqlunet.treeview.renderer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.sqlunet.treeview.R;
import org.sqlunet.treeview.model.TreeNode;

/**
 * Leaf renderer with icon
 * <p>
 * Created by Bogdan Melnychuk on 2/12/15.
 */
public class IconLeafRenderer extends TreeNode.Renderer<IconTreeItem>
{
	/// private static final String TAG = "IconLeafRenderer";

	int layoutRes = R.layout.layout_leaf;

	public IconLeafRenderer(Context context)
	{
		super(context);
	}

	/* (non-Javadoc)
	 * @see org.sqlunet.treeview.model.TreeNode.Renderer#createNodeView(org.sqlunet.treeview.model.TreeNode, java.lang.Object)
	 */
	@Override
	public View createNodeView(final TreeNode node, final IconTreeItem value)
	{
		final LayoutInflater inflater = LayoutInflater.from(this.context);
		final View view = inflater.inflate(layoutRes, null, false);

		// junction
		// final ImageView junctionView = (ImageView) view.findViewById(R.id.junction_icon);

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
				//
			}
		});

		return view;
	}
}
