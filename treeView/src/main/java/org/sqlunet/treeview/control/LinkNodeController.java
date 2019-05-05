package org.sqlunet.treeview.control;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
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
public class LinkNodeController extends Controller<CompositeValue>
{
	// static private final String TAG = "LinkNodeController";

	/**
	 * Constructor
	 */
	public LinkNodeController()
	{
		super();
	}

	@Override
	public View createNodeView(@NonNull final Context context, final TreeNode node, @NonNull final CompositeValue value)
	{
		final LayoutInflater inflater = LayoutInflater.from(context);
		@SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.layout_node_link, null, false);

		// icon
		final ImageView iconView = view.findViewById(R.id.node_icon);
		iconView.setImageResource(value.icon);

		// text
		TextView valueView = view.findViewById(R.id.node_value);
		valueView.setText(value.text);

		// link listener
		final View hotLink = view.findViewById(R.id.node_link);
		if (hotLink != null)
		{
			hotLink.setOnClickListener(v -> followLink());
		}

		return view;
	}

	/**
	 * Follow link
	 */
	private void followLink()
	{
		final CompositeValue value = (CompositeValue) this.node.getValue();
		if (value != null)
		{
			assert value.payload != null;
			final Link link = (Link) value.payload[0];
			link.process();
		}
	}
}
