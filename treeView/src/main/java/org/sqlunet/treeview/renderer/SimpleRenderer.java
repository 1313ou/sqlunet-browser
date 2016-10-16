package org.sqlunet.treeview.renderer;

import org.sqlunet.treeview.model.TreeNode;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

/**
 * Simple renderer
 *
 * Created by Bogdan Melnychuk on 2/11/15.
 */
public class SimpleRenderer extends TreeNode.Renderer<Object>
{
	public SimpleRenderer(Context context)
	{
		super(context);
	}

	@Override
	public View createNodeView(TreeNode node, Object value)
	{
		final TextView tv = new TextView(this.context);
		tv.setText(String.valueOf(value));
		return tv;
	}
}
