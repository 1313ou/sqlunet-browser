package org.sqlunet.treeview.control;

import android.content.Context;
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
public class IconLeafController extends Controller<Value>
{
	/// private static final String TAG = "IconLeafController";

	/**
	 * Resource used (changed by derived classes)
	 */
	int layoutRes = R.layout.layout_leaf;

	/**
	 * Constructor
	 *
	 * @param context context
	 */
	public IconLeafController(Context context)
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
		final ImageView iconView = (ImageView) view.findViewById(R.id.icon);
		iconView.setImageResource(value.icon);

		// value (label)
		TextView valueView = (TextView) view.findViewById(R.id.node_value);
		valueView.setText(value.text);

		// listener
		view.findViewById(R.id.node_more).setOnClickListener(new View.OnClickListener()
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
