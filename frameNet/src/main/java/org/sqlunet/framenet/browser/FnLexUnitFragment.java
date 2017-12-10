package org.sqlunet.framenet.browser;

import android.os.Bundle;
import android.os.Parcelable;

import org.sqlunet.browser.Module;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.framenet.R;
import org.sqlunet.framenet.loaders.LexUnitModule;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.treeview.model.TreeNode;

/**
 * A fragment representing a lex unit
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnLexUnitFragment extends TreeFragment
{
	// static private final String TAG = "FnLexUnitF";

	/**
	 * Constructor
	 */
	public FnLexUnitFragment()
	{
		this.layoutId = R.layout.fragment_fnlexunit;
		this.treeContainerId = R.id.data_contents;
		this.header = "LexUnit";
		this.iconId = R.drawable.member;
	}

	@Override
	public void onStart()
	{
		super.onStart();

		// query
		final Bundle args = getArguments();
		assert args != null;
		final int type = args.getInt(ProviderArgs.ARG_QUERYTYPE);
		if (args.containsKey(ProviderArgs.ARG_QUERYPOINTER))
		{
			// pointer
			final Parcelable pointer = args.getParcelable(ProviderArgs.ARG_QUERYPOINTER);

			// root node
			final TreeNode root = this.treeView.getRoot();
			final TreeNode queryNode = root.getChildren().iterator().next();

			// module
			final Module module = new LexUnitModule(this);
			module.init(type, pointer);
			module.process(queryNode);
		}
	}
}
