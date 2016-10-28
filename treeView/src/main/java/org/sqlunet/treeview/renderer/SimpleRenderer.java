package org.sqlunet.treeview.renderer;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.sqlunet.treeview.model.TreeNode;

/**
 * Simple renderer
 *
 * @author Bogdan Melnychuk on 2/11/15.
 */
public class SimpleRenderer extends TreeNode.Renderer<Object>
{
	/**
	 * Constructor
	 *
	 * @param context context
	 */
	public SimpleRenderer(Context context)
	{
		super(context);
	}

	@Override
	public View createNodeView(TreeNode node, Object value)
	{
		final TextView textView = new TextView(this.context);
		textView.setText(String.valueOf(value));
		return textView;
	}
}
