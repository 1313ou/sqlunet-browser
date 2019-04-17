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
public class TextController extends Controller<Object>
{
	/**
	 * Constructor
	 */
	public TextController()
	{
		super();
	}

	@NonNull
	@Override
	public View createNodeView(@NonNull final Context context, final TreeNode node, final Object value)
	{
		final TextView textView = new TextView(context);
		textView.setText((CharSequence) value);
		return textView;
	}
}
