package org.sqlunet.treeview.control;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.sqlunet.treeview.model.TreeNode;

/**
 * Simple controller
 *
 * @author Bogdan Melnychuk on 2/11/15.
 */
public class SimpleController extends Controller<Object>
{
	/**
	 * Constructor
	 *
	 * @param context context
	 */
	public SimpleController(Context context)
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
