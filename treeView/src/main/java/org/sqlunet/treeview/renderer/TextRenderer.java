package org.sqlunet.treeview.renderer;

import org.sqlunet.treeview.model.TreeNode;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

/**
 * Text renderer
 *
 * Created by Bogdan Melnychuk on 2/11/15.
 */
public class TextRenderer extends TreeNode.Renderer<Object>
{
	public TextRenderer(Context context)
	{
		super(context);
	}

	@Override
	public View createNodeView(TreeNode node, Object value)
	{
		final TextView tv = new TextView(this.context);
		tv.setText((CharSequence) value);
		return tv;
	}
}
