package org.sqlunet.treeview.control;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.sqlunet.treeview.R;
import org.sqlunet.treeview.model.TreeNode;

/**
 * Link leaf controller
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class LinkNodeController extends Controller<Value>
{
	// static private final String TAG = "LinkNodeController";

	/**
	 * Constructor
	 *
	 * @param context context
	 */
	public LinkNodeController(final Context context)
	{
		super(context);
	}

	@Override
	public View createNodeView(final TreeNode node, final Value value)
	{
		final LayoutInflater inflater = LayoutInflater.from(this.context);
		@SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.layout_node_link, null, false);

		// icon
		final ImageView iconView = (ImageView) view.findViewById(R.id.node_icon);
		iconView.setImageResource(value.icon);

		// text
		TextView valueView = (TextView) view.findViewById(R.id.node_value);
		valueView.setText(value.text);

		// link listener
		final View hotLink = view.findViewById(R.id.node_link);
		if (hotLink != null)
		{
			hotLink.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					followLink();
				}
			});
		}

		return view;
	}

	/**
	 * Follow link
	 */
	private void followLink()
	{
		final Value value = (Value) this.node.getValue();
		final Link link = (Link) value.payload[0];
		link.process();
	}
}
