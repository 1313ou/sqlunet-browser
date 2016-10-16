package org.sqlunet.browser.selector;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.sqlunet.browser.DetailActivity;
import org.sqlunet.browser.DetailFragment;
import org.sqlunet.browser.R;
import org.sqlunet.provider.SqlUNetContract;
import org.sqlunet.wordnet.SensePointer;
import org.sqlunet.wordnet.browser.SenseFragment;

/**
 * An activity representing a list of synsets. This activity has different presentations for handset and tablet-size devices. On handsets, the activity presents
 * a list of items, which when touched, lead to a {@link DetailActivity} representing item details. On tablets, the activity presents the list of items and item
 * details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a {@link SelectorFragment} and the item details (if present) is a {@link SenseFragment}.
 * <p>
 * This activity also implements the required {@link SelectorFragment.Listener} interface to listen for item selections.
 *
 * @author Bernard Bou
 */
public class SelectorActivity extends Activity implements SelectorFragment.Listener
{
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
	 */
	private boolean isTwoPane = false;

	// C R E A T I O N

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// layout
		setContentView(R.layout.activity_selector);

		// show the Up button in the action bar.
		final ActionBar actionBar = getActionBar();
		assert actionBar != null;
		actionBar.setDisplayHomeAsUpEnabled(true);

		// query
		final Intent intent = getIntent();
		final String query = intent.getStringExtra(SqlUNetContract.ARG_QUERYSTRING);

		// copy to query view
		final TextView queryView = (TextView) findViewById(R.id.queryView);
		queryView.setText(query);

		// two-pane specific set up
		if (findViewById(R.id.container_main) != null)
		{
			// the detail container view will be present only in the large-screen layouts (res/values-large and res/values-sw600dp).
			// if this view is present, then the activity should be in two-pane mode.
			this.isTwoPane = true;

			// detail fragment
			final Fragment detailFragment = new DetailFragment();
			getFragmentManager() //
					.beginTransaction() //
					.replace(R.id.container_main, detailFragment) //
					.commit();

			// in two-pane mode, list items should be given the 'activated' state when touched.
			final SelectorFragment fragment = (SelectorFragment) getFragmentManager().findFragmentById(R.id.selector);
			fragment.setActivateOnItemClick(true);
		}
	}

	// I T E M S E L E C T I O N H A N D L I N G

	/**
	 * Callback method from {@link SelectorFragment.Listener} indicating that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(final SensePointer pointer)
	{
		if (this.isTwoPane)
		{
			// in two-pane mode, show the detail view in this activity by adding or replacing the detail fragment using a fragment transaction.
			DetailFragment fragment = (DetailFragment) getFragmentManager().findFragmentById(R.id.container_main);
			fragment.search(pointer);
		} else
		{
			// in single-pane mode, simply start the detail activity for the selected item ID.
			final Bundle arguments = new Bundle();
			arguments.putParcelable(SqlUNetContract.ARG_QUERYPOINTER, pointer);

			final Intent intent = new Intent(this, DetailActivity.class);
			intent.putExtras(arguments);
			startActivity(intent);
		}
	}
}
