package org.sqlunet.treeview.control;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import org.sqlunet.treeview.model.TreeNode;

/**
 * Text controller
 *
 * @author Bogdan Melnychuk on 2/11/15.
 */
public class IconTextController extends Controller<Object>
{
	/**
	 * Constructor
	 *
	 */
	public IconTextController()
	{
		super();
	}

	@NonNull
	@Override
	public View createNodeView(@NonNull final Context context, final TreeNode node, final Object value)
	{
		final Value data = (Value) value;
		final TextView textView = new TextView(context);
		textView.setText(data.text);
		textView.setCompoundDrawablePadding(10);
		textView.setCompoundDrawablesWithIntrinsicBounds(data.icon, 0, 0, 0);
		return textView;
	}
}
