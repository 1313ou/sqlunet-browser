package org.sqlunet.treeview.control;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.sqlunet.treeview.R;
import org.sqlunet.treeview.model.TreeNode;

/**
 * Leaf controller with icon
 *
 * @author Bogdan Melnychuk on 2/12/15.
 */
public class LeafController extends Controller<Value>
{
	/// static private final String TAG = "LeafController";

	/**
	 * Resource used (changed by derived classes)
	 */
	int layoutRes = R.layout.layout_leaf;

	/**
	 * Constructor
	 *
	 * @param context context
	 */
	public LeafController(Context context)
	{
		super(context);
	}

	@Nullable
	@Override
	protected View createNodeView(final TreeNode node, @NonNull final Value value)
	{
		final LayoutInflater inflater = LayoutInflater.from(this.context);
		final View view = inflater.inflate(this.layoutRes, null, false);

		// junction
		// final ImageView junctionView = (ImageView) view.findViewById(R.id.junction_icon);

		// icon
		final ImageView iconView = view.findViewById(R.id.node_icon);
		iconView.setImageResource(value.icon);

		// text
		TextView valueView = view.findViewById(R.id.node_value);
		valueView.setText(value.text);

		return view;
	}
}
