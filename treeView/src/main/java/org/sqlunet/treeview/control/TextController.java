package org.sqlunet.treeview.control;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import org.sqlunet.treeview.model.TreeNode;

/**
 * Text controller
 *
 * @author Bogdan Melnychuk on 2/11/15.
 */
public class TextController extends Controller<Object>
{
	/**
	 * Constructor
	 *
	 * @param context context
	 */
	public TextController(Context context)
	{
		super(context);
	}

	@NonNull
	@Override
	public View createNodeView(final TreeNode node, final Object value)
	{
		final TextView textView = new TextView(this.context);
		textView.setText((CharSequence) value);
		return textView;
	}
}
