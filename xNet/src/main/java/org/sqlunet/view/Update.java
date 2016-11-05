package org.sqlunet.view;

import android.view.View;
import android.widget.TextView;

import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.renderer.QueryRenderer;
import org.sqlunet.treeview.renderer.Renderer;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.xnet.R;

/**
 * Tree factory
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Update
{
	/**
	 * Set node text
	 *
	 * @param node  node
	 * @param value character sequence
	 */
	static public void setNodeValue(final TreeNode node, final CharSequence value)
	{
		// delete node from parent if null value
		if (value == null || value.length() == 0)
		{
			TreeView.remove(node);
			return;
		}

		// update value
		node.setValue(value);

		// update view
		final Renderer<?> renderer = node.getRenderer();
		final View view = renderer.getNodeView();
		if (view != null)
		{
			if (view instanceof TextView)
			{
				final TextView textView = (TextView) view;
				textView.setText(value);
			}
			else
			{
				final TextView textView = (TextView) view.findViewById(R.id.node_value);
				if (textView != null)
				{
					textView.setText(value);
				}
			}
		}
	}

	// R E S U L T S  A V A I L A B L E

	/**
	 * No results have been attached to this node
	 *
	 * @param node       node
	 * @param addNewNode whether results were supposed to be new subnodes or relace query node
	 */
	static public void onNoResult(final TreeNode node, boolean addNewNode)
	{
		if (addNewNode)
		{
			TreeView.disable(node);
		}
		else
		{
			TreeView.remove(node);
		}
	}

	/**
	 * Results have been attached to this node
	 *
	 * @param node node
	 */
	static public void onResults(final TreeNode node)
	{
		TreeView.expand(node, false);
	}

	/**
	 * Results have been attached to this node
	 *
	 * @param node   node
	 * @param levels number of levels
	 */
	static public void onResults(final TreeNode node, int levels)
	{
		TreeView.expand(node, levels);
	}

	// Q U E R Y  R E A D Y

	static public void onQueryReady(final TreeNode node)
	{
		final Renderer<?> renderer = node.getRenderer();
		if (renderer instanceof QueryRenderer)
		{
			final QueryRenderer queryRenderer = (QueryRenderer) renderer;
			if (queryRenderer.triggerNow)
			{
				queryRenderer.processQuery();
			}
		}
	}
}
