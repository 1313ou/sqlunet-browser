package org.sqlunet.browser.xselector;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.sqlunet.bnc.browser.BNCFragment;
import org.sqlunet.browser.DetailActivity;
import org.sqlunet.browser.DetailFragment;
import org.sqlunet.browser.R;
import org.sqlunet.browser.web.WebFragment;
import org.sqlunet.framenet.browser.FrameNetFragment;
import org.sqlunet.propbank.browser.PropBankFragment;
import org.sqlunet.provider.SqlUNetContract;
import org.sqlunet.settings.Settings;
import org.sqlunet.verbnet.browser.VerbNetFragment;
import org.sqlunet.wordnet.browser.SenseFragment;

/**
 * X selector activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class XSelectorActivity extends Activity implements XSelectorFragment.Listener
{
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
	 */
	private boolean isTwoPane = false;

	// C R E A T I O N

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// layout
		setContentView(R.layout.activity_xselector);

		// show the Up button in the action bar.
		final ActionBar actionBar = getActionBar();
		assert actionBar != null;
		actionBar.setDisplayHomeAsUpEnabled(true);

		// query
		final Intent intent = getIntent();
		final String data = intent.getStringExtra(SqlUNetContract.ARG_QUERYSTRING);

		// copy to query view
		final TextView queryView = (TextView) findViewById(R.id.queryView);
		queryView.setText(data);

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
			final XSelectorFragment xFragment = (XSelectorFragment) getFragmentManager().findFragmentById(R.id.xselector);
			xFragment.setActivateOnItemClick(true);
		}
	}

	// I T E M S E L E C T I O N H A N D L I N G

	/**
	 * VerbNet fragment
	 */
	private Fragment verbnetFragment;

	/**
	 * PropBank fragment
	 */
	private Fragment propbankFragment;

	/**
	 * FrameNet fragment
	 */
	private Fragment framenetFragment;

	/**
	 * Callback method from {@link XSelectorFragment.Listener} indicating that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(final XPointer pointer)
	{
		if (this.isTwoPane)
		{
			// in two-pane mode, show the detail view in this activity by adding or replacing the detail fragment using a fragment transaction.
			final Bundle arguments = new Bundle();
			arguments.putParcelable(SqlUNetContract.ARG_QUERYPOINTER, pointer);
			final Settings.DetailViewMode mode = Settings.getDetailViewModePref(this);

			// transaction
			final FragmentTransaction transaction = getFragmentManager().beginTransaction();

			// detail fragment
			switch (mode)
			{
				case VIEW:
					if (Settings.getWordNetPref(this))
					{
						// final View labelView = findViewById(R.id.label_wordnet);
						// labelView.setVisibility(View.VISIBLE);
						final Fragment senseFragment = new SenseFragment();
						senseFragment.setArguments(arguments);
						transaction.replace(R.id.container_wordnet, senseFragment);
					}

					if (Settings.getVerbNetPref(this) && pointer.getXSources().contains("vn")) //$NON-NLS-1$
					{
						// final View labelView = findViewById(R.id.label_verbnet);
						// labelView.setVisibility(View.VISIBLE);
						this.verbnetFragment = new VerbNetFragment();
						this.verbnetFragment.setArguments(arguments);
						transaction.replace(R.id.container_verbnet, this.verbnetFragment);
					}
					else if (this.verbnetFragment != null)
					{
						transaction.remove(this.verbnetFragment);
						this.verbnetFragment = null;
					}

					if (Settings.getPropBankPref(this) && pointer.getXSources().contains("pb")) //$NON-NLS-1$
					{
						// final View labelView = findViewById(R.id.label_propbank);
						// labelView.setVisibility(View.VISIBLE);
						this.propbankFragment = new PropBankFragment();
						this.propbankFragment.setArguments(arguments);
						transaction.replace(R.id.container_propbank, this.propbankFragment);
					}
					else if (this.propbankFragment != null)
					{
						transaction.remove(this.propbankFragment);
						this.propbankFragment = null;
					}

					if (Settings.getFrameNetPref(this) && pointer.getXSources().contains("fn")) //$NON-NLS-1$
					{
						// final View labelView = findViewById(R.id.label_framenet);
						// labelView.setVisibility(View.VISIBLE);
						this.framenetFragment = new FrameNetFragment();
						this.framenetFragment.setArguments(arguments);
						transaction.replace(R.id.container_framenet, this.framenetFragment);
					}
					else if (this.framenetFragment != null)
					{
						transaction.remove(this.framenetFragment);
						this.framenetFragment = null;
					}

					if (Settings.getBncPref(this))
					{
						// final View labelView = findViewById(R.id.label_bnc);
						// labelView.setVisibility(View.VISIBLE);
						final Fragment bncFragment = new BNCFragment();
						bncFragment.setArguments(arguments);
						transaction.replace(R.id.container_bnc, bncFragment);
					}
					break;

				case WEB:
					final Fragment fragment = new WebFragment();

					// arguments
					fragment.setArguments(arguments);

					// detail fragment replace
					transaction.replace(R.id.container_main, fragment);
					break;
			}
			transaction.commit();
		}
		else
		{
			// in single-pane mode, simply start the detail activity for the selected item ID.
			final Intent intent = new Intent(this, DetailActivity.class);
			intent.putExtra(SqlUNetContract.ARG_QUERYPOINTER, pointer);
			startActivity(intent);
		}
	}
}
