package org.sqlunet.treeview.control;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.sqlunet.treeview.R;
import org.sqlunet.treeview.model.TreeNode;

/**
 * More controller with icon
 *
 * @author Bogdan Melnychuk on 2/12/15.
 */
public class MoreController extends Controller<Value>
{
	/// static private final String TAG = "LeafController";

	/**
	 * Resource used (changed by derived classes)
	 */
	@SuppressWarnings("WeakerAccess")
	protected int layoutRes = R.layout.layout_more;

	/**
	 * Constructor
	 *
	 * @param context context
	 */
	public MoreController(Context context)
	{
		super(context);
	}

	@Override
	public View createNodeView(final TreeNode node, final Value value)
	{
		final LayoutInflater inflater = LayoutInflater.from(this.context);
		final View view = inflater.inflate(this.layoutRes, null, false);

		// junction
		// final ImageView junctionView = (ImageView) view.findViewById(R.id.junction_icon);

		// icon
		final ImageView iconView = (ImageView) view.findViewById(R.id.node_icon);
		iconView.setImageResource(value.icon);

		// text
		TextView valueView = (TextView) view.findViewById(R.id.node_value);
		valueView.setText(value.text);

		return view;
	}
}
