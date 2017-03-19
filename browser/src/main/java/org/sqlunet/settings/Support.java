package org.sqlunet.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.sqlunet.support.util.IABListener;

/**
 * Created by bbou on 3/18/17.
 */

public class Support extends AppCompatActivity implements IABListener
{
	static private final String TAG = "IAB MainActivity";

	// F R A G M E N T

	public static class BuyFragment extends PlaceholderFragment
	{
		public BuyFragment()
		{
			super(R.layout.fragment_buy);
		}
	}

	// E V E N T S

	/**
	 * Adapter to in-app billing
	 */
	private IABAdapter iabAdapter;

	private boolean enabled;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if (this.iabAdapter != null)
		{
			try
			{
				this.iabAdapter.destroy();
			}
			catch (Exception e)
			{
				//
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Log.d(TAG, "onActivityResult" + " request=" + requestCode + " result=" + resultCode + " intent=" + data);
		if (this.iabAdapter != null && !this.iabAdapter.handleActivityResult(requestCode, resultCode, data))
		{
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		this.enabled = Checker.check(this);
		if (this.enabled)
		{
			waitUI(false);
			enableApp(true);
			return;
		}

		// setup adapter
		if (this.iabAdapter == null)
		{
			this.iabAdapter = new IABAdapter();
			this.iabAdapter.create(this, this);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		menu.setGroupEnabled(R.id.app_available, this.enabled);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		int itemId = item.getItemId();
		if (itemId == R.id.action_buy)
		{
			if (this.iabAdapter == null)
			{
				this.iabAdapter = new IABAdapter();
				this.iabAdapter.create(this, new IABListener()
				{
					@Override
					public void onStart(Op op)
					{
						MainActivity.this.onStart(op);
					}

					@SuppressWarnings("synthetic-access")
					@Override
					public void onFinish(boolean result, Op op)
					{
						Log.d(TAG, "Finished IAB" + op);
						//MainActivity.this.onFinish(result, op);

						// chain inventory to buying
						switch (op)
						{
							case INVENTORY:
								MainActivity.this.iabAdapter.buy();
								break;
							case BUY:
								break;
							case SETUP:
							default:
								break;
						}
					}
				});
			}
			else
			{
				this.iabAdapter.buy();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);

	}

	/**
	 * Try to start Treebolic settings activity
	 */
	@Override
	protected void tryStartTreebolicSettings()
	{
		final Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	// L I S T E N E R

	@Override
	public void onStart(Op op)
	{
		//waitUI(true);
	}

	@Override
	public void onFinish(boolean result, Op op)
	{
		waitUI(false);
		switch (op)
		{
			case BUY:
			case INVENTORY:
				this.enabled = result;
				enableApp(result);
				break;

			case SETUP:
			default:
				break;
		}
	}

	private void waitUI(final boolean flag)
	{
		findViewById(R.id.wait_progress).setVisibility(flag ? View.VISIBLE : View.GONE);
		findViewById(R.id.container).setVisibility(flag ? View.GONE : View.VISIBLE);
	}

	private void enableApp(final boolean flag)
	{
		Log.d(TAG, "enableApp" + ' ' + flag);
		final Fragment fragment = flag ? makeMainFragment() : new BuyFragment();
		getSupportFragmentManager()//
				.beginTransaction()//
				.replace(R.id.container, fragment)//
				.commit();
		if (flag)
		{
			updateButton(this);
		}
	}

	// B U Y
	public void onBuy(@SuppressWarnings("unused") View v)
	{
		if (this.iabAdapter != null)
		{
			this.iabAdapter.buy();
		}
	}

}
